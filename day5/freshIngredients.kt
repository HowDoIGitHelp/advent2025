package day5

import java.io.File

fun List<Pair<Long, Long>>.simplified(): List<Pair<Long, Long>> {
    var simplifiedRanges: List<Pair<Long,Long>> = listOf()

    this.forEach {
        simplifiedRanges = simplifiedRanges.extendedRangeList(it)
    }

    return simplifiedRanges.toList()
}

fun List<Pair<Long, Long>>.extendedRangeList(newRange: Pair<Long, Long>): List<Pair<Long, Long>> {
    if (this.isEmpty())
        return listOf(newRange)
    else {
        var i = this.indexOfFirst {
            newRange.first <= it.second
        }
        if (i < 0)
            i = this.lastIndex
        return if (newRange.isInsideOf(this[i])) {
            this
        } else if (newRange.contains(this[i])) {
            (this.take(i) + this.drop(i + 1)).extendedRangeList(newRange)
        } else if (newRange.isLeftOf(this[i])) {
            this.take(i) + listOf(newRange) + this.drop(i)
        } else if (newRange.isRightOf(this[i])) {
            this.take(i+1) + listOf(newRange) + this.drop(i+1)
        } else if (newRange.extendsLeft(this[i])) {
            (this.take(i) + this.drop(i + 1)).extendedRangeList(newRange.combineLeft(this[i]))
        } else if (newRange.extendsRight(this[i])) {
            (this.take(i) + this.drop(i + 1)).extendedRangeList(newRange.combineRight(this[i]))
        } else {
            throw IllegalStateException("new range: $newRange, i: $i, index:${this[i]}")
        }
    }
}

fun Pair<Long, Long>.isInsideOf(anotherPair: Pair<Long, Long>) =
    this.first in anotherPair.first..anotherPair.second && this.second in anotherPair.first..anotherPair.second

fun Pair<Long, Long>.contains(anotherPair: Pair<Long, Long>) = anotherPair.isInsideOf(this)

fun Pair<Long, Long>.extendsRight(anotherPair: Pair<Long, Long>) =
    this.second in anotherPair.first..anotherPair.second && this.first !in anotherPair.first..anotherPair.second

fun Pair<Long, Long>.extendsLeft(anotherPair: Pair<Long, Long>) =
    this.first in anotherPair.first..anotherPair.second && this.second !in anotherPair.first..anotherPair.second

fun Pair<Long, Long>.isLeftOf(anotherPair: Pair<Long, Long>) =
    this.second < anotherPair.first

fun Pair<Long, Long>.isRightOf(anotherPair: Pair<Long, Long>) =
    this.first > anotherPair.second

fun Pair<Long, Long>.combineLeft(anotherPair: Pair<Long, Long>) = anotherPair.first to this.second

fun Pair<Long, Long>.combineRight(anotherPair: Pair<Long, Long>) = this.first to anotherPair.second

fun Long.isFresh(ranges: List<Pair<Long, Long>>) = ranges.any { this in it.first..it.second }

fun main() {
    val (rangesText, ingredientsText) = File("day5/input.in").readText()
        .split("\n\n")

    println("${rangesText.split("\n").size} ${ingredientsText.split("\n").size}")
    val ranges = rangesText.split("\n").map {
        it.split("-").let { range ->
            range[0].toLong() to range[1].toLong()
        }
    }

    val rawEndpoints = rangesText.split("\n").map {
        it.split("-").let { range ->
            listOf(range[0].toLong(),range[1].toLong())
        }
    }.fold(listOf<Long>()) { acc, longs ->
        acc + longs
    }

    println("min: ${rawEndpoints.min()}, max: ${rawEndpoints.max()}")

    val ingredients = ingredientsText.split("\n").map(String::toLong)

    val freshRanges = ranges.simplified()
    println(freshRanges)
    val endpoints = freshRanges.map(Pair<Long, Long>::toList).fold(listOf<Long>()) { acc, longs ->
        acc + longs
    }

    for ((i, endpoint) in endpoints.dropLast(1).withIndex()) {
        if (endpoint >= endpoints[i+1]) {
            println("$endpoint >= ${endpoints[i + 1]}")
        }
    }

    println(freshRanges.sumOf {
        it.second - it.first + 1
    })
    println(freshRanges.map {
        it.second - it.first + 1
    })
    val freshIngredients = ingredients.filter {
        it.isFresh(ranges.simplified())
    }

    println(freshIngredients.size)

    println((3L to 5L).extendsLeft(2L to 4L))
    println((3L to 5L).combineLeft(2L to 4L))
}