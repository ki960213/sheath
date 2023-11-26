package com.github.ki960213.sheathcore.component

import com.github.ki960213.sheathcore.annotation.NewInstance
import com.github.ki960213.sheathcore.annotation.Qualifier
import com.github.ki960213.sheathcore.extention.findAttachedAnnotation
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSupertypeOf

class Dependency(
    val type: KType,
    val qualifier: Annotation?,
    val isNewInstance: Boolean,
) {
    infix fun on(component: SheathComponent): Boolean = when {
        !type.isSupertypeOf(component.type) -> false
        qualifier == null -> true
        qualifier == component.qualifier -> true
        else -> false
    }

    override fun equals(other: Any?): Boolean =
        if (other is Dependency) type == other.type && qualifier == other.qualifier else false

    override fun hashCode(): Int = type.hashCode() * 31 + qualifier.hashCode()

    companion object {
        fun from(param: KParameter) = Dependency(
            type = param.type,
            qualifier = param.findAttachedAnnotation<Qualifier>(),
            isNewInstance = param.hasAnnotation<NewInstance>(),
        )

        fun from(property: KProperty1<*, *>) = Dependency(
            type = property.returnType,
            qualifier = property.findAttachedAnnotation<Qualifier>(),
            isNewInstance = property.hasAnnotation<NewInstance>(),
        )
    }
}
