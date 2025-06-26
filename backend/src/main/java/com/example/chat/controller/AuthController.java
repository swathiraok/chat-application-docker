// Create a new package: chat/src/main/java/com/example/chat/controller
// Inside this package, create the following two files:

// File: chat/src/main/java/com/example/chat/controller/AuthController.java
// REST Controller for user authentication (registration and login).
package com.example.chat.controller;

import com.example.chat.model.User; // Import the User model
import com.example.chat.repository.UserRepository; // Import the UserRepository
import org.springframework.beans.factory.annotation.Autowired; // For dependency injection
import org.springframework.http.HttpStatus; // For HTTP status codes
import org.springframework.http.ResponseEntity; // For building HTTP responses
import org.springframework.web.bind.annotation.*; // For REST annotations like @RestController, @PostMapping, @RequestBody, @CrossOrigin

import java.util.Map; // For handling login credentials as a map
import java.util.Optional; // For optional user lookup

@RestController // This annotation combines @Controller and @ResponseBody, indicating that this class handles REST requests.
@RequestMapping("/api/auth") // Base path for all endpoints defined in this controller (e.g., /api/auth/register).
@CrossOrigin(origins = "*") // Allows requests from the Angular frontend running on localhost:4200.
                                                // In production, this should be restricted to your frontend's domain.
public class AuthController {

    @Autowired // Spring automatically injects an instance of UserRepository.
    private UserRepository userRepository;

    /**
     * Handles user registration via a POST request to /api/auth/register.
     * @param user The User object received in the request body (contains username and password).
     * @return ResponseEntity indicating success or failure.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // Check if a user with the given username already exists in the database.
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST); // 400 Bad Request
        }
        // If username is unique, save the new user to MongoDB.
        // WARNING: In a real-world application, the password MUST be hashed and salted before storing.
        userRepository.save(user);
        return new ResponseEntity<>("User registered successfully!", HttpStatus.OK); // 200 OK
    }

    /**
     * Handles user login via a POST request to /api/auth/login.
     * @param credentials A Map containing "username" and "password" from the request body.
     * @return ResponseEntity with success message (and username) or error message.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        // Attempt to find the user by username in the database.
        Optional<User> userOptional = userRepository.findByUsername(username);

        // Check if the user exists AND if the provided password matches the stored password.
        // WARNING: In a real-world application, compare hashed passwords using a secure method (e.g., BCrypt).
        if (userOptional.isPresent() && userOptional.get().getPassword().equals(password)) {
            // For simplicity, we just return the username.
            // In a real application, a JSON Web Token (JWT) would be generated and returned here for secure session management.
            return new ResponseEntity<>(Map.of("username", username, "message", "Login successful!"), HttpStatus.OK); // 200 OK
        } else {
            return new ResponseEntity<>("Invalid credentials!", HttpStatus.UNAUTHORIZED); // 401 Unauthorized
        }
    }
}