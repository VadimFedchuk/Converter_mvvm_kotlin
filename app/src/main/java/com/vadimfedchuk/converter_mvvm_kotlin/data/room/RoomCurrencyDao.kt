package com.vadimfedchuk.converter_mvvm_kotlin.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Flowable

@Dao
interface RoomCurrencyDao {

  @Query(RoomContract.SELECT_CURRENCIES_COUNT)
  fun getCurrenciesTotal(): Flowable<Int>

  @Insert
  fun insertAll(currencies: List<CurrencyEntity>)

  @Query(RoomContract.SELECT_CURRENCIES)
  fun getAllCurrencies(): Flowable<List<CurrencyEntity>>

}

