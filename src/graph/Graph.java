package graph;

import main.Dijkstra;
import java.util.*;
import java.util.stream.IntStream;

public class Graph {

    private ArrayList<Vertex> vertices = new ArrayList<>();
    private ArrayList<Edge> edges = new ArrayList<>();
    private final HashMap<Integer, Vertex> vertexIdMap = new HashMap<>();
    private final HashMap<Integer, Edge> edgeIdMap = new HashMap<>();
    private int[][] adjacencyMatrix;
    private int[][] incidenceMatrix;

    public Graph(String adjacencyList) {
        String[] lines = adjacencyList.split("\n");

        for (String ignored : lines)
            this.vertices.add(new Vertex());

        for (int v1 = 0; v1 < lines.length; v1++)
            for (String s : lines[v1].split(","))
                this.edges.add(new Edge(this.vertices.get(v1), this.vertices.get(Integer.parseInt(s))));

        for (Vertex vertex : vertices) this.vertexIdMap.put(vertex.getId(), vertex);
        for (Edge edge : edges) this.edgeIdMap.put(edge.getId(), edge);
        this.adjacencyMatrix = calcAdjacencyMatrix(this.vertices, this.edges);
        this.incidenceMatrix = calcIncidenceMatrix(this.vertices, this.edges);
    }

    protected Graph(ArrayList<Vertex> vertices, ArrayList<Edge> edges) {
        this.vertices = new ArrayList<>(vertices);
        this.edges = new ArrayList<>(edges);
        this.vertices.sort(Comparator.comparingInt(Vertex::getId));
        this.edges.sort(Comparator.comparingInt(Edge::getId));
        for (Vertex vertex : vertices) this.vertexIdMap.put(vertex.getId(), vertex);
        for (Edge edge : edges) this.edgeIdMap.put(edge.getId(), edge);
    }

    public Vertex getVertex(int index) {
        return vertices.get(index);
    }

    public Edge getEdge(int index) {
        return edges.get(index);
    }

    public Vertex getVertexById(int id) {
        return vertexIdMap.get(id);
    }

    public Edge getEdgeById(int id) {
        return edgeIdMap.get(id);
    }

    public int[][] getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    public int[][] getIncidenceMatrix() {
        return incidenceMatrix;
    }

    public int v() {
        return vertices.size();
    }

    public int e() {
        return edges.size();
    }

    public static int[][] calcAdjacencyMatrix(ArrayList<Vertex> vertices, ArrayList<Edge> edges) {
        int[][] adjacencyMatrix = new int[vertices.size()][vertices.size()];
        for (Edge edge : edges) {
            Vertex v1 = edge.getV1();
            Vertex v2 = edge.getV2();
            adjacencyMatrix[vertices.indexOf(v1)][vertices.indexOf(v2)] += 1;
            adjacencyMatrix[vertices.indexOf(v2)][vertices.indexOf(v1)] += 1;
        }
        return adjacencyMatrix;
    }

    public static int[][] calcIncidenceMatrix(ArrayList<Vertex> vertices, ArrayList<Edge> edges) {
        int[][] incidenceMatrix = new int[vertices.size()][edges.size()];
        for (Edge edge : edges) {
            incidenceMatrix[vertices.indexOf(edge.getV1())][edges.indexOf(edge)] += 1;
            incidenceMatrix[vertices.indexOf(edge.getV2())][edges.indexOf(edge)] += 1;
        }
        return incidenceMatrix;
    }

    public boolean adjacent(int v1, int v2) {
        return adjacencyMatrix[v1][v2] == 1;
    }

    public boolean incident(int v, int e) {
        return incidenceMatrix[v][e] > 0;
    }

    public boolean isSimple() {
        for (int v = 0; v < v(); v++)
            if (adjacencyMatrix[v][v] > 0)
                return false;
        for (int v1 = 0; v1 < v(); v1++)
            for (int v2 = v1 + 1; v2 < v(); v2++)
                if (adjacencyMatrix[v1][v2] > 1)
                    return false;
        return true;
    }

    public boolean isComplete() {
        return isSimple() && e() == (v() * (v() - 1)) / 2;
    }

    public boolean identical(Graph g) {
        return this.equals(g);
    }

    public boolean isomorphic(Graph g) {
        if (vertices.size() != g.vertices.size()) return false;
        if (edges.size() != g.edges.size()) return false;
        return false; // TODO
    }

    public boolean isBipartite() {
        ArrayList<Vertex> left = new ArrayList<>();
        ArrayList<Vertex> right = new ArrayList<>();
        for (Edge edge : edges) {
            Vertex v1 = edge.getV1();
            Vertex v2 = edge.getV2();
            if ((left.contains(v1) && left.contains(v2)) ||
                    (right.contains(v1) && right.contains(v2))) return false;
            else if ((left.contains(v1) && right.contains(v2)) ||
                    (right.contains(v1) && left.contains(v2))) continue;
            else {
                if (left.contains(v1)) right.add(v2);
                else if (right.contains(v1)) left.add(v2);
                else if (left.contains(v2)) right.add(v1);
                else if (right.contains(v2)) left.add(v1);
                else {
                    left.add(v1);
                    right.add(v2);
                }
            }
        }
        return true;
    }

    public Graph complement() {
        if (!isSimple())
            return null;
        ArrayList<Edge> newEdges = new ArrayList<>();
        for (int i = 0; i < v(); i++)
            for (int j = i + 1; j < v(); j++)
                if (adjacencyMatrix[i][j] == 0)
                    newEdges.add(new Edge(getVertexById(i), getVertexById(j)));
        return new Graph(vertices, newEdges);
    }

    public boolean subgraphOf(Graph g) {
        return g.vertices.containsAll(vertices) && g.edges.containsAll(edges);
    }

    public boolean supergraphOf(Graph g) {
        return vertices.containsAll(g.vertices) && edges.containsAll(g.edges);
    }

    public boolean spanningSubgraphOf(Graph g) {
        return this.subgraphOf(g) && this.vertices.size() == g.vertices.size();
    }

    public Graph simple() {
        ArrayList<Vertex> newVertices = new ArrayList<>(vertices);
        ArrayList<Edge> newEdges = new ArrayList<>();
        int[][] adjacencyMatrix = new int[vertices.size()][vertices.size()];
        for (Edge edge : edges) {
            if (!edge.getV1().equals(edge.getV2())) {
                int ind1 = vertices.indexOf(edge.getV1());
                int ind2 = vertices.indexOf(edge.getV2());
                if (adjacencyMatrix[ind1][ind2] > 0) {
                    newEdges.add(edge);
                    adjacencyMatrix[ind1][ind2] += 1;
                    adjacencyMatrix[ind2][ind1] += 1;
                }
            }
        }
        return new Graph(newVertices, newEdges);
    }

    public static Graph complete(int k) {
        StringBuilder adjacencyList = new StringBuilder();
        List<Integer> numbersList = IntStream.range(0, k)
                .boxed()
                .toList();
        String numbersString = numbersList
                .toString()
                .replace("[", "")
                .replace("]", "")
                .replace(" ", "");
        for (int i = 0; i < k; i++) {
            adjacencyList.append(numbersString, 0, 2 * i)
                         .append(numbersString, 2 * i + 1, 2 * k - 1)
                         .append("\n");
        }
        return new Graph(adjacencyList.toString());
    }

    public Graph induceByVertices(ArrayList<Vertex> vertices) {
        ArrayList<Edge> inducedEdges = (ArrayList<Edge>) edges
                .stream()
                .filter(e -> vertices.contains(e.getV1()) &&
                        vertices.contains(e.getV2()))
                .toList();
        return new Graph(vertices, inducedEdges);
    }

    public Graph induceByEdges(ArrayList<Edge> edges) {
        Set<Vertex> inducedVertices = new HashSet<>();
        for (Edge edge: edges) {
            inducedVertices.add(edge.getV1());
            inducedVertices.add(edge.getV2());
        }
        return new Graph(new ArrayList<>(inducedVertices), edges);
    }

    public Graph minusVertices(ArrayList<Vertex> vertices) {
        ArrayList<Vertex> diffVertices = (new ArrayList<>(this.vertices));
        diffVertices.removeAll(vertices);
        return induceByVertices(diffVertices);
    }

    public Graph minusEdges(ArrayList<Edge> edges) {
        ArrayList<Edge> diffEdges = (new ArrayList<>(this.edges));
        diffEdges.removeAll(edges);
        return induceByEdges(diffEdges);
    }

    public boolean disjoint(Graph g) {
        Set<Integer> ids = new HashSet<>();
        ids.addAll(vertices.stream().map(Vertex::getId).toList());
        ids.addAll(g.vertices.stream().map(Vertex::getId).toList());
        return ids.size() == v() + g.v();
    }

    public boolean edgeDisjoint(Graph g) {
        Set<Edge> edges = new HashSet<>();
        edges.addAll(this.edges);
        edges.addAll(g.edges);
        return edges.size() == e() + g.e();
    }

    public Graph union(Graph g) {
        ArrayList<Vertex> newVertices = new ArrayList<>(vertices);
        ArrayList<Edge> newEdges = new ArrayList<>(edges);
        for (Vertex vertex : g.vertices)
            if (!newVertices.contains(vertex)) newVertices.add(vertex);
        for (Edge edge : g.edges)
            if (!newEdges.contains(edge)) newEdges.add(edge);
        return new Graph(newVertices, newEdges);
    }

    public Graph intersection(Graph g) {
        ArrayList<Vertex> newVertices = new ArrayList<>(vertices);
        ArrayList<Edge> newEdges = new ArrayList<>(edges);
        for (Vertex vertex : g.vertices)
            if (!newVertices.contains(vertex)) newVertices.remove(vertex);
        for (Edge edge : g.edges)
            if (!newEdges.contains(edge)) newEdges.remove(edge);
        return new Graph(newVertices, newEdges);
    }

    public int degree(int v) {
        return Arrays.stream(adjacencyMatrix[v]).sum();
    }

    public boolean regular() {
        for (int k = 0; k < vertices.size(); k++)
            if (regular(k)) return true;
        return false;
    }

    public boolean regular(int k) {
        for (Vertex vertex : vertices)
            if (degree(vertex.getId()) != k) return false;
        return true;
    }

    public boolean connected(int v1, int v2) {
        return Dijkstra.bfs(this, getVertexById(v1), getVertexById(v2)) >= 0;
    }

    public int distance(int v1, int v2) {
        return Dijkstra.distance(this, getVertexById(v1), getVertexById(v2));
    }

    public int diameter() {
        return Dijkstra.diameter(this);
    }

    public int girth() {
        return Dijkstra.girth(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Graph g) {
            return g.vertices.equals(vertices) && g.edges.equals(edges);
        }
        return false;
    }

}
