package com.poc.currencyexchange.model

import com.google.gson.annotations.SerializedName

data class ExchangeRatesResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("quotes") val quotes: Map<String, Double>
)