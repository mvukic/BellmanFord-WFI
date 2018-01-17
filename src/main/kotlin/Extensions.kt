import models.Vertex

fun MutableList<Vertex>.addIfNotIncluded(v: Vertex){
    if(!this.contains(v)) this.add(v)
}