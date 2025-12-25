package day9

import java.io.File
import java.lang.Math.floorMod
import kotlin.math.sign

typealias Corner = Pair<Long, Long>

typealias Segment = Pair<Corner, Corner>

fun area1(topRightCorner: Corner, bottomLeftCorner: Corner): Long {
    val height = bottomLeftCorner.second - topRightCorner.second + 1
    val width = topRightCorner.first - bottomLeftCorner.first + 1
    return height * width
}

fun area2(topLeftCorner: Corner, bottomRightCorner: Corner): Long {
    val height = bottomRightCorner.second - topLeftCorner.second + 1
    val width = bottomRightCorner.first - topLeftCorner.first + 1
    return height * width
}

fun area(topCorner: Corner, bottomCorner: Corner, polyCorners: List<Corner> = listOf(-1L to -1L)): Long {
    //println("polygon ${(topCorner to bottomCorner).toPolygon().toSegments()}")
    return if (!(topCorner to bottomCorner).toPolygon().isInscribedIn(polyCorners)) {
        0L
    } else if (topCorner.first < bottomCorner.first) {
        area2(topCorner, bottomCorner)
    } else {
        area1(topCorner, bottomCorner)
    }
}

fun vectorFrom(pointA: Corner, pointB: Corner) = (pointA.first - pointB.first) to (pointA.second - pointB.second)

fun transformationDeterminant(pointA: Corner, pointB: Corner) =
    (pointA.first * pointB.second) - (pointA.second * pointB.first)

fun List<Corner>.toSegments() = this.zip(this.drop(1) + this.first())
    .map {
        it.first to it.second
    }


fun List<Corner>.isInscribedIn(polyCorners: List<Corner>): Boolean {
    val reducedRectangle = this.toSegments().rectangleReduce()
    val segments = this.toSegments().rectangleReduce()
    val hasIntersections = segments.any { innerSegments ->
        polyCorners.toSegments().any { outerSegments ->
            innerSegments.strictIntersects(outerSegments)
        }
    }
    val cornerInside = reducedRectangle.map { it.first }.all {
        it.isInside(polyCorners)
    }
    val intersections = segments.map { innerSegments ->
        polyCorners.toSegments().map { outerSegments ->
            innerSegments.strictIntersects(outerSegments)
        }
    }
    //println(segments)
    //println("has intersections: $hasIntersections, corner inside: $cornerInside")
    return !hasIntersections && cornerInside
}


fun Segment.toPolygon(): List<Corner> {
    val (topCorner, bottomCorner) = this.toList().sortedBy { it.second }
    val otherTopCorner = bottomCorner.first to topCorner.second
    val otherBottomCorner = topCorner.first to bottomCorner.second

    val (topLeft, topRight) = listOf(topCorner, otherTopCorner).sortedBy { it.first }
    val (bottomLeft, bottomRight) = listOf(bottomCorner, otherBottomCorner).sortedBy { it.first }

    return listOf(topLeft, topRight, bottomRight, bottomLeft)
}

fun List<Segment>.rectangleReduce(): List<Segment> {
    //println("to be reduced: $this")
    require(this.map{ it.first }.toList().toSet().size == 4)
    val (topLeft, topRight, bottomRight, bottomLeft) = this.map { it.first }
    val rTopLeft = (topLeft.first + 1L) to (topLeft.second + 1L)
    val rTopRight = (topRight.first - 1L) to (topRight.second + 1L)
    val rBottomRight = (bottomRight.first - 1) to (bottomRight.second - 1L)
    val rBottomLeft = (bottomLeft.first + 1) to (bottomLeft.second - 1)

    return listOf(rTopLeft, rTopRight, rBottomRight, rBottomLeft).toSegments()
}

fun List<Segment>.toDiagonal(): Segment {
    val (topLeft, topRight, bottomRight, bottomLeft) = this.map { it.first }
    return topLeft to bottomRight
}

fun Segment.isVertical() = this.first.first == this.second.first

fun Segment.isHorizontal() = this.first.second == this.second.second

fun Segment.toHorizontalRange(): LongRange {
    require(this.isHorizontal())
    return listOf(this.first.first, this.second.first).sorted().let {
        it[0]..it[1]
    }
}

fun Segment.toVerticalRange(): LongRange {
    require(this.isVertical())
    return listOf(this.first.second, this.second.second).sorted().let {
        it[0]..it[1]
    }
}

fun LongRange.exclusive() = this.first + 1..<this.last

fun Segment.strictIntersects(otherSegment: Segment): Boolean {
    //println("checking intersection of $this and $otherSegment")
    val uniquePoints: Set<Corner> = this.toList().toSet().union(otherSegment.toList().toSet())
    val coincident = uniquePoints.size < 4

    //println("intersection check: ${this.toPolygon().toSegments()}")
    //println("points: $uniquePoints")
    //println("coincident $coincident")
    return if (this.isVertical() && otherSegment.isHorizontal()) {
        this.first.first in otherSegment.toHorizontalRange() && otherSegment.first.second in this.toVerticalRange()
    } else if (this.isHorizontal() && otherSegment.isVertical()) {
        otherSegment.first.first in this.toHorizontalRange() && this.first.second in otherSegment.toVerticalRange()
    } else {
        //println("not intersecting")
        false
    }
}

fun Segment.intersectsExclusive(otherSegment: Segment) =
    if (this.isVertical() && otherSegment.isHorizontal()) {
        this.first.first in otherSegment.toHorizontalRange()
            .exclusive() && otherSegment.first.second in this.toVerticalRange().exclusive()
    } else if (this.isHorizontal() && otherSegment.isVertical()) {
        otherSegment.first.first in this.toHorizontalRange()
            .exclusive() && this.first.second in otherSegment.toVerticalRange().exclusive()
    } else {
        false
    }

fun Segment.rayIntersects(otherSegment: Segment) =
    if (this.isVertical() && otherSegment.isHorizontal()) {
        this.first.first in otherSegment.toHorizontalRange()
            .exclusive() && otherSegment.first.second in this.toVerticalRange().exclusive()
    } else if (this.isHorizontal() && otherSegment.isVertical()) {
        otherSegment.first.first in this.toHorizontalRange()
            .exclusive() && this.first.second in otherSegment.toVerticalRange().exclusive()
    } else if (this.isVertical() && otherSegment.isVertical()) {
        this.first.first == otherSegment.first.first &&
                (otherSegment.toVerticalRange().any { it in this.toVerticalRange() } || this.toVerticalRange()
                    .any { it in otherSegment.toVerticalRange() })
    } else {
        this.first.second == otherSegment.first.second &&
                (otherSegment.toHorizontalRange().any { it in this.toHorizontalRange() } || this.toHorizontalRange()
                    .any { it in otherSegment.toHorizontalRange() })
    }

fun Corner.isInside(polyCorners: List<Corner>): Boolean {
    val segmentIntersections = polyCorners.zip(polyCorners.drop(1) + polyCorners.first())
        .map {
            it.first to it.second
        }.filter { ((-1L to this.second) to this).rayIntersects(it) }
    //println("intersections: $segmentIntersections")
    return (segmentIntersections.size % 2) == 1
}

fun topReductionIsBigger(
    sortedTopMost: List<Corner>,
    sortedBottomMost: List<Corner>,
    polyCorners: List<Corner>
): Boolean {
    require(sortedTopMost.size > 1)
    require(sortedBottomMost.size > 1)

    val reducedTopTrueArea = area(sortedTopMost[1], sortedBottomMost[0])
    val reducedBottomTrueArea = area(sortedTopMost[0], sortedBottomMost[1])
    val reducedTopQualifiedArea = area(sortedTopMost[1], sortedBottomMost[0], polyCorners)
    val reducedBottomQualifiedArea = area(sortedTopMost[0], sortedBottomMost[1], polyCorners)

    if (reducedTopQualifiedArea == 0L && reducedBottomQualifiedArea == 0L) {
        return reducedTopTrueArea > reducedBottomTrueArea
    } else {
        return reducedTopQualifiedArea > reducedBottomQualifiedArea
    }
}

fun main() {
    val coords = File("day9/input.in").readLines()
        .map {
            val (x, y) = it.split(",")
            x.toLong() to y.toLong()
        }


    val bottom = coords.maxOf { it.second }
    val right = coords.maxOf { it.first }

    println("general corners")
    println(coords)
    val topRightCorner = coords.maxBy {
        area1(it, 0L to bottom)
    }

    val bottomLeftCorner = coords.maxBy {
        area1(right to 0L, it)
    }

    val topLeftCorner = coords.maxBy {
        area2(it, right to bottom)
    }

    val bottomRightCorner = coords.maxBy {
        area2(0L to 0L, it)
    }

    println(topRightCorner)
    println(bottomLeftCorner)
    println(topLeftCorner)
    println(bottomRightCorner)
    println((topRightCorner to bottomLeftCorner).toPolygon().toSegments())
    println(area(topRightCorner, bottomLeftCorner, coords))
    println(area(topLeftCorner, bottomRightCorner, coords))

    println("convex only corners:")

    val cornerSigns = coords.withIndex().map { (i, coord) ->
        val prev = coords[floorMod(i - 1, coords.size)]
        val next = coords[floorMod(i + 1, coords.size)]
        val v1 = vectorFrom(prev, coord)
        val v2 = vectorFrom(coord, next)
        transformationDeterminant(v1, v2).sign
    }

    println(cornerSigns)
    val majoritySign = cornerSigns.sum().sign
    println("majority sign: $majoritySign")

    val convexCorners = coords.zip(cornerSigns)
        .filter { (coord, sign) ->
            sign == majoritySign
        }.map { it.first }

    val concaveCorners = coords.zip(cornerSigns)
        .filter { (coord, sign) ->
            sign != majoritySign
        }.map { it.first }


    val sortedTopRightMost = coords.sortedBy {
        area1(it, 0L to bottom)
    }.reversed()

    val sortedBottomLeftMost = coords.sortedBy {
        area1(right to 0L, it)
    }.reversed()

    val sortedTopLeftMost = coords.sortedBy {
        area2(it, right to bottom)
    }.reversed()

    val sortedBottomRightMost = coords.sortedBy {
        area2(0L to 0L, it)
    }.reversed()

    println("---------------------------")

    var currentTopRightIndex = 0
    var currentBottomLeftIndex = 0

    var currentArea =
        area(sortedTopRightMost[currentTopRightIndex], sortedBottomLeftMost[currentBottomLeftIndex], coords)
    while (
        (currentTopRightIndex + 1 < sortedTopRightMost.size || currentBottomLeftIndex + 1 < sortedBottomLeftMost.size) &&
        currentArea == 0L
    ) {
        if (
            currentBottomLeftIndex + 1 >= sortedBottomLeftMost.size ||
            (currentTopRightIndex + 1 < sortedTopRightMost.size &&
                    currentBottomLeftIndex + 1 < sortedBottomRightMost.size &&
                    topReductionIsBigger(
                        sortedTopRightMost.drop(currentTopRightIndex),
                        sortedBottomLeftMost.drop(currentBottomLeftIndex),
                        coords
                    ))
        ) {
            currentTopRightIndex += 1
        } else {
            currentBottomLeftIndex += 1
        }
        currentArea =
            area(sortedTopRightMost[currentTopRightIndex], sortedBottomLeftMost[currentBottomLeftIndex], coords)
    }
    println(sortedTopRightMost)
    println(sortedBottomLeftMost)
    println(sortedTopRightMost[currentTopRightIndex])
    println(sortedBottomLeftMost[currentBottomLeftIndex])
    println(area(sortedTopRightMost[currentTopRightIndex], sortedBottomLeftMost[currentBottomLeftIndex], coords))

    println("---------------------------")
    var currentTopLeftIndex = 0
    var currentBottomRightIndex = 0

    currentArea = area(sortedTopLeftMost[currentTopLeftIndex], sortedBottomRightMost[currentBottomRightIndex], coords)
    while (
        (currentTopLeftIndex + 1 < sortedTopLeftMost.size || currentBottomRightIndex + 1 < sortedBottomRightMost.size) &&
        currentArea == 0L
    ) {
        if (
            currentBottomRightIndex + 1 >= sortedBottomRightMost.size ||
            (currentTopLeftIndex + 1 < sortedTopLeftMost.size &&
                    currentBottomRightIndex + 1 < sortedBottomRightMost.size &&
                    topReductionIsBigger(
                        sortedTopLeftMost.drop(currentTopLeftIndex),
                        sortedBottomRightMost.drop(currentBottomRightIndex),
                        coords
                    ))
        ) {
            currentTopLeftIndex += 1
        } else {
            currentBottomRightIndex += 1
        }
        currentArea =
            area(sortedTopLeftMost[currentTopLeftIndex], sortedBottomRightMost[currentBottomRightIndex], coords)
    }
    println(sortedTopLeftMost)
    println(sortedBottomRightMost)
    println(sortedTopLeftMost[currentTopLeftIndex])
    println(sortedBottomRightMost[currentBottomRightIndex])
    println(area(sortedTopLeftMost[currentTopLeftIndex], sortedBottomRightMost[currentBottomRightIndex], coords))

    println(((9L to 5L) to (2L to 3L)).toPolygon().isInscribedIn(coords))
  //  println(((2L to 1L) to (11L to 1L)).strictIntersects((2L to 1L) to (11L to 1L)))
  //  println(((2L to 1L) to (11L to 1L)).strictIntersects((7L to 1L) to (7L to 3L)))

    val allPairs = coords.map { corner ->
        coords.map {
            if (corner == it) {
                null
            } else if (corner.first == it.first || corner.second == it.second) {
                null
            } else {
                corner to it
            }
        }
    }.flatten().filterNotNull()
    println(allPairs)

    val maxArea = allPairs.maxOf {
        val diagonal = it.toPolygon().toSegments().toDiagonal()
        area(diagonal.first, diagonal.second, coords)
    }

    println(maxArea)

}