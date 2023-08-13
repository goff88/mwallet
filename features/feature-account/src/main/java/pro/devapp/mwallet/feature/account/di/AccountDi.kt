package pro.devapp.mwallet.feature.account.di

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import pro.devapp.mwallet.feature.account.CmcAPI
import pro.devapp.mwallet.feature.account.WalletViewModel

fun registerAccountDi(module: Module) {
    module.apply {
        viewModelOf(::WalletViewModel)
        singleOf(::CmcAPI)
    }
}