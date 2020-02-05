package com.vadimfedchuk.converter_mvvm_kotlin.di

import com.vadimfedchuk.converter_mvvm_kotlin.view.ConverterViewModel
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class, RoomModule::class, RemoteModule::class])
@Singleton interface AppComponent {

    fun inject(converterViewModel: ConverterViewModel)
}