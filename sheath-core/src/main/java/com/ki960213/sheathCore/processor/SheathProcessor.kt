package com.ki960213.sheathCore.processor

import com.google.auto.service.AutoService
import com.ki960213.sheathCore.annotation.IndexAnnotated
import com.ki960213.sheathCore.scanner.ClassIndex
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException
import java.io.Reader
import java.util.Collections
import java.util.TreeSet
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.NestingKind
import javax.lang.model.element.TypeElement
import javax.lang.model.util.ElementScanner8
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic
import javax.tools.FileObject
import javax.tools.StandardLocation

@AutoService(Processor::class)
class SheathProcessor : AbstractProcessor() {

    private val annotatedMap: MutableMap<String, MutableSet<String>> = mutableMapOf()

    private var annotationDriven: Boolean = true
    private val indexedAnnotations: MutableSet<String> = mutableSetOf()

    private lateinit var types: Types
    private lateinit var filer: Filer
    private lateinit var elementUtils: Elements
    private lateinit var messager: Messager

    private val TypeElement.fullName: String?
        get() = when (nestingKind) {
            NestingKind.TOP_LEVEL -> qualifiedName.toString()
            NestingKind.MEMBER -> {
                val enclosingElement = enclosingElement

                if (enclosingElement is TypeElement) {
                    enclosingElement.fullName?.let { "$it\$$simpleName" }
                } else {
                    null
                }
            }

            NestingKind.LOCAL, NestingKind.ANONYMOUS -> null
            else -> null
        }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun getSupportedAnnotationTypes(): MutableSet<String> = Collections.singleton("*")

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        types = processingEnv.typeUtils
        filer = processingEnv.filer
        elementUtils = processingEnv.elementUtils
        messager = processingEnv.messager
    }

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment,
    ): Boolean {
        try {
            roundEnv.rootElements.forEach { element ->
                if (element !is TypeElement) return@forEach
                element.accept(
                    object : ElementScanner8<Unit, Unit>() {
                        override fun visitType(typeElement: TypeElement, o: Unit?) {
                            try {
                                typeElement.annotationMirrors.forEach { mirror ->
                                    val annotationElement =
                                        mirror.annotationType.asElement() as TypeElement
                                    storeAnnotation(annotationElement, typeElement)
                                }
                            } catch (e: Throwable) {
                                messager.printMessage(
                                    Diagnostic.Kind.ERROR,
                                    "[SheathProcessor] " + e.message,
                                )
                            }

                            return super.visitType(typeElement, o)
                        }
                    },
                    null,
                )
            }

            if (!roundEnv.processingOver()) return false

            writeIndexFile(ClassIndex.ANNOTATED_INDEX_PREFIX, annotatedMap)
        } catch (e: IOException) {
            messager.printMessage(
                Diagnostic.Kind.ERROR,
                "[SheathProcessor] Can't write index file: " + e.message,
            )
        } catch (e: Throwable) {
            e.printStackTrace()
            messager.printMessage(
                Diagnostic.Kind.ERROR,
                "[SheathProcessor] Internal error: " + e.message,
            )
        }
        return false
    }

    private fun storeAnnotation(annotationElement: TypeElement, rootElement: TypeElement) {
        if (indexedAnnotations.contains(annotationElement.qualifiedName.toString())) {
            annotatedMap.putElement(annotationElement.qualifiedName.toString(), rootElement)
        } else if (annotationDriven) {
            annotationElement.getAnnotation(IndexAnnotated::class.java)?.run {
                annotatedMap.putElement(annotationElement.qualifiedName.toString(), rootElement)
            }
        }
    }

    private fun <K> MutableMap<K, MutableSet<String>>.putElement(
        keyElement: K,
        valueElement: TypeElement,
    ) {
        valueElement.fullName?.run { putElement(keyElement, this@run) }
    }

    private fun <K> MutableMap<K, MutableSet<String>>.putElement(
        keyElement: K,
        valueElement: String,
    ) {
        if (keyElement !in this) this[keyElement] = TreeSet<String>()
        this[keyElement]?.add(valueElement)
    }

    private fun writeIndexFile(prefix: String, indexMap: Map<String, MutableSet<String>>) {
        indexMap.entries.forEach { (key, value) ->
            writeSimpleNameIndexFile(value, prefix + key)
        }
    }

    private fun writeSimpleNameIndexFile(elementList: MutableSet<String>, resourceName: String) {
        val file = readOldIndexFile(elementList, resourceName)
        if (file != null) {
            try {
                writeIndexFile(elementList, resourceName, file)
                return
            } catch (_: IllegalStateException) {
            }
        }
        writeIndexFile(elementList, resourceName, null)
    }

    private fun readOldIndexFile(entries: MutableSet<String>, resourceName: String): FileObject? {
        var reader: Reader? = null
        try {
            val resource = filer.getResource(StandardLocation.CLASS_OUTPUT, "", resourceName)
            reader = resource.openReader(true)
            readOldIndexFile(entries, reader)
            return resource
        } catch (e: FileNotFoundException) {
            val realPath = e.message.toString()
            if (File(realPath).exists()) {
                FileReader(realPath).use {
                    readOldIndexFile(entries, it)
                }
            }
        } catch (_: IOException) {
        } catch (_: UnsupportedOperationException) {
        } finally {
            reader?.close()
        }
        return null
    }

    private fun readOldIndexFile(entries: MutableSet<String>, reader: Reader) {
        BufferedReader(reader).use {
            var line = it.readLine()
            while (line != null) {
                entries.add(line)
                line = it.readLine()
            }
        }
    }

    private fun writeIndexFile(
        entries: MutableSet<String>,
        resourceName: String,
        overrideFile: FileObject?,
    ) {
        overrideFile ?: filer.createResource(StandardLocation.CLASS_OUTPUT, "", resourceName)
            .openWriter()
            .use { writer ->
                entries.forEach { writer?.write("${it}\n") }
            }
    }
}
