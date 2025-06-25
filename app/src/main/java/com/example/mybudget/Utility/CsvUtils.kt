package com.example.mybudget.Utility

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.mybudget.DataModel.TransactionData
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun exportTransactionsToCsv(context: Context, transactions: List<TransactionData>): File {
    val fileName = "transactions.csv"
    val file = File(context.cacheDir, fileName)

    file.bufferedWriter().use { out ->
        out.write("Account Name,Category,Amount Paid,Date Paid\n")
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        for (t in transactions) {
            val line = "${t.acct_name},${t.account_category},${t.account_amount_paid},${formatter.format(t.account_date_Paid)}"
            out.write(line)
            out.write("\n")
        }
    }

    return file
}

fun emailCsvFile(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_SUBJECT, "Transaction Export")
        putExtra(Intent.EXTRA_TEXT, "Here is your exported transaction data.")
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(intent, "Send email using:"))
}
