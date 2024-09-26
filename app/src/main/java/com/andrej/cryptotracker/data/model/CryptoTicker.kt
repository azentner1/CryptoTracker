package com.andrej.cryptotracker.data.model

import java.text.DecimalFormat

data class CryptoTicker(
    val symbol: String,
    val lastPrice: Double,
    val dailyChange: Double,
    val dailyRelativeChange: Double,
    val volume: Double
) {
    val formattedSymbol: String
        get() = formatSymbol(symbol)

    val formattedPrice: String
        get() {
            val decimalFormat = DecimalFormat("#,##0.00")
            return "$${decimalFormat.format(lastPrice)}"
        }

    val formattedDailyChange: String
        get() {
            val decimalFormat = DecimalFormat("#,##0.00")
            return "${decimalFormat.format(dailyRelativeChange * 100)}%"
        }

    fun formatSymbol(symbol: String): String {
        var formatedSymbol =  if(symbol.first() == 't') {
            symbol.replaceFirst("t", "", ignoreCase = true)
        } else {
            symbol
        }

        formatedSymbol = formatedSymbol.replace("USD", "").replace(":", "")
        return formatedSymbol
    }
}
