package com.ki960213.sheathAndroid

import android.content.Context
import com.ki960213.sheathAndroid.component.ContextSheathComponent
import com.ki960213.sheathCore.component.SheathComponent
import com.ki960213.sheathCore.scanner.ComponentScanner

object SheathApplication {

    lateinit var sheathContainer: SheathContainer

    fun run(context: Context) {
        val scanner = ComponentScanner()
        val components: List<SheathComponent> =
            scanner.findAll() + ContextSheathComponent(context)

        sheathContainer = SheathContainer.from(components)
    }
}
