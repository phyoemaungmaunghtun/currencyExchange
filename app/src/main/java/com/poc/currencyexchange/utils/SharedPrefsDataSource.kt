package com.poc.currencyexchange.utils

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPrefsDataSource(private val sharedPreferences: SharedPreferences) {
    companion object {
        const val CURRENCY_PREFS = "CURRENCY"
        const val CURRENCY_LIST = "currency_list"
        const val CURRENCY_RATE = "currency_rate"
        const val LAST_REFRESH_TIME = "last_refresh"
    }

    fun saveCurrencyList(currencyList: Map<String, String>) {
        val json = Gson().toJson(currencyList)
        sharedPreferences.edit().putString(CURRENCY_LIST, json).apply()
    }

    fun getCurrencyList(): Map<String, String> {
        val json = sharedPreferences.getString(CURRENCY_LIST, null)
        return if (json != null) {
            val type = object :
                TypeToken<Map<String, String>>() {}.type
            val stringPairs: Map<String, String> =
                Gson().fromJson(json, type)
            return stringPairs
        } else {
            mutableMapOf()
        }
    }

    fun saveCurrencyRate(currencyList: Map<String, Double>) {
        val json = Gson().toJson(currencyList)  // Serialize the map to JSON
        sharedPreferences.edit().putString(CURRENCY_RATE, json).apply()
        saveLastRefreshTime(System.currentTimeMillis())
    }

    fun getCurrencyRate(): Map<String, Double> {
        val json = sharedPreferences.getString(CURRENCY_RATE, null)
        return if (json != null) {
            val type = object :
                TypeToken<Map<String, Double>>() {}.type
            Gson().fromJson(json, type)
        } else {
            mutableMapOf()
        }
    }

    private fun saveLastRefreshTime(time: Long) {
        sharedPreferences.edit().putLong(LAST_REFRESH_TIME, time).apply()
    }

    fun getLastRefreshTime(): Long {
        return sharedPreferences.getLong(LAST_REFRESH_TIME, 0L)
    }
}