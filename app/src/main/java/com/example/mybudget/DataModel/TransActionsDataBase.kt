// TransactionDatabase.kt
package com.example.mybudget.data

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mybudget.Converters
import com.example.mybudget.DataModel.PaymentMethodDao
import com.example.mybudget.DataModel.TransactionDao
import com.example.mybudget.DataModel.TransactionData
import com.example.mybudget.DataModel.PaymentMethod
import com.example.mybudget.R


@Database(entities = [TransactionData::class, PaymentMethod::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TransactionDatabase() : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun paymentMethodDao(): PaymentMethodDao

    companion object {
        @Volatile
        private var INSTANCE: TransactionDatabase? = null

        fun getDatabase(context: Context): TransactionDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TransactionDatabase::class.java,
                    "transaction_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}
