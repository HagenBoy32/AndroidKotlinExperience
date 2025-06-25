package com.example.mybudget.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybudget.DataModel.TransactionData
import com.example.mybudget.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TransactionRepository

    val transactions: StateFlow<List<TransactionData>>
    var currentEditingTransaction: TransactionData? = null

    init {
        val dao = TransactionDatabase.getDatabase(application).transactionDao()
        repository = TransactionRepository(dao)

        transactions = repository.allTransactions
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun addTransaction(transaction: TransactionData) {
        viewModelScope.launch {
            repository.insert(transaction)
        }
    }

    fun updateTransaction(transaction: TransactionData) {
        viewModelScope.launch {
            repository.update(transaction)
        }
    }

    fun deleteTransaction(transaction: TransactionData) {
        viewModelScope.launch {
            repository.delete(transaction)
        }
    }
}
