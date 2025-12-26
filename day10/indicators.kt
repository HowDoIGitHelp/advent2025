package day10

import java.io.File
import kotlin.math.pow

var cache: MutableMap<List<Int>, Int> = mutableMapOf()
fun ((List<Int>, List<String>) -> Int).memoized(): (List<Int>, List<String>) -> Int {
    //println("cache size = ${cache.size}")
    return { param1, param2 ->
        cache.getOrPut(param1) {
            this(param1, param2)
        }
    }
}

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


fun List<Int>.downjolt(button: String): List<Int> {
    val newJoltages: MutableList<Int> = mutableListOf()
    for ((joltage, bit) in this.zip(button.toList())) {
        newJoltages.add(joltage - bit.digitToInt())
    }
    return newJoltages
}

fun List<Int>.patterns(buttons: List<String>): List<String> {
    val patternList: MutableList<String> = mutableListOf()
    val togglePattern = this.map {
        it % 2
    }.joinToString("")
    val buttonsPowersetSize = 2.0.pow(buttons.size).toInt()
    for (i in (0..<buttonsPowersetSize)) {
        val binary = i.toString(radix = 2).padStart(buttons.size, '0')
        val setMembership = binary.map(Char::toBoolean)
        val pressedButtons = buttons.zip(setMembership).filter {
            it.second
        }.map { it.first }
        //println("i:$i, setmembers: ${setMembership}, pressed: $pressedButtons")
        val pressOutcome = pressedButtons.fold("0".repeat(this.size)) { acc, string ->
            acc.xor(string)
        }
        if (pressOutcome == togglePattern)
            patternList.add(binary)
    }
    return patternList
}

fun List<Int>.setDownjolt(buttons: List<String>, setMembership: String): List<Int> {
    var downjolted = this
    buttons.zip(setMembership.map(Char::toBoolean)).forEach { (button, isMember) ->
        if (isMember)
            downjolted = downjolted.downjolt(button)
    }
    return downjolted
}

fun List<Int>.halfjolt() =
    this.map {
        require(it % 2 == 0)
        it/2
    }

fun String.sumOfOnes() = this.sumOf(Char::digitToInt)

fun buttonPresses(joltageRequirements: List<Int>, buttons: List<String>): Int {
    if (joltageRequirements.any { it < 0 }) {
        return 100000000
    } else if (joltageRequirements.all { it == 0 }) {
        return 0
    } else {
        val patterns = joltageRequirements.patterns(buttons)
        if (patterns.isEmpty())
            return 100000000
        val halves = patterns.map {
            joltageRequirements.setDownjolt(buttons, it).halfjolt()
        }
        return halves.zip(patterns).minOf { (half, pattern) ->
            pattern.sumOfOnes() + (2 * ::buttonPresses.memoized()(half, buttons))
        }
    }
}

fun main() {
    val indicatorRequirements: MutableList<String> = mutableListOf()
    val joltageRequirements: MutableList<List<Int>> = mutableListOf()
    val buttons: MutableList<List<String>> = mutableListOf()

    File("day10/input.in").readLines().forEach {
        val words = it.split(" ")
        val firstWord = words.first()
        indicatorRequirements.add(firstWord.slice(1..<firstWord.lastIndex).replace('.','0').replace('#','1'))
        joltageRequirements.add(words.last().drop(1).dropLast(1).split(",").map(String::toInt))
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

    var totalPresses1 = 0
    for ((indicatorRequirement,buttonSet) in indicatorRequirements.zip(buttons)) {
        totalPresses1 += indicatorRequirement.map(Char::digitToInt).patterns(buttonSet).minOf {
            it.sumOfOnes()
        }
    }
    println(totalPresses1)

    var totalPresses2 = 0
    for ((joltageRequirement, buttonSet) in joltageRequirements.zip(buttons)) {
        val result = ::buttonPresses.memoized()(joltageRequirement, buttonSet)
        totalPresses2 += result
        cache = mutableMapOf()
    }
    println(totalPresses2)
}