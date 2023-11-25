package com.ki960213.sheathCore.sorter

internal class Graph(private val nodes: Set<Node>) {

    private val dependencyMap: Map<Node, List<Node>> = nodes.associateWith { node ->
        nodes.filter { it.isDependingOn(node) }
    }

    init {
        nodes.forEach { node ->
            if (node.dependencyCount > node.dependencyCountInGraph) {
                throw IllegalArgumentException("${node.sheathComponent} 컴포넌트의 종속 항목 중 등록되지 않은 컴포넌트가 있습니다.")
            }
            if (node.dependencyCount < node.dependencyCountInGraph) {
                throw IllegalArgumentException("${node.sheathComponent} 컴포넌트의 종속 항목 중 모호한 종속 항목이 존재합니다.")
            }
        }
    }

    private val Node.dependencyCountInGraph: Int
        get() = nodes.count { node -> isDependingOn(node) }

    fun getNodesThatDependOn(node: Node): List<Node> =
        dependencyMap[node] ?: throw IllegalArgumentException("$node 노드는 그래프에 없는 노드입니다.")
}
