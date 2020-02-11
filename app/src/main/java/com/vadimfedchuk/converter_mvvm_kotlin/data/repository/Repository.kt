package com.vadimfedchuk.converter_mvvm_kotlin.data.repository

import androidx.lifecycle.LiveData
import com.vadimfedchuk.converter_mvvm_kotlin.pojo.AvailableExchange
import com.vadimfedchuk.converter_mvvm_kotlin.pojo.Currency
import io.reactivex.Flowable

interface Repository {

  fun getTotalCurrencies(): Flowable<Int>

  fun addCurrencies()

  fun getCurrencyList(): LiveData<List<Currency>>

  fun getAvailableExchange(currencies: String): LiveData<AvailableExchange>

}
