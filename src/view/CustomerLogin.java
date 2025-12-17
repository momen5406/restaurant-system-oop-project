package view;

import javax.swing.*;
import java.awt.*;

public class CustomerLogin extends JFrame {

    private JTextField phoneField;

    public CustomerLogin() {
        setTitle("Customer Login");
        setSize(400, 200); // Smaller size since fewer fields
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel title = new JLabel("Login Customer", SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel formPanel = new JPanel(new GridLayout(2, 1, 10, 10)); // 2 rows (Label + Field)
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        formPanel.add(new JLabel("Enter your Phone Number:"));
        phoneField = new JTextField();
        formPanel.add(phoneField);

        JButton loginBtn = new JButton("Login");

        JPanel btnPanel = new JPanel();
        btnPanel.add(loginBtn);

        add(title, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        loginBtn.addActionListener(e -> performLogin());
    }

    private void performLogin() {

    }
}