package tfl_v1;

/**
 * This class represents a physical TfL station.
 */
public class Station {

    private String name;

    // Our hand-coded data structure (Standard Array)
    private Connection[] connections;
    private int connectionCount;      // Keeps track of how many tracks are currently attached

    /**
     * Constructor: Creates a new station with a starting array size.
     */
    public Station(String name) {
        this.name = name;
        this.connections = new Connection[2];
        this.connectionCount = 0;
    }

    /**
     * Adds a new track to this station.
     * Auto growing array
     */
    public void addConnection(Connection newConnection) {
        if (connectionCount == connections.length) {

            Connection[] biggerArray = new Connection[connections.length * 2];

            for (int i = 0; i < connections.length; i++) {
                biggerArray[i] = connections[i];
            }

            this.connections = biggerArray;
        }

        // 5. Safely add the new connection to the array
        this.connections[connectionCount] = newConnection;
        this.connectionCount++;
    }


    public String getName() {
        return name;
    }

    public Connection[] getConnections() {
        return connections;
    }

    public int getConnectionCount() {
        return connectionCount;
    }
}