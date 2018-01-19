package impl

import interfaces.*
import models.*
import kotlin.system.measureNanoTime

class BellmanFordRegular(private val graph: Graph) : ISolver {

    companion object {
        val name = "Bellman-Ford (regular)"
    }

    // Distances from source vertex to other vertices
    private val distances: MutableMap<Vertex,Int> = mutableMapOf()
    // Map of vertex and its predecessor on the shortest path
    private val predecessor: MutableMap<Vertex, Vertex?> = mutableMapOf()
    // List of vertices on shortest path
    private val path: MutableList<Vertex> = mutableListOf()
    // List of edges that construct the shortest path
    private var edgesOnPath: List<Edge> = listOf()

    // Starting vertex
    private var source: Vertex? = null
    // Ending vertex
    private var target: Vertex? = null

    init{
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
            println(BellmanFordRegular.name)
            println("\tExecution time: $time ns (${time/1000000} ms)")
        }
        return this
    }

    /**
     * Approximation to the correct distance is gradually replaced by more accurate values
     */
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
                throw Error("Graph contains a negative-weight cycle")
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
     * Generates list of consecutive edges on shortest path.
     */
    override fun generateEdgesOnPath(): ISolver{
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
        return this
    }

    /**
     * Prints shortest path.
     */
    override fun printPath(){
        generateEdgesOnPath()
        val sb = StringBuilder()
        sb.append("\t")
        edgesOnPath.forEachIndexed { index, edge ->
            sb.append("${edge.from.name} -(${edge.weight})> ")
            if(index == edgesOnPath.count()-1) sb.append(edge.to.name)
        }
        println(sb.toString())

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