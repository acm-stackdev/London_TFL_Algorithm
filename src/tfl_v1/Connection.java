package tfl_v1;

/**
 * This class represents a single, one-way track between two stations.
 * It holds the travel time, the line name, and engineer statuses (delays/closures).
 */
public class Connection {

    private Station destination;
    private String lineName;
    private String direction;

    private double normalTime;
    private double delayTime;
    private boolean isOpen;


    public Connection(Station destination, String lineName, String direction, double normalTime) {
        this.destination = destination;
        this.lineName = lineName;
        this.direction = direction;
        this.normalTime = normalTime;

        this.delayTime = 0.0;
        this.isOpen = true;
    }

    // CUSTOMER METHODS

    /**
     * Calculates the total time it takes to travel this track right now.
     * Route-finding algorithm uses this.
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

    public double getNormalTime() {
        return normalTime;
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