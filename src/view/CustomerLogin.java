package view;

import controller.CustomerController;
import model.Customer;
import model.User;

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
        String phone = phoneField.getText();

        Customer customer = CustomerController.login(phone);

        if (customer == null) {
            JOptionPane.showMessageDialog(this, "Phone not found!");
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Welcome " + customer.getName()
        );

        this.dispose();

        new CustomerProfile(customer).setVisible(true);
    }
}