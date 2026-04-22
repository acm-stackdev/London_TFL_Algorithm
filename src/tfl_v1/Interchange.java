package tfl_v1;

/**
 * Represents the time it takes to walk between two different lines at a specific station.
 */
public class Interchange {
    private String fromLine;
    private String toLine;
    private double walkTime;

    public Interchange(String fromLine, String toLine, double walkTime) {
        this.fromLine = fromLine;
        this.toLine = toLine;
        this.walkTime = walkTime;
    }

    public String getFromLine() {
        return fromLine;
    }
    public String getToLine() {
        return toLine;
    }
    public double getWalkTime() {
        return walkTime;
    }
}