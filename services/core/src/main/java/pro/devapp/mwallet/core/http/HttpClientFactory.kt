package pro.devapp.mwallet.core.http

import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class HttpClientFactory {

    fun createClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.hostnameVerifier { hostname, session -> true }

        val sslContext = SSLContext.getInstance("SSL")
        val trustManager = t()
        sslContext.init(null, arrayOf<TrustManager>(trustManager), SecureRandom())
        builder.sslSocketFactory(sslContext.socketFactory, trustManager as X509TrustManager)
        return builder.build()
    }

    fun t(): TrustManager {
        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }
    }
}