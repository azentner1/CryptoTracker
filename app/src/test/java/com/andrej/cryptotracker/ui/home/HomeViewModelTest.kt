package com.andrej.cryptotracker.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.andrej.cryptotracker.data.model.CryptoTicker
import com.andrej.cryptotracker.data.repository.CryptoRepository
import com.andrej.cryptotracker.domain.intent.HomeIntent
import com.andrej.cryptotracker.domain.model.HomeState
import com.andrej.cryptotracker.domain.usecase.FetchTickersUseCase
import com.andrej.cryptotracker.utils.NetworkObserver
import dagger.hilt.android.testing.BindValue
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: CryptoRepository
    private lateinit var fetchTickersUseCase: FetchTickersUseCase
    private lateinit var networkObserver: NetworkObserver
    private lateinit var viewModel: HomeViewModel
    private lateinit var fakeNetworkObserver: FakeNetworkObserver

    private val testDispatcher = StandardTestDispatcher()

    @BindValue
    var refreshInterval: Long = Long.MAX_VALUE

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        fetchTickersUseCase = FetchTickersUseCase(repository)

        fakeNetworkObserver = FakeNetworkObserver()
        networkObserver = fakeNetworkObserver

        viewModel = HomeViewModel(fetchTickersUseCase, fakeNetworkObserver, refreshInterval)
    }

    @Test
    fun `when LoadTickers intent is sent, state is Loading`() = runTest {
        val tickers = listOf(CryptoTicker("BTC",  45000.0, dailyChange = 0.5, dailyRelativeChange = 0.6, volume = 20000.0))
        coEvery { repository.getTickers() } returns tickers

        viewModel.sendIntent(HomeIntent.LoadTickers)
        advanceUntilIdle()

        assertEquals(HomeState.Success(tickers), viewModel.state.value)
        coVerify { repository.getTickers() }
    }

    @Test
    fun `when fetchCryptoTickers fails, state is Error`() = runTest {
        coEvery { repository.getTickers() } throws Exception("Network error")

        viewModel.sendIntent(HomeIntent.LoadTickers)
        advanceUntilIdle()

        assert(viewModel.state.value is HomeState.Error)
    }

    @Test
    fun `when searchTicker is called, state shows filtered tickers`() = runTest {
        val tickers = listOf(
            CryptoTicker("BTC", 45000.0, 100.0, 0.05, 50000.0),
            CryptoTicker("ETH", 3200.0, 50.0, 0.03, 30000.0)
        )
        coEvery { repository.getTickers() } returns tickers

        viewModel.sendIntent(HomeIntent.LoadTickers)
        advanceUntilIdle()

        viewModel.sendIntent(HomeIntent.SearchTicker("BTC"))
        advanceUntilIdle()

        val expectedFilteredTickers = listOf(tickers[0])
        assertEquals(HomeState.Success(expectedFilteredTickers), viewModel.state.value)
    }

    @Test
    fun `when network is disconnected, state is Error`() = runTest {
        fakeNetworkObserver.setNetworkState(false)

        viewModel.sendIntent(HomeIntent.LoadTickers)
        advanceUntilIdle()

        assert(viewModel.state.value is HomeState.Error)
    }

    @Test
    fun `given network disconnect after loading tickers, state shows cached tickers`() = runTest {
        val tickers = listOf(
            CryptoTicker("BTC", 45000.0, 100.0, 0.05, 50000.0),
            CryptoTicker("ETH", 3200.0, 50.0, 0.03, 30000.0)
        )

        coEvery { repository.getTickers() } returns tickers

        viewModel.sendIntent(HomeIntent.LoadTickers)
        advanceUntilIdle()

        assertEquals(HomeState.Success(tickers), viewModel.state.value)

        fakeNetworkObserver.setNetworkState(false)

        advanceUntilIdle()

        val currentState = viewModel.state.value
        assert(currentState is HomeState.NetworkError)
        if (currentState is HomeState.NetworkError) {
            assertEquals(tickers, currentState.cachedTickers)
        }
    }
}

class FakeNetworkObserver : NetworkObserver {
    private val _isConnected = MutableStateFlow(true) // Start with connected

    override val isConnected: Flow<Boolean> = _isConnected

    fun setNetworkState(connected: Boolean) {
        _isConnected.value = connected
    }
}
