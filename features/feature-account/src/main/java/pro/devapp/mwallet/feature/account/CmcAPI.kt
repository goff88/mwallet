package pro.devapp.mwallet.feature.account

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Request
import okhttp3.Response
import pro.devapp.mwallet.core.http.HttpClientFactory

internal class CmcAPI {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    suspend fun calculateBalance(amount: Double): Double {
        return (amount * getCoinPrice() * 100).toInt().toDouble() / 100
    }

    @OptIn(ExperimentalStdlibApi::class)
    private suspend fun getCoinPrice(): Double {
        return withContext(scope.coroutineContext) {
            val clientFactory = HttpClientFactory()

            val request: Request = Request.Builder()
                .url("https://pro-api.coinmarketcap.com/v2/cryptocurrency/quotes/latest?symbol=PZM&convert=RUB")
                .addHeader("X-CMC_PRO_API_KEY", "8dfb0a18-f1f7-407a-be14-484f06e17d3c")
                .addHeader("Accept", "application/json")
                .get()
                .build()

            val call: Call = clientFactory.createClient().newCall(request)
            val response: Response = call.execute()

            val responseString = response.body!!.string()
            print(responseString)

            val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
            val jsonAdapter: JsonAdapter<CmcResponse> = moshi.adapter()

            val data = jsonAdapter.fromJson(responseString)
            data?.data?.PZM?.get(0)?.quote?.RUB?.price ?: 0.0
        }
    }
}