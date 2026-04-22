package tfl_v1;

/**
 * This class represents a single, one-way track between two stations.
 * It holds the travel time, the line name, and engineer statuses (delays/closures).
 */
public class Connection {

    // Core attributes for the route
    private Station destination;
    private String lineName;
    private String direction;

    // Time and Status attributes
    private double normalTime;
    private double delayTime;
    private boolean isOpen;

    /**
     * Constructor: Creates a new track.
     * By default, a new track has 0 delay and is open.
     */
    public Connection(Station destination, String lineName, String direction, double normalTime) {
        this.destination = destination;
        this.lineName = lineName;
        this.direction = direction;
        this.normalTime = normalTime;

        // Default engineer statuses
        this.delayTime = 0.0;
        this.isOpen = true;
    }

    // CUSTOMER METHODS

    /**
     * Calculates the total time it takes to travel this track right now.
     * Route-finding algorithm will use!
     */
    public double getTotalTime() {
        return normalTime + delayTime;
    }

    public Station getDestination() {
        return destination;
    }

    public String getLineName() {
        return lineName;
    }

    public String getDirection() {
        return direction;
    }


    public void setDelayTime(double delayMinutes) {
        this.delayTime = delayMinutes;
    }

    public double getDelayTime() {
        return this.delayTime;
    }

    public void setOpenStatus(boolean status) {
        this.isOpen = status;
    }

    public boolean isOpen() {
        return isOpen;
    }
}