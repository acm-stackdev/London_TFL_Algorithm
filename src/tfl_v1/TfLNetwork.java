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
        // Linear Search
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

    public void addNewInterchange(String stationName, String fromLine, String toLine, double time) {
        Station station = getStation(stationName);
        if (station != null) {
            station.addInterchange(new Interchange(fromLine, toLine, time));
        }
    }

    // ==========================================
    // ENGINEER METHODS
    // ==========================================

    public void addDelayToTrack(String startStationName, String targetStationName, double delayMinutes) {
        Station startStation = getStation(startStationName);
        Station targetStation = getStation(targetStationName);

        if (startStation == null) {
            System.out.println("Error: Start station '" + startStationName + "' does not exist in the network.");
            return;
        }
        if (targetStation == null) {
            System.out.println("Error: Target station '" + targetStationName + "' does not exist in the network.");
            return;
        }

        for (int i = 0; i < startStation.getConnectionCount(); i++) {
            Connection track = startStation.getConnections()[i];
            if (track.getDestination().getName().equalsIgnoreCase(targetStationName)) {
                track.setDelayTime(delayMinutes);
                System.out.println("Success: Added " + delayMinutes + " min delay from "
                        + startStation.getName() + " to " + targetStation.getName() + ".");
                return;
            }
        }

        System.out.println("Error: No direct route available between "
                + startStation.getName() + " and " + targetStation.getName() + ".");
    }

    /**
     * Removes (clears) any existing delay on a track between two stations.
     */
    public void removeDelayFromTrack(String startStationName, String targetStationName) {
        Station startStation = getStation(startStationName);
        Station targetStation = getStation(targetStationName);

        if (startStation == null) {
            System.out.println("Error: Start station '" + startStationName + "' does not exist in the network.");
            return;
        }
        if (targetStation == null) {
            System.out.println("Error: Target station '" + targetStationName + "' does not exist in the network.");
            return;
        }

        for (int i = 0; i < startStation.getConnectionCount(); i++) {
            Connection track = startStation.getConnections()[i];
            if (track.getDestination().getName().equalsIgnoreCase(targetStationName)) {
                if (track.getDelayTime() == 0.0) {
                    System.out.println("Info: No delay exists on track from "
                            + startStation.getName() + " to " + targetStation.getName() + ".");
                } else {
                    track.setDelayTime(0.0);
                    System.out.println("Success: Delay removed from "
                            + startStation.getName() + " to " + targetStation.getName()
                            + ". Track is now running normally.");
                }
                return;
            }
        }

        System.out.println("Error: No direct route available between "
                + startStation.getName() + " and " + targetStation.getName() + ".");
    }

    public void openOrCloseTrack(String startStationName, String targetStationName, boolean isOpen) {
        Station startStation = getStation(startStationName);
        Station targetStation = getStation(targetStationName);

        if (startStation == null) {
            System.out.println("Error: Start station '" + startStationName + "' does not exist in the network.");
            return;
        }
        if (targetStation == null) {
            System.out.println("Error: Target station '" + targetStationName + "' does not exist in the network.");
            return;
        }

        for (int i = 0; i < startStation.getConnectionCount(); i++) {
            Connection track = startStation.getConnections()[i];
            if (track.getDestination().getName().equalsIgnoreCase(targetStationName)) {
                track.setOpenStatus(isOpen);
                String statusMessage = isOpen ? "OPEN" : "CLOSED";
                System.out.println("Success: Track from " + startStation.getName()
                        + " to " + targetStation.getName() + " is now " + statusMessage + ".");
                return;
            }
        }

        System.out.println("Error: No direct route available between "
                + startStation.getName() + " and " + targetStation.getName() + ".");
    }

    // Checks if a track is open or closed before the Engineer changes it
    public String getTrackStatusString(String startStationName, String targetStationName) {
        Station startStation = getStation(startStationName);
        if (startStation != null) {
            for (int i = 0; i < startStation.getConnectionCount(); i++) {
                Connection track = startStation.getConnections()[i];
                if (track.getDestination().getName().equalsIgnoreCase(targetStationName)) {
                    return track.isOpen() ? "OPEN" : "CLOSED";
                }
            }
        }
        return "Not Found";
    }

    // Prints ONLY the delays
    public void printDelayStatus() {
        System.out.println("\n--- DELAY STATUS REPORT ---");
        boolean foundDelayed = false;
        for (int i = 0; i < totalStations; i++) {
            Station s = stationList[i];
            for (int j = 0; j < s.getConnectionCount(); j++) {
                Connection c = s.getConnections()[j];
                if (c.getDelayTime() > 0) {
                    System.out.println(c.getLineName() + " (" + c.getDirection() + "): "
                            + s.getName() + " to " + c.getDestination().getName()
                            + " - " + c.getDelayTime() + " min delay");
                    foundDelayed = true;
                }
            }
        }
        if (!foundDelayed) System.out.println("No delays on the network. Everything is running smoothly.");
        System.out.println("---------------------------");
    }

    // Prints ONLY the closures
    public void printClosureStatus() {
        System.out.println("\n--- CONNECTION STATUS REPORT ---");
        boolean foundClosed = false;
        for (int i = 0; i < totalStations; i++) {
            Station s = stationList[i];
            for (int j = 0; j < s.getConnectionCount(); j++) {
                Connection c = s.getConnections()[j];
                if (!c.isOpen()) {
                    System.out.println(c.getLineName() + " (" + c.getDirection() + "): "
                            + s.getName() + " to " + c.getDestination().getName() + " - CLOSED");
                    foundClosed = true;
                }
            }
        }
        if (!foundClosed) System.out.println("All tracks are currently OPEN.");
        System.out.println("--------------------------------");
    }


    // ==========================================
    // CUSTOMER METHODS (Search & Filter) & Benchmark
    // ==========================================

    public void displayStationInformation(String stationName) {
        // START BENCHMARK: Search & Sort
        long startTime = System.nanoTime();
        Station foundStation = getStation(stationName);

        if (foundStation == null) {
            System.out.println("Error: Could not find a station named '" + stationName + "'.");
            return;
        }

        // --- DOUBLE SORTING LOGIC (Bubble Sort) O(N^2) ---
        int count = foundStation.getConnectionCount();
        Connection[] sortedTracks = new Connection[count];
        for (int i = 0; i < count; i++) {
            sortedTracks[i] = foundStation.getConnections()[i];
        }

        for (int i = 0; i < count - 1; i++) {
            for (int j = 0; j < count - i - 1; j++) {
                String line1 = sortedTracks[j].getLineName();
                String line2 = sortedTracks[j + 1].getLineName();
                String dest1 = sortedTracks[j].getDestination().getName();
                String dest2 = sortedTracks[j + 1].getDestination().getName();

                int lineCompare = lineNameCompare(line1, line2);

                if (lineCompare > 0 || (lineCompare == 0 && dest1.compareToIgnoreCase(dest2) > 0)) {
                    Connection temp = sortedTracks[j];
                    sortedTracks[j] = sortedTracks[j + 1];
                    sortedTracks[j + 1] = temp;
                }
            }
        }

        // STOP BENCHMARK
        long endTime = System.nanoTime();

        // --- DISPLAY LOGIC ---
        System.out.println("\n=======================================================");
        System.out.println("   LIVE DEPARTURES: " + foundStation.getName().toUpperCase());
        System.out.println("=======================================================");

        String currentLine = "";
        for (int i = 0; i < count; i++) {
            Connection track = sortedTracks[i];

            if (!track.getLineName().equalsIgnoreCase(currentLine)) {
                currentLine = track.getLineName();
                System.out.println("\n[ " + currentLine.toUpperCase() + " LINE ]");
            }

            String dest = track.getDestination().getName().toUpperCase();

            if (!track.isOpen()) {
                System.out.printf("  %-30s %s%n", dest, "CLOSED");
                continue;
            }

            if (track.getDelayTime() > 5.0) {
                System.out.printf("  %-30s %s%n", dest, "SEVERE DELAYS");
                continue;
            }

            int t1 = (int)(Math.random() * 3);
            int t2 = t1 + (int)(Math.random() * 4) + 2;
            int t3 = t2 + (int)(Math.random() * 5) + 3;
            String t1Disp = (t1 == 0) ? "Due" : t1 + " min";

            System.out.printf("  %-30s %s, %d min, %d min%n", dest, t1Disp, t2, t3);
        }

        double executionTimeMs = (endTime - startTime) / 1_000_000.0;
        System.out.println("\n=======================================================");
        System.out.println("Double-Sort & Search Time: " + String.format("%.5f", executionTimeMs) + " ms");
        System.out.println("=======================================================");
    }

    // CUSTOMER METHOD (Line Filter & Benchmark)
    public void displayStationsOnLine(String searchLineName) {
        System.out.println("\n--- Stations on the " + searchLineName + " Line (By Alphabet) ---");

        Station[] results = new Station[totalStations];
        int count = 0;
        for (int i = 0; i < totalStations; i++) {
            for (int j = 0; j < stationList[i].getConnectionCount(); j++) {
                if (stationList[i].getConnections()[j].getLineName().equalsIgnoreCase(searchLineName)) {
                    results[count++] = stationList[i];
                    break;
                }
            }
        }

        if (count == 0) {
            System.out.println("No stations found for line: " + searchLineName);
            return;
        }

        // BENCHMARK SORTING: Bubble Sort (A-Z)
        long sortStart = System.nanoTime();

        for (int i = 0; i < count - 1; i++) {
            for (int j = 0; j < count - i - 1; j++) {
                if (results[j].getName().compareToIgnoreCase(results[j + 1].getName()) > 0) {
                    Station temp = results[j];
                    results[j] = results[j + 1];
                    results[j + 1] = temp;
                }
            }
        }

        long sortEnd = System.nanoTime();

        for (int i = 0; i < count; i++) {
            System.out.println((i + 1) + "  " + results[i].getName());
        }

        double sortTimeMs = (sortEnd - sortStart) / 1_000_000.0;
        System.out.println("-------------------------------------");
        System.out.println("Bubble Sort Time (A-Z): " + String.format("%.5f", sortTimeMs) + " ms");
        System.out.println("-------------------------------------");
    }

    // ==========================================
    // CUSTOMER METHOD: Search for a Station on a Line
    // Runs BOTH Linear Search and Binary Search and compares performance
    // ==========================================

    /**
     * Searches for a station on a given line using both Linear Search and Binary Search,
     * then prints a side-by-side performance comparison.
     *
     * Why both?
     *   - Linear Search works on any array (unsorted) — O(n)
     *   - Binary Search requires a sorted array first  — O(log n)
     *   - Running both on the same input proves which is faster and by how much
     *
     * Step 1: Collect all stations on the line (shared setup)
     * Step 2: LINEAR SEARCH on the unsorted array   → record iterations + time
     * Step 3: Bubble Sort the array (pre-condition for binary search)
     * Step 4: BINARY SEARCH on the sorted array     → record iterations + time
     * Step 5: Print side-by-side comparison report
     */
    public void searchStationOnLine(String lineName, String stationName) {

        //  Step 1: Collect all stations on this line
        Station[] lineStations = new Station[totalStations];
        int count = 0;

        for (int i = 0; i < totalStations; i++) {
            for (int j = 0; j < stationList[i].getConnectionCount(); j++) {
                if (stationList[i].getConnections()[j].getLineName().equalsIgnoreCase(lineName)) {
                    lineStations[count] = stationList[i];
                    count++;
                    break;
                }
            }
        }

        if (count == 0) {
            System.out.println("Error: No stations found for line '" + lineName + "'.");
            System.out.println("Check the line name — e.g. Circle, Jubilee, Victoria, Bakerloo.");
            return;
        }

        //  Step 2: LINEAR SEARCH (on unsorted array)

        long linearStart = System.nanoTime();

        int linearResult     = -1;
        int linearIterations = 0;

        for (int i = 0; i < count; i++) {
            linearIterations++;
            if (lineStations[i].getName().equalsIgnoreCase(stationName)) {
                linearResult = i;
                break; // found — stop here
            }
        }
        // If not found, linearIterations = count (scanned everything)

        long linearEnd = System.nanoTime();
        double linearTimeMs = (linearEnd - linearStart) / 1_000_000.0;
        long   linearTimeNs = linearEnd - linearStart;

        //  Step 3: Bubble Sort (pre-condition for binary search)
        // Binary search CANNOT run on an unsorted array — sort first
        Station[] sortedStations = new Station[count];
        for (int i = 0; i < count; i++) {
            sortedStations[i] = lineStations[i];
        }

        for (int i = 0; i < count - 1; i++) {
            for (int j = 0; j < count - i - 1; j++) {
                if (sortedStations[j].getName().compareToIgnoreCase(sortedStations[j + 1].getName()) > 0) {
                    Station temp         = sortedStations[j];
                    sortedStations[j]    = sortedStations[j + 1];
                    sortedStations[j + 1] = temp;
                }
            }
        }

        //  Step 4: BINARY SEARCH (on sorted array)
        long binaryStart = System.nanoTime();

        int low              = 0;
        int high             = count - 1;
        int binaryResult     = -1;
        int binaryIterations = 0;

        while (low <= high) {
            binaryIterations++;
            int mid     = (low + high) / 2;
            int compare = sortedStations[mid].getName().compareToIgnoreCase(stationName);

            if (compare == 0) {
                binaryResult = mid;
                break;
            } else if (compare < 0) {
                low = mid + 1;  // target is in the right half
            } else {
                high = mid - 1; // target is in the left half
            }
        }

        long binaryEnd = System.nanoTime();
        double binaryTimeMs = (binaryEnd - binaryStart) / 1_000_000.0;
        long   binaryTimeNs = binaryEnd - binaryStart;


        //  Step 5: Print Results & Performance Comparison
        boolean found = (linearResult != -1 || binaryResult != -1);
        String resultLabel = found ? "FOUND" : "NOT FOUND";

        System.out.println("\n=======================================================");
        System.out.println("  Search: \"" + stationName + "\" on " + lineName + " Line");
        System.out.println("  Total stations on this line: " + count);
        System.out.println("=======================================================");
        System.out.println("  RESULT: " + resultLabel);
        System.out.println("-------------------------------------------------------");
        System.out.printf("  %-28s %-15s %-15s%n", "", "LINEAR SEARCH", "BINARY SEARCH");
        System.out.println("  -------------------------------------------------------");
        System.out.printf("  %-28s %-15s %-15s%n",
                "Complexity:",
                "O(n)",
                "O(log n)");
        System.out.printf("  %-28s %-15s %-15s%n",
                "Requires sorted array?",
                "No",
                "Yes");
        System.out.printf("  %-28s %-15d %-15d%n",
                "Iterations taken:",
                linearIterations,
                binaryIterations);
        System.out.printf("  %-28s %-15d %-15d%n",
                "Max possible iterations:",
                count,
                (int) Math.ceil(Math.log(count) / Math.log(2)));
        System.out.printf("  %-28s %-15s %-15s%n",
                "Time taken (ms):",
                String.format("%.5f", linearTimeMs),
                String.format("%.5f", binaryTimeMs));
        System.out.printf("  %-28s %-15d %-15d%n",
                "Time taken (ns):",
                linearTimeNs,
                binaryTimeNs);
        System.out.println("  -------------------------------------------------------");

        // Summary
        int iterationsSaved = linearIterations - binaryIterations;
        if (iterationsSaved > 0) {
            System.out.println(" Binary Search saved " + iterationsSaved
                    + " iteration(s) over Linear Search.");
        } else if (iterationsSaved < 0) {
            System.out.println(" Linear Search was faster by "
                    + Math.abs(iterationsSaved) + " iteration(s) this time.");
            System.out.println("  (This can happen when the target is near the start of the list)");
        } else {
            System.out.println("  Both searches took the same number of iterations this time.");
        }

        // If found, show station departure info
        if (found) {
            Station foundStation = (linearResult != -1) ? lineStations[linearResult]
                    : sortedStations[binaryResult];
            System.out.println("\n  Departures from " + foundStation.getName()
                    + " on " + lineName + " line:");
            boolean hasDepartures = false;
            for (int i = 0; i < foundStation.getConnectionCount(); i++) {
                Connection c = foundStation.getConnections()[i];
                if (c.getLineName().equalsIgnoreCase(lineName)) {
                    String status = !c.isOpen() ? " [CLOSED]"
                            : (c.getDelayTime() > 0 ? " [+" + c.getDelayTime() + " min delay]" : "");
                    System.out.printf("    -> %-28s (%s)  %.2f min%s%n",
                            c.getDestination().getName(),
                            c.getDirection(),
                            c.getNormalTime(),
                            status);
                    hasDepartures = true;
                }
            }
            if (!hasDepartures) {
                System.out.println("    No departures recorded on this line.");
            }
        }

        System.out.println("=======================================================");
    }

    // CUSTOMER METHOD: Find Fastest Route (Dijkstra's Algorithm)

    public void findFastestRoute(String startStationName, String targetStationName) {

        if (startStationName.equalsIgnoreCase(targetStationName)) {
            System.out.println("Error: Start and destination are the same station. Please try again.");
            return;
        }

        Station startNode = getStation(startStationName);
        Station targetNode = getStation(targetStationName);

        if (startNode == null) {
            System.out.println("Error: Start station '" + startStationName + "' not found in network.");
            return;
        }
        if (targetNode == null) {
            System.out.println("Error: Destination station '" + targetStationName + "' not found in network.");
            return;
        }

        // START ALGORITHM BENCHMARK TIMER
        long startTime = System.nanoTime();

        // Dijkstra's Algorithm using parallel array
        double[]  shortestTimes    = new double[totalStations];
        boolean[] hasBeenVisited   = new boolean[totalStations];
        int[]     previousIndex    = new int[totalStations];       // index of the start station
        String[]  previousLineName = new String[totalStations];    // index of the previous station
        String[]  previousDir      = new String[totalStations];    // direction we were travelling
        double[]  legTravelTime    = new double[totalStations];    // raw travel time for this leg
        double[]  interchangeTime  = new double[totalStations];    // interchange penalty paid on arrival

        for (int i = 0; i < totalStations; i++) {
            shortestTimes[i]  = Double.MAX_VALUE;
            hasBeenVisited[i] = false;
            previousIndex[i]  = -1;
            previousLineName[i] = "";
            previousDir[i]      = "";
            legTravelTime[i]    = 0.0;
            interchangeTime[i]  = 0.0;
        }

        int startIndex = getStationIndex(startStationName);
        shortestTimes[startIndex]  = 0.0;
        previousLineName[startIndex] = "";   // no line yet at the very start

        // Core Dijkstra loop
        for (int i = 0; i < totalStations; i++) {

            int currentIdx = getShortestUnvisitedIndex(shortestTimes, hasBeenVisited);

            if (currentIdx == -1 || shortestTimes[currentIdx] == Double.MAX_VALUE) {
                break; // all reachable nodes processed
            }

            hasBeenVisited[currentIdx] = true;
            Station currentStation = stationList[currentIdx];

            for (int j = 0; j < currentStation.getConnectionCount(); j++) {
                Connection track = currentStation.getConnections()[j];

                if (!track.isOpen()) {
                    continue; // skip closed tracks
                }

                int neighborIdx = getStationIndex(track.getDestination().getName());
                if (neighborIdx == -1 || hasBeenVisited[neighborIdx]) {
                    continue;
                }

                // How long to walk between lines at this station?
                String lineArrivedOn = previousLineName[currentIdx];
                double walkPenalty   = currentStation.getInterchangeTime(lineArrivedOn, track.getLineName());

                double timeToNeighbor = shortestTimes[currentIdx] + walkPenalty + track.getTotalTime();

                if (timeToNeighbor < shortestTimes[neighborIdx]) {
                    shortestTimes[neighborIdx]    = timeToNeighbor;
                    previousIndex[neighborIdx]    = currentIdx;
                    previousLineName[neighborIdx] = track.getLineName();
                    previousDir[neighborIdx]      = track.getDirection();
                    legTravelTime[neighborIdx]    = track.getTotalTime();    // travel time for this segment
                    interchangeTime[neighborIdx]  = walkPenalty;            // interchange paid to board this segment
                }
            }
        }

        // STOP ALGORITHM BENCHMARK TIMER
        long endTime = System.nanoTime();
        double executionTimeMs = (endTime - startTime) / 1_000_000.0;

        int targetIndex = getStationIndex(targetStationName);

        // ---- No route found ----
        if (shortestTimes[targetIndex] == Double.MAX_VALUE) {
            System.out.println("\nNo route found between " + startStationName + " and " + targetStationName + ".");
            System.out.println("Some tracks on this route may be closed.");
            return;
        }

        // ---- Reconstruct path backwards from target to start ----
        // pathIndices[0] = target, pathIndices[pathLength-1] = start
        int[] pathIndices = new int[totalStations];
        int   pathLength  = 0;

        int traceIdx = targetIndex;
        while (traceIdx != -1) {
            pathIndices[pathLength] = traceIdx;
            pathLength++;
            if (traceIdx == startIndex) break;
            traceIdx = previousIndex[traceIdx];
        }

        //Build forward-ordered arrays
        int[]    forwardPath = new int[pathLength];
        for (int i = 0; i < pathLength; i++) {
            forwardPath[i] = pathIndices[pathLength - 1 - i];
        }

        //Print the route in the required spec format
        System.out.println("\n=======================================================");
        System.out.println("  Route: " + startStationName + " to " + targetStationName);
        System.out.println("=======================================================");

        int stepNumber = 1;

        // Step (1): Start — show station name + first line + direction
        String firstLine = previousLineName[forwardPath[1]];
        String firstDir  = previousDir[forwardPath[1]];
        System.out.println("(" + stepNumber + ") Start: "
                + stationList[forwardPath[0]].getName()
                + ", " + firstLine + " (" + firstDir + ")");
        stepNumber++;

        // Walk through each leg from index 1 onward
        // forwardPath[i] = station we're AT, forwardPath[i-1] = station we came FROM
        for (int i = 1; i < pathLength; i++) {
            int    fromIdx  = forwardPath[i - 1];
            int    toIdx    = forwardPath[i];
            String lineName = previousLineName[toIdx];
            String dir      = previousDir[toIdx];
            double travelT  = legTravelTime[toIdx];
            double icTime   = interchangeTime[toIdx];
            String fromName = stationList[fromIdx].getName();
            String toName   = stationList[toIdx].getName();

            // If there was an interchange cost arriving at this leg,
            // it means we changed lines at fromIdx — print the Change step first.
            if (icTime > 0.0) {
                // The line we were on BEFORE this interchange = line of the previous leg
                String prevLine = previousLineName[forwardPath[i - 1]];
                String prevDir  = previousDir[forwardPath[i - 1]];
                // Edge case: at start station previousLineName is "" — use first line instead
                if (prevLine.isEmpty()) {
                    prevLine = firstLine;
                    prevDir  = firstDir;
                }
                System.out.println("(" + stepNumber + ") Change: " + fromName
                        + "  " + prevLine + " (" + prevDir + ")"
                        + " to " + lineName + " (" + dir + ")  "
                        + String.format("%.2f", icTime) + "min");
                stepNumber++;
            }

            if (i == pathLength - 1) {
                // Print the last travel leg then End on the next step
                System.out.println("(" + stepNumber + ") " + lineName + " (" + dir + "): "
                        + fromName + " to " + toName + "  "
                        + String.format("%.2f", travelT) + "min");
                stepNumber++;
                System.out.println("(" + stepNumber + ") End: " + toName
                        + ", " + lineName + " (" + dir + ")");
            } else {
                System.out.println("(" + stepNumber + ") " + lineName + " (" + dir + "): "
                        + fromName + " to " + toName + "  "
                        + String.format("%.2f", travelT) + "min");
                stepNumber++;
            }
        }

        System.out.println("-------------------------------------------------------");
        System.out.printf("Total Journey Time: %.2f minutes%n", shortestTimes[targetIndex]);
        System.out.println("-------------------------------------------------------");
        System.out.println("V1 Algorithm Execution Time: " + String.format("%.4f", executionTimeMs) + " ms");
        System.out.println("=======================================================");
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
        int    minIndex = -1;
        for (int i = 0; i < totalStations; i++) {
            if (!hasBeenVisited[i] && shortestTimes[i] <= minTime) {
                minTime  = shortestTimes[i];
                minIndex = i;
            }
        }
        return minIndex;
    }

    private int lineNameCompare(String s1, String s2) {
        return s1.compareToIgnoreCase(s2);
    }
}