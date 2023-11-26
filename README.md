Sheath는 Android 전용 기능이 추가된 코틀린 전용 종속 항목 삽입 라이브러리입니다. Hilt와 Koin의 사용성을 보완하고자 만들어 졌습니다.

## Sheath 애플리케이션 클래스

Sheath를 사용하는 모든 앱은 `SheathApplication.run(applicationContext)` 명령어를 실행하여 Sheath 애플리케이션을 시작해야 합니다.

Sheath 애플리케이션을 시작하면 의존성 관리 대상인 Sheath 컴포넌트를 Sheath 컨테이너에
등록합니다. [Android 클래스](https://github.com/ki960213/sheath#android-%ED%81%B4%EB%9E%98%EC%8A%A4%EC%97%90-%EC%A2%85%EC%86%8D-%ED%95%AD%EB%AA%A9-%EC%82%BD%EC%9E%85)
와 Sheath 컴포넌트는 Sheath 컨테이너에 등록된 종속 항목을 제공 받을 수 있습니다.

## Sheath 컴포넌트 등록하기

Sheath 컴포넌트란 의존성 관리 대상을 의미합니다. Sheath 컴포넌트를 등록하는 방법은 다음과 같습니다.

* 클래스에 `@Component` 주석 혹은 `@Component` 주석이 붙은 애노테이션 붙이기
* Sheath 모듈에 종속 항목을 제공하는 함수 정의하고 그 함수에 `@Component` 주석 붙이기

### 클래스에 `@Component` 주석 혹은 `@Component` 주석이 붙은 애노테이션 붙이기

어떤 클래스를 Sheath 컴포넌트로 등록하고 싶다면 `@Component` 주석 혹은 `@Component` 주석이 붙은 애노테이션을 붙이면 됩니다.

```kotlin
@Component
class ExampleClass {
    ...
}
```

참고로 `@Component` 주석이 붙은 애노테이션이란 단지 가독성을 위해 존재하는 애노테이션입니다. `@Component` 주석을 붙인 것과 동일하게
취급됩니다. `@Component` 주석이 붙은 애노테이션 종류는 다음과 같습니다.

* `@UseCase`
* `@Repository`
* `@DataSource`

> **제약사항**
`@Component` 주석 혹은 `@Component` 주석이 붙은 애노테이션은 인스턴스를 생성할 수 없는 클래스에는 붙일 수 없습니다. 만약 abstract 클래스나
> interface에 붙인다면 Sheath 애플리케이션이 실행된 후 런타임 에러가 발생합니다.

### Sheath 모듈에 종속 항목을 제공하는 함수 정의하고 그 함수에 `@Component` 주석 붙이기

클래스가 외부 라이브러리에서 제공되므로 클래스를 소유하지 않은 경우(Retrofit, `OkHttpClient` 또는 Room 데이터베이스와 같은 클래스) 또는 빌더 패턴으로
인스턴스를 생성해야 하는 경우 클래스에 애노테이션을 붙여서 Sheath 컴포넌트로 등록할 수 없습니다.

이러한 경우 Sheath 모듈 내에 함수를 생성하고 이 함수에 `@Component` 주석을 붙여 특정 타입의 인스턴스를 제공하는 방법을 Sheath에 알릴 수 있습니다.

주석이 달린 함수는 `Sheath`에 다음 정보를 제공합니다.

* 함수 반환 타입은 함수가 어떤 타입의 인스턴스를 제공하는지 Sheath에 알려줍니다.
* 함수 매개변수는 해당 타입의 종속 항목을 Sheath에 알려줍니다.
* 함수 본문은 해당 타입의 인스턴스를 제공하는 방법을 Sheath에 알려줍니다. 인스턴스를 생성해야 할 때 함수 본문을 실행합니다.

```kotlin
@Module
object AnalyticsModule {

    @Component
    fun provideAnalyticsService(
        // 해당 타입의 종속 항목
    ): AnalyticsService = Retrofit.Builder()
        .baseUrl("https://example.com")
        .build()
        .create(AnalyticsService::class.java)
}
```

> **참고**
`@Component` 주석이 붙은 애노테이션을 함수에 붙여도 잘 동작합니다.

> **제약사항**
> Sheath 모듈은 object 클래스여야 합니다. 그렇지 않으면 Sheath 애플리케이션이 실행된 후 런타임 에러가 발생합니다. 만약 함수가 nullable 타입을 반환한다면
> Sheath 애플리케이션이 실행된 후 런타임 에러가 발생합니다.

## Android 클래스에 종속 항목 삽입

Sheath 애플리케이션을 실행하여 Sheath 컴포넌트를 등록하게 되면 다른 Android 클래스에 컨테이너에 등록된 종속 항목을 제공할 수 있습니다.

Sheath는 현재 다음 Android 클래스를 지원합니다.

* `Application`
* `ViewModel`(`@SheathViewModel`을 사용하여)
* `Activity`
* `Fragment`
* `Service`
* `BroadcaseReceiver`

Android 클래스에서 종속 항목을 가져오려면 다음과 같이 `inject()`를 사용하여 종속 항목 주입을 실행합니다.

```kotlin
class ExapleActivity : AppCompatActivity() {
    private analytics: AnalyticsAdapter by inject()
    ...
}
```

## 일반 클래스에 종속 항목 삽입

일반 클래스에 종속 항목 삽입 방법은 생성자 삽입, 프로퍼티 삽입, 메서드 삽입 세 가지가 있습니다. 참고로 Sheath 컴포넌트를 삽입 받는 클래스는 모든 삽입이 완료된 상태의
종속 항목을 주입 받습니다.

### 생성자 삽입

생성자에 `@Inject` 주석을 붙이거나 어떠한 생성자에 `@Inject` 주석이 붙어 있지 않다면 주 생성자로 종속 항목을 삽입받습니다. 만약 여러 생성자에 `@Inject`
주석이 붙어 있다면 Sheath 애플리케이션이 실행된 후 런타임 에러가 발생합니다.

```kotlin
@Component
class ExampleClass(exam: ExampleClass2) {
    ...
}
```

아래의 경우 `@Inject`가 붙은 생성자를 통해 종속 항목을 삽입받습니다.

```kotlin
@Component
class ExampleClass(str: String) {

    @Inject
    constructor(exam: ExampleClass2) : this(exam.toString())
    ...
}
```

### 프로퍼티 삽입

프로퍼티에 `@Inject` 주석을 붙여 종속 항목을 삽입받을 수 있습니다.

```kotlin
@Component
class ExampleClass {

    @Inject
    private lateinit var exam: ExampleClass2
    ...
}
```

프로퍼티를 통해 삽입받을 땐 `var`를 사용하여 프로퍼티를 선언해야 합니다. 그렇지 않으면 Sheath 애플리케이션이 실행된 후 런타임 에러가 발생합니다.

### 메서드 삽입

메서드에 `@Inject` 주석을 붙여 종속 항목을 삽입받을 수 있습니다.

```kotlin
@Component
class ExampleClass {

    @Inject
    fun exam(exam: ExampleClass2) {
        ...
    }
}
```

참고로 매개변수가 없더라도 종속 항목을 삽입할 때 수행될 수 있습니다.

## Sheath 컴포넌트 범위

Sheath 컴포넌트의 범위는 세 가지가 있습니다.

* 싱글톤
* 프로토타입
* 종속 항목을 제공 받는 인스턴스의 생명주기에 맞춰진 범위

### 싱글톤

Sheath 컴포넌트를 등록할 때 `@SheathViewModel` 주석을 붙인 경우를 제외하고 추가적인 설정을 하지 않으면 싱글톤입니다. 처음 컨테이너에 등록될 때 만든
인스턴스가 제공됩니다.

### 프로토타입

Sheath 컴포넌트로 등록할 때 `@Prototype` 주석을 붙이면 해당 컴포넌트는 어떤 클래스에 제공되든 항상 새로운 인스턴스가 제공됩니다.

```kotlin
@Prototype
@Component
class ExampleClass {
    ...
}

@Module
object ExampleModule {

    @Prototype
    @Component
    fun provideExampleClass2(): ExampleClass2 {
        ...
    }
}
```

### 종속 항목을 제공 받는 인스턴스의 생명주기에 맞춰진 범위

항상 새로운 인스턴스의 종속 항목을 주입 받고 싶다면 종속 항목 주입 방법 선언 시 `@NewInstance` 주석을 붙이면 됩니다.

만약 생성자 주입 시 새로운 인스턴스를 받고 싶다면 아래처럼 생성자의 매개변수에 `@NewInstance` 주석을 붙이면 됩니다.

```kotlin
@Component
class ExampleClass(@NewInstance private val exam: ExampleClass2) {
    ...
}
```

만약 프로퍼티 주입 시 새로운 인스턴스를 받고 싶다면 아래처럼 프로퍼티에 `@NewInstance` 주석을 붙이면 됩니다.

```kotlin
@Component
class ExampleClass {

    @NewInstance
    @Inject
    private lateinit var exam: ExampleClass2
}
```

만약 메서드 주입 시 새로운 인스턴스를 받고 싶다면 아래처럼 메서드의 매개변수에 `@NewInstance` 주석을 붙이면 됩니다.

```kotlin
@Component
class ExampleClass {

    @Inject
    fun examFun(@NewInstance exam: ExampleClass2) {
        ...
    }
}
```

만약 Sheath 모듈에 정의된 함수에서 새로운 인스턴스를 받고 싶다면 아래처럼 메서드의 매개변수에 `@NewInstance` 주석을 붙이면 됩니다.

```kotlin
@Module
object ExamModule {

    @Component
    fun provideExample(@NewInstance exam: ExampleClass2): ExampleClass {
        ...
    }
}
```

Android 클래스에서 새로운 인스턴스를 받고 싶다면 아래처럼 `inject()` 메서드에 `isNewInstance` 값을 `true`로 설정하면 됩니다.

```kotlin
class ExampleActivity : AppCompatActivity() {

    private val exampleComponent: ExampleComponent by inject(isNewInstance = true)
    ...
}
```

## Sheath로 ViewModel 객체 삽입

`@SheathViewModel`로 주석 처리하여 `ViewModel`을 Sheath에 제공합니다.

```kotlin
@SheathViewModel
class ExampleViewModel(
    private val repository: ExampleRepository,
) : ViewModel() {
    ...
}
```

그런 다음 액티비티 또는 프래그먼트는 `by viewModels()`나 `activityViewModels()` 메서드를 사용하여 평소와 같이 `ViewModel` 인스턴스를
가져올 수 있습니다.

```kotlin
class ExampleActivity : AppCompatActivity() {
    private val exampleViewModel: ExampleViewModel by viewModels()
    ...
}
```

> **참고**: `@SheathViewModel` 주석은 `@Prototype` 주석과 `@Component` 주석을 붙인 애노테이션입니다.

## 동일한 타입에 대해 여러 종속 항목 제공

종속 항목과 동일한 타입의 다양한 구현을 제공하는 Sheath가 필요한 경우에는 한정자를 사용하여 동일한 타입에 대해 종속 항목을 정의해야 합니다.

한정자는 특정 타입에 대해 여러 Sheath 컴포넌트가 등록되어 있을 때 그 타입의 특정 종속 항목을 식별하는 데 사용하는 주석입니다.

다음 예를 생각해 보세요. `AnalyticsService` 호출을 가로채야 한다면 인터셉터와 함께 `OkHttpClient` 객체를 사용할 수 있습니다. 다른 서비스에서는 호출을
다른 방식으로 가로채야 할 수도 있습니다. 이 경우에는 서로 다른 두 가지 `OkHttpClient` 구현을 제공하는 방법을 Sheath에 알려야 합니다.

먼저 다음과 같이 `@Qualifier` 주석을 사용하여 사용할 한정자를 정의합니다.

```kotlin
@Qualifier
annotation class AuthInterceptorOkHttpClient

@Qualifier
annotation class OtherInterceptorOkHttpClient
```

그런 다음, Sheath는 각 한정자와 일치하는 타입의 인스턴스를 제공하는 방법을 알아야 합니다. 이 경우 Sheath 모듈을 사용할 수 있습니다. 두 메서드 모두 동일한 반환
타입을 갖지만 한정자는 다음과 같이 두 가지의 서로 다른 Sheath 컴포넌트로 메서드에 라벨을 지정합니다.

```kotlin
@Module
object NetworkModule {

    @AuthInterceptorOkHttpClient
    @Component
    fun provideAuthInterceptorOkHttpClient(
        authInterceptor: AuthInterceptor
    ) = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    @OtherInterceptorOkHttpClient
    @Component
    fun provideOtherInterceptorOkHttpClient(
        otherInterceptor: OtherInterceptor
    ) = OkHttpClient.Builder()
        .addInterceptor(otherInterceptor)
        .build()
}
```

다음과 같이 프로퍼티 또는 매개변수에 해당 한정자로 주석을 지정햐여 필요한 특정 종속 항목을 삽입할 수 있습나다.

```kotlin
// Sheath 모듈의 함수에 삽입 시
@Module
object AnalyticsModule {

    @Component
    fun provideAnalyticsService(
        @AuthInterceptorOkHttpClient okHttpClient: OkHttpClient
    ): AnalyticsService = Retrofit.Builder()
        .baseUrl("https://example.com")
        .client(okHttpClient)
        .build()
        .create(AnalyticsService::class.java)
}

// 생성자 삽입 시
@Component
class ExampleService(
    @AuthInterceptorOkHttpClient private val okHttpClient: OkHttpClient
) : ...

// 프로퍼티 삽입 시
class ExampleClass {

    @AuthInterceptorOkHttpClient
    @Inject private lateinit var okHttpClient: OkHttpClient
    ...
}

// Android 클래스에 삽입 시
class ExampleActivity : AppCompatActivity() {

    private val okHttpClient: OkHttpClient by inject(qualifier = AuthInterceptorOkHttpClient::class)
    ...
}
```

종속 항목인지 판단하는 기준은 타입과 한정자입니다. 만약 매개변수 혹은 프로퍼티에 한정자가 설정되지 않았다면 타입만을 기준으로 종속 항목인지 판단합니다. 만약 종속 항목이
모호하다면 런타임 에러가 발생합니다. 따라서 한정자를 타입에 추가한다면 그 타입의 종속 항목을 제공하는 가능한 모든 방법에 한정자를 추가하는 것이 좋습니다.

> **참고**
> Sheath 컴포넌트는 타입과 한정자에 의해 구분됩니다. 만약 같은 타입의 컴포넌트를 한정자로 구분짓지 않는다면 Sheath 애플리케이션이 실행된 후 런타임 에러가 발생합니다

## Context 컴포넌트

Sheath는 애플리케이션 또는 액티비티의 `Context` 클래스가 필요할 수 있으므로 Context 컴포넌트를 기본적으로 컨테이너에 등록합니다. Context 컴포넌트의
인스턴스는 Sheath 애플리케이션을 실행할 때 인자로 넣은 인스턴스입니다.

## Hilt, Koin과 차이점

**1. Scope**

Koin은 범위를 커스텀으로 지정하고 범위가 닫힐 때까지 해당 범위 내에서 같은 인스턴스를 사용할 수 있습니다. Hilt는 Android 클래스와 관련된 범위를 지정할 수
있습니다. 또한 Hilt는 범위를 지정하지 않으면 기본적으로 항상 새 인스턴스를 생성합니다.

Sheath는 세 가지의 범위만 존재합니다. 그리고 범위를 지정하지 않으면 기본적으로 싱글톤 인스턴스를 사용합니다.

**2. 생성자 삽입**

Koin은 생성자 삽입을 할 때 모듈에서 `get()` 메서드를 사용해 종속 항목을 삽입합니다. Hilt는 생성자 삽입을 할 때 `@Inject` 주석을 붙이기
위해 `constructor` 예약어를 생략할 수 없습니다.

Sheath는 생성자에 `@Inject` 주석이 붙어 있지 않다면 주 생성자로 생성자 삽입을 하기 때문에 `constructor` 예약어를 생략해도 됩니다.

**3. 모듈**

Koin은 모든 의존성 관리 대상을 모듈에 등록해야 합니다. Hilt는 종속 항목의 타입과 의존성 관리 대상이 타입이 다르면 결합 방식을 알리기 위해 모듈에 등록해야 합니다.

Sheath는 종속 항목의 타입이 의존성 관리 대상의 부모 타입이라면 종속 항목을 제공받을 수 있습니다.

**4. 잘못된 의존 그래프(종속 항목을 제공할 수 없거나 모호하거나 의존 사이클이 존재하는 경우)일 때 에러 발생 시점**

Koin은 해당 종속 항목이 사용될 때 에러가 발생합니다. Hilt는 컴파일 타임에 에러가 발생합니다.

Sheath는 Android 클래스에서는 해당 종속 항목이 사용될 때 에러가 발생합니다. 일반 클래스에서 잘못되었다면 Sheath 애플리케이션이 실행된 후 에러가 발생합니다.
따라서 Android의 `Application` 클래스에서 `onCreate()` 일 때 Sheath 애플리케이션을 실행하여 에러를 빨리 마주하는 게 좋습니다.

## 주의할 점

난독화를 하면 작동하지 않습니다.
