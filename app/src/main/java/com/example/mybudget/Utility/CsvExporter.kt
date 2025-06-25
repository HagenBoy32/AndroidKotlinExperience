package com.example.mybudget.Utility


import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.example.mybudget.DataModel.TransactionData
import java.text.SimpleDateFormat
import java.util.*

object CsvExporter {

    fun exportToDownloadsAndEmail(context: Context, transactions: List<TransactionData>) {
        if (transactions.isEmpty()) {
            Toast.makeText(context, "No transactions to export.", Toast.LENGTH_SHORT).show()
            return
        }

        val fileName = "transactions_${System.currentTimeMillis()}.csv"
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        // Save to Downloads folder using MediaStore API
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "text/csv")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        if (uri != null) {
            resolver.openOutputStream(uri).use { out ->
                out?.bufferedWriter()?.use { writer ->
                    writer.write("Account Name,Category,Amount Paid,Date Paid\n")
                    for (t in transactions) {
                        val line = "${t.acct_name},${t.account_category},${t.account_amount_paid},${formatter.format(t.account_date_Paid)}"
                        writer.write(line)
                        writer.write("\n")
                    }
                }
            }

            emailFile(context, uri, fileName)
        } else {
            Toast.makeText(context, "Failed to create file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun emailFile(context: Context, uri: android.net.Uri, fileName: String) {
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_SUBJECT, "Transaction Export")
            putExtra(Intent.EXTRA_TEXT, "Attached is your exported transaction data.")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(emailIntent, "Send email using:"))
    }
}
