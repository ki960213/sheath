package com.github.ki960213.sheathcore.sorter

import com.github.ki960213.sheathcore.component.SheathComponent
import kotlin.properties.Delegates.observable

internal class Node(val sheathComponent: SheathComponent) {

    val dependencyCount: Int = sheathComponent.dependencyCount

    var inDegreeCount: Int by observable(dependencyCount) { _, _, newValue ->
        check(newValue >= 0) { "노드의 진입 차수가 0 미만일 수 없습니다." }
    }
        private set

    fun minusInDegree() {
        inDegreeCount--
    }

    fun isDependingOn(other: Node): Boolean = sheathComponent.isDependingOn(other.sheathComponent)

    override fun equals(other: Any?): Boolean =
        if (other is Node) sheathComponent == other.sheathComponent else false

    override fun hashCode(): Int = sheathComponent.hashCode()
}
