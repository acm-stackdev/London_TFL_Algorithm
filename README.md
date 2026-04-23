# TfL Network Manager - Version 1 (Algorithmic Implementation)

## Overview
This project is a high-fidelity simulation of the TfL Underground network. **Version 1 is a "Hand-Coded" implementation**, designed to demonstrate mastery of low-level data structures. Every dynamic storage element, search routine, and sorting mechanism has been built using native Java arrays, strictly avoiding the `java.util` collections library.

---

## Algorithmic Architecture

The primary focus of this version is the implementation of classical algorithms using primitive data structures.

### 1. Pathfinding: Dijkstra's Algorithm $O(V^2)$
The system calculates the fastest route using a custom Dijkstra implementation.
* **Mechanism:** Uses parallel primitive arrays to track shortest paths and visited nodes.
* **Edge Weights:** Traversals account for base travel time, engineer-reported delays, and specific interchange walking penalties.
* **Itinerary Generation:** Uses a backtracking algorithm to trace breadcrumbs from the destination to the source, generating a step-by-step travel plan.

### 2. Multi-Level Sorting: Double Bubble Sort $O(N^2)$
For the Live Departure Board, the system implements a **Composite-Key Bubble Sort**.
* **Primary Key:** Underground Line (Alphabetical A-Z).
* **Secondary Key:** Destination Station (Alphabetical A-Z).
* **Implementation:** Nested `for` loops iterate through connections, performing swaps based on multi-level conditional logic to ensure the "App-style" grouped UI layout.

### 3. Searching & Filtering: Linear Search $O(N)$
Without HashMaps, the system demonstrates the foundational logic of linear traversal:
* **Lookup:** Every station retrieval requires a full iteration through the `stationList` array.
* **Filtering:** Line-based searches iterate through all network edges to extract matches into a temporary array for sorting.

### 4. Memory Management: Dynamic Array Resizing
To handle the "Insert Data" requirement, a custom `ArrayList`-style resizing logic is used. Arrays begin with a small footprint and automatically double in size ($O(N)$ reallocation) as the CSV data is parsed.

---

## Performance Benchmarking (The Baseline)

A critical component of this project is the **Performance Suite**. Using `System.nanoTime()`, we have isolated the execution time of four key algorithmic operations. This data serves as the **Baseline** for our Version 2 comparison.

| Operation | Algorithm Used | Measured Logic |
| :--- | :--- | :--- |
| **Database Load** | Dynamic Insertion | CSV parsing + $O(N)$ Array Resizing |
| **Pathfinding** | Dijkstra's | Path calculation (excluding UI printing) |
| **Station Search** | Linear Search | $O(N)$ lookup in the Station Array |
| **Board Sorting** | Double Bubble Sort | Grouping by Line + Alpha-sort by Station |

---

## Functional Summary

### Customer Portal
* **Journey Planner:** Graphical vertical timeline showing every stop, line change, and total travel time.
* **Live Departures:** Grouped and sorted view of upcoming trains, simulating a real-world platform display with "Due/min" intervals.
* **Line Filter:** A-Z sorted list of all stations on a selected Underground line.

### Engineer Portal
* **Disruption Management:** Update track weights (Delays) or logically remove edges (Closures).
* **Status Pre-check:** Real-time state verification before committing network changes.
* **Network Reports:** Isolated status summaries for active delays and closures.

---