package day10

import java.io.File
import kotlin.math.pow

fun String.hammingDistance(anotherBitString: String) =
    this.zip(anotherBitString).filter {
        it.first != it.second
    }.size

fun Char.toBoolean() =
    when (this) {
        '0' -> false
        '1' -> true
        else -> throw IllegalStateException("invalid character $this")
    }

fun Boolean.toChar() = if (this) '1' else '0'

fun String.xor(anotherBitString: String) =
    this.zip(anotherBitString).map {
        it.first.toBoolean().xor(it.second.toBoolean()).toChar()
    }.joinToString("")

fun String.shortestPathTo(buttons: List<String>): Map<String, Int> {
    val binaryUpperBound = (2.0.pow(this.length)).toInt()
    val minDistances = (0..<binaryUpperBound).associate {
        it.toString(2).padStart(this.length, '0') to Int.MAX_VALUE
    }.toMutableMap()

    minDistances[this] = 0
    val isVisited =  minDistances.keys.associateWith { false }.toMutableMap()

    val queue: MutableList<String> = mutableListOf(this)

    while (queue.isNotEmpty()) {
        val currentString = queue.first()
        queue.removeFirst()
        isVisited[currentString] = true

        val neighbors = buttons.map {
            currentString.xor(it)
        }.filter {
            !(isVisited[it] ?: true)
        }

        neighbors.forEach {
            val currentDistance = minDistances[currentString] ?: Int.MAX_VALUE
            val tentativeMinimumDistance = minDistances[it] ?: Int.MAX_VALUE
            if (1 + currentDistance <  tentativeMinimumDistance) {
                minDistances[it] = 1 + currentDistance
            }
            queue.add(it)
        }

    }
    return minDistances.toMap()
}

fun String.search(target: String, buttons: List<String>): Int {
    println("$this - $target")
    if (this == target) {
        return 0
    } else {
        var minLength = Int.MAX_VALUE
        for (button in buttons) {
            val length = this.xor(button).search(target, buttons)
            if (length == 0) {
                return 1
            } else if (length < minLength) {
                minLength = length
            }
        }
        return 1 + minLength
    }
}

fun main() {
    val indicatorRequirements: MutableList<String> = mutableListOf()
    val joltageRequirements: MutableList<String> = mutableListOf()
    val buttons: MutableList<List<String>> = mutableListOf()

    File("day10/input.in").readLines().forEach {
        val words = it.split(" ")
        val firstWord = words.first()
        indicatorRequirements.add(firstWord.slice(1..<firstWord.lastIndex).replace('.','0').replace('#','1'))
        joltageRequirements.add(words.last())
        buttons.add(words.slice(1..<words.lastIndex).map { buttonCode ->
            val oneIndices = buttonCode.slice(1..<buttonCode.lastIndex).split(',')
            val binary = "0".repeat(firstWord.length - 2).toCharArray()
            oneIndices.forEach { indexStr ->
                binary[indexStr.toInt()] = '1'
            }
            binary.joinToString("")
        })
    }

    println(indicatorRequirements)
    println(joltageRequirements)
    println(buttons)

    println(buttons[0].map {
        val afterPress = it.xor(indicatorRequirements[0])
        listOf(indicatorRequirements[0], afterPress, indicatorRequirements[0].hammingDistance(afterPress))
    })

    var sum = 0
    for (i in 0..indicatorRequirements.lastIndex) {
        val minDistances = "0".repeat(indicatorRequirements[i].length).shortestPathTo(buttons[i])
        sum += minDistances[indicatorRequirements[i]] ?: 0
        println("${indicatorRequirements[i]}: ${minDistances[indicatorRequirements[i]]}")
    }
    println("sum: $sum")
}