package pro.devapp.mwallet

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import pro.devapp.mwallet.di.mainModule

class MWalletApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin{
            androidLogger()
            androidContext(this@MWalletApplication)
            modules(mainModule)
        }
    }
}