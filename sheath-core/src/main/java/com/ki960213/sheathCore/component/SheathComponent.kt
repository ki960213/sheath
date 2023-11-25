package com.ki960213.sheathCore.component

import kotlin.reflect.KType

abstract class SheathComponent {
    abstract val type: KType

    abstract val qualifier: Annotation?

    abstract val isSingleton: Boolean

    protected abstract val dependencies: Set<Dependency>

    val dependencyCount: Int
        get() = dependencies.size

    private val dependingComponents = DependingComponents()

    val instance: Any by lazy { getNewInstance() }

    fun isDependingOn(component: SheathComponent): Boolean = dependencies.any { it on component }

    fun initialize(components: List<SheathComponent>) {
        val dependingComponents = dependencies.associateWith { dependency ->
            components.find { component -> dependency on component }
                ?: throw IllegalArgumentException("$type 타입의 컴포넌트를 초기화 하기에 ${dependency.type} 타입의 Sheath 컴포넌트가 부족합니다.")
        }
        this.dependingComponents.putAll(dependingComponents)
    }

    abstract fun getNewInstance(): Any

    protected fun getInstanceOf(dependency: Dependency): Any {
        val component = dependingComponents[dependency]

        return if (dependency.isNewInstance || !component.isSingleton) {
            component.getNewInstance()
        } else {
            component.instance
        }
    }

    override fun equals(other: Any?): Boolean =
        if (other is SheathComponent) type == other.type && qualifier == other.qualifier else false

    override fun hashCode(): Int = type.hashCode() * 31 + qualifier.hashCode()

    override fun toString(): String =
        "SheathComponent(type=$type" + (if (qualifier != null) ", qualifier=$qualifier" else "") + ")"
}
