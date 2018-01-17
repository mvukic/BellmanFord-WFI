package interfaces

import models.Vertex

interface ISolver {

    fun solve(): ISolver

    fun from(start: Vertex): ISolver

    fun to(finish: Vertex): ISolver

    fun printPath()

    fun export(exporter: IExporter): String

}