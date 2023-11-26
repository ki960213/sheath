package com.github.ki960213.sheathcore.scanner

import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import kotlin.reflect.KClass

object ClassIndex {

    const val ANNOTATED_INDEX_PREFIX = "META-INF/annotations/"

    fun getAnnotatedClasses(
        annotation: KClass<out Annotation>,
        classLoader: ClassLoader = Thread.currentThread().contextClassLoader!!,
    ): Iterable<KClass<*>> = findClasses(classLoader, getAnnotatedNames(annotation, classLoader))

    private fun findClasses(
        classLoader: ClassLoader,
        entries: Iterable<String>,
    ): Set<KClass<*>> = entries.mapNotNull {
        runCatching {
            classLoader.loadClass(it).kotlin
        }.getOrElse {
            if (it is ClassNotFoundException || it is NoClassDefFoundError) null else throw it
        }
    }.toSet()

    private fun getAnnotatedNames(
        annotation: KClass<out Annotation>,
        classLoader: ClassLoader,
    ): Iterable<String> = readIndexFile(
        classLoader = classLoader,
        resourceFile = ANNOTATED_INDEX_PREFIX + annotation.java.canonicalName,
    )

    private fun readIndexFile(
        classLoader: ClassLoader,
        resourceFile: String,
    ): Iterable<String> {
        val entries = mutableSetOf<String>()

        runCatching {
            classLoader.getResources(resourceFile)
        }.onFailure {
            if (it is IOException) throw RuntimeException("ClassIndex: Cannot read class index", it)
        }.onSuccess { resources ->
            while (resources.hasMoreElements()) {
                val resource = resources.nextElement()
                entries.addAnnotationNamesFrom(resource)
            }
        }
        return entries
    }

    private fun MutableSet<String>.addAnnotationNamesFrom(resource: URL) {
        runCatching {
            BufferedReader(InputStreamReader(resource.openStream(), "UTF-8")).use {
                var line = it.readLine()
                while (line != null) {
                    add(line)
                    line = it.readLine()
                }
            }
        }.onFailure {
            if (it !is FileNotFoundException) throw it
        }
    }
}
