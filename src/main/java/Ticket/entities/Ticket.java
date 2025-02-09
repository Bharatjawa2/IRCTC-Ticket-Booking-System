package Ticket.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies; // Fixed import
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDate; // Use LocalDate instead of String

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class) // Fixed annotation
public class Ticket {

    private String ticketId;
    private String userId;
    private String source;
    private String destination;
    private LocalDate dateOfTravel; // Changed from String to LocalDate
    private Train train;

    public Ticket() {}

    public Ticket(String ticketId, String userId, String source, String destination, LocalDate dateOfTravel, Train train) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.source = source;
        this.destination = destination;
        this.dateOfTravel = dateOfTravel;
        this.train = train;
    }

    public String getTicketInfo() {
        return String.format("Ticket ID: %s belongs to User %s from %s to %s on %s", ticketId, userId, source, destination, dateOfTravel);
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getDateOfTravel() { // Changed return type to LocalDate
        return dateOfTravel;
    }

    public void setDateOfTravel(LocalDate dateOfTravel) { // Changed parameter type to LocalDate
        this.dateOfTravel = dateOfTravel;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }
}
