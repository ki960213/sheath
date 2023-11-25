package com.ki960213.sheathCore.scanner

import com.ki960213.sheathCore.annotation.Component
import com.ki960213.sheathCore.annotation.DataSource
import com.ki960213.sheathCore.annotation.Module
import com.ki960213.sheathCore.annotation.Repository
import com.ki960213.sheathCore.annotation.SheathViewModel
import com.ki960213.sheathCore.annotation.UseCase
import com.ki960213.sheathCore.component.ClassSheathComponent
import com.ki960213.sheathCore.component.FunctionSheathComponent
import com.ki960213.sheathCore.component.SheathComponent
import com.ki960213.sheathCore.extention.hasAnnotationOrHasAttachedAnnotation
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
