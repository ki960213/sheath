package sorter

import com.google.common.truth.Truth.assertThat
import com.ki960213.sheathCore.annotation.Component
import com.ki960213.sheathCore.component.ClassSheathComponent
import com.ki960213.sheathCore.sorter.sorted
import org.junit.Test

internal class SorterKtTest {

    @Test
    fun `SheathComponent 목록을 받으면 위상 정렬된 목록을 반환한다`() {
        val components = listOf(
            ClassSheathComponent(Test1::class),
            ClassSheathComponent(Test2::class),
            ClassSheathComponent(Test3::class),
            ClassSheathComponent(Test4::class),
        )

        val result = components.sorted()

        val expected = listOf(
            ClassSheathComponent(Test4::class),
            ClassSheathComponent(Test2::class),
            ClassSheathComponent(Test3::class),
            ClassSheathComponent(Test1::class),
        )
        assertThat(result).isEqualTo(expected)
    }

    @Component
    private class Test1(val test2: Test2, val test3: Test3)

    @Component
    private class Test2(val test4: Test4)

    @Component
    private class Test3(val test4: Test4)

    @Component
    private class Test4

    @Test
    fun `SheathComponent 간에 의존 사이클이 존재하면 에러가 발생한다`() {
        val components = listOf(
            ClassSheathComponent(Test5::class),
            ClassSheathComponent(Test6::class),
            ClassSheathComponent(Test7::class),
        )

        try {
            components.sorted()
        } catch (e: IllegalStateException) {
            assertThat(e).hasMessageThat()
                .isEqualTo("SheathComponent 간 의존 사이클이 존재합니다.")
        }
    }

    @Component
    private class Test5(val test6: Test6)

    @Component
    private class Test6(val test7: Test7)

    @Component
    private class Test7(val test5: Test5)
}
