package com.andrej.cryptotracker.domain.model

import com.andrej.cryptotracker.data.model.CryptoTicker

sealed class HomeState {
    object Loading : HomeState()
    data class Success(val tickers: List<CryptoTicker>) : HomeState()
    data class NetworkError(val messageResId: Int, val cachedTickers: List<CryptoTicker>) : HomeState()
    data class Error(val messageResId: Int) : HomeState()
}
