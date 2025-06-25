package com.example.mybudget.DataModel

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "transactionsdata")
data class TransactionData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,  // optional: set default to 0 for easier insertions
    var acct_name: String = "",
    var account_amount_paid: String = "",
    var account_date_Paid: Date = Date(),
    var account_category: String = ""
)
