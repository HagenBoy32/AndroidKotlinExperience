// TransactionRepository.kt
package com.example.mybudget.data

import com.example.mybudget.DataModel.TransactionDao
import com.example.mybudget.DataModel.TransactionData

class TransactionRepository(private val dao: TransactionDao) {
    val allTransactions = dao.getAllTransactions()

    suspend fun insert(transaction: TransactionData) = dao.insert(transaction)
    suspend fun update(transaction: TransactionData) = dao.update(transaction)
    suspend fun delete(transaction: TransactionData) = dao.delete(transaction)
}