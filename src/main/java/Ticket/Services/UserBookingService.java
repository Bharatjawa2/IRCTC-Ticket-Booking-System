package Ticket.Services;

import Ticket.entities.Ticket;
import Ticket.entities.Train;
import Ticket.entities.User;
import Ticket.util.UserServiceUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class UserBookingService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<User> userList;
    private final User user;

    public static final String USER_FILE_PATH = "src/main/java/Ticket/localDB/users.json";

    public UserBookingService(User user) throws IOException {
        this.user = user;
        objectMapper.registerModule(new JavaTimeModule());
        loadUserListFromFile();
    }

    public UserBookingService() throws IOException {
        this.user = null;  // No user provided
        objectMapper.registerModule(new JavaTimeModule());
        loadUserListFromFile();
    }

    private void loadUserListFromFile() throws IOException {
        File userFile = new File(USER_FILE_PATH);
        System.out.println("Loading users from: " + userFile.getAbsolutePath());
        if (userFile.exists() && userFile.length() > 0) {
            System.out.println("File exists. Loading data...");
            userList = objectMapper.readValue(userFile, new TypeReference<List<User>>() {});
        } else {
            System.out.println("File does not exist or is empty. Initializing empty user list.");
            userList = new ArrayList<>();
        }
    }

    public Boolean loginUser() {
        if (user == null || user.getName() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("User credentials cannot be null.");
        }

        return userList.stream().anyMatch(user1 ->
                user1.getName().equals(user.getName()) &&
                        UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword()));
    }

    public Boolean signUp(User newUser) {
        if (newUser == null || newUser.getName() == null || newUser.getPassword() == null) {
            throw new IllegalArgumentException("User details cannot be null.");
        }

        if (userList.stream().anyMatch(user1 -> user1.getName().equals(newUser.getName()))) {
            throw new IllegalArgumentException("User with the same name already exists.");
        }

        try {
            userList.add(newUser);
            saveUserListToFile();
            return Boolean.TRUE;
        } catch (IOException ex) {
            ex.printStackTrace();
            return Boolean.FALSE;
        }
    }

    private void saveUserListToFile() throws IOException {
        objectMapper.writeValue(new File(USER_FILE_PATH), userList);
    }

    public void fetchBookings() {
        if (user == null) {
            throw new IllegalStateException("User is not logged in.");
        }

        userList.stream()
                .filter(user1 -> user1.getName().equals(user.getName()) &&
                        UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword()))
                .findFirst()
                .ifPresent(User::printTickets);
    }

    public Boolean cancelBooking(String ticketId) {
        if (ticketId == null || ticketId.isEmpty()) {
            throw new IllegalArgumentException("Ticket ID cannot be null or empty.");
        }

        if (user == null) {
            throw new IllegalStateException("User is not logged in.");
        }

        boolean removed = user.getTicketsBooked().removeIf(ticket -> ticket.getTicketId().equals(ticketId));

        if (removed) {
            try {
                saveUserListToFile();
                return Boolean.TRUE;
            } catch (IOException ex) {
                ex.printStackTrace();
                return Boolean.FALSE;
            }
        } else {
            return Boolean.FALSE;
        }
    }

    public List<Train> getTrains(String source, String destination) {
        if (source == null || destination == null || source.isEmpty() || destination.isEmpty()) {
            throw new IllegalArgumentException("Source and destination cannot be null or empty.");
        }

        try {
            TrainService trainService = new TrainService();
            return trainService.searchTrains(source, destination);
        } catch (IOException ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<List<Integer>> fetchSeats(Train train) {
        if (train == null) {
            throw new IllegalArgumentException("Train cannot be null.");
        }
        return train.getSeats();
    }

    public Boolean bookTrainSeat(Train train, int row, int seat) {
        if (train == null) {
            throw new IllegalArgumentException("Train cannot be null.");
        }

        try {
            TrainService trainService = new TrainService();
            List<List<Integer>> seats = train.getSeats();

            if (row >= 0 && row < seats.size() && seat >= 0 && seat < seats.get(row).size()) {
                if (seats.get(row).get(seat) == 0) {
                    // Book the seat
                    seats.get(row).set(seat, 1);
                    train.setSeats(seats);
                    trainService.updateTrain(train);

                    // Create a new Ticket object
                    String ticketId = UUID.randomUUID().toString();
                    LocalDate dateOfTravel = LocalDate.now(); // Use current date or allow user to input
                    Ticket newTicket = new Ticket(
                            ticketId,
                            user.getUserId(),
                            train.getStations().get(0), // Source station
                            train.getStations().get(train.getStations().size() - 1), // Destination station
                            dateOfTravel,
                            train
                    );

                    // Add the ticket to the user's tickets_booked list
                    user.getTicketsBooked().add(newTicket);

                    // Save the updated user data
                    saveUserListToFile();

                    return Boolean.TRUE; // Booking successful
                }
            }
            return Boolean.FALSE; // Invalid seat selection or already booked
        } catch (IOException ex) {
            ex.printStackTrace();
            return Boolean.FALSE;
        }
    }
}