package pro.devapp.mwallet.di

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import pro.devapp.mwallet.components.CmcAPI
import pro.devapp.mwallet.core.CoreAPI
import pro.devapp.mwallet.data.AccountInMemoryRepository
import pro.devapp.mwallet.data.EncryptedStorage
import pro.devapp.mwallet.data.PassPhraseManager
import pro.devapp.mwallet.data.PinManager
import pro.devapp.mwallet.feature.createaccount.ClipBoard
import pro.devapp.mwallet.feature.createaccount.CreateAccountViewModel
import pro.devapp.mwallet.feature.pinpad.PinPadViewModel
import pro.devapp.mwallet.screen.myqr.MyQrViewModel
import pro.devapp.mwallet.screen.sendmoney.SendMoneyViewModel
import pro.devapp.mwallet.screen.signin.SignInViewModel
import pro.devapp.mwallet.screen.wallet.WalletViewModel
import pro.devapp.mwallet.screen.welcome.WelcomeViewModel

val mainModule = module {
    viewModelOf(::SignInViewModel)
    viewModelOf(::WalletViewModel)
    viewModelOf(::CreateAccountViewModel)
    viewModelOf(::SendMoneyViewModel)
    viewModelOf(::PinPadViewModel)
    viewModelOf(::WelcomeViewModel)
    viewModelOf(::MyQrViewModel)

    singleOf(::CoreAPI)
    singleOf(::CmcAPI)
    singleOf(::AccountInMemoryRepository)
    singleOf(::ClipBoard)
    singleOf(::EncryptedStorage)

    factoryOf(::PinManager)
    factoryOf(::PassPhraseManager)
}