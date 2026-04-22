package tfl_v1;

/**
 * This class represents a physical TfL station.
 * It uses custom auto-growing arrays to store all tracks (connections) leaving this station,
 * as well as the specific walking times required to change lines (interchanges).
 */
public class Station {

    private String name;

    private Connection[] connections;
    private int connectionCount;


    private Interchange[] interchanges;
    private int interchangeCount;

    public Station(String name) {
        this.name = name;
        this.connections = new Connection[2];
        this.connectionCount = 0;

        this.interchanges = new Interchange[2];
        this.interchangeCount = 0;
    }

    // CONNECTION LOGIC (Tracks)
    //Adds a new track to this station
    public void addConnection(Connection newConnection) {
        if (connectionCount == connections.length) {
            Connection[] biggerArray = new Connection[connections.length * 2];
            for (int i = 0; i < connections.length; i++) {
                biggerArray[i] = connections[i];
            }
            this.connections = biggerArray;
        }

        this.connections[connectionCount] = newConnection;
        this.connectionCount++;
    }

    // ==========================================
    // INTERCHANGE LOGIC (Line Changes)
    // ==========================================

    /**
     * Adds a new line-change walking time to this station.
     * Uses the exact same auto-growing array logic.
     */
    public void addInterchange(Interchange newInterchange) {
        if (interchangeCount == interchanges.length) {
            Interchange[] biggerArray = new Interchange[interchanges.length * 2];
            for (int i = 0; i < interchanges.length; i++) {
                biggerArray[i] = interchanges[i];
            }
            this.interchanges = biggerArray;
        }

        this.interchanges[interchangeCount] = newInterchange;
        this.interchangeCount++;
    }

    /**
     * Calculates how long it takes a passenger to walk from one line to another.
     */
    public double getInterchangeTime(String fromLine, String toLine) {
        // If it is the start of the journey, or they are staying on the same line, no extra walking time!
        if (fromLine.isEmpty() || fromLine.equalsIgnoreCase(toLine)) {
            return 0.0;
        }

        // Look through our loaded CSV data for this specific line change
        for (int i = 0; i < interchangeCount; i++) {
            Interchange ic = interchanges[i];

            if (ic.getFromLine().equalsIgnoreCase(fromLine) && ic.getToLine().equalsIgnoreCase(toLine)) {
                return ic.getWalkTime();
            }
        }

        // Fallback: If the lines are different but we forgot to put them in the CSV, default to 2 minutes
        return 2.0;
    }

    // GETTERS METHOD

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