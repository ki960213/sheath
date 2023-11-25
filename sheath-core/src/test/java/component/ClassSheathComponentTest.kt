package component

import com.google.common.truth.Truth.assertThat
import com.ki960213.sheathCore.annotation.Component
import com.ki960213.sheathCore.annotation.Inject
import com.ki960213.sheathCore.annotation.NewInstance
import com.ki960213.sheathCore.annotation.Prototype
import com.ki960213.sheathCore.component.ClassSheathComponent
import org.junit.Test
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties

internal class ClassSheathComponentTest {

    @Test
    fun `클래스에 Component 애노테이션이 붙어있지 않고 Component 애노테이션이 붙은 애노테이션이 붙어 있지 않다면 에러가 발생한다`() {
        try {
            ClassSheathComponent(Test1001::class)
        } catch (e: IllegalArgumentException) {
            assertThat(e)
                .hasMessageThat()
                .isEqualTo("클래스에 @Component 혹은 @Component가 붙은 애노테이션이 붙어 있지 않다면 SheathComponent를 생성할 수 없습니다.")
        }
    }

    class Test1001

    @Test
    fun `클래스에 @Inject가 여러 생성자에 붙어 있다면 에러가 발생한다`() {
        try {
            ClassSheathComponent(Test1002::class)
        } catch (e: IllegalArgumentException) {
            assertThat(e)
                .hasMessageThat()
                .isEqualTo("여러 개의 생성자에 @Inject 애노테이션을 붙일 수 없습니다.")
        }
    }

    @Component
    class Test1002 @Inject constructor() {
        @Inject
        constructor(any: Any) : this()
    }

    interface Test1004

    @Component
    class Test1005 : Test1004

    @Component
    class Test1006 : Test1004

    @Test
    fun `ClassSheathComponent 객체를 생성하면 타입은 클래스의 타입과 같다`() {
        val actual = ClassSheathComponent(Test101::class)

        assertThat(actual.type).isEqualTo(Test101::class.createType())
    }

    @Component
    class Test101

    @Test
    fun `ClassSheathComponent 객체를 생성할 때 지역 클래스라면 에러가 발생한다`() {
        @Component
        class Test

        try {
            ClassSheathComponent(Test::class)
        } catch (e: IllegalArgumentException) {
            assertThat(e)
                .hasMessageThat()
                .isEqualTo("전역적인 클래스로만 SheathComponent를 생성할 수 있습니다.")
        }
    }

    @Test
    fun `ClassSheathComponent 객체를 생성할 때 클래스에 Prototype 애노테이션이 붙어 있다면 싱글톤이 아니다`() {
        val actual = ClassSheathComponent(Test102::class)

        assertThat(actual.isSingleton).isFalse()
    }

    @Prototype
    @Component
    class Test102

    @Test
    fun `ClassSheathComponent 객체를 생성할 때 클래스에 Prototype 애노테이션이 붙은 애노테이션이 붙어 있다면 싱글톤이 아니다`() {
        val actual = ClassSheathComponent(Test103::class)

        assertThat(actual.isSingleton).isFalse()
    }

    @Prototype
    annotation class PrototypeAttached

    @PrototypeAttached
    @Component
    class Test103

    @Test
    fun `ClassSheathComponent 객체를 생성할 때 클래스에 Prototype 애노테이션이 붙은 애노테이션이나 Prototype 애노테이션이 붙어 있지 않다면 싱글톤이다`() {
        val actual = ClassSheathComponent(Test104::class)

        assertThat(actual.isSingleton).isTrue()
    }

    @Component
    class Test104

    @Component
    class Test1(test2: Test2)

    @Component
    class Test2

    private fun KClass<*>.getProperty(name: String): KProperty1<*, *> =
        declaredMemberProperties.find { it.name == name }!!

    @Component
    class Test3 {

        @Inject
        private lateinit var test2: Test2
    }

    @Component
    class Test4 {
        @Inject
        private fun test(test2: Test2): Unit = Unit
    }

    interface Test24

    @Component
    class Test25 : Test24

    @Component
    class Test26 : Test24

    @Test
    fun `인스턴스화 하면 인스턴스가 할당된다`() {
        val sheathComponent1 = ClassSheathComponent(Test1::class)
        val sheathComponent2 = ClassSheathComponent(Test2::class)
        sheathComponent2.initialize(emptyList())

        sheathComponent1.initialize(listOf(sheathComponent2))

        assertThat(sheathComponent1.instance).isInstanceOf(Test1::class.java)
    }

    @Test
    fun `새로운 인스턴스를 반환하면 이전 인스턴스와 다른 새 인스턴스가 반환된다`() {
        val sheathComponent1 = ClassSheathComponent(Test1::class)
        val sheathComponent2 = ClassSheathComponent(Test2::class)
        sheathComponent2.initialize(emptyList())
        sheathComponent1.initialize(listOf(sheathComponent2))

        val actual = sheathComponent1.getNewInstance()

        assertThat(actual).isNotEqualTo(sheathComponent1.instance)
    }

    @Test
    fun `새 인스턴스 반환 시 생성자의 종속 항목이 싱글톤이면 같은 종속 항목을 주입받는다`() {
        val sheathComponent1 = ClassSheathComponent(Test5::class)
        val sheathComponent2 = ClassSheathComponent(Test6::class)
        sheathComponent2.initialize(emptyList())
        sheathComponent1.initialize(listOf(sheathComponent2))

        val actual = (sheathComponent1.getNewInstance() as Test5).test6

        assertThat(actual).isEqualTo(sheathComponent2.instance)
    }

    @Component
    class Test5(val test6: Test6)

    @Component
    class Test6

    @Test
    fun `새 인스턴스 반환 시 생성자의 종속 항목이 싱글톤이 아니면 새 종속 항목을 주입받는다`() {
        val sheathComponent1 = ClassSheathComponent(Test7::class)
        val sheathComponent2 = ClassSheathComponent(Test8::class)
        sheathComponent2.initialize(emptyList())
        sheathComponent1.initialize(listOf(sheathComponent2))

        val actual = (sheathComponent1.getNewInstance() as Test7).test8

        assertThat(actual).isNotEqualTo(sheathComponent2.instance)
    }

    @Component
    class Test7(val test8: Test8)

    @Prototype
    @Component
    class Test8

    @Test
    fun `새 인스턴스 반환 시 프로퍼티의 종속 항목이 싱글톤이면 같은 종속 항목을 주입받는다`() {
        val sheathComponent1 = ClassSheathComponent(Test9::class)
        val sheathComponent2 = ClassSheathComponent(Test10::class)
        sheathComponent2.initialize(emptyList())
        sheathComponent1.initialize(listOf(sheathComponent2))

        val actual = (sheathComponent1.getNewInstance() as Test9).test10

        assertThat(actual).isEqualTo(sheathComponent2.instance)
    }

    @Component
    class Test9 {
        @Inject
        lateinit var test10: Test10
    }

    @Component
    class Test10

    @Test
    fun `새 인스턴스 반환 시 프로퍼티의 종속 항목이 싱글톤이 아니면 새 종속 항목을 주입받는다`() {
        val sheathComponent1 = ClassSheathComponent(Test11::class)
        val sheathComponent2 = ClassSheathComponent(Test12::class)
        sheathComponent2.initialize(emptyList())
        sheathComponent1.initialize(listOf(sheathComponent2))

        val actual = (sheathComponent1.getNewInstance() as Test11).test12

        assertThat(actual).isNotEqualTo(sheathComponent2.instance)
    }

    @Component
    class Test11 {
        @Inject
        lateinit var test12: Test12
    }

    @Prototype
    @Component
    class Test12

    @Test
    fun `새 인스턴스 반환 시 메서드의 종속 항목이 싱글톤이면 같은 종속 항목을 주입받는다`() {
        val sheathComponent1 = ClassSheathComponent(Test13::class)
        val sheathComponent2 = ClassSheathComponent(Test14::class)
        sheathComponent2.initialize(emptyList())
        sheathComponent1.initialize(listOf(sheathComponent2))

        val actual = (sheathComponent1.getNewInstance() as Test13).test14

        assertThat(actual).isEqualTo(sheathComponent2.instance)
    }

    @Component
    class Test13 {
        lateinit var test14: Test14

        @Inject
        private fun test(test14: Test14) {
            this.test14 = test14
        }
    }

    @Component
    class Test14

    @Test
    fun `새 인스턴스 반환 시 메서드의 종속 항목이 싱글톤이 아니면 새 종속 항목을 주입받는다`() {
        val sheathComponent1 = ClassSheathComponent(Test15::class)
        val sheathComponent2 = ClassSheathComponent(Test16::class)
        sheathComponent2.initialize(emptyList())
        sheathComponent1.initialize(listOf(sheathComponent2))

        val actual = (sheathComponent1.getNewInstance() as Test15).test16

        assertThat(actual).isNotEqualTo(sheathComponent2.instance)
    }

    @Component
    class Test15 {
        lateinit var test16: Test16

        @Inject
        private fun test(test16: Test16) {
            this.test16 = test16
        }
    }

    @Prototype
    @Component
    class Test16

    @Test
    fun `새 인스턴스 반환 시 생성자의 매개 변수에 @NewInstance가 붙어있다면 새 종속 항목을 주입받는다`() {
        val sheathComponent1 = ClassSheathComponent(Test17::class)
        val sheathComponent2 = ClassSheathComponent(Test18::class)
        sheathComponent2.initialize(emptyList())
        sheathComponent1.initialize(listOf(sheathComponent2))

        val actual = (sheathComponent1.getNewInstance() as Test17).test18

        assertThat(actual).isNotEqualTo(sheathComponent2.instance)
    }

    @Component
    class Test17(@NewInstance val test18: Test18)

    @Component
    class Test18

    @Test
    fun `새 인스턴스 반환 시 프로퍼티에 @NewInstance가 붙어있다면 새 종속 항목을 주입받는다`() {
        val sheathComponent1 = ClassSheathComponent(Test19::class)
        val sheathComponent2 = ClassSheathComponent(Test20::class)
        sheathComponent2.initialize(emptyList())
        sheathComponent1.initialize(listOf(sheathComponent2))

        val actual = (sheathComponent1.getNewInstance() as Test19).test20

        assertThat(actual).isNotEqualTo(sheathComponent2.instance)
    }

    @Component
    class Test19 {
        @NewInstance
        @Inject
        lateinit var test20: Test20
    }

    @Component
    class Test20

    @Test
    fun `새 인스턴스 반환 시 메서드의 매개 변수에 @NewInstance가 붙어있다면 새 종속 항목을 주입받는다`() {
        val sheathComponent1 = ClassSheathComponent(Test21::class)
        val sheathComponent2 = ClassSheathComponent(Test22::class)
        sheathComponent2.initialize(emptyList())
        sheathComponent1.initialize(listOf(sheathComponent2))

        val actual = (sheathComponent1.getNewInstance() as Test21).test22

        assertThat(actual).isNotEqualTo(sheathComponent2.instance)
    }

    @Component
    class Test21 {
        lateinit var test22: Test22

        @Inject
        private fun test(@NewInstance test22: Test22) {
            this.test22 = test22
        }
    }

    @Component
    class Test22
}
