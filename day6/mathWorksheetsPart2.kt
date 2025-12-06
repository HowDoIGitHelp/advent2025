package day6

import java.io.File

fun List<List<String>>.alignedTranspose() =
    (0..this[0].lastIndex).map { j ->
        this.map { it[j] }
    }

fun String.toAlignedNumbers(numberLengths: List<Int>): List<String> {
    val alignedNumbers: MutableList<String> = mutableListOf()
    var start = 0
    var i = 0
    while (start < this.length) {
        alignedNumbers.add(this.slice(start..<start + numberLengths[i]))
        start += numberLengths[i] + 1
        i += 1
    }

    return alignedNumbers.toList()
}

fun List<String>.cephalopodOperation(seed: Long, operation: (Long, Long) -> Long): Long {
    var cumulativeResult = seed

    val transposed = (0..this.first().lastIndex).map { position ->
        this.map { number ->
            number[position]
        }.joinToString("").replace(" ","").toLong()
    }

    return transposed.fold(seed, operation)
}

fun String.alignedOperations(): List<String> {
    val alignedOps: MutableList<String> = mutableListOf()
    var start = 0
    val pattern = Regex("[\\+\\*] +")
    var match = pattern.find(this)
    while (start < this.length && match != null) {
        alignedOps.add(match.value)
        start = match.range.last + 1
        println("${match.range}:${this}")
        match = pattern.find(this, start)
    }
    return alignedOps.toList()
}

fun main() {
    val lines = File("day6/input.in").readLines()
    val maxLineLength = lines.maxOf { it.length }
    val worksheet = lines.map {
        it.padEnd(maxLineLength)
    }
    val operands = worksheet.dropLast(1)
    val operations = Regex("[+*] +").findAll(worksheet.last() + " ").map { it.value.dropLast(1) }.toList()
    val alignedRows = operands.map {
        it.toAlignedNumbers(operations.map { it.length })
    }
    val operationBlocks = alignedRows.alignedTranspose()

    val results: MutableList<Long> = mutableListOf()
    for ((operationBlock, operator) in operationBlocks.zip(operations)) {
        results.add(when (operator.first()) {
            '*' -> operationBlock.cephalopodOperation(1L, Long::times)
            '+' -> operationBlock.cephalopodOperation(0L, Long::plus)
            else -> throw IllegalStateException("unrecognized operation")
        })
    }

    println(results)
    println(results.sum())
}