package Ticket;

import Ticket.Services.UserBookingService;
import Ticket.entities.Train;
import Ticket.entities.User;
import Ticket.util.UserServiceUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        System.out.println("Running Train Booking System");
        Scanner scanner = new Scanner(System.in);
        int option = 0;
        UserBookingService userBookingService = null;

        try {
            userBookingService = new UserBookingService();
        } catch (IOException ex) {
            System.out.println("Error loading user data: " + ex.getMessage());
            return;
        }

        Train trainSelectedForBooking = null; // Initialize train selection outside loop

        while (option != 7) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Sign up");
            System.out.println("2. Login");
            System.out.println("3. Fetch Bookings");
            System.out.println("4. Search Trains");
            System.out.println("5. Book a Seat");
            System.out.println("6. Cancel my Booking");
            System.out.println("7. Exit the App");

            try {
                option = Integer.parseInt(scanner.nextLine());  // Use nextLine() to avoid input issues
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (option) {
                case 1:
                    handleSignUp(scanner, userBookingService);
                    break;

                case 2:
                    userBookingService = handleLogin(scanner);
                    break;

                case 3:
                    if (userBookingService != null) {
                        System.out.println("Fetching your bookings...");
                        userBookingService.fetchBookings();
                    } else {
                        System.out.println("Please login first.");
                    }
                    break;

                case 4:
                    trainSelectedForBooking = handleSearchTrains(scanner, userBookingService);
                    break;

                case 5:
                    if (trainSelectedForBooking == null) {
                        System.out.println("Please select a train first (Option 4).");
                    } else {
                        handleBookSeat(scanner, userBookingService, trainSelectedForBooking);
                    }
                    break;

                case 6:
                    if (userBookingService != null) {
                        handleCancelBooking(scanner, userBookingService);
                    } else {
                        System.out.println("Please login first.");
                    }
                    break;

                case 7:
                    System.out.println("Exiting the application. Have a great day!");
                    break;

                default:
                    System.out.println("Invalid option. Please choose again.");
                    break;
            }
        }
        scanner.close();
    }

    private static void handleSignUp(Scanner scanner, UserBookingService userBookingService) {
        System.out.println("Enter username for sign-up:");
        String nameToSignUp = scanner.nextLine();
        System.out.println("Enter password:");
        String passwordToSignUp = scanner.nextLine();

        User newUser = new User(
                UUID.randomUUID().toString(), // userId
                nameToSignUp, // name
                passwordToSignUp, // password
                UserServiceUtil.hashPassword(passwordToSignUp), // hashedPassword
                new ArrayList<>() // ticketsBooked
        );

        if (userBookingService.signUp(newUser)) {
            System.out.println("Sign-up successful!");
        } else {
            System.out.println("Sign-up failed. Try again.");
        }
    }

    private static UserBookingService handleLogin(Scanner scanner) {
        System.out.println("Enter username for login:");
        String nameToLogin = scanner.nextLine();
        System.out.println("Enter password:");
        String passwordToLogin = scanner.nextLine();

        User userToLogin = new User(
                UUID.randomUUID().toString(), // userId
                nameToLogin, // name
                passwordToLogin, // password
                "", // hashedPassword (empty for now)
                new ArrayList<>() // ticketsBooked
        );

        try {
            UserBookingService userBookingService = new UserBookingService(userToLogin);
            if (userBookingService.loginUser()) {
                System.out.println("Login successful!");
                return userBookingService;
            } else {
                System.out.println("Invalid credentials. Please try again.");
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return null;
    }

    private static Train handleSearchTrains(Scanner scanner, UserBookingService userBookingService) {
        System.out.println("Enter source station:");
        String source = scanner.nextLine();
        System.out.println("Enter destination station:");
        String dest = scanner.nextLine();

        if (source == null || dest == null || source.isEmpty() || dest.isEmpty()) {
            System.out.println("Source and destination cannot be empty.");
            return null;
        }

        List<Train> trains = userBookingService.getTrains(source, dest);
        if (trains.isEmpty()) {
            System.out.println("No trains found for the given route.");
            return null;
        }

        System.out.println("\nAvailable Trains:");
        int index = 1;
        for (Train t : trains) {
            System.out.println(index + ". Train ID: " + t.getTrainId());
            for (Map.Entry<String, String> entry : t.getStationTimes().entrySet()) {
                System.out.println("   Station: " + entry.getKey() + ", Time: " + entry.getValue());
            }
            index++;
        }

        System.out.println("Type 1");
        try {
            int trainIndex = Integer.parseInt(scanner.nextLine()) - 1;
            if (trainIndex >= 0 && trainIndex < trains.size()) {
                Train selectedTrain = trains.get(trainIndex);
                System.out.println("You selected Train ID: " + selectedTrain.getTrainId());
                return selectedTrain;
            } else {
                System.out.println("Invalid selection. Please choose a number between 1 and " + trains.size() + ".");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
        return null;
    }

    private static void handleBookSeat(Scanner scanner, UserBookingService userBookingService, Train train) {
        System.out.println("Available seats:");
        List<List<Integer>> seats = userBookingService.fetchSeats(train);

        for (int i = 0; i < seats.size(); i++) {
            for (int j = 0; j < seats.get(i).size(); j++) {
                System.out.print(seats.get(i).get(j) + " ");
            }
            System.out.println();
        }

        System.out.println("Enter row number:");
        int row = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter seat number:");
        int col = Integer.parseInt(scanner.nextLine());

        if (row < 0 || row >= seats.size() || col < 0 || col >= seats.get(row).size()) {
            System.out.println("Invalid seat selection.");
            return;
        }

        System.out.println("Booking your seat...");
        boolean booked = userBookingService.bookTrainSeat(train, row, col);

        if (booked) {
            System.out.println("Booking successful! Enjoy your journey.");
        } else {
            System.out.println("Seat is already booked or invalid selection.");
        }
    }

    private static void handleCancelBooking(Scanner scanner, UserBookingService userBookingService) {
        System.out.println("Enter ticket ID to cancel:");
        String ticketId = scanner.nextLine();

        if (ticketId == null || ticketId.isEmpty()) {
            System.out.println("Ticket ID cannot be empty.");
            return;
        }

        if (userBookingService.cancelBooking(ticketId)) {
            System.out.println("Ticket cancelled successfully.");
        } else {
            System.out.println("Ticket cancellation failed or ticket not found.");
        }
    }
}