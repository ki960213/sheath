package com.github.ki960213.sheathcore.scanner

import com.github.ki960213.sheathcore.annotation.Component
import com.github.ki960213.sheathcore.annotation.DataSource
import com.github.ki960213.sheathcore.annotation.Module
import com.github.ki960213.sheathcore.annotation.Repository
import com.github.ki960213.sheathcore.annotation.SheathViewModel
import com.github.ki960213.sheathcore.annotation.UseCase
import com.github.ki960213.sheathcore.component.ClassSheathComponent
import com.github.ki960213.sheathcore.component.FunctionSheathComponent
import com.github.ki960213.sheathcore.component.SheathComponent
import com.github.ki960213.sheathcore.extention.hasAnnotationOrHasAttachedAnnotation
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberFunctions

class ComponentScanner {
    fun findAll(): List<SheathComponent> {
        val components = mutableListOf<SheathComponent>()
        val annotatedClasses = INDEXED_ANNOTATIONS.flatMap { ClassIndex.getAnnotatedClasses(it) }
        annotatedClasses.forEach {
            if (it.isComponent()) components.add(ClassSheathComponent(it))
            if (it.isModule()) components.addAll(it.extractSheathComponent())
        }
        println("스캔한 컴포넌트:")
        components.forEach { println(it) }
        return components
    }

    private fun KClass<*>.isComponent(): Boolean =
        this.annotations.any { hasAnnotationOrHasAttachedAnnotation<Component>() }

    private fun KClass<*>.isModule(): Boolean =
        this.annotations.any { it.annotationClass == Module::class }

    private fun KClass<*>.extractSheathComponent(): List<SheathComponent> =
        declaredMemberFunctions.mapNotNull { function ->
            if (function.hasAnnotationOrHasAttachedAnnotation<Component>()) {
                FunctionSheathComponent(function)
            } else {
                null
            }
        }

    companion object {
        private val INDEXED_ANNOTATIONS = listOf(
            Component::class,
            SheathViewModel::class,
            UseCase::class,
            Repository::class,
            DataSource::class,
            Module::class,
        )
    }
}
