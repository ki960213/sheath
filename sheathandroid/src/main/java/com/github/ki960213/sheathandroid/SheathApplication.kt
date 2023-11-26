package com.github.ki960213.sheathandroid

import android.content.Context
import com.github.ki960213.sheathandroid.component.ContextSheathComponent
import com.github.ki960213.sheathcore.component.SheathComponent
import com.github.ki960213.sheathcore.scanner.ComponentScanner

object SheathApplication {

    lateinit var sheathContainer: SheathContainer

    fun run(context: Context) {
        val scanner = ComponentScanner()
        val components: List<SheathComponent> =
            scanner.findAll() + ContextSheathComponent(context)

        sheathContainer = SheathContainer.from(components)
    }
}
