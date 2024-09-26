package com.andrej.cryptotracker.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BitfinexApiService {

    @GET("tickers")
    suspend fun getTickers(@Query("symbols") symbols: String): Response<List<List<Any>>>

}
