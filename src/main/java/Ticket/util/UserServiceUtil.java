package Ticket.util;

import org.mindrot.jbcrypt.BCrypt;

public class UserServiceUtil {

    // Hash the plain password using BCrypt
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Plain password cannot be null or empty.");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Check if the provided plain password matches the hashed password
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Plain password cannot be null or empty.");
        }
        if (hashedPassword == null || hashedPassword.isEmpty()) {
            throw new IllegalArgumentException("Hashed password cannot be null or empty.");
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}