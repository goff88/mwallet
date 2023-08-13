package pro.devapp.mwallet.feature.account

internal data class CmcResponse(
    val data: CmcData
)

internal data class CmcData(
    val PZM: List<CmcDataPZM>
)

internal data class CmcDataPZM(
    val quote: CmcDataPZMQuote
)

internal data class CmcDataPZMQuote(
    val RUB: CmcDataPZMQuoteRUB
)

internal data class CmcDataPZMQuoteRUB(
    val price: Double
)