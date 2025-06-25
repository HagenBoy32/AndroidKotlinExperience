package com.example.mybudget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.mybudget.DataModel.TransactionData
//import java.lang.reflect.Modifier
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
@Composable
fun TransactionCard(
    transaction: TransactionData,
    onEditClick: (TransactionData) -> Unit,
    onDeleteClick: (TransactionData) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Account: ${transaction.acct_name}")
            Text("Category: ${transaction.account_category}")
            Text("Amount Paid: $${transaction.account_amount_paid}")
            Text("Date Paid: ${SimpleDateFormat("yyyy-MM-dd", Locale.US).format(transaction.account_date_Paid)}")

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { onEditClick(transaction) }) {
                    Text("Edit")
                }
                TextButton(onClick = { onDeleteClick(transaction) }) {
                    Text("Delete")
                }
            }
        }
    }
}
