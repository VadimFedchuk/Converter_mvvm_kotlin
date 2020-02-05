package com.vadimfedchuk.converter_mvvm_kotlin.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = RoomContract.TABLE_CURRENCIES)
data class CurrencyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    var countryCode: String,
    var countryName: String
)