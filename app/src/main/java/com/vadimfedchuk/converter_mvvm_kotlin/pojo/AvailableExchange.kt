package com.vadimfedchuk.converter_mvvm_kotlin.pojo

import java.io.Serializable

data class AvailableExchange(var availableExchangesMap: Map<String, Double>): Serializable
