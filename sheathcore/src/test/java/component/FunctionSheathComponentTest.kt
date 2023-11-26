package component

import com.github.ki960213.sheathcore.annotation.Component
import com.github.ki960213.sheathcore.annotation.Module
import com.github.ki960213.sheathcore.annotation.Prototype
import com.github.ki960213.sheathcore.component.FunctionSheathComponent
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import kotlin.reflect.full.createType

internal class FunctionSheathComponentTest {

    @Test
    fun `함수에 Component 애노테이션이 붙어있지 않고 Component 애노테이션이 붙은 애노테이션이 붙어 있지 않다면 에러가 발생한다`() {
        try {
            FunctionSheathComponent(Module1001::test)
        } catch (e: IllegalArgumentException) {
            assertThat(e)
                .hasMessageThat()
                .isEqualTo("함수에 @Component 혹은 @Component가 붙은 애노테이션이 붙어 있지 않다면 SheathComponent를 생성할 수 없습니다.")
        }
    }

    @Module
    object Module1001 {
        fun test(): Unit = Unit
    }

    @Test
    fun `함수가 정의된 클래스가 object가 아니면 에러가 발생한다`() {
        try {
            FunctionSheathComponent(Module1002::test)
        } catch (e: IllegalArgumentException) {
            assertThat(e)
                .hasMessageThat()
                .isEqualTo("${Module1002::test.name} 함수가 정의된 클래스가 object가 아닙니다.")
        }
    }

    @Module
    class Module1002 {
        @Component
        fun test(): Unit = Unit
    }

    @Test
    fun `함수가 정의된 클래스에 @Module이 붙어 있지 않다면 에러가 발생한다`() {
        try {
            FunctionSheathComponent(Module1003::test)
        } catch (e: IllegalArgumentException) {
            assertThat(e)
                .hasMessageThat()
                .isEqualTo("${Module1003::test.name} 함수가 정의된 클래스에 @Module이 붙어있어야 합니다.")
        }
    }

    object Module1003 {
        @Component
        fun test(): Unit = Unit
    }

    @Module
    object Module1004 {
        @Component
        fun test(): Unit = Unit
    }

    interface Test1004

    @Component
    class Test1005 : Test1004

    @Component
    class Test1006 : Test1004

    @Test
    fun `FunctionSheathComponent 객체를 생성하면 타입은 함수의 리턴 타입과 같다`() {
        val actual = FunctionSheathComponent(Module101::test)

        assertThat(actual.type).isEqualTo(Test105::class.createType())
    }

    @Module
    object Module101 {
        @Component
        fun test(): Test105 = Test105()
    }

    class Test105

    @Test
    fun `FunctionSheathComponent 객체를 생성할 때 함수에 Prototype 애노테이션이 붙어 있다면 싱글톤이 아니다`() {
        val actual = FunctionSheathComponent(Module102::test)

        assertThat(actual.isSingleton).isFalse()
    }

    @Module
    object Module102 {
        @Prototype
        @Component
        fun test(): Unit = Unit
    }

    @Test
    fun `FunctionSheathComponent 객체를 생성할 때 함수에 Prototype 애노테이션이 붙은 애노테이션이 붙어 있다면 싱글톤이 아니다`() {
        val actual = FunctionSheathComponent(Module103::test)

        assertThat(actual.isSingleton).isFalse()
    }

    @Module
    object Module103 {
        @PrototypeAttached
        @Component
        fun test(): Unit = Unit
    }

    @Prototype
    annotation class PrototypeAttached

    @Test
    fun `FunctionSheathComponent 객체를 생성할 때 함수에 Prototype 애노테이션이 붙은 애노테이션이나 Prototype 애노테이션이 붙어 있지 않다면 싱글톤이다`() {
        val actual = FunctionSheathComponent(Module104::test)

        assertThat(actual.isSingleton).isTrue()
    }

    @Module
    object Module104 {
        @Component
        fun test(): Unit = Unit
    }
}
