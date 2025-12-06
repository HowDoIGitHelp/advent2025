package day6

import java.io.File

fun List<List<Long>>.calculate(operations: List<String>): List<Long> {
    var cumulativeResults: List<Long> = operations.map {
        when (it) {
            "*" -> 1L
            else -> 0L
        }
    }

    for (operands in this) {
        cumulativeResults = cumulativeResults.combine(operands, operations)
    }

    return cumulativeResults.toList()
}

fun List<Long>.combine(operands: List<Long>, operations: List<String>): List<Long> {
    if (this.size != operations.size || this.size != operands.size)
        throw IllegalStateException("unequal lengths")

    val results: MutableList<Long> = mutableListOf()
    for ((operands, operator) in this.zip(operands).zip(operations)) {
        results.add(
            when (operator) {
                "+" -> operands.first + operands.second
                "*" -> operands.first * operands.second
                else -> throw IllegalStateException("unsupported operation")
            }
        )
    }

    return results.toList()
}

fun main() {
    val worksheet = File("day6/input.in").readLines().map {
        it.split(Regex("\\s+")).filter(String::isNotEmpty)
    }

    println(worksheet)
    val operations = worksheet.last()
    val operands = worksheet.dropLast(1).map {
        it.map(String::toLong)
    }

    val answers = operands.calculate(operations)

    println(answers)
    println(answers.fold(0L) { acc, n ->
        acc + n
    })
}