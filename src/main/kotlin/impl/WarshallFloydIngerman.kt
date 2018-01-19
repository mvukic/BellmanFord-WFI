package impl

import interfaces.IExporter
import interfaces.ISolver
import models.Edge
import models.Graph
import models.Vertex
import kotlin.system.measureNanoTime

class WarshallFloydIngerman(private val graph: Graph) : ISolver{

    companion object {
        val name = "Warshall-Floyd-Ingerman"
    }

    // Matrix of weights , keys are pairs od vertices, values are weights
    private val distances: MutableMap<Pair<Vertex,Vertex>,Int> = mutableMapOf()

    private val next: MutableMap<Pair<Vertex,Vertex>,Vertex?> = mutableMapOf()

    // List ov vertices on shortest path
    private val path: MutableList<Vertex> = mutableListOf()
    // List of edges that construct the shortest path
    private var edgesOnPath: List<Edge> = listOf()

    private var source: Vertex? = null
    private var target: Vertex? = null

    init {
        // Build adjacency matrix
        graph.vertices.forEach { i ->
            graph.vertices.forEach { k ->
                // Check if directed connection exists
                val edge: Edge? = graph.edges.find { it.from == i && it.to == k }
                // Distance between vertex and itself
                if(i == k){
                    // Vertex and itself
                    distances[Pair(i,k)] = 0
                    next[Pair(i,k)] = null
                } else{
                    if(edge == null){
                        // if vertices aren't connected
                        distances[Pair(i,k)] = 9999
                        next[Pair(i,k)] = null
                    }else{
                        // if vertices have a connection
                        distances[Pair(i,k)] = edge.weight
                        next[Pair(i,k)] = k
                    }
                }
            }
        }

    }

    override fun solve(): ISolver {
        measureNanoTime {
            resolveMatrix()
        }.let { time ->
            println(WarshallFloydIngerman.name)
            println("\tExecution time: $time ns (${time/1000000} ms)")
        }
        return this
    }

    private fun printMatrix(){
        graph.vertices.forEach { i ->
            var str = ""
            graph.vertices.forEach { k ->
                val d = distances[Pair(i,k)]
                str += if(d == 9999) String.format(" INF ") else String.format("%4d ",d)
            }
            println(str)
        }
        println()
    }

    private fun resolveMatrix(){
        graph.vertices.forEach { k ->
            graph.vertices.forEach { i ->
                graph.vertices.forEach { j ->
                    if(distances[Pair(i,j)]!! > distances[Pair(i,k)]!! + distances[Pair(k,j)]!!){
                        distances[Pair(i,j)] = distances[Pair(i,k)]!! + distances[Pair(k,j)]!!
                        next[Pair(i,j)] = next[Pair(i,k)]
                    }
                }
            }
        }
    }

    private fun reconstructPath(){
        if(next[Pair(source,target)] == null){
            path.clear()
        }else{
            var u: Vertex? = source
            path.add(u!!)
            while(u != target){
                u = next[Pair(u,target)]
                path.add(u!!)
            }
        }
    }

    /**
     * Sets starting vertex of algorithm and resets maps.
     */
    override fun from(start: Vertex): ISolver {
        source = start
        return this
    }

    /**
     * Sets end path vertex.
     */
    override fun to(finish: Vertex): ISolver {
        target = finish
        return this
    }

    override fun generateEdgesOnPath(): ISolver{
        // First we need to reconstruct the path
        reconstructPath()
        // dynamically creates window of size 2 and moves it until the end
        // those two elements are 'from' and 'to' vertices in edge
        edgesOnPath = path.windowed(2,1){
            graph.edges.find { edge -> edge.from == it[0] &&  edge.to == it[1] }!!
        }
        return this
    }

    /**
     * Prints shortest path.
     */
    override fun printPath() {
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
        return exporter.export(graph, edgesOnPath)
    }

}