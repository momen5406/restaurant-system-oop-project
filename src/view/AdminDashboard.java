package view;

import controller.AdminController;
import model.Employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class AdminDashboard extends JFrame {
    private AdminController adminController;
    private JTabbedPane tabbedPane;

    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, jobField, salaryField, idField, passwordField;

    public AdminDashboard() {
        adminController = new AdminController();
        setTitle("Restaurant | Admin Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        tabbedPane.add("Manage Employees", createEmployeePanel());
        tabbedPane.add("Manage Meals", createMealPanel());
        tabbedPane.add("Reports", createReportPanel());

        add(tabbedPane);
        setVisible(true);
    }

    // Employee Page
    private JPanel createEmployeePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "UserName", "Role", "Job Title", "Salary"};
        tableModel = new DefaultTableModel(columns, 0);
        employeeTable = new JTable(tableModel);

        refreshEmployeeTable();
        panel.add(new JScrollPane(employeeTable), BorderLayout.CENTER);

        // --- SOUTH: The Input Form ---
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

    // Meal Page
    private JPanel createMealPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Meal Management Table goes here..."));
        return panel;
    }

    // Generate Report Page
    private JPanel createReportPanel() {
        JPanel panel = new JPanel();

        JButton generateButton = new JButton("Generate Employee Reports");
        JTextArea reportArea = new JTextArea(10, 40);
        reportArea.setLineWrap(true);

        generateButton.addActionListener(e -> {
            String report = adminController.EmployeesReport();
            reportArea.setText(report);
            reportArea.setFont(new Font("Arial", Font.BOLD, 18));
        });

        panel.add(generateButton);
        panel.add(new JScrollPane(reportArea));
        return panel;
    }
}
