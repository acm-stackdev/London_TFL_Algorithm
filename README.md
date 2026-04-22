# TfL Network Manager - Version 1 (Algorithmic Implementation)

## Overview
This project is a console-based application that simulates the Transport for London (TfL) Underground network. **Version 1 is built entirely using hand-coded data structures**, strictly avoiding standard Java collections like `ArrayList`, `HashMap`, or `PriorityQueue`. The purpose of this version is to demonstrate a foundational understanding of arrays, dynamic memory allocation, and core computer science algorithms.

---

## Core Algorithms & Data Structures

Because this system relies purely on native Java arrays, several classical algorithmic approaches were implemented to handle routing, searching, and memory management.

### 1. Shortest Path (Dijkstra's Algorithm)
To calculate the fastest route between two stations, the system uses a custom implementation of **Dijkstra’s Algorithm**.
* **Implementation:** Uses parallel arrays (`shortestTimes`, `hasBeenVisited`, `previousStation`) instead of standard Min-Heaps/Priority Queues.
* **Edge Weights:** The algorithm dynamically calculates track traversal time plus specific interchange penalties (walking times) fetched from `Interchanges.csv`.
* **Dynamic Routing:** Automatically ignores edges (tracks) that Engineers have marked as `CLOSED` and accurately adds `SEVERE DELAYS` to the edge weights.

### 2. Searching & Filtering (Linear Search - $O(N)$)
Without HashMaps, the application relies on **Linear Search** algorithms to locate specific nodes (Stations) and edges (Connections).
* **Station Lookup:** To find a station, the system iterates through the master `Station[]` array one by one until a string match is found.
* **Grouping:** The Live Departures board uses a nested linear loop to dynamically find and group unique underground lines without using `HashSet`.

### 3. Data Insertion (Dynamic Array Resizing)
To handle the "Insert Data" requirement without standard Java Lists, the system features auto-growing arrays.
* **Mechanism:** When reading the CSV database, if a `Station[]` or `Connection[]` array reaches its capacity, the algorithm creates a new array double the size ($O(N)$ space) and sequentially copies the existing data over ($O(N)$ time) before adding the new element.

---

## Performance Benchmarking Suite

To scientifically evaluate the efficiency of these hand-coded structures, a high-precision benchmarking suite using `System.nanoTime()` has been integrated. This isolates the pure mathematical execution time (excluding UI/Console printing) for four critical operations:

1. **Database Boot Time (Insertion):** Measures the time taken to read the CSVs, dynamically resize arrays, and build the Graph network.
2. **Algorithm Execution Time (Pathfinding):** Measures the exact millisecond calculation time of Dijkstra's Algorithm.
3. **Array Search Time:** Measures the $O(N)$ linear search required to find a station for the Live Departures board.
4. **Array Filter Time:** Measures the $O(N)$ time required to traverse the entire network and filter stations by a specific Line.

*Note: These benchmarks will serve as the baseline data for the final report, proving the performance differential when upgrading to standard Java Collections in Version 2.*

---

## Summary of Functions

The application features a robust, crash-proof nested terminal UI divided into two user modes:

### Customer Access
* **Plan a Journey:** Calculates the shortest path between two stations and generates a visual, app-style vertical timeline of the itinerary, including specific line interchanges.
* **Live Departure Board:** Simulates a real-time digital platform screen. It groups outgoing tracks by Line and displays dynamically generated train arrival times (e.g., "Due, 2 min, 5 min").
* **Filter by Line:** Iterates through the network and returns all stations servicing a specific Underground line.

### Engineer Access
* **Add Delay to Track:** Updates the weight of a specific graph edge, which directly impacts the customer's route-finding algorithm.
* **Manage Connections (Open/Close):** Logically "deletes" or restores edges in the graph network, rendering them impassable. Includes a pre-check to show current track status.
* **View Network Status:** Generates isolated reports detailing all current delays and closures across the entire database.

---