package com.example.mybudget.repository

import com.example.mybudget.DataModel.PaymentMethod
import com.example.mybudget.DataModel.PaymentMethodDao
import com.example.mybudget.DataModel.TransactionData
import kotlinx.coroutines.flow.Flow
import java.util.*

class PaymentMethodRepository(private val dao: PaymentMethodDao) {

    fun getAllMethods(): Flow<List<PaymentMethod>> = dao.getAllMethods()
    suspend fun addMethod(name: String) {
        //dao.insert(PaymentMethod(name = name))
    }
    suspend fun deleteMethod(method: PaymentMethod)
            = dao.delete(method)

    suspend fun insert(PaymentMethod: PaymentMethod)
            = dao.insert(PaymentMethod)

    //suspend fun update(transaction: TransactionData)
    // = dao.update(transaction)

    suspend fun delete(paymentmethod: PaymentMethod)
            = dao.delete(paymentmethod)





}
