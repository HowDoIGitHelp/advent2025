package gifts

import java.io.File

data class Problem(
    val size: Pair<Int, Int>,
    val pieceRequirements: List<Int>
)

typealias Shape = List<String>

fun Shape.area(): Int =
    this.sumOf { line ->
        line.filter { it == '#' }
            .length
    }

fun Problem.requiredArea(shapeAreas: List<Int>) =
    this.pieceRequirements.zip(shapeAreas).sumOf { (nPiecesRequired, shapeArea) ->
        nPiecesRequired * shapeArea
    }

fun Problem.area() = this.size.first * this.size.second

fun Problem.numberOfPieces() = this.pieceRequirements.sum()

fun main() {
    val sections = File("day12/input.in").readText().split("\n\n")
    val problems = sections.last().split("\n").map {
        val line = it.split(" ")
        val (width, length) = line[0].dropLast(1).split("x").map(String::toInt)
        Problem(width to length, line.drop(1).map(String::toInt))
    }
    val shapes = sections.dropLast(1).map {
        it.split("\n").drop(1)
    }
    println(shapes)
    val shapeAreas = shapes.map(Shape::area)

    var nGuaranteedFit = 0
    var nImpossibleFit = 0
    var nUnsure = 0
    for (problem in problems) {
        val problemArea = problem.area()
        val requiredArea = problem.requiredArea(shapeAreas)
        val numberOfPieces = problem.numberOfPieces()
        val numberOf3x3s = (problem.size.first / 3) * (problem.size.second / 3)
        if (problemArea < requiredArea) {
            //println("impossible fit")
            nImpossibleFit += 1
        } else if (numberOfPieces <= numberOf3x3s) {
            //println("guaranteed fit")
            nGuaranteedFit += 1
        } else {
            //println("possible fit: ${problemArea} >= ${requiredArea}")
            nUnsure += 1
        }
    }

    println("$nGuaranteedFit, $nImpossibleFit, $nUnsure")
}