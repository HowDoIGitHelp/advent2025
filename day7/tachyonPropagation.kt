package day7

import java.io.File

fun List<Long>.propagate(nextLine: String): Pair<List<Long>, Long> {
    val propagation: MutableList<Long> = MutableList(this.size) { 0L }
    var splits = 0L
    for ((i, char) in nextLine.withIndex()) {
        if (this[i] > 0 && char == '^') {
            propagation[i - 1] += this[i]
            propagation[i + 1] += this[i]
            splits += 1
        } else if (this[i] > 0) {
            propagation[i] += this[i]
        }
    }
    return propagation to splits
}

fun main() {
    val lines = File("day7/input.in").readLines()
    println()
    var propagation = lines.first().map {
        when (it) {
            'S' -> 1L
            else -> 0L
        }
    }
    var totalSplits = 0L
    lines.drop(1).forEach {
        println(propagation)
        propagation.propagate(it).let { (newLine, splits) ->
            propagation = newLine
            totalSplits += splits
        }
    }

    println(propagation)
    println(propagation.sum())
    println(totalSplits)

}