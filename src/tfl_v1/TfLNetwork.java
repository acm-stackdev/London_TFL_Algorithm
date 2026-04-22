package tfl;

/**
 * The master class that manages the entire TfL network.
 * It holds the list of stations and contains the logic for Engineer and Customer actions.
 */
public class TfLNetwork {

    private Station[] stationList;
    private int totalStations;

    public TfLNetwork() {
        this.stationList = new Station[5]; // Starts small, grows automatically
        this.totalStations = 0;
    }

    // ==========================================
    // SETUP METHODS (Used for loading CSV data)
    // ==========================================

    public void addStation(String name) {
        // If the array is full, make a bigger one (Dynamic Array Logic)
        if (totalStations == stationList.length) {
            Station[] biggerArray = new Station[stationList.length * 2];
            for (int i = 0; i < stationList.length; i++) {
                biggerArray[i] = stationList[i];
            }
            stationList = biggerArray;
        }

        stationList[totalStations] = new Station(name);
        totalStations++;
    }

    public Station getStation(String name) {
        for (int i = 0; i < totalStations; i++) {
            if (stationList[i].getName().equalsIgnoreCase(name)) {
                return stationList[i];
            }
        }
        return null;
    }

    public void addNewTrack(String startStationName, String targetStationName, String lineName, String direction, double travelTime) {
        Station startStation = getStation(startStationName);
        Station targetStation = getStation(targetStationName);

        if (startStation != null && targetStation != null) {
            Connection newTrack = new Connection(targetStation, lineName, direction, travelTime);
            startStation.addConnection(newTrack);
        }
    }

    // ENGINEER METHODS

    public void addDelayToTrack(String startStationName, String targetStationName, double delayMinutes) {
        Station startStation = getStation(startStationName);

        if (startStation != null) {
            // Check all tracks leaving the start station
            for (int i = 0; i < startStation.getConnectionCount(); i++) {
                Connection track = startStation.getConnections()[i];

                if (track.getDestination().getName().equalsIgnoreCase(targetStationName)) {
                    track.setDelayTime(delayMinutes);
                    System.out.println("Success: Added " + delayMinutes + " min delay from " + startStationName + " to " + targetStationName);
                    return;
                }
            }
        }
        System.out.println("Error: Track not found.");
    }

    public void openOrCloseTrack(String startStationName, String targetStationName, boolean isOpen) {
        Station startStation = getStation(startStationName);

        if (startStation != null) {
            for (int i = 0; i < startStation.getConnectionCount(); i++) {
                Connection track = startStation.getConnections()[i];

                if (track.getDestination().getName().equalsIgnoreCase(targetStationName)) {
                    track.setOpenStatus(isOpen);
                    String statusMessage = isOpen ? "Opened" : "Closed";
                    System.out.println("Success: Track from " + startStationName + " to " + targetStationName + " is now " + statusMessage);
                    return;
                }
            }
        }
        System.out.println("Error: Track not found.");
    }

    public void printNetworkStatus() {
        System.out.println("\n--- TFL NETWORK STATUS REPORT ---");

        System.out.println("\n[CLOSED TRACK SECTIONS]");
        boolean foundClosed = false;
        for (int i = 0; i < totalStations; i++) {
            Station s = stationList[i];
            for (int j = 0; j < s.getConnectionCount(); j++) {
                Connection c = s.getConnections()[j];
                if (!c.isOpen()) {
                    System.out.println(c.getLineName() + " (" + c.getDirection() + "): " + s.getName() + " to " + c.getDestination().getName() + " - CLOSED");
                    foundClosed = true;
                }
            }
        }
        if (!foundClosed) System.out.println("All tracks are open.");

        System.out.println("\n[DELAYED TRACK SECTIONS]");
        boolean foundDelayed = false;
        for (int i = 0; i < totalStations; i++) {
            Station s = stationList[i];
            for (int j = 0; j < s.getConnectionCount(); j++) {
                Connection c = s.getConnections()[j];
                if (c.getDelayTime() > 0) {
                    System.out.println(c.getLineName() + " (" + c.getDirection() + "): " + s.getName() + " to " + c.getDestination().getName() + " - " + c.getDelayTime() + " min delay");
                    foundDelayed = true;
                }
            }
        }
        if (!foundDelayed) System.out.println("No delays on the network.");
        System.out.println("---------------------------------");
    }

    // CUSTOMER METHODS (Search & Filter)

    public void displayStationInformation(String stationName) {
        Station foundStation = getStation(stationName);

        if (foundStation == null) {
            System.out.println("Error: Could not find a station named '" + stationName + "'.");
            return;
        }

        System.out.println("\n--- Station Information ---");
        System.out.println("Name: " + foundStation.getName());
        System.out.println("Total Direct Connections: " + foundStation.getConnectionCount());
        System.out.println("Available Lines & Destinations:");

        for (int i = 0; i < foundStation.getConnectionCount(); i++) {
            Connection track = foundStation.getConnections()[i];
            String dest = track.getDestination().getName();
            String line = track.getLineName();
            String dir = track.getDirection();
            double time = track.getTotalTime();
            String status = track.isOpen() ? "Normal" : "CLOSED";

            System.out.println("  -> " + dest + " | Line: " + line + " (" + dir + ") | Time: " + time + " mins | Status: " + status);
        }
        System.out.println("---------------------------");
    }

    public void displayStationsOnLine(String searchLineName) {
        System.out.println("\n--- Stations on the " + searchLineName + " Line ---");
        boolean foundAny = false;

        for (int i = 0; i < totalStations; i++) {
            Station currentStation = stationList[i];
            boolean isOnLine = false;

            // Check if any tracks leaving this station belong to the requested line
            for (int j = 0; j < currentStation.getConnectionCount(); j++) {
                if (currentStation.getConnections()[j].getLineName().equalsIgnoreCase(searchLineName)) {
                    isOnLine = true;
                    break;
                }
            }

            if (isOnLine) {
                System.out.println("- " + currentStation.getName());
                foundAny = true;
            }
        }

        if (!foundAny) {
            System.out.println("No stations found for line: " + searchLineName);
        }
    }

    // CUSTOMER METHOD (Find Fastest Route)

    public void findFastestRoute(String startStationName, String targetStationName) {
        Station startNode = getStation(startStationName);
        Station targetNode = getStation(targetStationName);

        if (startNode == null || targetNode == null) {
            System.out.println("Error: Invalid start or end station.");
            return;
        }

        // Parallel arrays to track the route finding status (Replaces HashMap/PriorityQueue)
        double[] shortestTimes = new double[totalStations];
        boolean[] hasBeenVisited = new boolean[totalStations];
        Station[] previousStation = new Station[totalStations];
        String[] previousLineName = new String[totalStations];

        // Initialize default values for the search
        for (int i = 0; i < totalStations; i++) {
            shortestTimes[i] = Double.MAX_VALUE; // Represents "Infinity"
            hasBeenVisited[i] = false;
        }

        // Set the starting station time to 0
        int startIndex = getStationIndex(startStationName);
        shortestTimes[startIndex] = 0.0;
        previousLineName[startIndex] = "";

        // The core search loop (Greedy Algorithm / Dijkstra's)
        for (int i = 0; i < totalStations; i++) {

            // 1. Find the unvisited station with the shortest time so far
            int currentStationIndex = getShortestUnvisitedIndex(shortestTimes, hasBeenVisited);

            // If we can't find a station, or the remaining ones are unreachable, stop searching
            if (currentStationIndex == -1 || shortestTimes[currentStationIndex] == Double.MAX_VALUE) {
                break;
            }

            hasBeenVisited[currentStationIndex] = true;
            Station currentStation = stationList[currentStationIndex];

            // 2. Check all tracks leaving this current station
            for (int j = 0; j < currentStation.getConnectionCount(); j++) {
                Connection track = currentStation.getConnections()[j];

                // CRITICAL: Skip this track if an Engineer closed it!
                if (!track.isOpen()) {
                    continue;
                }

                int neighborIndex = getStationIndex(track.getDestination().getName());

                // Calculate time penalty for changing lines (2 minutes)
                double lineChangePenalty = 0.0;
                String lineWeArrivedOn = previousLineName[currentStationIndex];

                if (!lineWeArrivedOn.isEmpty() && !lineWeArrivedOn.equals(track.getLineName())) {
                    lineChangePenalty = 2.0;
                }

                // Calculate total time to reach this neighbor
                double timeToReachNeighbor = shortestTimes[currentStationIndex] + track.getTotalTime() + lineChangePenalty;

                // If we found a faster way to reach the neighbor, update our records
                if (!hasBeenVisited[neighborIndex] && timeToReachNeighbor < shortestTimes[neighborIndex]) {
                    shortestTimes[neighborIndex] = timeToReachNeighbor;
                    previousStation[neighborIndex] = currentStation;
                    previousLineName[neighborIndex] = track.getLineName();
                }
            }
        }

        // Print the final result
        int targetIndex = getStationIndex(targetStationName);
        if (shortestTimes[targetIndex] == Double.MAX_VALUE) {
            System.out.println("No route found! Tracks might be closed.");
        } else {
            System.out.println("Total Journey Time: " + String.format("%.2f", shortestTimes[targetIndex]) + " minutes.");
            // NOTE: The exact station-by-station printout will go here for full marks.
        }
    }

    // HELPER METHODS (To keep code clean)

    private int getStationIndex(String name) {
        for (int i = 0; i < totalStations; i++) {
            if (stationList[i].getName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    private int getShortestUnvisitedIndex(double[] shortestTimes, boolean[] hasBeenVisited) {
        double minTime = Double.MAX_VALUE;
        int minIndex = -1;

        for (int i = 0; i < totalStations; i++) {
            if (!hasBeenVisited[i] && shortestTimes[i] <= minTime) {
                minTime = shortestTimes[i];
                minIndex = i;
            }
        }
        return minIndex;
    }
}