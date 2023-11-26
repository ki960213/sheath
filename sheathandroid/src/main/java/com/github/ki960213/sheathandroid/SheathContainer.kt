package com.github.ki960213.sheathandroid

import com.github.ki960213.sheathcore.component.SheathComponent
import com.github.ki960213.sheathcore.sorter.sorted
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSupertypeOf

class SheathContainer private constructor(private val components: Set<SheathComponent>) {

    operator fun get(
        type: KType,
        qualifier: KClass<*>? = null,
    ): SheathComponent {
        val dependingComponents =
            components.filter { type.isSupertypeOf(it.type) && (if (qualifier == null) true else qualifier == it.qualifier) }
        if (dependingComponents.isEmpty()) throw IllegalArgumentException("$type 타입의 종속 항목이 존재하지 않습니다.")
        if (dependingComponents.size > 1) throw IllegalArgumentException("$type 타입의 종속 항목이 모호합니다.")
        return dependingComponents.first()
    }

    companion object {
        fun from(components: List<SheathComponent>): SheathContainer {
            val distinctComponents = components.toSet()
            require(distinctComponents.size == components.size) { "중복된 컴포넌트가 존재합니다." }

            val initializedComponents = distinctComponents.sorted()
                .fold(mutableListOf<SheathComponent>()) { acc, component ->
                    component.initialize(acc)
                    acc.add(component)
                    acc
                }
            return SheathContainer(initializedComponents.toSet())
        }
    }
}
