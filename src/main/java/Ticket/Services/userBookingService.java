package Ticket.Services;

import Ticket.entities.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class userBookingService {
    private User user;
    private List<User> userList;
    private ObjectMapper objectMapper=new ObjectMapper();
    private static final String USERS_PATH="../localDB/users.json";

    public userBookingService(User user_name) throws IOException {
        this.user=user_name;
        File users=new File(USERS_PATH);
        userList=objectMapper.readValue(users, new TypeReference<List<User>>() {});
    }
}
