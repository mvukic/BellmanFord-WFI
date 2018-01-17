package impl

import interfaces.IExporter
import models.Edge
import models.Graph

class DotExporter : IExporter {

    private val directed = "->"

    override fun export(graph: Graph, edges: List<Edge>): String {
        val sb = StringBuilder()
        sb.appendln("digraph BellmanFord {")

        // Write vertices on shortest path
        // Colour shortest path
        sb.appendln("//Shortest path edges")
        edges.forEach { edge ->
            sb.appendln("${edge.from.name} $directed ${edge.to.name} [ label=\"${edge.weight}\" ] [color=blue];")
        }
        sb.appendln("//other edges")

        // write other vertices
        graph.edges
                .minus(edges)
                .asIterable()
                .forEach { edge ->
                    sb.appendln("${edge.from.name} $directed ${edge.to.name} [ label=\"${edge.weight}\" ];")
        }
        sb.append("}")
        return sb.toString()
    }

}