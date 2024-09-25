package com.poc.currencyexchange.di

import android.content.Context
import android.content.SharedPreferences
import com.poc.currencyexchange.utils.SharedPrefsDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(
            SharedPrefsDataSource.CURRENCY_PREFS,
            Context.MODE_PRIVATE
        )
    }

    @Provides
    fun provideSharedPrefsDataSource(
        sharedPreferences: SharedPreferences
    ): SharedPrefsDataSource {
        return SharedPrefsDataSource(sharedPreferences)
    }
}
