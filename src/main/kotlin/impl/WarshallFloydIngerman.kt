package impl

import interfaces.IExporter
import interfaces.ISolver
import models.Edge
import models.Graph
import models.Vertex
import kotlin.system.measureNanoTime

class WarshallFloydIngerman(private val graph: Graph) : ISolver{

    // Matrix of weights
    private val d: MutableMap<Pair<Vertex,Vertex>,Int> = mutableMapOf()
    private val next: MutableMap<Pair<Vertex,Vertex>,Vertex> = mutableMapOf()

    private val path: MutableList<Vertex> = mutableListOf()
    private var edgesOnPath: List<Edge> = listOf()

    private var source: Vertex? = null
    private var target: Vertex? = null

    init {
        // Map of minimum distances initialized to infinity
        graph.edges.forEach {
            d[Pair(it.from,it.to)] = it.weight
            d[Pair(it.to,it.from)] = 9999
            next[Pair(it.from,it.to)] = it.to
        }
        // Distance between vertex and it self
        graph.vertices.forEach {
            d[Pair(it,it)] = 0
        }
    }

    override fun solve(): ISolver {
        measureNanoTime {
            resolveMatrix()
        }.let { time ->
            println("Execution time: $time ns")
        }
        return this
    }

    private fun resolveMatrix(){
        graph.vertices.forEach { k ->
            graph.vertices.forEach { i ->
                graph.vertices.forEach { j ->
                    if(d[Pair(i,j)]!! > d[Pair(i,k)]!! + d[Pair(k,j)]!!){
                        d[Pair(i,j)] = d[Pair(i,k)]!! + d[Pair(k,j)]!!
                        next[Pair(i,j)] = next[Pair(i,k)]!!
                    }
                }
            }
        }
    }

    private fun generatePath(){
        var u = source
        val v = target
        if(next.containsKey(Pair(u,v))){
            path.add(u!!)
            while(u != v){
                u = next[Pair(u,v)]
                path.add(u!!)
            }
        }else{
            path.clear()
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

    override fun printPath() {
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

    override fun export(exporter: IExporter): String {
        return exporter.export(graph, listOf())
    }

}