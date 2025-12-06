package day4

import java.io.File

fun List<String>.numberOfRollNeighbors(x: Int, y: Int): Int {
    val topLeft = y-1 to x-1
    val top = y-1 to x
    val topRight = y-1 to x+1

    val left = y to x-1
    val right = y to x+1

    val bottomLeft = y+1 to x-1
    val bottom = y+1 to x
    val bottomRight = y+1 to x+1

    val neighborsCoordinates = listOf(
        topLeft,
        top,
        topRight,
        left,
        right,
        bottomLeft,
        bottom,
        bottomRight,
    )
    var sum = 0
    for (coord in neighborsCoordinates) {
        try {
            when(this[coord.first][coord.second]) {
                '@' -> sum += 1
                else -> sum += 0
            }
        } catch (e: IndexOutOfBoundsException) {
            sum += 0
        }
    }

    return sum
}

fun List<String>.numberOfAccessibleRolls(): Int {
    var sum = 0
    for (y in 0..<size) {
        for (x in 0..<this[0].length) {
            val neighbors = numberOfRollNeighbors(x,y)
            sum += when (this[y][x]) {
                '@' -> if (neighbors < 4) 1 else 0
                else -> 0
            }
        }
    }
    return sum
}

fun List<String>.removedRolls(): List<String> {
    val rollStack: MutableList<String> = mutableListOf()
    for (y in 0..<size) {
        var line = ""
        for (x in 0..<this[0].length) {
            val neighbors = numberOfRollNeighbors(x,y)
            line += when (this[y][x]) {
                '@' -> if (neighbors < 4) "." else "@"
                else -> "."
            }
        }
        rollStack.add(line)
    }
    return rollStack.toList()
}

fun main() {
    var lines = File("day4/input.in").readText().split("\n")
    var removeableRolls = lines.numberOfAccessibleRolls()
    var totalRemovedRolls = 0
    while (removeableRolls >= 1) {
        println(removeableRolls)
        totalRemovedRolls += removeableRolls
        lines = lines.removedRolls()
        removeableRolls = lines.numberOfAccessibleRolls()
    }
    println(lines.joinToString("\n"))
    println(totalRemovedRolls)
}