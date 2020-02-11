package com.vadimfedchuk.converter_mvvm_kotlin.view


import android.os.Build
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.vadimfedchuk.converter_mvvm_kotlin.R
import kotlinx.android.synthetic.main.fragment_converter.*


class ConverterFragment : Fragment() {

    companion object {
        fun newInstance() = ConverterFragment()
    }

    private val currencies = ArrayList<String>()
    private lateinit var currenciesAdapter: ArrayAdapter<String>
    private lateinit var currencyFrom: String
    private lateinit var currencyTo: String

    private lateinit var converterViewModel: ConverterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {
        converterViewModel = ViewModelProvider(this).get(ConverterViewModel::class.java)
        converterViewModel.let { lifecycle.addObserver(it) }
        converterViewModel.initLocalCurrencies()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_converter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        populateSpinnerAdapter()
    }

    private fun populateSpinnerAdapter() {
        converterViewModel.loadCurrencyList()?.observe(viewLifecycleOwner, Observer { currencyList ->
            currencyList?.forEach {
                currencies.add(it.code + "  " + it.country)
            }
            currenciesAdapter.setDropDownViewResource(R.layout.item_spinner)
            currenciesAdapter.notifyDataSetChanged()
        })
    }

    private fun initUI() {
        initSpinners()
        initConvertButton()
    }

    private fun initSpinners() {
        currenciesAdapter = ArrayAdapter(activity!!, R.layout.item_spinner, currencies)
        from_currency_spinner.adapter = currenciesAdapter
        from_currency_spinner.setSelection(0)
        to_currency_spinner.adapter = currenciesAdapter
        to_currency_spinner.setSelection(0)
    }

    private fun initConvertButton() {
        convert_button.setOnClickListener { convert() }
    }

    private fun convert() {
        val quantity = currency_edit.text.toString()
        currencyFrom = getCurrencyCode(from_currency_spinner.selectedItem.toString())
        currencyTo = getCurrencyCode(to_currency_spinner.selectedItem.toString())
        val currencies = "$currencyFrom,$currencyTo"

        if (quantity.isNotEmpty() && currencyFrom != currencyTo) {
            converterViewModel.getAvailableExchange(currencies)?.observe(viewLifecycleOwner, Observer { availableExchange ->
                availableExchange?.run {
                    exchange(quantity.toDouble(), availableExchangesMap)
                }
            })
        } else {
            Toast.makeText(activity, getString(R.string.error_convert), Toast.LENGTH_SHORT).show()
        }
    }

    private fun exchange(quantity: Double, availableExchangesMap: Map<String, Double>) {
        val exchangesKeys = availableExchangesMap.keys.toList()
        val exchangesValues = availableExchangesMap.values.toList()

        val fromCurrency = exchangesValues[0]
        val toCurrency = exchangesValues[1]

        val fromCurrencyKey = getCurrencyCodeResult(exchangesKeys[0])
        val toCurrencyKey = getCurrencyCodeResult(exchangesKeys[1])

        val usdExchange = quantity.div(fromCurrency)
        val exchangeResult = usdExchange.times(toCurrency)

        val result = quantity.toString() + " " + fromCurrencyKey + " = " + exchangeResult.format(4) + " " + toCurrencyKey
        showResult(result)

    }

    private fun showResult(result: String) {
        val builder: AlertDialog.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AlertDialog.Builder(context!!, R.style.AppCompatAlertDialogStyle)
        } else {
            AlertDialog.Builder(context!!)
        }

        val setMessage = TextView(activity)
        setMessage.text = result
        setMessage.gravity = Gravity.CENTER_HORIZONTAL
        builder.setView(setMessage)
        builder.setTitle(getString(R.string.currency_converter))
            .setPositiveButton(android.R.string.yes, null)
            .setIcon(R.drawable.ic_money_green)
            .show()
    }

    private fun getCurrencyCode(currency: String) = currency.substring(0, 3)

    private fun getCurrencyCodeResult(currency: String) = currency.substring(3)

    private fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

}
