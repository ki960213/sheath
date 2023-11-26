package com.github.ki960213.sheathcore.component

import com.github.ki960213.sheathcore.annotation.Component
import com.github.ki960213.sheathcore.annotation.Inject
import com.github.ki960213.sheathcore.annotation.Prototype
import com.github.ki960213.sheathcore.annotation.Qualifier
import com.github.ki960213.sheathcore.extention.findAttachedAnnotation
import com.github.ki960213.sheathcore.extention.hasAnnotationOrHasAttachedAnnotation
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.isAccessible

internal class ClassSheathComponent(
    private val clazz: KClass<*>,
) : SheathComponent() {

    override val type: KType = clazz.createType()

    override val qualifier: Annotation? = clazz.findAttachedAnnotation<Qualifier>()

    override val isSingleton: Boolean = !clazz.hasAnnotationOrHasAttachedAnnotation<Prototype>()

    override val dependencies: Set<Dependency> = clazz.extractDependenciesFromConstructor() +
        clazz.extractDependenciesFromProperties() +
        clazz.extractDependenciesFromFunctions()

    init {
        clazz.validateComponentAnnotation()
        clazz.validateConstructorInjection()
    }

    private fun KClass<*>.validateComponentAnnotation() {
        require(hasAnnotationOrHasAttachedAnnotation<Component>()) {
            "클래스에 @Component 혹은 @Component가 붙은 애노테이션이 붙어 있지 않다면 SheathComponent를 생성할 수 없습니다."
        }
    }

    private fun KClass<*>.validateConstructorInjection() {
        require(constructors.count { it.hasAnnotation<Inject>() } <= 1) {
            "여러 개의 생성자에 @Inject 애노테이션을 붙일 수 없습니다."
        }
    }

    override fun getNewInstance(): Any {
        val newInstance = clazz.createInstanceWithConstructorInjection()
        newInstance.executePropertyInjection()
        newInstance.executeFunctionInjection()
        return newInstance
    }

    private fun KClass<*>.createInstanceWithConstructorInjection(): Any {
        val constructor = getInjectConstructor()
        val arguments = constructor.valueParameters.map { getInstanceOf(Dependency.from(it)) }
        return constructor.call(*arguments.toTypedArray())!!
    }

    private fun KClass<*>.getInjectConstructor(): KFunction<*> =
        constructors.find { it.hasAnnotation<Inject>() }
            ?: primaryConstructor
            ?: throw IllegalArgumentException("$clazz 클래스는 생성자에 @Inject이 붙지 않고 주 생성자가 없어서 인스턴스화 할 수 없습니다.")

    private fun Any.executePropertyInjection() {
        val properties = clazz.findAnnotatedProperties<Inject>()
        properties.forEach { property ->
            val instance = getInstanceOf(Dependency.from(property))
            val mutableProperty = property as KMutableProperty1
            mutableProperty.setter.isAccessible = true
            mutableProperty.setter.call(this, instance)
        }
    }

    private inline fun <reified A : Annotation> KClass<*>.findAnnotatedProperties(): List<KProperty1<*, *>> =
        declaredMemberProperties.filter { it.hasAnnotation<A>() }

    private fun Any.executeFunctionInjection() {
        val functions = clazz.findAnnotatedFunctions<Inject>()
        functions.forEach { function ->
            val arguments = function.valueParameters.map { getInstanceOf(Dependency.from(it)) }
            function.isAccessible = true
            function.call(this, *arguments.toTypedArray())
        }
    }

    private inline fun <reified A : Annotation> KClass<*>.findAnnotatedFunctions(): List<KFunction<*>> =
        declaredMemberFunctions.filter { it.hasAnnotation<A>() }

    private fun KClass<*>.extractDependenciesFromConstructor(): Set<Dependency> =
        constructors.find { it.hasAnnotation<Inject>() }
            ?.valueParameters
            ?.map { Dependency.from(it) }
            ?.toSet()
            ?: extractDependenciesFromPrimaryConstructor()

    private fun KClass<*>.extractDependenciesFromPrimaryConstructor(): Set<Dependency> =
        primaryConstructor?.valueParameters
            ?.map { Dependency.from(it) }
            ?.toSet()
            ?: emptySet()

    private fun KClass<*>.extractDependenciesFromProperties(): Set<Dependency> =
        declaredMemberProperties.filter { it.hasAnnotation<Inject>() }
            .map { Dependency.from(it) }
            .toSet()

    private fun KClass<*>.extractDependenciesFromFunctions(): Set<Dependency> =
        declaredMemberFunctions.filter { it.hasAnnotation<Inject>() }
            .flatMap { function ->
                function.valueParameters.map { Dependency.from(it) }
            }
            .toSet()
}
