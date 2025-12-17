package view;

import controller.UserController;
import model.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame {

    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton customerButton;
    private UserController userController;

    public LoginFrame() {
        userController = new UserController();

        frame = new JFrame("Restaurant | Login");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 250);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel userLabel = new JLabel("Username:");
        usernameField = new JTextField(15);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(15);

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

        loginButton = new JButton("Login");
        loginButton.addActionListener(e -> performLogin());

        customerButton = new JButton("login as customer");
        customerButton.addActionListener(e -> openCustomer());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(customerButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.setContentPane(mainPanel);
    }

    public void show() {
        frame.setVisible(true);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        User user = userController.login(username, password);

        if (user == null) {
            JOptionPane.showMessageDialog(frame, "Invalid username or password");
            return;
        }

        JOptionPane.showMessageDialog(
                frame,
                "Welcome " + user.getRole() + ": " + user.getUsername()
        );

        frame.dispose();

        SwingUtilities.invokeLater(() -> {
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                new AdminDashboard(user);
            } else if ("EMPLOYEE".equalsIgnoreCase(user.getRole())) {
                new EmployeeDashboard(user);
            } else {
                JOptionPane.showMessageDialog(null, "Unknown user role!");
            }
        });
    }

    private void openCustomer() {
        frame.dispose();
        new CustomerLogin().setVisible(true);
    }
}
