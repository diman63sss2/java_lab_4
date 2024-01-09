package com.example.dezdemoniyslab.services.user;

import com.example.dezdemoniyslab.jms.Producer;
import com.example.dezdemoniyslab.models.LogType;
import com.example.dezdemoniyslab.models.User;
import com.example.dezdemoniyslab.requests.users.UserRegisterRequest;
import com.example.dezdemoniyslab.requests.users.UserUpdateRequest;
import com.example.dezdemoniyslab.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {
    private final BaseUserService baseUserService;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private final EmailService emailService;
    private final Producer producer;

    private boolean isValidEmail(String email) {
        return Pattern.matches(EMAIL_REGEX, email);
    }
    private boolean isRequestCorrect(UserRegisterRequest userRegisterRequest) throws IllegalArgumentException {
        // Perform field validations
        if (userRegisterRequest.getUsername() == null || userRegisterRequest.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if (userRegisterRequest.getFirstname() == null || userRegisterRequest.getFirstname().isEmpty()) {
            throw new IllegalArgumentException("Firstname cannot be empty");
        }

        if (userRegisterRequest.getLastname() == null || userRegisterRequest.getLastname().isEmpty()) {
            throw new IllegalArgumentException("Lastname cannot be empty");
        }

        if (userRegisterRequest.getEmail() == null || userRegisterRequest.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (!isValidEmail(userRegisterRequest.getEmail())) {
            throw new IllegalArgumentException("Invalid email address");
        }

        // Perform password validation

        if (userRegisterRequest.getPassword() == null || userRegisterRequest.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        // Perform role validation against a predefined list of role values

        if (userRegisterRequest.getRole() == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        return true;
    }



    private boolean isRequestCorrect (UserUpdateRequest userUpdateRequest) throws IllegalArgumentException {

        if (userUpdateRequest.getUsername() == null || userUpdateRequest.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if (userUpdateRequest.getFirstname() == null || userUpdateRequest.getFirstname().isEmpty()) {
            throw new IllegalArgumentException("Firstname cannot be empty");
        }

        if (userUpdateRequest.getLastname() == null || userUpdateRequest.getLastname().isEmpty()) {
            throw new IllegalArgumentException("Lastname cannot be empty");
        }

        if (userUpdateRequest.getEmail() == null || userUpdateRequest.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (!isValidEmail(userUpdateRequest.getEmail())) {
            throw new IllegalArgumentException("Invalid email address");
        }

        // Perform password validation

        if (userUpdateRequest.getPassword() == null || userUpdateRequest.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        // Perform role validation against a predefined list of role values

        if (userUpdateRequest.getRole() == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        return true;
    }

    public List<User> getAllUsers() {
        return baseUserService.getAllUsersFromDatabase();
    }

    public void registerUser(UserRegisterRequest userRegisterRequest) throws IllegalArgumentException {
        isRequestCorrect(userRegisterRequest);

        User user = User.builder()
                .firstname(userRegisterRequest.getFirstname())
                .lastname(userRegisterRequest.getLastname())
                .email(userRegisterRequest.getEmail())
                .username(userRegisterRequest.getUsername())
                .password(userRegisterRequest.getPassword())
                .role(userRegisterRequest.getRole())
                .build();


        String message = "Creation a new user  with values" + "\n" + userRegisterRequest;
        producer.sendMessage(user, LogType.CREATE, message);
        emailService.sendTextEmail(EmailService.adminEmail, LogType.CREATE.name(), message );
        baseUserService.saveUserToDatabase(user);
    }

    public void updateUser(UserUpdateRequest userUpdateRequest, Long userId) throws IllegalArgumentException {
        isRequestCorrect(userUpdateRequest);
        // todo: will send info about editor
        User userFromDb = baseUserService.getUserFromDatabaseById(userId);
        if (userFromDb == null) {
            throw new IllegalArgumentException("User with such id: " + userId + " not exists");
        }
        User user = User.builder()
                .firstname(userUpdateRequest.getFirstname())
                .lastname(userUpdateRequest.getLastname())
                .email(userUpdateRequest.getEmail())
                .username(userUpdateRequest.getUsername())
                .password(userUpdateRequest.getPassword())
                .role(userUpdateRequest.getRole())
                .build();


        String message = "New values for book with id " + user.getId() + "\n" + userUpdateRequest;
        producer.sendMessage(user, LogType.UPDATE, message);
        emailService.sendTextEmail(EmailService.adminEmail, LogType.UPDATE.name(), message );
        baseUserService.saveUserToDatabase(user);
    }

    public void deleteUserById(Long userId) throws  IllegalArgumentException{
        User userFromDb = baseUserService.getUserFromDatabaseById(userId);
        if (userFromDb == null) {
            throw new IllegalArgumentException("User with such id: " + userId + " not exists");
        }

        String message = "New values for book with id " + userId + "\n" + "isDeleted: " + true;
        producer.sendMessage(userFromDb, LogType.UPDATE, message);
        emailService.sendTextEmail(EmailService.adminEmail, LogType.UPDATE.name(), message );
        baseUserService.softDeleteUserFromDatabaseById(userId);
    }

    public User getUserById(Long id) {
        return baseUserService.getUserFromDatabaseById(id);
    }


}
