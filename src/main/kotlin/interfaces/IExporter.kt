package interfaces

import models.Edge
import models.Graph

interface IExporter {

    fun export(graph: Graph, edges: List<Edge>): String

}