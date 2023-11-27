package sorter

import com.google.common.truth.Expect
import com.google.common.truth.Truth.assertThat
import com.ki960213.sheathcore.annotation.Component
import com.ki960213.sheathcore.component.ClassSheathComponent
import com.ki960213.sheathcore.sorter.Graph
import com.ki960213.sheathcore.sorter.Node
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test

internal class GraphTest {

    @JvmField
    @Rule
    val expect: Expect = Expect.create()

    @Test
    fun `그래프를 만들 때 어떤 노드가 노드 목록에 없는 노드를 의존한다면 에러가 발생한다`() {
        val nodes = setOf(
            Node(ClassSheathComponent(Test1::class)),
        )

        try {
            Graph(nodes)
        } catch (e: IllegalArgumentException) {
            assertThat(e).hasMessageThat()
                .isEqualTo("${ClassSheathComponent(Test1::class)} 컴포넌트의 종속 항목 중 등록되지 않은 컴포넌트가 있습니다.")
        }
    }

    @Component
    private class Test1(val test2: Test2)

    @Component
    private class Test2

    @Component
    class Test3(test5: Test5, test6: Test6)

    @Component
    open class Test4

    @Component
    class Test5 : Test4()

    @Component
    class Test6 : Test4()

    @Test
    fun `그래프를 노드를 이용해 생성하면 각 노드를 의존하는 노드들을 저장한다`() {
        val node1 = Node(ClassSheathComponent(Test7::class))
        val node2 = Node(ClassSheathComponent(Test8::class))
        val node3 = Node(ClassSheathComponent(Test9::class))

        val graph = Graph(setOf(node1, node2, node3))

        expect.that(graph.getNodesThatDependOn(node1)).containsExactly(node3)
        expect.that(graph.getNodesThatDependOn(node2)).containsExactly(node1, node3)
        expect.that(graph.getNodesThatDependOn(node3)).containsExactly()
    }

    @Test
    fun `그래프에 없는 노드의 의존 노드를 가져오면 에러가 발생한다`() {
        val node1 = Node(ClassSheathComponent(Test1::class))
        val node2 = Node(ClassSheathComponent(Test2::class))
        val node3 = Node(ClassSheathComponent(Test3::class))
        val graph = Graph(setOf(node1, node2))

        try {
            graph.getNodesThatDependOn(node3)
            fail("테스트 실패함")
        } catch (e: IllegalArgumentException) {
            assertThat(e).hasMessageThat().isEqualTo("$node3 노드는 그래프에 없는 노드입니다.")
        }
    }

    @Component
    private class Test7(val test8: Test8)

    @Component
    private class Test8

    @Component
    private class Test9(val test7: Test7, val test8: Test8)
}
