package com.example.mybudget.DataModel

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface PaymentMethodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(method: PaymentMethod)

    @Delete
    suspend fun delete(method: PaymentMethod)

    @Query("SELECT * FROM payment_methods ORDER BY name ASC")
    fun getAllMethods(): Flow<List<PaymentMethod>>
}
