package com.example.mybudget.DataModel

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "payment_methods")
data class PaymentMethod(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val name: String,
    val Available_Credit: Double,
    val Credit_Limit: Double
)


