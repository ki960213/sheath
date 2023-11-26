package com.ki960213.sheathandroid.extention

import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ki960213.sheathandroid.SheathApplication
import kotlin.reflect.typeOf

@MainThread
inline fun <reified VM : ViewModel> ComponentActivity.viewModels(
    noinline extrasProducer: (() -> CreationExtras)? = null,
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null,
): Lazy<VM> = ViewModelLazy(
    VM::class,
    { viewModelStore },
    factoryProducer ?: { sheathViewModelFactory<VM>() },
    { extrasProducer?.invoke() ?: this.defaultViewModelCreationExtras },
)

inline fun <reified VM : ViewModel> sheathViewModelFactory(): ViewModelProvider.Factory =
    viewModelFactory {
        initializer {
            val viewModelComponent = SheathApplication.sheathContainer[typeOf<VM>()]
            viewModelComponent.getNewInstance() as VM
        }
    }
