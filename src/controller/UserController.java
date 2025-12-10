package controller;

import model.User;
import util.FileManager;

import java.util.ArrayList;

public class UserController {
    // Login
    public User login(String inputUsername, String inputPassword) {
        ArrayList<User> allUsers = FileManager.loadUsers();

        for ( User user: allUsers ) {
            if ( user.getUsername().equals(inputUsername) && user.getPassword().equals(inputPassword) ) {
                return user;
            }
        }
        return null;
    }

    // Update User Info
    public boolean updateUserInfo(User currentUser, String newUsername, String newPassword) {
        ArrayList<User> allUsers = FileManager.loadUsers();
        boolean found = false;

        // Find logged-in user exist
        for ( User user: allUsers ) {
            if ( user.getId().equals(currentUser.getId()) ) {
                user.setUsername(newUsername);
                user.setPassword(newPassword);
                found = true;
                break;
            }
        }

        // Save changes to the user.txt
        if ( found ) {
            FileManager.saveUsers(allUsers);
        }

        return found;
    }
}
