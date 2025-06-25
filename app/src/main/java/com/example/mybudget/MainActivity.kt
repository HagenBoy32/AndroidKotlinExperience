package com.example.mybudget
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import android.app.Application
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mybudget.DataModel.TransactionData
import com.example.mybudget.Utility.emailCsvFile
import com.example.mybudget.Utility.exportTransactionsToCsv
import com.example.mybudget.ui.theme.MyBudgetTheme
import com.example.mybudget.viewmodel.TransactionViewModel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import com.example.mybudget.Utility.CsvExporter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyBudgetTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PayTransaction(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}
////////////////////////=
@Composable
fun PayTransaction(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val viewModel: TransactionViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application)
    )

    var acctName by remember { mutableStateOf("") }
    var acctCategory by remember { mutableStateOf("") }
    var amountPaid by remember { mutableStateOf("") }
    var datePaid by remember { mutableStateOf("") }
    var editingTransaction by remember { mutableStateOf<TransactionData?>(null) }
    val transactions by viewModel.transactions.collectAsState()
    var showExportDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Add Transaction", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = acctName,
            onValueChange = { acctName = it },
            label = { Text("Account Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = acctCategory,
            onValueChange = { acctCategory = it },
            label = { Text("Category") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = amountPaid,
            onValueChange = { amountPaid = it },
            label = { Text("Amount Paid") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = datePaid,
            onValueChange = { datePaid = it },
            label = { Text("Date (yyyy-MM-dd)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                try {
                    val amount = amountPaid.toDouble()
                    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val date = formatter.parse(datePaid) ?: Date()

                    val transaction = TransactionData(
                        acct_name = acctName,
                        account_category = acctCategory,
                        account_amount_paid = amount.toString(),
                        account_date_Paid = date
                    )

                    viewModel.addTransaction(transaction)

                    if (editingTransaction != null) {
                        val updated = editingTransaction!!.copy(
                            acct_name = acctName,
                            account_category = acctCategory,
                            account_amount_paid = amount.toString(),
                            account_date_Paid = date
                        )
                        viewModel.updateTransaction(updated)
                        editingTransaction = null
                    } else {
                        val transaction = TransactionData(
                            acct_name = acctName,
                            account_category = acctCategory,
                            account_amount_paid = amount.toString(),
                            account_date_Paid = date
                        )
                        viewModel.addTransaction(transaction)
                    }
                    // Clear form
                    acctName = ""
                    acctCategory = ""
                    amountPaid = ""
                    datePaid = ""

                    if (editingTransaction != null) {
                        val updated = editingTransaction!!.copy(
                            acct_name = acctName,
                            account_category = acctCategory,
                            account_amount_paid = amount.toString(),
                            account_date_Paid = date
                        )
                        viewModel.updateTransaction(updated)
                        editingTransaction = null
                    } else {
                        val transaction = TransactionData(
                            acct_name = acctName,
                            account_category = acctCategory,
                            account_amount_paid = amount.toString(),
                            account_date_Paid = date
                        )
                        viewModel.addTransaction(transaction)
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Invalid input", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Transaction")
        }

        Button(
            onClick = { showExportDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Export & Email CSV")
        }

        Divider(thickness = 2.dp)

        Text("Saved Transactions", style = MaterialTheme.typography.titleMedium)

        LazyColumn(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(transactions) { transaction ->
                TransactionCard(
                    transaction = transaction,
                    onEditClick = { t ->
                        acctName = t.acct_name
                        acctCategory = t.account_category
                        amountPaid = t.account_amount_paid
                        datePaid = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(t.account_date_Paid)
                        editingTransaction = t
                    },
                    onDeleteClick = {
                        viewModel.deleteTransaction(it)
                    }
                )
            }

        }
    }

    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Confirm Export") },
            text = { Text("Do you want to export and email your transaction data as a CSV file?") },
            confirmButton = {
                TextButton(onClick = {
                    CsvExporter.exportToDownloadsAndEmail(context, transactions)
                    showExportDialog = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

}


@Preview(showBackground = true)
@Composable
fun PayTransactionPreview() {
    MyBudgetTheme {
        PayTransaction(modifier = Modifier.padding(16.dp))
    }
}



