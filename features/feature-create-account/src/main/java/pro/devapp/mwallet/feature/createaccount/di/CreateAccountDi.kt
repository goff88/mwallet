package pro.devapp.mwallet.feature.createaccount.di

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import pro.devapp.mwallet.feature.createaccount.ClipBoard
import pro.devapp.mwallet.feature.createaccount.CreateAccountViewModel

fun registerCreateAccountDi(module: Module) {
    module.apply {
        singleOf(::ClipBoard)
        viewModelOf(::CreateAccountViewModel)
    }
}