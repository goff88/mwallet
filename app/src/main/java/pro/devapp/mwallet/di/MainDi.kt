package pro.devapp.mwallet.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import pro.devapp.mwallet.core.CoreAPI
import pro.devapp.mwallet.data.di.registerDataDi
import pro.devapp.mwallet.feature.account.di.registerAccountDi
import pro.devapp.mwallet.feature.createaccount.di.registerCreateAccountDi
import pro.devapp.mwallet.feature.myqr.di.registerMyQrDi
import pro.devapp.mwallet.feature.pinpad.di.registerPinPadDi
import pro.devapp.mwallet.feature.scanqr.di.registerQrScannerDi
import pro.devapp.mwallet.feature.sendmoney.di.registerSendMoneyDi
import pro.devapp.mwallet.feature.signin.di.registerSignInDi

val mainModule = module {

    registerAccountDi(this)
    registerCreateAccountDi(this)
    registerMyQrDi(this)
    registerQrScannerDi(this)
    registerDataDi(this)
    registerSignInDi(this)
    registerSendMoneyDi(this)
    registerPinPadDi(this)

    singleOf(::CoreAPI)
}