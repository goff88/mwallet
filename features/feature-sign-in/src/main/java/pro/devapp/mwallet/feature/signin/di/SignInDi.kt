package pro.devapp.mwallet.feature.signin.di

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import pro.devapp.mwallet.feature.signin.SignInViewModel
import pro.devapp.mwallet.feature.signin.WelcomeViewModel

fun registerSignInDi(module: Module) {
    module.apply {
        viewModelOf(::WelcomeViewModel)
        viewModelOf(::SignInViewModel)
    }
}