import java.util.*;

class StepData
{
    public final int[][] valuesTable;
    public final int[] visitedNodes;
    public final int currentNode;
    public final int lowestNode;
    public final boolean[] neighborsToVisit;
    public final boolean[] smallerNeighbors;
    public final int[] neighbors;

    public StepData(int[][] valuesTable, int[] visitedNodes, int currentNode, int lowestNode, boolean[] neighborsToVisit, boolean[] smallerNeighbors, int[] neighbors)
    {
        this.valuesTable = valuesTable;
        this.visitedNodes = visitedNodes;
        this.currentNode = currentNode;
        this.lowestNode = lowestNode;
        this.neighborsToVisit = neighborsToVisit;
        this.smallerNeighbors = smallerNeighbors;
        this.neighbors = neighbors;
    }
}

public class Dijkstra // A graph ADT that computes the shortest path using Dijkstra's Algorithm
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

    public ArrayList<StepData> shortestPath(int source)
    {
        ArrayList<StepData> steps = new ArrayList<>();
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
            values[i][1] = i;
        }

        int current = source;
        while(visited.size() != adj.size())
        {
            boolean[] neighborsToVisit = new boolean[adj.get(current).size()];
            boolean[] smallerNeighbors = new boolean[adj.get(current).size()];
            // visit all unvisited neighborsToVisit of the current node
            LinkedList<Edge> edges = adj.get(current);
            for(int i = 0; i < edges.size(); i++)
            {
                int cost = edges.get(i).distance + values[current][0]; // add the cost to reach the destination and the cost to reach the current vertex
                if(!visited.contains(edges.get(i).destination))
                {
                    if(cost < values[edges.get(i).destination][0]) // if that value is less then the cost of reaching that neighbor from start then replace
                    {
                        values[edges.get(i).destination][0] = cost;
                        values[edges.get(i).destination][1] = current;
                        smallerNeighbors[i] = true;
                    }
                    else
                    {
                        smallerNeighbors[i] = false;
                    }
                    neighborsToVisit[i] = true;
                }
                else
                {
                    neighborsToVisit[i] = false;
                }
            }
            visited.add(current);

            // find the neighbor with lowest cost then assign it to the current.
            int lowestIndex = 0;
            int lowestDistance = Integer.MAX_VALUE;
            for(Edge edge : adj.get(current))
            {
                if(!visited.contains(edge.destination) && values[edge.destination][0] < lowestDistance)
                {
                    lowestIndex = edge.destination;
                    lowestDistance = values[edge.destination][0];
                }
            }
            int[][] stepValue = new int[values.length][];
            for(int i = 0; i < stepValue.length; i++)
            {
                stepValue[i] = values[i].clone();
            }

            // create array of neighbors for step data
            int[] neighbors = new int[edges.size()];
            for(int i = 0; i < neighbors.length; i++)
            {
                neighbors[i] = edges.get(i).destination;
            }
            steps.add(new StepData(stepValue, Arrays.stream(visited.toArray()).mapToInt(i -> (int) i).toArray(), current, lowestIndex, neighborsToVisit, smallerNeighbors, neighbors));
            current = lowestIndex;
        }
        return steps;
    }
}

class Edge
{
    public int destination;
    public int distance;
}
