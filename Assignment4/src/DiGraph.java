import java.util.*;

public class DiGraph {
    int vertices;
    List<List<Edge>> adjacencyList;

    public DiGraph(int vertices) {
        this.vertices = vertices;
        adjacencyList = new ArrayList<>(vertices);
        for (int i = 0; i < vertices; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    public void addEdge(int start, int end, double weight) {
        Edge edge = new Edge(start, end, weight);
        adjacencyList.get(start).add(edge);
    }

    public List<Integer> dijkstra(int start, int end) {
        double[] distances = new double[vertices];
        boolean[] visited = new boolean[vertices];
        int[] previous = new int[vertices];

        for (int i = 0; i < vertices; i++) {
            distances[i]=Integer.MAX_VALUE;
        }
        for (int i = 0; i < vertices; i++) {
            previous[i]=-1;
        }
        distances[start] = 0;

        PriorityQueue<Edge> priorityQueue = new PriorityQueue<Edge>(vertices, Comparator.comparingDouble(edge -> edge.weight));

        priorityQueue.add(new Edge(start, start, 0));

        while (!priorityQueue.isEmpty()) {
            Edge tempE = priorityQueue.poll();
            int current = tempE.end;

            if (visited[current]) {
                continue;
            }

            visited[current] = true;

            for (Edge neighbor : adjacencyList.get(current)) {
                int neighborVertex = neighbor.end;
                double newDistance = distances[current] + neighbor.weight;

                if (newDistance < distances[neighborVertex]) {
                    distances[neighborVertex] = newDistance;
                    previous[neighborVertex] = current;
                    priorityQueue.add(new Edge(neighborVertex, neighborVertex, newDistance));
                }
            }
        }

        // using previous build the path
        List<Integer> path = new ArrayList<>();
        int pathElement = end;

        while (pathElement != -1) {
            path.add(pathElement);
            pathElement = previous[pathElement];
        }

        Collections.reverse(path);
        if (path.get(0) == start) {
            return path;
        }
        else {
            return Collections.emptyList();
        }
    }
}
