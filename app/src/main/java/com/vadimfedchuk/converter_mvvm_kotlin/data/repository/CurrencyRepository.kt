package com.vadimfedchuk.converter_mvvm_kotlin.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vadimfedchuk.converter_mvvm_kotlin.data.remote.CurrencyResponse
import com.vadimfedchuk.converter_mvvm_kotlin.data.remote.RemoteCurrencyDataSource
import com.vadimfedchuk.converter_mvvm_kotlin.data.room.CurrencyEntity
import com.vadimfedchuk.converter_mvvm_kotlin.data.room.RoomCurrencyDataSource
import com.vadimfedchuk.converter_mvvm_kotlin.pojo.AvailableExchange
import com.vadimfedchuk.converter_mvvm_kotlin.pojo.Currency
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepository @Inject constructor(
    private val roomCurrencyDataSource: RoomCurrencyDataSource,
    private val remoteCurrencyDataSource: RemoteCurrencyDataSource
) : Repository {

    val allCompositeDisposable: MutableList<Disposable> = arrayListOf()

    override fun getTotalCurrencies() = roomCurrencyDataSource.currencyDao().getCurrenciesTotal()

    override fun addCurrencies() {
       val currencyEntityList = RoomCurrencyDataSource.getAllCurrencies()
        roomCurrencyDataSource.currencyDao().insertAll(currencyEntityList)
    }

    override fun getCurrencyList(): LiveData<List<Currency>> {
        val roomCurrencyDao = roomCurrencyDataSource.currencyDao()
        val mutableLiveData = MutableLiveData<List<Currency>>()
        val disposable = roomCurrencyDao.getAllCurrencies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ currencyList ->
                mutableLiveData.value = transform(currencyList)
            }, {t: Throwable? ->
                t?.printStackTrace()
            })
        allCompositeDisposable.add(disposable)
        return mutableLiveData
    }

    private fun transform(currencies: List<CurrencyEntity>):MutableList<Currency> {
        return mutableListOf<Currency>().apply {
            currencies.forEach {
                add(Currency(it.countryCode, it.countryName))
            }
        }
    }

    override fun getAvailableExchange(currencies: String): LiveData<AvailableExchange> {
        val mutableLiveData = MutableLiveData<AvailableExchange>()
        val disposable = remoteCurrencyDataSource.requestAvailableExchange(currencies)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if(it.isSuccess) mutableLiveData.value = transform(it)
                else throw Throwable("CurrencyRepository -> on Error occurred")
            }, { throwable: Throwable? -> throwable?.printStackTrace()
            })
        allCompositeDisposable.add(disposable)
        return mutableLiveData
    }

    private fun transform(exchangeMap: CurrencyResponse): AvailableExchange {
        return AvailableExchange(exchangeMap.currencyQuotes)
    }
}