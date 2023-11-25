package com.ki960213.sheathAndroid.extention

import android.content.ComponentCallbacks
import com.ki960213.sheathAndroid.SheathApplication
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

inline fun <reified T : Any> ComponentCallbacks.inject(
    mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
    qualifier: KClass<*>? = null,
    isNewInstance: Boolean = false,
) = lazy(mode) { get<T>(qualifier, isNewInstance) }

inline fun <reified T : Any> ComponentCallbacks.get(
    qualifier: KClass<*>?,
    isNewInstance: Boolean,
): T {
    val container = SheathApplication.sheathContainer
    val sheathComponent = container[typeOf<T>(), qualifier]

    val instance = if (!sheathComponent.isSingleton || isNewInstance) {
        sheathComponent.getNewInstance()
    } else {
        sheathComponent.instance
    }

    return instance as T
}
