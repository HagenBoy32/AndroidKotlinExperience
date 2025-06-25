package com.example.mybudget.DataModel

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import androidx.room.Delete as Delete
import androidx.room.OnConflictStrategy.Companion as OnConflictStrategy1

/////
@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy1.REPLACE)
    suspend fun insert(transaction: TransactionData)

    @Update
    suspend fun update(transaction: TransactionData)

    @Delete
    suspend fun delete(transaction: TransactionData)

    @Query("SELECT * FROM transactionsdata ORDER BY account_date_Paid DESC")
    fun getAllTransactions(): Flow<List<TransactionData>>
}