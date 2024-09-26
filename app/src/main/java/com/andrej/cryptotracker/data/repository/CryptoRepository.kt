package com.andrej.cryptotracker.data.repository

import com.andrej.cryptotracker.data.api.BitfinexApiService
import com.andrej.cryptotracker.data.model.CryptoTicker
import javax.inject.Inject

class CryptoRepository @Inject constructor(
    private val apiService: BitfinexApiService
) {

    suspend fun getTickers(): List<CryptoTicker> {
        try {
            val symbols = tickerSymbols.joinToString(",")
            val response = apiService.getTickers(symbols)
            if (response.isSuccessful) {
                val tickers = response.body()
                return tickers?.map {
                    CryptoTicker(
                        symbol = it[0] as String,
                        dailyChange = it[5] as Double,
                        dailyRelativeChange = it[6] as Double,
                        lastPrice = it[7] as Double,
                        volume = it[8] as Double,
                    )
                } ?: emptyList()
            } else {
                throw Exception("Failed to fetch tickers.")
            }
        } catch (e: Exception) {
            throw e
        }
    }

    private val tickerSymbols = listOf(
        "tBTCUSD",
        "tETHUSD",
        "tCHSB:USD",
        "tLTCUSD",
        "tXRPUSD",
        "tDSHUSD",
        "tRRTUSD",
        "tEOSUSD",
        "tSANUSD",
        "tDATUSD",
        "tSNTUSD",
        "tDOGE:USD",
        "tLUNA:USD",
        "tMATIC:USD",
        "tNEXO:USD",
        "tOCEAN:USD",
        "tBEST:USD",
        "tAAVE:USD",
        "tPLUUSD",
        "tFILUSD"
    )
}
