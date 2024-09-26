package com.andrej.cryptotracker.domain.intent

sealed class HomeIntent {
    object LoadTickers : HomeIntent()
    data class SearchTicker(val query: String) : HomeIntent()
}
