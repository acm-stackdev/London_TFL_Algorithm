package tfl_v1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * The main entry point for the application.
 * Handles loading data from multiple CSV files.
 */
public class TfLMain {

    public static void main(String[] args) {
        TfLNetwork network = new TfLNetwork();

        System.out.println("==================================");
        System.out.println("   Starting TfL Network Manager   ");
        System.out.println("==================================");
        System.out.println("Loading Database...");

        // Load our two specific CSV files
        loadConnections(network, "Connections.csv");
        loadInterchanges(network, "Interchanges.csv");

        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        // --- MAIN PAGE ---
        while (isRunning) {
            System.out.println("\n==================================");
            System.out.println("       TfL NETWORK MANAGER V1     ");
            System.out.println("==================================");
            System.out.println("Select User Mode:");
            System.out.println("1. Customer Access");
            System.out.println("2. Engineer Access");
            System.out.println("0. Exit System");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    runCustomerMenu(scanner, network);
                    break;
                case "2":
                    runEngineerMenu(scanner, network);
                    break;
                case "0":
                    System.out.println("Exiting System. Goodbye!");
                    isRunning = false;
                    break;
                default:
                    System.out.println("Invalid input. Please try again.");
            }
        }
        scanner.close();
    }

    // CUSTOMER MENU ROUTINE

    private static void runCustomerMenu(Scanner scanner, TfLNetwork network) {
        boolean inCustomerMenu = true;

        while (inCustomerMenu) {
            System.out.println("\n--- CUSTOMER MENU ---");
            System.out.println("1. Plan a Journey (Fastest Route)");
            System.out.println("2. Live Departure Board (Search Station)");
            System.out.println("3. View Stations on a Line (Filter by Line)");
            System.out.println("0. Return to Main Menu");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("\n--- TfL Journey Planner ---");
                    System.out.print("From (Start Station): ");
                    String routeStart = scanner.nextLine();

                    System.out.print("To (Destination Station): ");
                    String routeTarget = scanner.nextLine();

                    network.findFastestRoute(routeStart, routeTarget);
                    break;

                case "2":
                    System.out.print("Enter Station Name for Live Board: ");
                    String searchName = scanner.nextLine();
                    network.displayStationInformation(searchName);
                    break;

                case "3":
                    System.out.print("Enter Line Name (e.g., Victoria, Jubilee): ");
                    String lineName = scanner.nextLine();
                    network.displayStationsOnLine(lineName);
                    break;

                case "0":
                    inCustomerMenu = false;
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // ENGINEER MENU ROUTINE

    private static void runEngineerMenu(Scanner scanner, TfLNetwork network) {
        boolean inEngineerMenu = true;

        while (inEngineerMenu) {
            System.out.println("\n--- ENGINEER MENU ---");
            System.out.println("1. Add Delay to Track");
            System.out.println("2. Open / Close Track");
            System.out.println("3. View Network Status (Delays & Closures)");
            System.out.println("0. Return to Main Menu");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter Start Station: ");
                    String delayStart = scanner.nextLine();
                    System.out.print("Enter Target Station: ");
                    String delayTarget = scanner.nextLine();
                    System.out.print("Enter Delay (minutes): ");
                    try {
                        double delayMins = Double.parseDouble(scanner.nextLine());
                        network.addDelayToTrack(delayStart, delayTarget, delayMins);
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Please enter a valid number for minutes.");
                    }
                    break;

                case "2":
                    System.out.print("Enter Start Station: ");
                    String statusStart = scanner.nextLine();
                    System.out.print("Enter Target Station: ");
                    String statusTarget = scanner.nextLine();
                    System.out.print("Type 'open' or 'close': ");
                    String openOrClose = scanner.nextLine();
                    boolean isOpen = openOrClose.equalsIgnoreCase("open");
                    network.openOrCloseTrack(statusStart, statusTarget, isOpen);
                    break;

                case "3":
                    network.printNetworkStatus();
                    break;

                case "0":
                    inEngineerMenu = false;
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }


    // DATA LOADING METHODS
    private static void loadConnections(TfLNetwork network, String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int loadedCount = 0;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Start Station") || line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String startStation = parts[0].trim();
                    String targetStation = parts[1].trim();
                    double travelTime = Double.parseDouble(parts[2].trim());
                    String lineName = parts[3].trim();
                    String direction = parts[4].trim();

                    if (network.getStation(startStation) == null) network.addStation(startStation);
                    if (network.getStation(targetStation) == null) network.addStation(targetStation);
                    network.addNewTrack(startStation, targetStation, lineName, direction, travelTime);
                    loadedCount++;
                }
            }
            System.out.println(" - " + loadedCount + " Connections loaded successfully.");
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error reading Connections file: " + e.getMessage());
        }
    }

    private static void loadInterchanges(TfLNetwork network, String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int loadedCount = 0;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Station") || line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String station = parts[0].trim();
                    String fromLine = parts[1].trim();
                    String toLine = parts[2].trim();
                    double walkTime = Double.parseDouble(parts[3].trim());

                    network.addNewInterchange(station, fromLine, toLine, walkTime);
                    loadedCount++;
                }
            }
            System.out.println(" - " + loadedCount + " Interchanges loaded successfully.");
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error reading Interchanges file: " + e.getMessage());
        }
    }
}