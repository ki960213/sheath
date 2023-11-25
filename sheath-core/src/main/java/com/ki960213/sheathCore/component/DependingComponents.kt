package com.ki960213.sheathCore.component

class DependingComponents {
    private val value: MutableMap<Dependency, SheathComponent> = mutableMapOf()

    operator fun get(dependency: Dependency): SheathComponent = value[dependency]
        ?: throw IllegalArgumentException("${dependency.type} 타입의 종속 항목을 제공하는 Sheath 컴포넌트가 존재하지 않습니다.")

    fun putAll(components: Map<Dependency, SheathComponent>) {
        value.putAll(components)
    }
}
