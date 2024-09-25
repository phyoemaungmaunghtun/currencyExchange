package com.poc.currencyexchange.repository

import com.poc.currencyexchange.model.CurrencyListResponse
import com.poc.currencyexchange.model.DataOrException
import com.poc.currencyexchange.model.ExchangeRatesResponse
import com.poc.currencyexchange.network.APIService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExchangeRatesRepositoryImpl @Inject constructor(
    private val apiService: APIService
) : ExchangeRatesRepository {

    private val getExchangeRate = DataOrException<ExchangeRatesResponse?, Boolean, Exception>()
    override suspend fun getExchangeRates(
        source: String,
    ): DataOrException<ExchangeRatesResponse?, Boolean, Exception> {
        try {
            getExchangeRate.loading = true
            val resp = apiService.getExchangeRates(source)
            getExchangeRate.data = resp.body()
            if (resp.code() == 200 && getExchangeRate.data?.success == true) {
                getExchangeRate.loading = false
            } else {
                throw Exception(getServerErrorResponses(resp.code()))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            getExchangeRate.e = e
        }
        return getExchangeRate
    }

    private val currencyList = DataOrException<CurrencyListResponse?, Boolean, Exception>()
    override suspend fun getCurrencyList(): DataOrException<CurrencyListResponse?, Boolean, Exception> {
        try {
            currencyList.loading = true
            val resp = apiService.getCurrencyList()
            currencyList.data = resp.body()
            if (resp.code() == 200 && currencyList.data?.success == true) {
                currencyList.loading = false
            } else {
                throw Exception(getServerErrorResponses(resp.code()))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            currencyList.e = e
        }
        return currencyList
    }

    private fun getServerErrorResponses(responseCode: Int): String {
        return when (responseCode) {
            404 -> "Server Response: $responseCode  not found"
            403 -> "Oops! It seems you don't have the necessary permissions to access this feature. Please contact your system administrator for assistance."
            500 -> "Server Response: $responseCode  server broken"
            504 -> "Server Response: $responseCode  gateway time-out"
            else -> "It is server problem and try again!"
        }
    }
}