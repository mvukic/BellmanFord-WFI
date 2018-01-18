import impl.BellmanFordFaster
import impl.BellmanFordRegular
import impl.DotExporter
import impl.WarshallFloydIngerman
import models.Edge
import models.Graph
import models.Vertex
import java.io.File

fun main(args: Array<String>) {

    // Zadatak 2.2
    val A = Vertex(0, "A")
    val B = Vertex(1, "B")
    val C = Vertex(2, "C")
    val D = Vertex(3, "D")
    val E = Vertex(4, "E")
    val F = Vertex(5, "F")
    val G = Vertex(6, "G")
    val H = Vertex(7, "H")
    val I = Vertex(8, "I")
    val J = Vertex(9, "J")
    val K = Vertex(10, "K")
    val L = Vertex(11, "L")
    val M = Vertex(12, "M")
    val N = Vertex(13, "N")
    val O = Vertex(14, "O")
    val P = Vertex(15, "P")
    val R = Vertex(16, "R")
    val S = Vertex(17, "S")


    val AB = Edge(A, B, 2)
    val AC = Edge(A, C, 6)
    val AD = Edge(A, D, 12)
    val BE = Edge(B, E, 9)
    val BC = Edge(B, C, 3)
    val CF = Edge(C, F, 1)
    val DH = Edge(D, H, -2)
    val EG = Edge(E, G, 1)
    val FH = Edge(F, H, 4)
    val FE = Edge(F, E, 2)
    val GI = Edge(G, I, 2)
    val HI = Edge(H, I, 3)
    val HK = Edge(H, K, 1)
    val HL = Edge(H, L, -3)
    val HM = Edge(H, M, -2)
    val IJ = Edge(I, J, -1)
    val JN = Edge(J, N, 5)
    val KJ = Edge(K, J, 0)
    val KO = Edge(K, O, -9)
    val LP = Edge(L, P, 7)
    val ML = Edge(M, L, 2)
    val MR = Edge(M, R, 2)
    val NO = Edge(N, O, 0)
    val NS = Edge(N, S, -9)
    val OS = Edge(O, S, 11)
    val PS = Edge(P, S, -6)
    val RS = Edge(R, S, 3)

    val graph = Graph().apply {
        vertices = listOf(A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,R,S)
        edges = listOf(AB,AC,AD,BE,BC,CF,DH,EG,FH,FE,GI,HI,HK,HL,HM,IJ,JN,KJ,KO,LP,ML,MR,NO,NS,OS,PS,RS)
    }

    // Primjer predavanje slajd 30.
//    val A = Vertex(0, "A")
//    val B = Vertex(1, "B")
//    val C = Vertex(2, "C")
//    val D = Vertex(3, "D")
//    val AB = Edge(A,B,5)
//    val AC = Edge(A,C,4)
//    val BC = Edge(B,C,-2)
//    val BD = Edge(B,D,3)
//    val CD = Edge(C,D,4)
//    val graph = Graph().apply {
//        vertices = listOf(A, B, C, D)
//        edges = listOf(AB, AC, BC, BC, BD, CD)
//    }

    val solver = WarshallFloydIngerman(graph)

    // Solve path
    solver.from(A)
            .to(P)
            .solve()
            .printPath()

    // Export to DOT format
//    val exporter = DotExporter()
//    val dot = solver.export(exporter)

//    File("dot.txt").printWriter().use { out ->
//        out.println(dot)
//    }

}