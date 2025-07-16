package com.example.mybudget.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybudget.DataModel.PaymentMethod
import com.example.mybudget.data.TransactionDatabase
import com.example.mybudget.repository.PaymentMethodRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PaymentMethodViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PaymentMethodRepository

    // Expose payment methods as StateFlow
    val allMethods: StateFlow<List<PaymentMethod>>

    init {
        Log.d("<<PaymentMethodViewModel>>", "init")

        val dao = TransactionDatabase.getDatabase(application).paymentMethodDao()
        repository = PaymentMethodRepository(dao)

        // Collect methods into a StateFlow to use in UI
        allMethods = repository.getAllMethods()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun insert(paymentMethod: PaymentMethod) {
        Log.d("<<PaymentMethodViewModel>>", "insert: $paymentMethod")
        viewModelScope.launch {
            repository.insert(paymentMethod)
        }
    }

    fun deleteMethod(method: PaymentMethod) = viewModelScope.launch {
        Log.d("<<PaymentMethodViewModel>>", "deleteMethod: ${method.name}")
        repository.deleteMethod(method)
    }

    fun addPaymentMethod(name: String, availableCredit: Double) {
        Log.d("<<PaymentMethodViewModel>>", "addPaymentMethod: $name")
        val newMethod = PaymentMethod(name = name, Available_Credit = availableCredit, Credit_Limit = 0.0)
        insert(newMethod)
    }
}
