package view;

import controller.UserController;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private UserController userController;

    public LoginFrame() {
        userController = new UserController();

        frame = new JFrame();
        frame.setTitle("Restaurant | Login");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400,250);
        frame.setLocationRelativeTo(null);

        // Main Container with padding of 20px
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel userLabel = new JLabel("Username: ");
        usernameField = new JTextField(15);

        JLabel passwordLabel = new JLabel("Password: ");
        passwordField = new JPasswordField(15);

        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);


        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        frame.setContentPane(mainPanel);
    }

    public void show() {
        frame.setVisible(true);
    }

    private void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        User user = userController.login(username, password);

        if ( user != null ) {
            JOptionPane.showMessageDialog(this.frame, "Welcome " + user.getRole() + ": " + user.getUsername());
            if ( user.getRole().equals("ADMIN") ) {
                this.frame.dispose();
                new AdminDashboard();
            } else if (user.getRole().equals("EMPLOYEE")) {
                // TODO: Open Employee Dashboard
                System.out.println("Opening Employee Dashboard..");
            }
            this.frame.dispose();
        } else {
            JOptionPane.showMessageDialog(this.frame, "Invalid username or Password");
        }
    }
}
