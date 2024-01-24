package graph;

public class Edge {

    private static int ID_COUNTER = 0;
    private final int id;
    private final Vertex v1;
    private final Vertex v2;
    private final int weight;

    public Edge(Vertex v1, Vertex v2) {
        this.id = ++ID_COUNTER;
        this.v1 = v1;
        this.v2 = v2;
        this.weight = 1;
    }

    public Edge(Vertex v1, Vertex v2, int weight) {
        this.id = ++ID_COUNTER;
        this.v1 = v1;
        this.v2 = v2;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public Vertex getV1() {
        return v1;
    }

    public Vertex getV2() {
        return v2;
    }

    public int getWeight() {
        return weight;
    }

    public boolean isLoop() {
        return v1.getId() == v2.getId();
    }

    public boolean isLink() {
        return v1.getId() != v2.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Edge e) {
            return id == e.getId() &&
                    v1.equals(e.getV1()) &&
                    v2.equals(e.getV2()) &&
                    weight == e.getWeight();
        }
        return false;
    }

}
