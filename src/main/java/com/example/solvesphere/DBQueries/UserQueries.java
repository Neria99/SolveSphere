package com.example.solvesphere.DBQueries;

public class UserQueries {
    public static final String INSERT_USER = "INSERT INTO users (username, email, password, date_of_birth, country, registration_date, profile_picture) VALUES (?, ?, ?, ?, ?, ?, ?)";

    public  static final String GET_USER_VIA_ID = "SELECT * FROM users WHERE id = ?";
    public static final String SELECT_USER_BY_USERNAME = "SELECT * FROM users WHERE username = ?";

    public static final String SELECT_USER_BY_USERNAME_AND_EMAIL = "SELECT COUNT(*) FROM users WHERE username = ? OR email = ?";

    public static final String INSERT_USER_INTEREST = "INSERT INTO fields_of_interest (user_id, interest_name, priority_level) VALUES (?, ?, ?)";

    public static final String SELECT_USER_INTEREST = "SELECT interest_name, priority_level FROM fields_of_interest WHERE user_id = ?";
}
