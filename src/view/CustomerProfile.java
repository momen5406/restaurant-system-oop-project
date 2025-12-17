package view;

import model.Customer;

import javax.swing.*;
import java.awt.*;

public class CustomerProfile extends JFrame {

    public CustomerProfile(Customer customer) {

        setTitle("Customer Profile");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Customer Profile", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        infoPanel.add(new JLabel("Customer ID:"));
        infoPanel.add(new JLabel(String.valueOf(customer.getId())));

        infoPanel.add(new JLabel("Name:"));
        infoPanel.add(new JLabel(customer.getName()));

        infoPanel.add(new JLabel("Phone:"));
        infoPanel.add(new JLabel(customer.getPhoneNumber()));

        infoPanel.add(new JLabel("Loyalty Points:"));
        infoPanel.add(new JLabel(String.valueOf(customer.getLoyaltyPoints())));

        infoPanel.add(new JLabel("Total Orders:"));
        infoPanel.add(new JLabel(String.valueOf(customer.getOrderIds().size())));

        add(infoPanel, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());

        JPanel btnPanel = new JPanel();
        btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }
}