package com.ki960213.sheathCore.sorter

import com.ki960213.sheathCore.component.SheathComponent
import java.util.LinkedList
import java.util.Queue

fun Collection<SheathComponent>.sorted(): List<SheathComponent> {
    val nodes: Set<Node> = this.map(::Node).toSet()
    val graph = Graph(nodes)

    val result: MutableList<Node> = mutableListOf()
    val queue: Queue<Node> = LinkedList()
    queue.addAll(nodes.filter { it.inDegreeCount == 0 })

    repeat(nodes.size) {
        val node = checkNotNull(queue.poll()) { "SheathComponent 간 의존 사이클이 존재합니다." }
        result.add(node)
        val dependNodes = graph.getNodesThatDependOn(node)
        dependNodes.forEach {
            it.minusInDegree()
            if (it.inDegreeCount == 0) queue.add(it)
        }
    }

    return result.map(Node::sheathComponent)
}
