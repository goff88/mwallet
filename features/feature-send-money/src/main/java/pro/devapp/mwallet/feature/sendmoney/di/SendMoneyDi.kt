package pro.devapp.mwallet.feature.sendmoney.di

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import pro.devapp.mwallet.feature.sendmoney.SendMoneyViewModel

fun registerSendMoneyDi(module: Module) {
    module.apply {
        viewModelOf(::SendMoneyViewModel)
    }
}