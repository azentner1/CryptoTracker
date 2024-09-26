package com.andrej.cryptotracker.di

import com.andrej.cryptotracker.data.api.BitfinexApiService
import com.andrej.cryptotracker.data.repository.CryptoRepository
import com.andrej.cryptotracker.domain.usecase.FetchTickersUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCryptoRepository(apiService: BitfinexApiService): CryptoRepository {
        return CryptoRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideFetchTickersUseCase(repository: CryptoRepository): FetchTickersUseCase {
        return FetchTickersUseCase(repository)
    }
}
