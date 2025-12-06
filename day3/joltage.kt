package day3

import java.io.File
import java.math.BigInteger

fun joltage(batteryLine: String): Int {
    val firstPosition = maxJoltPosition(batteryLine.dropLast(1))
    val rest = batteryLine.drop(firstPosition + 1)
    val secondPosition = maxJoltPosition(rest)

    return charArrayOf(batteryLine[firstPosition], rest[secondPosition]).concatToString().toInt()
}

fun maxJoltPosition(batteryLine: String): Int {
    var maxIndex = 0

    for ((index, char) in batteryLine.withIndex()) {
        if (char.digitToInt() > batteryLine[maxIndex].digitToInt()) {
            maxIndex = index
        }
    }

    return maxIndex
}

fun trueJoltage(batteryLine: String): BigInteger = batteryLine.toBigInteger()

fun removeLowestImpact(batteryLine: String): String {
    for ((index, char) in batteryLine.dropLast(1).withIndex()) {
        if (char.digitToInt() < batteryLine[index + 1].digitToInt())
            return batteryLine.take(index) + batteryLine.drop(index + 1)
    }
    return batteryLine.dropLast(1)
}

fun keepTwelveBatteries(batteryLine: String): String {
    var newLine = batteryLine
    while (newLine.length > 12) {
        newLine = removeLowestImpact(newLine)
    }

    return newLine
}

fun main() {
    val lines = File("day3/input.in").readText().split("\n")
    var totalJoltage = 0L.toBigInteger()
    for (line in lines) {
        println(line)
        val newBank = keepTwelveBatteries(line)
        val trueJoltage = trueJoltage(newBank)
        println(trueJoltage)
        totalJoltage += trueJoltage
    }
    println(totalJoltage)
}