package pro.devapp.mwallet.data.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import pro.devapp.mwallet.data.AccountInMemoryRepository
import pro.devapp.mwallet.data.EncryptedStorage
import pro.devapp.mwallet.data.PassPhraseManager
import pro.devapp.mwallet.data.PinManager

fun registerDataDi(module: Module) {
    module.apply {
        singleOf(::AccountInMemoryRepository)
        singleOf(::EncryptedStorage)
        factoryOf(::PinManager)
        factoryOf(::PassPhraseManager)
    }
}