package model;

public abstract class User {
    private String id;
    private String username;
    private String password;
    private String role;

    public User(String id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public void updateInfo(String newUsername, String newPassword) {
        this.username = newUsername;
        this.password = newPassword;
        System.out.println("User Info updated for ID: " + this.id);
    }
}
