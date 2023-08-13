package pro.devapp.mwallet.feature.account

internal data class CmcResponse(
    val data: CmcData
)

data class CmcData(
    val PZM: List<CmcDataPZM>
)

data class CmcDataPZM(
    val quote: CmcDataPZMQuote
)

data class CmcDataPZMQuote(
    val RUB: CmcDataPZMQuoteRUB
)

data class CmcDataPZMQuoteRUB(
    val price: Double
)