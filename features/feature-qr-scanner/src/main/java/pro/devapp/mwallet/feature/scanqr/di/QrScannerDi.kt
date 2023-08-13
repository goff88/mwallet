package pro.devapp.mwallet.feature.scanqr.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import pro.devapp.mwallet.api.qrscanner.QrScannerIntentFactory
import pro.devapp.mwallet.feature.scanqr.QrScannerIntentFactoryImpl

fun registerQrScannerDi(module: Module) {
    module.apply {
        factoryOf(::QrScannerIntentFactoryImpl).bind(QrScannerIntentFactory::class)
    }
}