@file:OptIn(FlowPreview::class)

package com.andrej.cryptotracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrej.cryptotracker.R
import com.andrej.cryptotracker.data.model.CryptoTicker
import com.andrej.cryptotracker.domain.intent.HomeIntent
import com.andrej.cryptotracker.domain.model.HomeState
import com.andrej.cryptotracker.domain.usecase.FetchTickersUseCase
import com.andrej.cryptotracker.utils.NetworkObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val fetchTickersUseCase: FetchTickersUseCase,
    private val networkObserver: NetworkObserver,
    private val refreshInterval: Long
) : ViewModel() {

    private val _state = MutableStateFlow<HomeState>(HomeState.Loading)
    val state: StateFlow<HomeState> = _state

    private val _intent = MutableSharedFlow<HomeIntent>()
    val intent: SharedFlow<HomeIntent> = _intent

    private var fetchedTickers = listOf<CryptoTicker>()
    private var isInitialLoad = true
    private var currentQuery = ""

    init {
        processIntents()
        observeNetwork()
        refreshTickers()
    }

    private fun observeNetwork() {
        networkObserver.isConnected
            .distinctUntilChanged()
            .onEach { connected ->
                if (!connected) {
                    _state.value = HomeState.NetworkError(R.string.you_are_offline, fetchedTickers)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun processIntents() {
        intent
            .debounce(DEBOUNCE_DURATION)
            .onEach { intent ->
                when (intent) {
                    is HomeIntent.LoadTickers -> fetchCryptoTickers()
                    is HomeIntent.SearchTicker -> searchTicker(intent.query)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun fetchCryptoTickers() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = if (isInitialLoad) HomeState.Loading else _state.value
            try {
                val tickers = fetchTickersUseCase.execute()

                if (tickers.isNotEmpty()) {
                    fetchedTickers = tickers
                    filterTickers(currentQuery)
                }
                isInitialLoad = false
            } catch (e: Exception) {
                handleFetchError()
            }
        }
    }

    private fun filterTickers(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val filteredTickers = fetchedTickers.filter {
                it.formattedSymbol.contains(query, ignoreCase = true)
            }
            _state.value = HomeState.Success(filteredTickers)
        }
    }

    private fun refreshTickers() {
        viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                sendIntent(HomeIntent.LoadTickers)
                delay(refreshInterval)
            }
        }
    }

    private fun searchTicker(query: String) {
        currentQuery = query

        filterTickers(query)
    }

    fun sendIntent(intent: HomeIntent) {
        viewModelScope.launch {
            _intent.emit(intent)
        }
    }

    private fun handleFetchError() {
        if (fetchedTickers.isEmpty()) {
            _state.value = HomeState.Error(R.string.failed_to_load_tickers)
        } else {
            _state.value = HomeState.NetworkError(R.string.you_are_offline, fetchedTickers)
        }
    }

    companion object {
        private const val DEBOUNCE_DURATION = 300L
    }
}
