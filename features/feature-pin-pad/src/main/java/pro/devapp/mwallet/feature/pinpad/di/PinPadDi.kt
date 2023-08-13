package pro.devapp.mwallet.feature.pinpad.di

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import pro.devapp.mwallet.feature.pinpad.PinPadViewModel

fun registerPinPadDi(module: Module) {
    module.apply {
        viewModelOf(::PinPadViewModel)
    }
}