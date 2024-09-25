package com.poc.currencyexchange.repository

import com.poc.currencyexchange.model.CurrencyListResponse
import com.poc.currencyexchange.model.DataOrException
import com.poc.currencyexchange.model.ExchangeRatesResponse

interface ExchangeRatesRepository {
    suspend fun getExchangeRates(
        source: String
    ): DataOrException<ExchangeRatesResponse?, Boolean, Exception>

    suspend fun getCurrencyList(): DataOrException<CurrencyListResponse?, Boolean, Exception>
}