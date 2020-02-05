package com.vadimfedchuk.converter_mvvm_kotlin.di

import android.content.Context
import com.vadimfedchuk.converter_mvvm_kotlin.data.room.RoomCurrencyDataSource
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule {

    @Provides @Singleton fun provideRoomCurencyDataSource(context: Context) =
        RoomCurrencyDataSource.buildPersistentCurrency(context)
}