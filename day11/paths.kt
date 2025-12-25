package day11

import java.io.File
val cache: MutableMap<Pair<Int, Int>, Long> = mutableMapOf()
fun ((Int, Int) -> Long).memoized(): (Int, Int) -> Long {
    //println("cache size = ${cache.size}")
    return { param1, param2 ->
        cache.getOrPut(param1 to param2) {
            this(param1, param2)
        }
    }
}

fun Array<Array<Int>>.nPathsFrom(root: Int): List<Long> {
    val queue = mutableListOf(root)
    val isVisited = MutableList(this.size) { false }
    val nodePaths = MutableList(this.size) { 0L }
    nodePaths[root] = 1L
    while (queue.isNotEmpty()) {
        val removedNode = queue.removeFirst()
        for (neighbor in this.neighborsOf(removedNode)) {
            if (!isVisited[neighbor]) {
                queue.add(neighbor)
                isVisited[neighbor] = true
            }
            nodePaths[neighbor] += nodePaths[removedNode]
            val visitedNeighborsOfNeighbor = this.neighborsOf(neighbor).filter{ isVisited[it] }
            for (vnn in visitedNeighborsOfNeighbor) {
                nodePaths[vnn] = this.incidentsOf(vnn).sumOf { nodePaths[it] }
            }
        }
    }
    /*
    for (i in 0..nodePaths.lastIndex) {
        if (root != root)
            nodePaths[i] = this.incidentsOf(i).sumOf { nodePaths[it] }
    }
    */
    return nodePaths.toList()
}

/*
fun Array<Array<Int>>.pathsFrom(root: Int): List<Int> {
    val queue = mutableListOf(root)
    val isVisited = MutableList(this.size) { false }
    val nodePaths = MutableList(this.size) { listOf<Int>() }
    nodePaths[root] = listOf(root)
    while (queue.isNotEmpty()) {
        val removedNode = queue.removeFirst()
        for (neighbor in this.neighborsOf(removedNode)) {
            if (!isVisited[neighbor])
                queue.add(neighbor)
            isVisited[neighbor] = true
            nodePaths[neighbor] = nodePaths[removedNode]
        }
    }
    return nodePaths.toList()
}
 */

fun Array<Array<Int>>.pathsFrom(sourceNode: Int, destinationNode: Int): Long {
    if (sourceNode == destinationNode) {
        return 1L
    } else {
        val incidentNodes = this.incidentsOf(destinationNode)
        return incidentNodes.sumOf {
            this::pathsFrom.memoized()(sourceNode, it)
        }
    }
}

fun Array<Array<Int>>.neighborsOf(node: Int): List<Int> =
    this[node].withIndex().filter{
        it.value == 1
    }.map {
        it.index
    }

fun Array<Array<Int>>.incidentsOf(node: Int): List<Int> =
    (0..this.lastIndex).map { this[it][node] }
        .withIndex().filter {
            it.value == 1
        }.map {
            it.index
        }

fun main() {
    val adjacencies = File("day11/input.in").readLines()
    val nodes = adjacencies.map {
        it.split(":").first()
    }

    val nodeMap = nodes.zip(0..nodes.lastIndex).toMap().toMutableMap()
    nodeMap["out"] = nodeMap.size

    val adjacencyMatrix = Array(nodeMap.size) { Array(nodeMap.size) { 0 } }

    for (line in adjacencies) {
        val node = line.split(" ").first().dropLast(1)
        val neighbors = line.split(" ").drop(1)

        for (neighbor in neighbors) {
            adjacencyMatrix[nodeMap[node] ?: -1][nodeMap[neighbor] ?: -1] = 1
        }
    }
    println(nodeMap)


    println("paths ")
    val a = adjacencyMatrix::pathsFrom.memoized()(nodeMap["svr"] ?: -1, nodeMap["fft"] ?: -1)
    val b = adjacencyMatrix::pathsFrom.memoized()(nodeMap["fft"] ?: -1, nodeMap["dac"] ?: -1)
    val c = adjacencyMatrix::pathsFrom.memoized()(nodeMap["dac"] ?: -1, nodeMap["out"] ?: -1)
    println("$a * $b * $c = ${a * b * c}")
}