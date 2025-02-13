package org.example

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.abs

data class Row(
    val timezone: String,
    val date: String,
    val time: String,
    val type: String,
    val currency: String,
    val amount: Double,
    val quoteCurrency: String,
    val quotePrice: Float?,
    val receivedPaidCurrency: String,
    val receivedPaidAmount: Float?,
    val feeCurrency: String,
    val feeAmount: Float?,
    val status: String,
    val transactionId: String,
    val address: String,
)

data class Transaction(
    val type: String,
    val amount: Double,
    val quoteCurrency: String? = null,
    val quotePrice: Float?,
    val receivedPaidAmount: Float?,
)

fun main(args: Array<String>) {
    val argParser = ArgParser("bitvavo-transactions")
    val csvPath by argParser.option(ArgType.String, fullName = "path", shortName = "p", description = "Path to the csv file").required()

    argParser.parse(args)

    val bufferedReader = Files.newBufferedReader(Paths.get(csvPath))
    val parser = CSVParserBuilder().withSeparator(',').build()
    val reader = CSVReaderBuilder(bufferedReader).withSkipLines(1).withCSVParser(parser).build()

    val lines = reader.readAll()

    val list = mutableListOf<Row>()
    for (line in lines) {
        list.add(Row(
            line[0],
            line[1],
            line[2],
            line[3],
            line[4],
            line[5].toDouble(),
            line[6],
            line[7].toFloatOrNull(),
            line[8],
            line[9].toFloatOrNull(),
            line[10],
            line[11].toFloatOrNull(),
            line[12],
            line[13],
            line[14],
        ))
    }

    val mostTradedAssets = mutableMapOf<String, MutableList<Transaction>>()

    for (row in list) {
        val transaction = Transaction(row.type, row.amount, row.quoteCurrency.ifEmpty { null }, row.quotePrice, row.receivedPaidAmount)
        var isFirstOfType = false
        val asset = mostTradedAssets.getOrElse(row.currency) { isFirstOfType = true; mutableListOf(transaction) }
        if (!isFirstOfType) {
            asset.add(transaction)
        }
        mostTradedAssets[row.currency] = asset
    }

    for ((key, value) in mostTradedAssets) {
        val buys = value.filter { it.type == "buy" }
        val sells = value.filter { it.type == "sell" }
        val deposits = value.filter { it.type == "deposit" }
        val stakings = value.filter { it.type == "staking" }
        val withdraws = value.filter { it.type == "withdrawal" }

        println("- $key:")
        println("    Amount of transactions: ${value.size}")
        println("    Purchased ${buys.sumOf { it.amount }} $key across ${buys.size} transactions")
        println("    Deposited ${deposits.sumOf { it.amount }} $key across ${deposits.size} transactions")
        println("    Staked ${String.format("%.8f", stakings.sumOf { it.amount })  } $key across ${stakings.size} transactions")
        println("    Withdrew ${String.format("%.8f", withdraws.sumOf { it.amount })} $key in ${sells.size} transactions")
        println("    Final balance: ${String.format("%.8f", value.sumOf { it.amount })} $key")
        println("    Total $key transacted (including buy and sell orders, deposits, staking and withdrawals): ${value.sumOf { abs(it.amount) }} $key")
        println()
    }
}
