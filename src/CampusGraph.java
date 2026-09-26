import java.util.*;

/**
 * CampusGraph.java
 * Author: Jawahidu Fathima Rifna - 23da-0842
 * MEMBER 4 RESPONSIBILITY: Graph implementation, campus locations,
 * connections, and BFS/DFS traversal.
 *
 * Requirements 7-11:
 *  - Represents campus locations as vertices and roads/paths as edges.
 *  - Uses an ADJACENCY LIST (a Map<location, List<neighbours>>).
 *  - Supports adding/removing locations and connections.
 *  - Displays neighbours / the whole network.
 *  - Supports both BFS and DFS traversal.
 *
 * The graph is undirected (a road connects both ways) and unweighted.
 */
public class CampusGraph {

    // adjacency list: location name -> set of directly connected locations
    private final Map<String, LinkedHashSet<String>> adjList = new LinkedHashMap<>();

    /** Requirement (menu item 10): Add a campus location (vertex). */
    public boolean addLocation(String location) {
        if (adjList.containsKey(location)) {
            System.out.println("ERROR: Location '" + location + "' already exists.");
            return false;
        }
        adjList.put(location, new LinkedHashSet<>());
        return true;
    }

    /** Requirement (menu item 11): Remove a campus location and all its connections. */
    public boolean removeLocation(String location) {
        if (!adjList.containsKey(location)) {
            System.out.println("ERROR: Location '" + location + "' does not exist.");
            return false;
        }
        adjList.remove(location);
        // remove this location from every other location's neighbour set
        for (LinkedHashSet<String> neighbours : adjList.values()) {
            neighbours.remove(location);
        }
        return true;
    }

    /** Requirement (menu item 12): Add a road/connection (edge) between two locations. */
    public boolean addConnection(String locA, String locB) {
        if (!adjList.containsKey(locA) || !adjList.containsKey(locB)) {
            System.out.println("ERROR: Both locations must exist before connecting them.");
            return false;
        }
        if (locA.equals(locB)) {
            System.out.println("ERROR: A location cannot connect to itself.");
            return false;
        }
        boolean added1 = adjList.get(locA).add(locB);
        boolean added2 = adjList.get(locB).add(locA);
        if (!added1 && !added2) {
            System.out.println("ERROR: Connection already exists between '" + locA + "' and '" + locB + "'.");
            return false;
        }
        return true;
    }

    /** Requirement (menu item 13): Remove a road/connection (edge). */
    public boolean removeConnection(String locA, String locB) {
        if (!adjList.containsKey(locA) || !adjList.containsKey(locB)) {
            System.out.println("ERROR: One or both locations do not exist.");
            return false;
        }
        boolean removed1 = adjList.get(locA).remove(locB);
        adjList.get(locB).remove(locA);
        if (!removed1) {
            System.out.println("ERROR: No connection exists between '" + locA + "' and '" + locB + "'.");
            return false;
        }
        return true;
    }

    /** Requirement (menu item 14): Display the whole campus network / neighbours of each location. */
    public void displayConnections() {
        if (adjList.isEmpty()) {
            System.out.println("No campus locations added yet.");
            return;
        }
        System.out.println("---- Campus Network (Adjacency List) ----");
        for (String location : adjList.keySet()) {
            System.out.println(location + " -> " + adjList.get(location));
        }
    }

    /** Requirement (menu item 15a): Breadth-First Search traversal from a start location. */
    public List<String> bfs(String start) {
        List<String> order = new ArrayList<>();
        if (!adjList.containsKey(start)) {
            System.out.println("ERROR: Starting location '" + start + "' does not exist.");
            return order;
        }
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty()) {
            String current = queue.poll();
            order.add(current);
            for (String neighbour : adjList.get(current)) {
                if (!visited.contains(neighbour)) {
                    visited.add(neighbour);
                    queue.add(neighbour);
                }
            }
        }
        return order;
    }

    /** Requirement (menu item 15b): Depth-First Search traversal from a start location. */
    public List<String> dfs(String start) {
        List<String> order = new ArrayList<>();
        if (!adjList.containsKey(start)) {
            System.out.println("ERROR: Starting location '" + start + "' does not exist.");
            return order;
        }
        Set<String> visited = new HashSet<>();
        dfsHelper(start, visited, order);
        return order;
    }

    private void dfsHelper(String current, Set<String> visited, List<String> order) {
        visited.add(current);
        order.add(current);
        for (String neighbour : adjList.get(current)) {
            if (!visited.contains(neighbour)) {
                dfsHelper(neighbour, visited, order);
            }
        }
    }

    public boolean hasLocation(String location) {
        return adjList.containsKey(location);
    }

    public Set<String> getAllLocations() {
        return adjList.keySet();
    }
}