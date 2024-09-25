package com.poc.currencyexchange.network

import com.poc.currencyexchange.model.CurrencyListResponse
import com.poc.currencyexchange.model.ExchangeRatesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Singleton

@Singleton
interface APIService {
    @GET("api/live")
    suspend fun getExchangeRates(
        @Query("source") source: String,
    ): Response<ExchangeRatesResponse>

    @GET("list")
    suspend fun getCurrencyList(
    ): Response<CurrencyListResponse>
}
