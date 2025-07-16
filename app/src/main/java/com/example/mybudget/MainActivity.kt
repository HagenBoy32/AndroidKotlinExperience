package com.example.mybudget

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mybudget.DataModel.TransactionData
import com.example.mybudget.Utility.CsvExporter
import com.example.mybudget.ui.theme.MyBudgetTheme
import com.example.mybudget.viewmodel.PaymentMethodViewModel
import com.example.mybudget.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //
        setContent {
            Log.d("<<MainActivity>>", "onCreate: ")
            MyBudgetTheme {
                Log.d("<<MainActivity>>", "onCreate:(1) ")
                val context = LocalContext.current
                Log.d("<<MainActivity>>", "onCreate:(2) ")
                val paymentMethodViewModel: PaymentMethodViewModel = viewModel(
                    factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application)
                )
                Log.d("<<MainActivity>>", "onCreate:(3) ")
                var showAddPaymentMethodDialog by remember { mutableStateOf(false) }
                Log.d("<<MainActivity>>", "Scaffold(4) ")
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopMenuBar(
                            onAddPaymentMethodClick = { showAddPaymentMethodDialog = true }
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        PayTransaction(paymentMethodViewModel = paymentMethodViewModel)
                    }
                    Log.d("<<MainActivity>>", "MyBudgetTheme ")
                    // Dialog to add payment method
                    if (showAddPaymentMethodDialog) {
                        AddPaymentMethodDialog(
                            onAdd = { name ->
                                paymentMethodViewModel.addPaymentMethod(name, availableCredit = 0.0)
                                Toast.makeText(context, "Saved: $name", Toast.LENGTH_SHORT).show()
                                showAddPaymentMethodDialog = false
                            },
                            onDismiss = { showAddPaymentMethodDialog = false }
                        )
                    }
                }
            }
        }

    }
}

@Composable
fun PayTransaction(modifier: Modifier = Modifier,
        paymentMethodViewModel: PaymentMethodViewModel) {
    
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

//
    // val context = LocalContext.current
    // val paymentMethodViewModel: PaymentMethodViewModel = viewModel(
    //     factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application)
    // )


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
//
        Button(
            onClick = {
                try {
                    val amount = amountPaid.toDouble()
                    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val date = formatter.parse(datePaid) ?: Date()

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
                        val newTransaction = TransactionData(
                            acct_name = acctName,
                            account_category = acctCategory,
                            account_amount_paid = amount.toString(),
                            account_date_Paid = date
                        )
                        viewModel.addTransaction(newTransaction)

                        // Only clear form after adding a new transaction
                        acctName = ""
                        acctCategory = ""
                        amountPaid = ""
                        datePaid = ""
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Invalid input", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (editingTransaction != null) "Update Transaction" else "Save Transaction")
        }


        Button(
            onClick = { showExportDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Export & Email CSV")
        }

        Divider(thickness = 2.dp)

        Text("Saved Transactions", style = MaterialTheme.typography.titleMedium)

        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(transactions) { transaction ->
                TransactionCard(
                    transaction = transaction,
                    onEditClick = { t ->
                        acctName = t.acct_name
                        acctCategory = t.account_category
                        amountPaid = t.account_amount_paid
                        datePaid =
                            SimpleDateFormat("yyyy-MM-dd", Locale.US).format(t.account_date_Paid)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopMenuBar(onAddPaymentMethodClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text("My Budget") },
        actions = {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Add Payment Method") },
                    onClick = {
                        expanded = false
                        onAddPaymentMethodClick()
                    }
                )
                // You can add more menu items here
            }
        }
    )
}

@Composable
fun AddPaymentMethodDialog(
    onAdd: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var methodName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Payment Method") },
        text = {
            TextField(
                value = methodName,
                onValueChange = { methodName = it },
                label = { Text("Method Name") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = {
                if (methodName.isNotBlank()) {
                    onAdd(methodName.trim())
                }
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

