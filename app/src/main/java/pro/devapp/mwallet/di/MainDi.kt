package pro.devapp.mwallet.di

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import pro.devapp.mwallet.api.qrscanner.QrScannerIntentFactory
import pro.devapp.mwallet.core.CoreAPI
import pro.devapp.mwallet.data.AccountInMemoryRepository
import pro.devapp.mwallet.data.EncryptedStorage
import pro.devapp.mwallet.data.PassPhraseManager
import pro.devapp.mwallet.data.PinManager
import pro.devapp.mwallet.feature.account.CmcAPI
import pro.devapp.mwallet.feature.account.WalletViewModel
import pro.devapp.mwallet.feature.createaccount.ClipBoard
import pro.devapp.mwallet.feature.createaccount.CreateAccountViewModel
import pro.devapp.mwallet.feature.myqr.MyQrViewModel
import pro.devapp.mwallet.feature.pinpad.PinPadViewModel
import pro.devapp.mwallet.feature.scanqr.QrScannerIntentFactoryImpl
import pro.devapp.mwallet.feature.sendmoney.SendMoneyViewModel
import pro.devapp.mwallet.feature.signin.SignInViewModel
import pro.devapp.mwallet.feature.signin.WelcomeViewModel

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

    factoryOf(::QrScannerIntentFactoryImpl).bind(QrScannerIntentFactory::class)
}