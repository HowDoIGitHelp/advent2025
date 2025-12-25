package day8

import java.io.File
import kotlin.math.min
import kotlin.math.max

class JunctionBox(
    val x: Long,
    val y: Long,
    val z: Long,
) {
    fun distance(anotherBox: JunctionBox) =
        (this.x - anotherBox.x) * (this.x - anotherBox.x) +
                (this.y - anotherBox.y) * (this.y - anotherBox.y) +
                (this.z - anotherBox.z) * (this.z - anotherBox.z)

    override fun toString() = "$x,$y,$z"
}

fun <T> MutableList<T>.pop(): T {
    val poppedElement = this.first()
    this.removeAt(0)
    return poppedElement
}

class UnionFind(size: Int) {
    val parent = (0..<size).toMutableList()
    val depth = MutableList(size) { 0 }
    val size = MutableList(size) { 0 }

    fun find(index: Int): Int {
        if (parent[index] != index)
            parent[index] = find(parent[index])
        return parent[index]
    }

    fun union(a: Int, b: Int) {
        val parentA = find(a)
        val parentB = find(b)

        if (parentA != parentB) {
            if (depth[parentA] > depth[parentB]) {
                parent[parentB] = parentA
            } else if (depth[parentB] > depth[parentA]){
                parent[parentA] = parentB
            } else {
                parent[parentB] = parentA
                depth[parentA] += 1
            }
        }
    }
}

fun main() {
    val boxes = File("day8/input.in").readLines().map {
        val (x, y, z) = it.split(",").map(String::toLong)
        JunctionBox(x, y, z)
    }

    val graph = MutableList(boxes.size) { MutableList(boxes.size) {0L} }

    (0..boxes.lastIndex).forEach { i ->
        (0..boxes.lastIndex).forEach { j ->
            if (i == j)
                graph[i][j] = Long.MAX_VALUE
            else
                graph[i][j] = boxes[i].distance(boxes[j])
        }
    }
    val edges: MutableMap<Pair<Int, Int>, Long> = mutableMapOf()
    for (i in 0..boxes.lastIndex) {
        for (j in (i+1)..boxes.lastIndex) {
            edges[i to j] = boxes[i].distance(boxes[j])
        }
    }

    var edgeList = edges.keys.sortedBy { edges[it] }.toMutableList()

    println(edgeList.map{ it to edges[it] }.joinToString("\n"))

    val subGraphs: MutableList<MutableSet<Int>> = mutableListOf(mutableSetOf())
    val subGraphMembership: MutableMap<Int, Int> = mutableMapOf()
    val pickedEdges: MutableSet<Pair<Int, Int>> = mutableSetOf()
    val mst: MutableSet<Pair<Int, Int>> = mutableSetOf()

    edgeList.pop().let {
        subGraphs[0].add(it.first)
        subGraphs[0].add(it.second)
        subGraphMembership[it.first] = 0
        subGraphMembership[it.second] = 0
        mst.add(it)
        mst.add(it.second to it.first)
    }


    var count = 0
    while (subGraphs[0].size < boxes.size) {
        count += 1
        val shortestAvailableEdge = edgeList.pop()
        shortestAvailableEdge.let {
            println("${boxes[it.first]} - ${boxes[it.second]}: ${boxes[it.first].x * boxes[it.second].x}")
        }

        val firstSubgraph = subGraphMembership[shortestAvailableEdge.first]
        val secondSubgraph = subGraphMembership[shortestAvailableEdge.second]
        if (firstSubgraph != null && secondSubgraph != null) {
            val keptSetIndex = min(firstSubgraph, secondSubgraph)
            val removedSetIndex = max(firstSubgraph, secondSubgraph)
            if (keptSetIndex != removedSetIndex) {
                subGraphs[keptSetIndex] = subGraphs[keptSetIndex].union(subGraphs[removedSetIndex]).toMutableSet()
                subGraphs[removedSetIndex].forEach {
                    subGraphMembership[it] = keptSetIndex
                }
                subGraphs[removedSetIndex] = mutableSetOf()
                mst.add(shortestAvailableEdge)
            } else {
                //println("ignoring cycle forming edge")
            }
        }
        else if (firstSubgraph == null && secondSubgraph == null) {
            //println("creating new subgraph")
            val newSet: MutableSet<Int> = mutableSetOf()
            newSet.add(shortestAvailableEdge.first)
            newSet.add(shortestAvailableEdge.second)
            subGraphs.add(newSet)
            subGraphMembership[shortestAvailableEdge.first] = subGraphs.lastIndex
            subGraphMembership[shortestAvailableEdge.second] = subGraphs.lastIndex
            mst.add(shortestAvailableEdge)
        } else if (firstSubgraph != null) {
            //println("adding ${shortestAvailableEdge.second} to $firstSubgraph")
            val existingSet = subGraphs[firstSubgraph]
            existingSet.add(shortestAvailableEdge.first)
            existingSet.add(shortestAvailableEdge.second)
            subGraphMembership[shortestAvailableEdge.second] = firstSubgraph
            mst.add(shortestAvailableEdge)
        } else if (secondSubgraph != null) {
            //println("adding ${shortestAvailableEdge.first} to $secondSubgraph")
            val existingSet = subGraphs[secondSubgraph]
            existingSet.add(shortestAvailableEdge.first)
            existingSet.add(shortestAvailableEdge.second)
            subGraphMembership[shortestAvailableEdge.first] = secondSubgraph
            mst.add(shortestAvailableEdge)
        } else {
            throw IllegalStateException()
        }
        //println()
    }

    println(mst)
    val sortedSubgraphs = subGraphs.sortedBy { it.size }.reversed()
    println(sortedSubgraphs.map { it.size })
    println(sortedSubgraphs.map { it.size }.take(3).fold(1) { acc, i -> acc * i })
}