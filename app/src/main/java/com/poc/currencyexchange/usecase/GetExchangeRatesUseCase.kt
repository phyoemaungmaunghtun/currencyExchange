package com.poc.currencyexchange.usecase

import com.poc.currencyexchange.model.CurrencyListResponse
import com.poc.currencyexchange.model.DataOrException
import com.poc.currencyexchange.model.ExchangeRatesResponse
import com.poc.currencyexchange.repository.ExchangeRatesRepository
import javax.inject.Inject

class GetExchangeRatesUseCase @Inject constructor(
    private val repository: ExchangeRatesRepository
) {
    suspend operator fun invoke(
        source: String,
    ): DataOrException<ExchangeRatesResponse?, Boolean, Exception> {
        return repository.getExchangeRates(source)
    }

    suspend operator fun invoke(
    ): DataOrException<CurrencyListResponse?, Boolean, Exception> {
        return repository.getCurrencyList()
    }
}