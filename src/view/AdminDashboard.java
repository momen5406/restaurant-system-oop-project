package view;

import controller.AdminController;
import controller.UserController;
import model.Employee;
import model.Offer;
import model.User;
import util.FileManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class AdminDashboard extends JFrame {
    private User loggedInUser;
    private AdminController adminController;
    private JTabbedPane tabbedPane;

    private JTable employeeTable, offerTable;
    private DefaultTableModel tableModel, offerTableModel;
    private JTextField nameField, jobField, salaryField, idField, passwordField, offerIdField, offerNameField, discountField;

    public AdminDashboard(User user) {
        this.loggedInUser = user;
        adminController = new AdminController();
        setTitle("Restaurant | Admin Dashboard - Welcome " + user.getUsername());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        tabbedPane.add("Manage Employees", createEmployeePanel());
        tabbedPane.add("Manage Meals", createMealPanel());
        tabbedPane.add("Reports", createReportPanel());
        tabbedPane.add("Special Offers", createOffersPanel());
        tabbedPane.add("Marketing", createMarketingPanel());
        tabbedPane.add("My Profile", createProfilePanel());

        add(tabbedPane);
        setVisible(true);
    }

    // Employee Page
    private JPanel createEmployeePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Search Bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        JButton resetBtn = new JButton("Show All");

        searchPanel.add(new JLabel("Search (ID or Name):"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(resetBtn);

        panel.add(searchPanel, BorderLayout.NORTH);


        // Table
        String[] columns = {"ID", "UserName", "Role", "Job Title", "Salary"};
        tableModel = new DefaultTableModel(columns, 0);
        employeeTable = new JTable(tableModel);

        refreshEmployeeTable();
        panel.add(new JScrollPane(employeeTable), BorderLayout.CENTER);

        // Input Form ---
        JPanel formPanel = new JPanel(new GridLayout(2, 6)); // 2 rows, multiple cols

        idField = new JTextField();
        nameField = new JTextField();
        passwordField = new JTextField();
        jobField = new JTextField();
        salaryField = new JTextField();

        JButton addButton = new JButton("Add");
        JButton deleteButton = new JButton("Delete");

        // Add labels and fields to the form
        formPanel.add(new JLabel("ID:")); formPanel.add(idField);
        formPanel.add(new JLabel("Name:")); formPanel.add(nameField);
        formPanel.add(new JLabel("Pass:")); formPanel.add(passwordField);
        formPanel.add(new JLabel("Job:")); formPanel.add(jobField);
        formPanel.add(new JLabel("Salary:")); formPanel.add(salaryField);
        formPanel.add(addButton); formPanel.add(deleteButton);

        panel.add(formPanel, BorderLayout.SOUTH);


        // Search and Reset Button
        searchBtn.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (query.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a name or ID.");
                return;
            }

            Employee foundEmp = adminController.searchEmployee(query);

            if (foundEmp != null) {
                tableModel.setRowCount(0);
                Object[] row = {
                        foundEmp.getId(),
                        foundEmp.getUsername(),
                        foundEmp.getRole(),
                        foundEmp.getJobTitle(),
                        foundEmp.getSalary()
                };
                tableModel.addRow(row);
            } else {
                JOptionPane.showMessageDialog(this, "Employee Not Found.");
                refreshEmployeeTable();
            }
        });

        resetBtn.addActionListener(e -> {
            searchField.setText("");
            refreshEmployeeTable();
        });

        // Add Employee to the file and display it
        addButton.addActionListener(e -> {
            try {
                String id = idField.getText();
                String name = nameField.getText();
                String pass = passwordField.getText();
                String job = jobField.getText();
                double salary = Double.parseDouble(salaryField.getText());

                adminController.addEmployee(id, name, pass, job, salary);

                refreshEmployeeTable();
                clearFields();
                JOptionPane.showMessageDialog(this, "Employee Added!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: Check your inputs (Salary must be number)");
            }
        });

        // Delete Employee from file and from table
        deleteButton.addActionListener(e -> {
            int selectedRow = employeeTable.getSelectedRow();
            if ( selectedRow != -1 ) {
                String idToDelete = (String) tableModel.getValueAt(selectedRow, 0);
                adminController.deleteEmployee(idToDelete);
                refreshEmployeeTable();
                JOptionPane.showMessageDialog(this, "Employee Deleted!");
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            }
        });

        return panel;
    }

    private void refreshEmployeeTable() {
        // Clear existing data
        tableModel.setRowCount(0);

        // Get fresh list from Controller
        ArrayList<Employee> employees = adminController.getAllEmployees();

        // Add rows to table
        for (Employee e : employees) {
            Object[] row = {e.getId(), e.getUsername(), e.getRole(), e.getJobTitle(), e.getSalary()};
            tableModel.addRow(row);
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        passwordField.setText("");
        jobField.setText("");
        salaryField.setText("");
    }

    // model.Meal Page
    private JPanel createMealPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("model.Meal Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JButton openMealsButton = new JButton("Open model.Meal Management");
        openMealsButton.setFont(new Font("Arial", Font.PLAIN, 14));
        openMealsButton.setPreferredSize(new Dimension(200, 40));
        openMealsButton.addActionListener(e -> {
            new MealsGUI();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(openMealsButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);

        return panel;
    }

    // Generate Report Page
    private JPanel createReportPanel() {
        JPanel panel = new JPanel();

        JButton genEmpReport = new JButton("Generate Employee Reports");
        JButton genCustReport = new JButton("Generate Customers Reports");
        JTextArea reportArea = new JTextArea(10, 40);
        reportArea.setLineWrap(true);

        genEmpReport.addActionListener(e -> {
            String report = adminController.EmployeesReport();
            reportArea.setText(report);
            reportArea.setFont(new Font("Arial", Font.BOLD, 18));
        });

        genCustReport.addActionListener(e -> {
            String report = adminController.CustomersReport();
            reportArea.setText(report);
            reportArea.setFont(new Font("Arial", Font.BOLD, 18));
        });

        panel.add(genEmpReport);
        panel.add(genCustReport);
        panel.add(new JScrollPane(reportArea));
        return panel;
    }

    private JPanel createOffersPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"Id", "Offer Name", "Discount"};
        offerTableModel = new DefaultTableModel(columns, 0);
        offerTable = new JTable(offerTableModel);

        refreshOfferTable();
        panel.add(new JScrollPane(offerTable), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridLayout(2, 1));

        offerIdField = new JTextField();
        offerNameField = new JTextField();
        discountField = new JTextField();

        JButton addButton = new JButton("Add");
        JButton deleteButton = new JButton("Delete");

        // Add labels and fields to the form
        formPanel.add(new JLabel("Offer Id:")); formPanel.add(offerIdField);
        formPanel.add(new JLabel("Offer Name:")); formPanel.add(offerNameField);
        formPanel.add(new JLabel("Discount:")); formPanel.add(discountField);
        formPanel.add(addButton); formPanel.add(deleteButton);

        panel.add(formPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            try {
                String offerId = offerIdField.getText();
                String offerName = offerNameField.getText();
                String discount = discountField.getText() + "%";

                adminController.addOffer(offerId, offerName, discount);

                refreshOfferTable();
                clearOfferFields();
                JOptionPane.showMessageDialog(this, "Offer Added!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: Check your inputs");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = offerTable.getSelectedRow();
            if ( selectedRow != -1 ) {
                String idToDelete = (String) offerTable.getValueAt(selectedRow, 0);
                adminController.deleteOffer(idToDelete);
                refreshOfferTable();
                JOptionPane.showMessageDialog(this, "Offer Deleted!");
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            }
        });

        return panel;
    }

    private void refreshOfferTable() {
        // Clear existing data
        offerTableModel.setRowCount(0);

        // Get fresh list from Controller
        ArrayList<Offer> offers = FileManager.loadOffers();

        ArrayList<Offer> offersList = new ArrayList<>(offers);

        // Add rows to table
        for (Offer o : offersList) {
            Object[] row = {o.getId(), o.getName(), o.getDiscount()};
            offerTableModel.addRow(row);
        }
    }

    private void clearOfferFields() {
        offerNameField.setText("");
        discountField.setText("");
    }

    private JPanel createMarketingPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10)); // Split into 3 sections

        // Marketing News
        JPanel newsPanel = new JPanel(new FlowLayout());
        newsPanel.setBorder(BorderFactory.createTitledBorder("1. Send Marketing News"));
        JTextField newsField = new JTextField(20);
        JButton btnNews = new JButton("Send Broadcast");

        newsPanel.add(new JLabel("Message:")); newsPanel.add(newsField);
        newsPanel.add(btnNews);

        // Loyalt Rules
        JPanel loyaltyPanel = new JPanel(new FlowLayout());
        loyaltyPanel.setBorder(BorderFactory.createTitledBorder("2. Loyalty & Reward Rules"));
        JTextField pointsField = new JTextField(5);
        JTextField rewardField = new JTextField(10);
        JButton btnLoyalty = new JButton("Update Rules");

        loyaltyPanel.add(new JLabel("Points Needed:")); loyaltyPanel.add(pointsField);
        loyaltyPanel.add(new JLabel("Reward Prize:")); loyaltyPanel.add(rewardField);
        loyaltyPanel.add(btnLoyalty);


        // 1. Marketing News
        btnNews.addActionListener(e -> {
            adminController.sendMarketingMessage(newsField.getText());
            JOptionPane.showMessageDialog(this, "News Sent to all Customers!");
        });

        // 2. Loyalty Rules
        btnLoyalty.addActionListener(e -> {
            int points = Integer.parseInt(pointsField.getText());
            String prize = rewardField.getText();
            adminController.setLoyaltyProgram(points, prize);
            JOptionPane.showMessageDialog(this, "Loyalty Rules Updated!");
        });

        panel.add(newsPanel);
        panel.add(loyaltyPanel);

        return panel;
    }


    private JPanel createProfilePanel() {
        JPanel panel = new JPanel();

        JLabel userLabel = new JLabel("New Username: ");
        JTextField userField = new JTextField(10);
        JLabel passwordLabel = new JLabel("New Password: ");
        JPasswordField passwordField = new JPasswordField(10);
        JButton updateButton = new JButton("Update my Info");
        JButton logout = new JButton("Logout");

        panel.add(userLabel);
        panel.add(userField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(updateButton);
        panel.add(logout);

        updateButton.addActionListener(e -> {
            String newUser = userField.getText();
            String newPass = new String(passwordField.getPassword());

            controller.UserController userCtrl = new controller.UserController();

            boolean success = userCtrl.updateUserInfo(this.loggedInUser, newUser, newPass);

            if ( success ) {
                JOptionPane.showMessageDialog(this, "Profile Updated! Please login again.");
                new LoginFrame().show();
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error updating profile.");
            }
        });

        logout.addActionListener(e -> {
            LoginFrame login = new LoginFrame();
            login.show();
            this.dispose();
        });

        return panel;
    }
}
