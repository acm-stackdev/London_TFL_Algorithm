# TfL Network Manager — Version 1

Version 1 is a fully hand-coded Java application with no use of Java library data structures or algorithm classes (no `ArrayList`, `HashMap`, `Collections.sort()` etc). All data structures and algorithms are implemented from scratch using primitive arrays.

---


## Data Structures (Hand-Coded)

### Dynamic Array (Auto-Growing)
Used in `TfLNetwork`, `Station` to store stations, connections and interchanges.

Starts at a small fixed size. When full, doubles in capacity by allocating a new array and copying all elements across — the same strategy used by Java's own `ArrayList` internally.

```
Complexity:
  Add element:  O(1) amortised  (occasional O(n) resize)
  Access by index: O(1)
  Search by name:  O(n) linear scan
```

### Adjacency List (Graph)
The network is modelled as a **directed weighted graph**:
- Each `Station` is a node
- Each `Connection` is a directed edge with a weight (travel time in minutes)
- Both directions between adjacent stations are stored as separate edges (e.g. Eastbound and Westbound)
- Interchanges (line changes) are stored separately per station as additional time penalties

---

## Algorithms

### 1. Linear Search — `getStation()` — O(n)
Used to look up a station by name across the full network. Scans from index 0 until a case-insensitive match is found.

```
Best case:  O(1) — station is first in the array
Worst case: O(n) — station is last or not found
```

Used throughout the system wherever a station name needs to be resolved to a `Station` object.

---

### 2. Bubble Sort — `displayStationsOnLine()`, `displayStationInformation()` — O(n²)
Used to sort stations alphabetically (A–Z) before displaying them, and to sort a station's departures by line name then destination name.

Two nested loops compare adjacent elements and swap them if out of order. Repeats until no swaps are needed.

```
Best case:  O(n)   — already sorted (no swaps needed)
Worst case: O(n²)  — reverse sorted
Space:      O(1)   — sorts in-place
```

Chosen because it is straightforward to implement from scratch without any library support, and the dataset size (per line: ~7–30 stations) is small enough that O(n²) is acceptable.

---

### 3. Binary Search — `searchStationOnLine()` — O(log n)
Used to search for a specific station within a line's station list. **Requires the array to be sorted first** (Bubble Sort is run as a pre-condition).

Repeatedly halves the search space by comparing the target against the middle element:
- If target < middle → search left half
- If target > middle → search right half
- If equal → found

```
Best case:  O(1)      — target is the middle element
Worst case: O(log n)  — target is at the edge or not present
Space:      O(1)
```

Run alongside Linear Search on the same input so iteration counts can be compared directly.

---

### 4. Linear Search vs Binary Search Comparison — `searchStationOnLine()`
Both algorithms are run on the same station list and their iteration counts and execution times are printed side by side.

```
Example — search "WESTMINSTER" on Circle line (27 stations):

                         LINEAR SEARCH   BINARY SEARCH
Complexity:              O(n)            O(log n)
Requires sorted array?   No              Yes
Iterations taken:        26              5
Max possible:            27              5
```

Key insight: Linear search wins when the target is near the start of the list (best case O(1)). Binary search wins consistently in the worst case and not-found case, taking at most ⌈log₂(n)⌉ iterations regardless.

---

### 5. Dijkstra's Algorithm — `findFastestRoute()` — O(n²)
Finds the fastest route in time between any two stations across all lines, accounting for:
- Travel time between stations (from CSV)
- Delay time added by engineers
- Closed tracks (skipped entirely)
- Interchange walking time when changing lines (from CSV)

**Implementation uses parallel arrays (no `PriorityQueue`):**

| Array | Purpose |
|---|---|
| `shortestTimes[]` | Best known time to reach each station |
| `hasBeenVisited[]` | Whether a station has been finalised |
| `previousIndex[]` | Which station we came from (for path reconstruction) |
| `previousLineName[]` | Which line we arrived on |
| `previousDir[]` | Which direction we were travelling |
| `legTravelTime[]` | Travel time for each segment |
| `interchangeTime[]` | Interchange penalty paid at each step |

**Core loop:**
1. Pick the unvisited station with the current shortest time — O(n)
2. For each connection leaving that station, calculate total time including any interchange penalty
3. If faster than the current best, update the record
4. Repeat until the target station is finalised

```
Time complexity:  O(n²)  — n iterations × O(n) scan for minimum each time
Space complexity: O(n)   — 7 parallel arrays of size n
```

A `PriorityQueue`-based Dijkstra (used in Version 2) reduces this to O((n + e) log n) where e = edges. The benchmarking output compares both.

**Path reconstruction:**
After the algorithm finishes, the path is traced backwards from target to start using `previousIndex[]`, then reversed into a forward-ordered array for printing. Each step is printed with its travel time, and any line changes are printed as separate interchange steps.

---
## Function Reference

| Method | Class | What it does |
|---|---|---|
| `findFastestRoute(start, end)` | TfLNetwork | Dijkstra's algorithm — finds fastest path, prints step-by-step itinerary |
| `searchStationOnLine(line, station)` | TfLNetwork | Runs Linear + Binary Search, prints side-by-side performance comparison |
| `displayStationsOnLine(line)` | TfLNetwork | Bubble sorts stations on a line A–Z, prints with sort time |
| `displayStationInformation(station)` | TfLNetwork | Bubble sorts departures by line+destination, prints live board |
| `addDelayToTrack(start, end, mins)` | TfLNetwork | Sets delay time on a connection |
| `removeDelayFromTrack(start, end)` | TfLNetwork | Clears delay time on a connection |
| `openOrCloseTrack(start, end, bool)` | TfLNetwork | Opens or closes a connection |
| `printDelayStatus()` | TfLNetwork | Lists all connections with active delays |
| `printClosureStatus()` | TfLNetwork | Lists all closed connections |
| `getStation(name)` | TfLNetwork | Linear search across all stations |
| `addStation(name)` | TfLNetwork | Adds station to dynamic array, resizes if needed |
| `addConnection(connection)` | Station | Adds directed edge to station's dynamic array |
| `getInterchangeTime(from, to)` | Station | Looks up walking time between two lines at this station |
| `getTotalTime()` | Connection | Returns normalTime + delayTime |
 
---

## Benchmarking Summary

| Algorithm | Complexity | Used For |
|---|---|---|
| Linear Search | O(n) | Station lookup, departure filtering |
| Binary Search | O(log n) | Station search within a sorted line |
| Bubble Sort | O(n²) | Sorting stations and departures for display |
| Dijkstra (array) | O(n²) | Fastest route finding — Version 1 |
| Dijkstra (PriorityQueue) | O((n+e) log n) | Fastest route finding — Version 2 |
