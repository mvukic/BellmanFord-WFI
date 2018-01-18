package impl

import interfaces.*
import models.*
import kotlin.system.measureNanoTime

class BellmanFordRegular(private val graph: Graph) : ISolver {

    private val distances: MutableMap<Vertex,Int> = mutableMapOf()
    private val predecessor: MutableMap<Vertex, Vertex?> = mutableMapOf()
    private val path: MutableList<Vertex> = mutableListOf()
    private var edgesOnPath: List<Edge> = listOf()

    private var source: Vertex? = null
    private var target: Vertex? = null

    init{
        initVariables()
    }

    private fun initVariables(){
        distances.clear()
        predecessor.clear()
        path.clear()
        edgesOnPath = listOf()
        // Initialize maps
        graph.vertices.forEach {
            distances[it] = 9999
            predecessor[it] = null
        }
    }

    /**
     * Sets starting vertex of algorithm and resets maps.
     */
    override fun from(start: Vertex): ISolver {
        source = start
        distances[source!!] = 0
        return this
    }

    override fun solve(): ISolver {
        measureNanoTime {
            relaxEdges()
            checkForNegativeWeightCycles()
        }.let { time ->
            println("Execution time: $time ns")
        }
        return this
    }

    private fun relaxEdges(){
        for(i in 1 until graph.vertices.count()){
            for((from, to, weight) in graph.edges){
                if(distances[from]!! + weight < distances[to]!!){
                    distances[to] = distances[from]!! + weight
                    predecessor[to] = from
                }
            }
        }
    }

    /**
     * Checks if there are any negative-weight cycles
     */
    private fun checkForNegativeWeightCycles(){
        for((from, to, weight) in graph.edges){
            if(distances[from]!! + weight < distances[to]!!){
                throw Error("models.Graph contains a negative-weight cycle")
            }
        }
    }

    /**
     * Sets end path vertex.
     */
    override fun to(finish: Vertex): ISolver {
        target = finish
        return this
    }

    /**
     * Prints shortest path.
     */
    override fun printPath(){
        path.add(target!!)
        var current: Vertex = target!!

        // save predecessors to list until start
        while(true){
            if(current == source) break
            val before = predecessor[current]!!
            current = before
            path.add(current)
        }

        // dynamically creates window of size 2 and moves it until the end
        // those two elements are 'from' and 'to' vertices in edge
        edgesOnPath = path.asReversed().windowed(2,1){
            graph.edges.find { edge -> edge.from == it[0] &&  edge.to == it[1] }!!
        }

        edgesOnPath.forEachIndexed { index, edge ->
            print("${edge.from.name} -(${edge.weight})> ")
            if(index == edgesOnPath.count()-1) print(edge.to.name)
        }
        println()

        // Clear shortest path
        path.clear()
    }

    /**
     * Uses exporter to export graph and shortest path for drawing.
     */
    override fun export(exporter: IExporter): String {
        return exporter.export(graph,edgesOnPath)
    }

}