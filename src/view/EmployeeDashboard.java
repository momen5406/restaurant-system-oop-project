package view;

import controller.EmployeeController;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class EmployeeDashboard extends JFrame {
    private User loggedInUser;
    private EmployeeController employeeController;
    private JTabbedPane tabbedPane;
    
    // Customer Management Components
    private JTable customerTable;
    private DefaultTableModel customerTableModel;
    private JTextField custIdField, custNameField, custPhoneField;
    
    // Order Management Components
    private JTable orderTable;
    private DefaultTableModel orderTableModel;
    private JTextField orderCustomerIdField;
    private JTextArea orderItemsArea;
    
    // Bill Management Components
    private JTable billTable;
    private DefaultTableModel billTableModel;
    private JTextField billCustomerIdField;
    private JTextArea billDetailsArea;

    public EmployeeDashboard(User user) {
        this.loggedInUser = user;
        employeeController = new EmployeeController();
        
        setTitle("Restaurant | Employee Dashboard - Welcome " + loggedInUser.getUsername());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        tabbedPane = new JTabbedPane();
        tabbedPane.add("Manage Customers", createCustomerPanel());
        tabbedPane.add("Manage Orders", createOrderPanel());
        tabbedPane.add("Manage Bills", createBillPanel());
        tabbedPane.add("Search Customer", createSearchPanel());
        tabbedPane.add("Logout", logout());

        add(tabbedPane);
        
        // Refresh tables after UI is built
        SwingUtilities.invokeLater(() -> {
            refreshCustomerTable();
            refreshOrderTable();
            refreshBillTable();
        });
        
        setVisible(true);
    }
    
    // ========== CUSTOMER MANAGEMENT TAB ==========
    
    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table
        String[] columns = {"ID", "Name", "Phone Number", "Loyalty Points", "Total Orders"};
        customerTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        customerTable = new JTable(customerTableModel);
        customerTable.setRowHeight(25);
        panel.add(new JScrollPane(customerTable), BorderLayout.CENTER);

        // Control Panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input Form
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Customer Information"));
        
        custIdField = new JTextField();
        custNameField = new JTextField();
        custPhoneField = new JTextField();

        formPanel.add(new JLabel("ID:"));
        formPanel.add(custIdField);
        formPanel.add(new JLabel("Name:"));
        formPanel.add(custNameField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(custPhoneField);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton addButton = new JButton("Add Customer");
        JButton updateButton = new JButton("Update Selected");
        JButton deleteButton = new JButton("Delete Selected");

        addButton.addActionListener(e -> addCustomer());
        updateButton.addActionListener(e -> updateCustomer());
        deleteButton.addActionListener(e -> deleteCustomer());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        controlPanel.add(formPanel);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(buttonPanel);

        panel.add(controlPanel, BorderLayout.SOUTH);

        // Load selected customer when clicked
        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = customerTable.getSelectedRow();
                if (selectedRow != -1) {
                    custIdField.setText(String.valueOf(customerTableModel.getValueAt(selectedRow, 0)));
                    custNameField.setText((String) customerTableModel.getValueAt(selectedRow, 1));
                    custPhoneField.setText((String) customerTableModel.getValueAt(selectedRow, 2));
                }
            }
        });

        return panel;
    }

    private void addCustomer() {
        try {
            int id = Integer.parseInt(custIdField.getText().trim());
            String name = custNameField.getText().trim();
            String phone = custPhoneField.getText().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!");
                return;
            }

            employeeController.addCustomer(id, name, phone);
            refreshCustomerTable();
            clearCustomerFields();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID must be a number!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void updateCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to update!");
            return;
        }

        try {
            int id = (int) customerTableModel.getValueAt(selectedRow, 0);
            String name = custNameField.getText().trim();
            String phone = custPhoneField.getText().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!");
                return;
            }

            employeeController.updateCustomer(id, name, phone);
            refreshCustomerTable();
            clearCustomerFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete!");
            return;
        }

        int id = (int) customerTableModel.getValueAt(selectedRow, 0);
        String name = (String) customerTableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to delete customer: " + name + " (ID: " + id + ")?",
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            employeeController.deleteCustomer(id);
            refreshCustomerTable();
            clearCustomerFields();
        }
    }

    private void refreshCustomerTable() {
        customerTableModel.setRowCount(0);
        ArrayList<Customer> customers = employeeController.getAllCustomers();

        for (Customer customer : customers) {
            Object[] row = {
                customer.getId(),
                customer.getName(),
                customer.getPhoneNumber(),
                customer.getLoyaltyPoints(),
                customer.getOrderIds().size()
            };
            customerTableModel.addRow(row);
        }
    }

    private void clearCustomerFields() {
        custIdField.setText("");
        custNameField.setText("");
        custPhoneField.setText("");
        customerTable.clearSelection();
    }
    
    // ========== ORDER MANAGEMENT TAB ==========
    
    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table
        String[] columns = {"Order ID", "Customer ID", "Date", "Status", "Total (EGP)", "Items"};
        orderTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        orderTable = new JTable(orderTableModel);
        orderTable.setRowHeight(25);
        panel.add(new JScrollPane(orderTable), BorderLayout.CENTER);

        // Control Panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Order Actions"));
        
        orderCustomerIdField = new JTextField();
        JButton createOrderBtn = new JButton("Create New Order");
        
        formPanel.add(new JLabel("Customer ID:"));
        formPanel.add(orderCustomerIdField);
        formPanel.add(new JLabel(""));
        formPanel.add(createOrderBtn);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton cancelOrderBtn = new JButton("Cancel Selected");
        JButton deleteOrderBtn = new JButton("Delete Selected");
        JButton updateStatusBtn = new JButton("Update Status");
        JButton viewDetailsBtn = new JButton("View Details");
        JButton generateBillBtn = new JButton("Generate Bill");

        createOrderBtn.addActionListener(e -> showCreateOrderDialog());
        cancelOrderBtn.addActionListener(e -> cancelOrder());
        deleteOrderBtn.addActionListener(e -> deleteOrder());
        updateStatusBtn.addActionListener(e -> updateOrderStatus());
        viewDetailsBtn.addActionListener(e -> viewOrderDetails());
        generateBillBtn.addActionListener(e -> generateBillForOrder());

        buttonPanel.add(cancelOrderBtn);
        buttonPanel.add(deleteOrderBtn);
        buttonPanel.add(updateStatusBtn);
        buttonPanel.add(viewDetailsBtn);
        buttonPanel.add(generateBillBtn);

        // Order Details Area
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Order Details"));
        
        orderItemsArea = new JTextArea(10, 30);
        orderItemsArea.setEditable(false);
        orderItemsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane detailsScroll = new JScrollPane(orderItemsArea);
        detailsPanel.add(detailsScroll, BorderLayout.CENTER);

        controlPanel.add(formPanel);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(buttonPanel);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(detailsPanel);

        panel.add(controlPanel, BorderLayout.SOUTH);

        // Load selected order when clicked
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow != -1) {
                    String orderId = (String) orderTableModel.getValueAt(selectedRow, 0);
                    Order order = employeeController.getOrderById(orderId);
                    if (order != null) {
                        displayOrderItems(order);
                    }
                }
            }
        });

        return panel;
    }
    
    private void showCreateOrderDialog() {
        String customerIdText = orderCustomerIdField.getText().trim();
        if (customerIdText.isEmpty()) {
            customerIdText = JOptionPane.showInputDialog(this, "Enter Customer ID:");
            if (customerIdText == null || customerIdText.trim().isEmpty()) {
                return;
            }
        }
        
        try {
            int customerId = Integer.parseInt(customerIdText);
            
            // Check if customer exists
            Customer customer = employeeController.searchCustomerById(customerId);
            if (customer == null) {
                JOptionPane.showMessageDialog(this, "Customer not found with ID: " + customerId);
                return;
            }
            
            JDialog dialog = new JDialog(this, "Create New Order for " + customer.getName(), true);
            dialog.setSize(500, 400);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());
            
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Items Panel
            JPanel itemsPanel = new JPanel(new BorderLayout());
            itemsPanel.setBorder(BorderFactory.createTitledBorder("Order Items (Format: itemId,name,category,price,quantity)"));
            
            JTextArea itemsArea = new JTextArea(8, 40);
            itemsArea.setText("P001,Pizza,Main Course,120.0,2\nD001,Coke,Beverage,20.0,3\nB001,Burger,Main Course,80.0,1");
            JScrollPane itemsScroll = new JScrollPane(itemsArea);
            itemsPanel.add(itemsScroll, BorderLayout.CENTER);
            
            // Instructions Panel
            JPanel instructionsPanel = new JPanel(new BorderLayout());
            instructionsPanel.setBorder(BorderFactory.createTitledBorder("Special Instructions"));
            
            JTextArea instructionsArea = new JTextArea(3, 40);
            JScrollPane instructionsScroll = new JScrollPane(instructionsArea);
            instructionsPanel.add(instructionsScroll, BorderLayout.CENTER);
            
            // Button Panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
            JButton createBtn = new JButton("Create Order");
            JButton cancelBtn = new JButton("Cancel");
            
            createBtn.addActionListener(e -> {
                try {
                    String instructions = instructionsArea.getText().trim();
                    ArrayList<OrderItem> items = employeeController.parseOrderItems(itemsArea.getText());
                    
                    if (items.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Please add at least one item!");
                        return;
                    }
                    
                    boolean success = employeeController.makeOrder(customerId, items, instructions);
                    if (success) {
                        refreshOrderTable();
                        orderCustomerIdField.setText("");
                        dialog.dispose();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
                }
            });
            
            cancelBtn.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(createBtn);
            buttonPanel.add(cancelBtn);
            
            mainPanel.add(itemsPanel, BorderLayout.CENTER);
            mainPanel.add(instructionsPanel, BorderLayout.SOUTH);
            
            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.add(mainPanel, BorderLayout.CENTER);
            bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.add(bottomPanel);
            dialog.setVisible(true);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Customer ID must be a number!");
        }
    }
    
    private void cancelOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to cancel!");
            return;
        }
        
        String orderId = (String) orderTableModel.getValueAt(selectedRow, 0);
        String status = (String) orderTableModel.getValueAt(selectedRow, 3);
        
        if (status.equals("COMPLETED") || status.equals("CANCELLED")) {
            JOptionPane.showMessageDialog(this, "Cannot cancel " + status.toLowerCase() + " order!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to cancel order: " + orderId + "?",
            "Confirm Cancel", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = employeeController.cancelOrder(orderId);
            if (success) {
                refreshOrderTable();
                orderItemsArea.setText("");
            }
        }
    }
    
    private void deleteOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to delete!");
            return;
        }
        
        String orderId = (String) orderTableModel.getValueAt(selectedRow, 0);
        String status = (String) orderTableModel.getValueAt(selectedRow, 3);
        
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to delete order: " + orderId + "?\nStatus: " + status,
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = employeeController.deleteOrder(orderId);
            if (success) {
                refreshOrderTable();
                orderItemsArea.setText("");
            }
        }
    }
    
    private void updateOrderStatus() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order!");
            return;
        }
        
        String orderId = (String) orderTableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) orderTableModel.getValueAt(selectedRow, 3);
        
        String[] statusOptions = employeeController.getStatusOptions();
        String newStatus = (String) JOptionPane.showInputDialog(
            this,
            "Select new status for order " + orderId + ":",
            "Update Order Status",
            JOptionPane.QUESTION_MESSAGE,
            null,
            statusOptions,
            currentStatus
        );
        
        if (newStatus != null && !newStatus.equals(currentStatus)) {
            boolean success = employeeController.updateOrderStatus(orderId, newStatus);
            if (success) {
                refreshOrderTable();
            }
        }
    }
    
    private void viewOrderDetails() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order!");
            return;
        }
        
        String orderId = (String) orderTableModel.getValueAt(selectedRow, 0);
        String details = employeeController.getOrderDetails(orderId);
        
        JTextArea detailsArea = new JTextArea(details, 20, 50);
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(detailsArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Order Details: " + orderId));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Order Details", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void generateBillForOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order!");
            return;
        }
        
        String orderId = (String) orderTableModel.getValueAt(selectedRow, 0);
        String status = (String) orderTableModel.getValueAt(selectedRow, 3);
        
        if (status.equals("CANCELLED")) {
            JOptionPane.showMessageDialog(this, "Cannot generate bill for cancelled order!");
            return;
        }
        
        if (status.equals("COMPLETED")) {
            JOptionPane.showMessageDialog(this, "Bill already generated for this completed order!");
            return;
        }
        
        // Generate bill with default payment method (CASH)
        String billResult = employeeController.generateBill(orderId, "CASH");
        
        JTextArea billArea = new JTextArea(billResult, 20, 60);
        billArea.setEditable(false);
        billArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(billArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Generated Bill"));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Bill Generated", JOptionPane.INFORMATION_MESSAGE);
        
        refreshOrderTable();
        refreshBillTable();
    }
    
    private void refreshOrderTable() {
        orderTableModel.setRowCount(0);
        ArrayList<Order> orders = employeeController.getAllOrders();
        
        for (Order order : orders) {
            Object[] row = {
                order.getOrderId(),
                order.getCustomerId(),
                order.getOrderDate(),
                order.getStatus(),
                String.format("%.2f", order.getTotalAmount()),
                order.getItems().size()
            };
            orderTableModel.addRow(row);
        }
    }
    
    private void displayOrderItems(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("Order ID: ").append(order.getOrderId()).append("\n");
        sb.append("Customer ID: ").append(order.getCustomerId()).append("\n");
        sb.append("Status: ").append(order.getStatus()).append("\n");
        sb.append("Total: ").append(String.format("%.2f", order.getTotalAmount())).append(" EGP\n");
        sb.append("Instructions: ").append(order.getSpecialInstructions() != null ? order.getSpecialInstructions() : "None").append("\n\n");
        sb.append("ORDER ITEMS:\n");
        
        for (OrderItem item : order.getItems()) {
            double subtotal = item.getPrice() * item.getQuantity();
            sb.append("• ").append(item.getQuantity()).append("x ").append(item.getName())
              .append(" (").append(item.getCategory()).append(")\n");
            sb.append("  Price: ").append(String.format("%.2f", item.getPrice())).append(" EGP each\n");
            sb.append("  Subtotal: ").append(String.format("%.2f", subtotal)).append(" EGP\n");
            sb.append("----------------------------------------\n");
        }
        
        orderItemsArea.setText(sb.toString());
    }
    
    // ========== BILL MANAGEMENT TAB ==========
    
    private JPanel createBillPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table
        String[] columns = {"Bill ID", "Order ID", "Customer ID", "Customer Name", "Date", "Total Amount"};
        billTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        billTable = new JTable(billTableModel);
        billTable.setRowHeight(25);
        panel.add(new JScrollPane(billTable), BorderLayout.CENTER);

        // Control Panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Bill Actions"));
        
        billCustomerIdField = new JTextField();
        JButton searchCustomerBillsBtn = new JButton("Search Customer Bills");
        
        formPanel.add(new JLabel("Customer ID:"));
        formPanel.add(billCustomerIdField);
        formPanel.add(new JLabel(""));
        formPanel.add(searchCustomerBillsBtn);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton viewBillBtn = new JButton("View Bill Details");
        JButton searchBillBtn = new JButton("Search Bill by ID");
        JButton deleteBillBtn = new JButton("Delete Selected");

        searchCustomerBillsBtn.addActionListener(e -> showCustomerBills());
        viewBillBtn.addActionListener(e -> viewBillDetails());
        searchBillBtn.addActionListener(e -> searchBill());
        deleteBillBtn.addActionListener(e -> deleteBill());

        buttonPanel.add(viewBillBtn);
        buttonPanel.add(searchBillBtn);
        buttonPanel.add(deleteBillBtn);

        // Bill Details Area
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Bill Details"));
        
        billDetailsArea = new JTextArea(10, 30);
        billDetailsArea.setEditable(false);
        billDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane detailsScroll = new JScrollPane(billDetailsArea);
        detailsPanel.add(detailsScroll, BorderLayout.CENTER);

        controlPanel.add(formPanel);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(buttonPanel);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(detailsPanel);

        panel.add(controlPanel, BorderLayout.SOUTH);

        // Load selected bill when clicked
        billTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = billTable.getSelectedRow();
                if (selectedRow != -1) {
                    String billId = (String) billTableModel.getValueAt(selectedRow, 0);
                    String details = employeeController.getBillDetails(billId);
                    billDetailsArea.setText(details);
                }
            }
        });

        return panel;
    }
    
    private void viewBillDetails() {
        int selectedRow = billTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill!");
            return;
        }
        
        String billId = (String) billTableModel.getValueAt(selectedRow, 0);
        String details = employeeController.getBillDetails(billId);
        
        JTextArea detailsArea = new JTextArea(details, 20, 60);
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(detailsArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Bill Details: " + billId));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Bill Details", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void searchBill() {
        String billId = JOptionPane.showInputDialog(this, "Enter Bill ID to search:");
        
        if (billId != null && !billId.trim().isEmpty()) {
            Bill bill = employeeController.getBillById(billId.trim());
            if (bill != null) {
                for (int i = 0; i < billTableModel.getRowCount(); i++) {
                    if (billTableModel.getValueAt(i, 0).equals(billId.trim())) {
                        billTable.setRowSelectionInterval(i, i);
                        billTable.scrollRectToVisible(billTable.getCellRect(i, 0, true));
                        
                        String details = employeeController.getBillDetails(billId.trim());
                        JTextArea detailsArea = new JTextArea(details, 20, 60);
                        detailsArea.setEditable(false);
                        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                        
                        JScrollPane scrollPane = new JScrollPane(detailsArea);
                        scrollPane.setBorder(BorderFactory.createTitledBorder("Bill Found: " + billId));
                        
                        JOptionPane.showMessageDialog(this, scrollPane, "Bill Found", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
            }
            JOptionPane.showMessageDialog(this, "Bill not found!");
        }
    }
    
    private void showCustomerBills() {
        String customerIdText = billCustomerIdField.getText().trim();
        if (customerIdText.isEmpty()) {
            customerIdText = JOptionPane.showInputDialog(this, "Enter Customer ID to view bills:");
            if (customerIdText == null || customerIdText.trim().isEmpty()) {
                return;
            }
        }
        
        try {
            int customerId = Integer.parseInt(customerIdText.trim());
            ArrayList<Bill> customerBills = employeeController.getBillsByCustomer(customerId);
            
            if (customerBills.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No bills found for customer ID: " + customerId);
                return;
            }
            
            // Create dialog to display customer bills
            JDialog billsDialog = new JDialog(this, "Customer Bills - ID: " + customerId, true);
            billsDialog.setSize(600, 400);
            billsDialog.setLocationRelativeTo(this);
            billsDialog.setLayout(new BorderLayout());
            
            // Create table
            String[] columns = {"Bill ID", "Order ID", "Date", "Total Amount"};
            DefaultTableModel model = new DefaultTableModel(columns, 0);
            
            for (Bill bill : customerBills) {
                Object[] row = {
                    bill.getBillId(),
                    bill.getOrderId(),
                    bill.getBillDate(),
                    String.format("%.2f EGP", bill.getFinalAmount())
                };
                model.addRow(row);
            }
            
            JTable billsTable = new JTable(model);
            billsTable.setRowHeight(25);
            
            JScrollPane scrollPane = new JScrollPane(billsTable);
            scrollPane.setBorder(BorderFactory.createTitledBorder("Bills for Customer ID: " + customerId));
            
            // Add view button
            JButton viewBtn = new JButton("View Selected Bill");
            viewBtn.addActionListener(e -> {
                int selectedRow = billsTable.getSelectedRow();
                if (selectedRow != -1) {
                    String billId = (String) model.getValueAt(selectedRow, 0);
                    String details = employeeController.getBillDetails(billId);
                    
                    JTextArea detailsArea = new JTextArea(details, 20, 60);
                    detailsArea.setEditable(false);
                    detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                    
                    JScrollPane detailsScroll = new JScrollPane(detailsArea);
                    detailsScroll.setBorder(BorderFactory.createTitledBorder("Bill Details"));
                    
                    JOptionPane.showMessageDialog(billsDialog, detailsScroll, "Bill Details", JOptionPane.INFORMATION_MESSAGE);
                }
            });
            
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(viewBtn);
            
            billsDialog.add(scrollPane, BorderLayout.CENTER);
            billsDialog.add(buttonPanel, BorderLayout.SOUTH);
            billsDialog.setVisible(true);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Customer ID (number)!");
        }
    }
    
    private void deleteBill() {
        int selectedRow = billTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill to delete!");
            return;
        }
        
        String billId = (String) billTableModel.getValueAt(selectedRow, 0);
        String orderId = (String) billTableModel.getValueAt(selectedRow, 1);
        String customerName = (String) billTableModel.getValueAt(selectedRow, 3);
        
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to delete bill?\n\n" +
            "Bill ID: " + billId + "\n" +
            "Order ID: " + orderId + "\n" +
            "Customer: " + customerName + "\n\n" +
            "This will change the order status back to 'SERVED'!",
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = employeeController.deleteBill(billId);
            if (success) {
                refreshBillTable();
                billDetailsArea.setText("");
            }
        }
    }
    
    private void refreshBillTable() {
        billTableModel.setRowCount(0);
        ArrayList<Bill> bills = employeeController.getAllBills();
        
        for (Bill bill : bills) {
            Object[] row = {
                bill.getBillId(),
                bill.getOrderId(),
                bill.getCustomerId(),
                bill.getCustomerName(),
                bill.getBillDate(),
                String.format("%.2f", bill.getFinalAmount())
            };
            billTableModel.addRow(row);
        }
    }
    
    // ========== SEARCH CUSTOMER TAB ==========
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search Panel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Customer"));

        // Input row
        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JComboBox<String> searchType = new JComboBox<>(new String[]{"By Name", "By ID"});
        
        inputRow.add(new JLabel("Search:"));
        inputRow.add(searchField);
        inputRow.add(Box.createHorizontalStrut(10));
        inputRow.add(new JLabel("Search By:"));
        inputRow.add(searchType);

        // Button row
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchButton = new JButton("Search");

        buttonRow.add(searchButton);

        searchPanel.add(inputRow);
        searchPanel.add(Box.createVerticalStrut(10));
        searchPanel.add(buttonRow);

        // Results Panel
        JTextArea resultArea = new JTextArea(15, 50);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Search Results"));

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Search Action
        searchButton.addActionListener(e -> {
            String searchText = searchField.getText().trim();
            String searchBy = (String) searchType.getSelectedItem();
            
            if (searchText.isEmpty()) {
                resultArea.setText("Please enter search criteria.");
                return;
            }

            Customer foundCustomer = null;
            
            if (searchBy.equals("By Name")) {
                foundCustomer = employeeController.searchCustomer(searchText);
            } else if (searchBy.equals("By ID")) {
                try {
                    int id = Integer.parseInt(searchText);
                    foundCustomer = employeeController.searchCustomerById(id);
                } catch (NumberFormatException ex) {
                    resultArea.setText("Invalid ID format. Please enter a number.");
                    return;
                }
            }

            if (foundCustomer != null) {
                resultArea.setText(formatCustomerDetails(foundCustomer));
            } else {
                resultArea.setText("No customer found with the given criteria.");
            }
        });

        return panel;
    }

    private String formatCustomerDetails(Customer customer) {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════\n");
        sb.append("                     CUSTOMER DETAILS                     \n");
        sb.append("═══════════════════════════════════════════════════════════\n");
        sb.append(String.format("ID:            %d\n", customer.getId()));
        sb.append(String.format("Name:          %s\n", customer.getName()));
        sb.append(String.format("Phone:         %s\n", customer.getPhoneNumber()));
        sb.append(String.format("Loyalty Points:%d\n", customer.getLoyaltyPoints()));
        sb.append(String.format("Total Orders:  %d\n", customer.getOrderIds().size()));
        sb.append("═══════════════════════════════════════════════════════════\n");
        
        return sb.toString();
    }

    private JPanel logout() {
        JPanel panel = new JPanel();

        JButton logout = new JButton("Logout");
        panel.add(logout);

        logout.addActionListener(e -> {
            LoginFrame login = new LoginFrame();
            login.show();
            this.dispose();
        });

        return panel;
    }
}