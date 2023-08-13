package pro.devapp.mwallet.feature.scanqr

import android.content.Context
import android.content.Intent
import pro.devapp.mwallet.api.qrscanner.QrScannerIntentFactory

class QrScannerIntentFactoryImpl : QrScannerIntentFactory {
    override fun createIntent(context: Context): Intent {
        return Intent(context, QrScannerActivity::class.java)
    }
}