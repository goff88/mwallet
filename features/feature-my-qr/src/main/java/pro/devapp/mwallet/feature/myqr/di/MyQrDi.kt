package pro.devapp.mwallet.feature.myqr.di

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import pro.devapp.mwallet.feature.myqr.MyQrViewModel

fun registerMyQrDi(module: Module) {
    module.apply {
        viewModelOf(::MyQrViewModel)
    }
}