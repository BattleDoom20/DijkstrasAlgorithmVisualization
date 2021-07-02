import java.util.*;

public class Dijkstra
{
    LinkedList<LinkedList<Edge>> adj;

    public Dijkstra(int nodeCount)
    {
        adj = new LinkedList<>();
        for(int i = 0; i < nodeCount; i++)
        {
            adj.add(new LinkedList<>());
        }
    }

    public void addEdge(int source, int destination, int distance)
    {
        Edge edge = new Edge();
        edge.destination = destination;
        edge.distance = distance;
        adj.get(source).add(edge);

        edge = new Edge();
        edge.destination = source;
        edge.distance = distance;
        adj.get(destination).add(edge);
    }

    public int[][] shortestPath(int source)
    {
        LinkedList<Integer> visited = new LinkedList<>();
        int[][] values = new int[adj.size()][2]; // this will contain the vertex with its cost to reach starting from the source and the vertex that is reached before it. Rows = vertices, columns = cost and previous vertex.
        for(int i = 0; i < values.length; i++)
        {
            if(i == source) // if the vertex is the source, initialize it to 0 cost and previous to itself. else, set the cost to infinity (at this case Integer.MAX_VALUE).
            {
                values[i][0] = 0;
                values[i][1] = source;
                continue;
            }
            values[i][0] = Integer.MAX_VALUE;
        }

        int current = source;
        while(visited.size() != adj.size())
        {
            // visit all unvisited neighbors of the current node
            for(Edge edge : adj.get(current))
            {
                int cost = edge.distance + values[current][0]; // add the cost to reach the destination and the cost to reach the current vertex
                if(!visited.contains(edge.destination) && cost < values[edge.destination][0]) // if that value is less then the cost of
                {
                    values[edge.destination][0] = cost;
                    values[edge.destination][1] = current;
                }
            }
            visited.add(current);

            // find the neighbor with lowest cost then assign it to the current.
            int lowestIndex = 0;
            int lowestDistance = Integer.MAX_VALUE;
            for(Edge edge : adj.get(current))
            {
                if(!visited.contains(edge.destination) && edge.distance < lowestDistance)
                {
                    lowestIndex = edge.destination;
                    lowestDistance = edge.distance;
                }
            }
            current = lowestIndex;
        }
        return values;
    }
}

class Edge
{
    public int destination;
    public int distance;
}
