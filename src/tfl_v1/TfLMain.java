package tfl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class TfLMain {

    public static void main(String[] args) {
        TfLNetwork network = new TfLNetwork();
        System.out.println("Loading TfL Network Data...");
        loadDataFromFile(network, "tfl_data.csv");

        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        // MAIN PORTAL
        while (isRunning) {
            System.out.println("\n==================================");
            System.out.println("       TfL NETWORK MANAGER (Hand Coded Version       ");
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
                    System.out.println("Shutting down... Goodbye!");
                    isRunning = false;
                    break;
                default:
                    System.out.println("Invalid input. Please try again.");
            }
        }
        scanner.close();
    }

    // ==========================================
    // CUSTOMER MENU ROUTINE
    // ==========================================
    private static void runCustomerMenu(Scanner scanner, TfLNetwork network) {
        boolean inCustomerMenu = true;

        while (inCustomerMenu) {
            System.out.println("\n--- CUSTOMER MENU ---");
            System.out.println("1. Plan a Journey (Fastest Route)");
            System.out.println("2. Search Station Details (By Name)");
            System.out.println("3. View Stations on a Line (Filter by Line)");
            System.out.println("4. Return to Main Menu");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter Start Station: ");
                    String routeStart = scanner.nextLine();
                    System.out.print("Enter Destination Station: ");
                    String routeTarget = scanner.nextLine();
                    network.findFastestRoute(routeStart, routeTarget);
                    break;

                case "2":
                    System.out.print("Enter Station Name: ");
                    String searchName = scanner.nextLine();
                    network.displayStationInformation(searchName);
                    break;

                case "3":
                    System.out.print("Enter Line Name (e.g., Victoria, Jubilee): ");
                    String lineName = scanner.nextLine();
                    network.displayStationsOnLine(lineName);
                    break;

                case "4":
                    inCustomerMenu = false;
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // ==========================================
    // ENGINEER MENU ROUTINE
    // ==========================================
    private static void runEngineerMenu(Scanner scanner, TfLNetwork network) {
        boolean inEngineerMenu = true;

        while (inEngineerMenu) {
            System.out.println("\n--- ENGINEER MENU ---");
            System.out.println("1. Add Delay to Track");
            System.out.println("2. Open / Close Track");
            System.out.println("3. View Network Status (Delays & Closures)");
            System.out.println("4. Return to Main Menu");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter Start Station: ");
                    String delayStart = scanner.nextLine();
                    System.out.print("Enter Target Station: ");
                    String delayTarget = scanner.nextLine();
                    System.out.print("Enter Delay (minutes): ");
                    double delayMins = Double.parseDouble(scanner.nextLine());
                    network.addDelayToTrack(delayStart, delayTarget, delayMins);
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

                case "4":
                    inEngineerMenu = false;
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // File loading logic remains the exact same as previously discussed...
    private static void loadDataFromFile(TfLNetwork network, String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String startStation = parts[0].trim();
                    String targetStation = parts[1].trim();
                    String lineName = parts[2].trim();
                    String direction = parts[3].trim();
                    double travelTime = Double.parseDouble(parts[4].trim());

                    if (network.getStation(startStation) == null) network.addStation(startStation);
                    if (network.getStation(targetStation) == null) network.addStation(targetStation);
                    network.addNewTrack(startStation, targetStation, lineName, direction, travelTime);
                }
            }
            System.out.println("Data loaded successfully!");
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
    }
}