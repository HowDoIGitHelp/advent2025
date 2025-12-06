import java.io.File

fun Long.digits() = this.toString().length

fun Long.patternIncrement(stepSize: Int, upperBound: Long): Long? {
    return if (this == "9".repeat(digits()).toLong()) {
        (this + 1L).repeatingPatterns(upperBound = upperBound).minOfOrNull { it }
    } else {
        (this.toString().take(stepSize).toLong() + 1L)
            .toString()
            .repeat(digits() / stepSize).toLong()
    }
}

fun divisors(digits: Int) = (1..<digits).filter {
    digits % it == 0
}

fun Long.repeatingPatterns(upperBound: Long = Long.MAX_VALUE) = divisors(digits()).map {
        this.repeatStart(it)
    }.filter {
        it in this..upperBound
    }.sorted().distinct()

fun Long.repeatStart(initSize: Int) = this.toString().substring(0..<initSize).repeat(digits()/initSize).toLong()

fun patternsSum(lowerBound: Long, upperBound: Long) = patterns(lowerBound, upperBound).fold(0L) { acc, lng ->
    acc + lng
}

fun Long.incrementSet(stepSize: Int, lowerBound:Long = Long.MIN_VALUE, upperBound: Long = Long.MAX_VALUE): Set<Long> {
    val incrementSet: MutableSet<Long> = mutableSetOf()
    var number: Long? = this
    while (number!= null && number <= upperBound) {
        incrementSet.add(number)
        number = number.patternIncrement(stepSize, upperBound)
    }

    return incrementSet.filter { it > lowerBound }.toSet()
}

fun patterns(lowerBound: Long, upperBound: Long): Set<Long> {
    val union: MutableSet<Long> = mutableSetOf()
    divisors(lowerBound.digits()).forEach {
        union.addAll(lowerBound.repeatStart(it).incrementSet(it, lowerBound = lowerBound, upperBound = upperBound))
    }
    return union.toSet()
}

fun Long.digitLimit() = "9".repeat(this.digits()).toLong()

fun distributeRanges(ranges: List<Pair<Long,Long>>): List<Pair<Long,Long>> {
    val newRanges: MutableList<Pair<Long,Long>> = mutableListOf()
    for (range in ranges) {
        if (range.first.digits() == range.second.digits()) {
            newRanges.add(range)
        } else {
            val nines = range.first.digitLimit()
            newRanges.add(range.first to nines)
            newRanges.addAll(distributeRanges(listOf(nines + 1L to range.second)))
        }
    }
    return newRanges
}


fun main() {
    val contents = File("day2/input.in").readText().dropLast(1)
    val ranges = distributeRanges(contents.split(",").map {
        it.split("-").let {
            (it[0].toLong() to it[1].toLong())
        }
    })
    println(ranges.fold(0L) { acc, (lower, upper) ->
        acc + patternsSum(lower, upper)
    })
}