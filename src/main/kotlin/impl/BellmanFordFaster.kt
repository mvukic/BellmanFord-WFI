package impl

import interfaces.IExporter
import interfaces.ISolver
import models.Edge
import models.Graph
import models.Vertex
import kotlin.system.measureNanoTime

class BellmanFordFaster(private val graph: Graph) : ISolver {

    private val distances: MutableMap<Vertex,Int> = mutableMapOf()
    private val predecessor: MutableMap<Vertex, Vertex?> = mutableMapOf()

    private val path: MutableList<Vertex> = mutableListOf()
    private var edgesOnPath: List<Edge> = listOf()

    private val toBeChecked: MutableList<Vertex> = mutableListOf()
    private val adjacent: MutableMap<Vertex,List<Vertex>> = mutableMapOf()

    private val mapOfWeights: MutableMap<Pair<Vertex,Vertex>,Int> = mutableMapOf()

    private var source: Vertex? = null
    private var target: Vertex? = null

    init {
        initVariables()
        // Speed up algorithm
        graph.vertices.forEach {
            adjacent[it] = adjacentTo(it)
        }
        // Speed up algorithm
        graph.edges.forEach {
            mapOfWeights[Pair(it.from,it.to)] = it.weight
        }
    }

    private fun initVariables(){
        // Initialize maps
        graph.vertices.forEach {
            distances[it] = 9999
            predecessor[it] = null
        }
    }

    override fun solve(): ISolver {
        measureNanoTime {
            while(toBeChecked.isNotEmpty()){
                val v = toBeChecked.first()
                toBeChecked.remove(v)
                adjacent[v]!!.forEach{ u ->
                    val newDistance = distances[v]!! + mapOfWeights[Pair(v,u)]!!
                    if(newDistance < distances[u]!!){
                        distances[u] = newDistance
                        predecessor[u] = v
                        if(!toBeChecked.contains(u)) toBeChecked.add(u)
                    }
                }
            }
        }.let { time ->
            println("Execution time: $time ns")
        }
        return this
    }

    private fun weightByNeighbours(from: Vertex, to: Vertex ): Int{
        return graph.edges.find {
            it.from == from && it.to == to
        }!!.weight
    }

    /**
     * Returns adjacent vertices to vertex 'v'
     */
    private fun adjacentTo(v: Vertex): List<Vertex>{
        val list = mutableListOf<Vertex>()
        graph.edges.forEach {
            if(it.from == v) list.add(it.to)
        }
        return list
    }

    override fun from(start: Vertex): ISolver {
        source = start
        toBeChecked.add(source!!)
        distances[source!!] = 0
        return this
    }

    override fun to(finish: Vertex): ISolver {
        target = finish
        return this
    }

    override fun printPath() {
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

    override fun export(exporter: IExporter): String {
        return exporter.export(graph, edgesOnPath)
    }

}