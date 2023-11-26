package com.github.ki960213.sheathandroid.component

import android.content.Context
import com.github.ki960213.sheathcore.component.Dependency
import com.github.ki960213.sheathcore.component.SheathComponent
import kotlin.reflect.KType
import kotlin.reflect.full.createType

internal class ContextSheathComponent(
    private val context: Context,
) : SheathComponent() {
    override val type: KType = Context::class.createType()

    override val qualifier: Annotation? = null

    override val isSingleton: Boolean = true

    override val dependencies: Set<Dependency> = emptySet()

    override fun getNewInstance(): Any = context
}
