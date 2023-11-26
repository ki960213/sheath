package com.ki960213.sheathcore.component

import com.ki960213.sheathcore.annotation.Component
import com.ki960213.sheathcore.annotation.Module
import com.ki960213.sheathcore.annotation.Prototype
import com.ki960213.sheathcore.annotation.Qualifier
import com.ki960213.sheathcore.extention.findAttachedAnnotation
import com.ki960213.sheathcore.extention.hasAnnotationOrHasAttachedAnnotation
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.javaMethod

internal class FunctionSheathComponent(
    private val function: KFunction<*>,
) : SheathComponent() {

    override val type: KType = function.returnType

    override val qualifier: Annotation? = function.findAttachedAnnotation<Qualifier>()

    override val isSingleton: Boolean = !function.hasAnnotationOrHasAttachedAnnotation<Prototype>()

    override val dependencies: Set<Dependency> = function.valueParameters
        .map { Dependency.from(it) }
        .toSet()

    private val moduleInstance: Any = function.javaMethod?.declaringClass?.kotlin?.objectInstance
        ?: throw IllegalArgumentException("${function.name} 함수가 정의된 클래스가 object가 아닙니다.")

    init {
        function.validateComponentAnnotation()
        function.validateModuleAnnotation()
        function.validateReturnType()
    }

    override fun getNewInstance(): Any {
        val arguments = function.valueParameters.map { getInstanceOf(Dependency.from(it)) }
        return function.call(moduleInstance, *arguments.toTypedArray())!!
    }

    private fun KFunction<*>.validateComponentAnnotation() {
        require(hasAnnotationOrHasAttachedAnnotation<Component>()) {
            "함수에 @Component 혹은 @Component가 붙은 애노테이션이 붙어 있지 않다면 SheathComponent를 생성할 수 없습니다."
        }
    }

    private fun KFunction<*>.validateModuleAnnotation() {
        requireNotNull(javaMethod?.declaringClass?.getAnnotation(Module::class.java)) {
            "${this.name} 함수가 정의된 클래스에 @Module이 붙어있어야 합니다."
        }
    }

    private fun KFunction<*>.validateReturnType() {
        require(!returnType.isMarkedNullable) {
            "${this.name} 함수의 반환 타입이 nullable 입니다. nullable인 함수는 Sheath 컴포넌트로 등록할 수 없습니다."
        }
    }
}
