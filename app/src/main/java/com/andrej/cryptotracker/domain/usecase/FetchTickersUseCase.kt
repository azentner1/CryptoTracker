package com.andrej.cryptotracker.domain.usecase

import com.andrej.cryptotracker.data.model.CryptoTicker
import com.andrej.cryptotracker.data.repository.CryptoRepository

class FetchTickersUseCase(private val repository: CryptoRepository) {

    suspend fun execute(): List<CryptoTicker> {
        return repository.getTickers()
    }
}
