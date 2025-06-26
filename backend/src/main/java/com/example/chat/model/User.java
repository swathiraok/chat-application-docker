package com.example.chat.model;

import lombok.Data; // Import Lombok's Data annotation
import org.springframework.data.annotation.Id; // Import Spring Data's Id annotation
import org.springframework.data.mongodb.core.mapping.Document; // Import Document annotation for MongoDB mapping

@Data // Lombok annotation: automatically generates getters, setters, equals(), hashCode(), and toString() methods.
@Document(collection = "users") // Maps this Java class to a MongoDB collection named "users".
public class User {
    @Id // Marks this field as the primary key (identifier) for the MongoDB document.
    private String id;
    private String username; // Stores the unique username of the user.
    private String password; // Stores the user's password.
                            // WARNING: In a real application, you MUST hash and salt passwords
                            //          before storing them. This is simplified for learning.

    // Default constructor: Required by Spring Data MongoDB.
    public User() {}

    // Parameterized constructor: For creating User objects.
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
