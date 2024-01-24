package graph;

public class Vertex {

    private static int ID_COUNTER = 0;
    private final int id;

    public Vertex() {
        this.id = ++ID_COUNTER;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Vertex v) {
            return id == v.getId();
        }
        return false;
    }

}
