package tfl_v1;

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


    // SETUP METHODS (Used for loading CSV data)

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
        return null; // Station not found
    }

    public void addNewTrack(String startStationName, String targetStationName, String lineName, String direction, double travelTime) {
        Station startStation = getStation(startStationName);
        Station targetStation = getStation(targetStationName);

        if (startStation != null && targetStation != null) {
            Connection newTrack = new Connection(targetStation, lineName, direction, travelTime);
            startStation.addConnection(newTrack);
        }
    }


    public void addNewInterchange(String stationName, String fromLine, String toLine, double time) {
        Station station = getStation(stationName);
        if (station != null) {
            station.addInterchange(new Interchange(fromLine, toLine, time));
        }
    }


    // ENGINEER METHODS

    public void addDelayToTrack(String startStationName, String targetStationName, double delayMinutes) {
        Station startStation = getStation(startStationName);

        if (startStation != null) {
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

        System.out.println("\n=======================================================");
        System.out.println("   LIVE DEPARTURES: " + foundStation.getName().toUpperCase());
        System.out.println("=======================================================");

        if (foundStation.getConnectionCount() == 0) {
            System.out.println("No outgoing tracks found for this station.");
            return;
        }

        // Loop through every track and simulate a live departure screen
        for (int i = 0; i < foundStation.getConnectionCount(); i++) {
            Connection track = foundStation.getConnections()[i];

            String line = track.getLineName();
            String dest = "to " + track.getDestination().getName();

            // Check if Engineers closed the track
            if (!track.isOpen()) {
                System.out.printf("%-12s %-25s %s%n", line, dest, "CLOSED - SEE STAFF");
                continue;
            }

            // Check if there are severe delays
            if (track.getDelayTime() > 5.0) {
                System.out.printf("%-12s %-25s %s%n", line, dest, "SEVERE DELAYS");
                continue;
            }

            // Simulate realistic live train arrivals based on the current time
            // Train 1: 0 to 2 mins away. If 0, we print "Due"
            int train1 = (int)(Math.random() * 3);
            // Train 2: 2 to 5 mins after Train 1
            int train2 = train1 + (int)(Math.random() * 4) + 2;
            // Train 3: 3 to 7 mins after Train 2
            int train3 = train2 + (int)(Math.random() * 5) + 3;

            String t1Display = (train1 == 0) ? "Due" : train1 + " min";

            // Print the beautifully formatted row
            System.out.printf("%-12s %-25s %s, %d min, %d min%n", line, dest, t1Display, train2, train3);
        }
        System.out.println("=======================================================");
    }


    public void displayStationsOnLine(String searchLineName) {
        System.out.println("\n--- Stations on the " + searchLineName + " Line ---");
        boolean foundAny = false;

        for (int i = 0; i < totalStations; i++) {
            Station currentStation = stationList[i];
            boolean isOnLine = false;

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

        // Parallel arrays for Dijkstra's Algorithm
        double[] shortestTimes = new double[totalStations];
        boolean[] hasBeenVisited = new boolean[totalStations];
        Station[] previousStation = new Station[totalStations];
        String[] previousLineName = new String[totalStations];

        for (int i = 0; i < totalStations; i++) {
            shortestTimes[i] = Double.MAX_VALUE;
            hasBeenVisited[i] = false;
        }

        int startIndex = getStationIndex(startStationName);
        shortestTimes[startIndex] = 0.0;
        previousLineName[startIndex] = "";

        // The core search loop (Dijkstra's Algorithm)
        for (int i = 0; i < totalStations; i++) {

            int currentStationIndex = getShortestUnvisitedIndex(shortestTimes, hasBeenVisited);

            if (currentStationIndex == -1 || shortestTimes[currentStationIndex] == Double.MAX_VALUE) {
                break;
            }

            hasBeenVisited[currentStationIndex] = true;
            Station currentStation = stationList[currentStationIndex];

            // Check all tracks leaving this current station
            for (int j = 0; j < currentStation.getConnectionCount(); j++) {
                Connection track = currentStation.getConnections()[j];

                if (track.isOpen() == false) {
                    continue;
                }

                int neighborIndex = getStationIndex(track.getDestination().getName());

                // Dynamic interchange logic (using your CSV data)
                String lineWeArrivedOn = previousLineName[currentStationIndex];
                double walkingPenalty = currentStation.getInterchangeTime(lineWeArrivedOn, track.getLineName());

                // Calculate total time to reach this neighbor
                double timeToReachNeighbor = shortestTimes[currentStationIndex] + track.getTotalTime() + walkingPenalty;

                // If we found a faster way, update our records
                if (hasBeenVisited[neighborIndex] == false && timeToReachNeighbor < shortestTimes[neighborIndex]) {
                    shortestTimes[neighborIndex] = timeToReachNeighbor;
                    previousStation[neighborIndex] = currentStation;
                    previousLineName[neighborIndex] = track.getLineName();
                }
            }
        }

        // ==========================================
        // NEW: PRINT THE STEP-BY-STEP ITINERARY
        // ==========================================
        int targetIndex = getStationIndex(targetStationName);

        if (shortestTimes[targetIndex] == Double.MAX_VALUE) {
            System.out.println("No route found! Tracks might be closed.");
        } else {
            System.out.println("\n--- Journey Itinerary ---");

            // 1. Create temporary hand-coded arrays to hold the path backwards
            Station[] backwardsPath = new Station[totalStations];
            String[] backwardsLines = new String[totalStations];
            int stepCount = 0;

            // 2. Trace the breadcrumbs backwards from Target to Start
            int traceIndex = targetIndex;
            while (traceIndex != startIndex) {
                backwardsPath[stepCount] = stationList[traceIndex];
                backwardsLines[stepCount] = previousLineName[traceIndex];

                traceIndex = getStationIndex(previousStation[traceIndex].getName());
                stepCount++;
            }
            // Add the start station as the final backward step
            backwardsPath[stepCount] = stationList[startIndex];
            backwardsLines[stepCount] = "";
            stepCount++;

            // 3. Print the path forwards (by looping backwards through our temporary array)
            System.out.println("[Start]  " + backwardsPath[stepCount - 1].getName());

            String currentLine = backwardsLines[stepCount - 2];
            System.out.println("   |     Take the " + currentLine + " Line");

            for (int i = stepCount - 2; i > 0; i--) {
                Station currentStop = backwardsPath[i];
                String nextLine = backwardsLines[i - 1];

                // Did the line change at this stop? If so, print an interchange!
                if (!nextLine.equals(currentLine)) {
                    System.out.println("[Change] " + currentStop.getName());
                    System.out.println("   |     Walk to the " + nextLine + " Line");
                    System.out.println("   |     Take the " + nextLine + " Line");
                    currentLine = nextLine;
                }
            }

            System.out.println("[Arrive] " + backwardsPath[0].getName());
            System.out.println("\nTotal Journey Time: " + String.format("%.0f", shortestTimes[targetIndex]) + " minutes");
            System.out.println("-------------------------");
        }
    }

    // HELPER METHODS

    private int getStationIndex(String name) {
        for (int i = 0; i < totalStations; i++) {
            if (stationList[i].getName().equalsIgnoreCase(name)) return i;
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

    private String calculateArrivalTime(String departureTime, double totalMinutes) {
        try {
            String[] parts = departureTime.split(":");
            if (parts.length != 2) return "Unknown";

            int hours = Integer.parseInt(parts[0].trim());
            int minutes = Integer.parseInt(parts[1].trim());

            minutes += (int) Math.round(totalMinutes);
            hours += (minutes / 60);
            minutes = minutes % 60;
            hours = hours % 24;

            String finalHour = (hours < 10) ? "0" + hours : String.valueOf(hours);
            String finalMin = (minutes < 10) ? "0" + minutes : String.valueOf(minutes);

            return finalHour + ":" + finalMin;
        } catch (Exception e) {
            return "Unknown";
        }
    }
}