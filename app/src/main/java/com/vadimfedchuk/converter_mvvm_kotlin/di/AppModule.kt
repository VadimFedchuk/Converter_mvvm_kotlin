package com.vadimfedchuk.converter_mvvm_kotlin.di

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val converterApplication: ConverterApplication) {

    @Provides @Singleton fun provideContext(): Context = converterApplication
}