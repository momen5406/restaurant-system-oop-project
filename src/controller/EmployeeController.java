package controller;

import model.*;
import util.FileManager;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class EmployeeController {
    
    // ========== CUSTOMER MANAGEMENT ==========
    
    public void addCustomer(int id, String name, String phoneNumber) {
        try {
            System.out.println("[Controller] Adding customer ID: " + id);
            
            ArrayList<Customer> customers = FileManager.loadCustomers();
            
            // Check if customer ID already exists
            for (Customer customer : customers) {
                if (customer.getId() == id) {
                    JOptionPane.showMessageDialog(null, "Customer ID already exists!");
                    return;
                }
            }
            
            // Create and save customer
            Customer newCustomer = new Customer(id, name, phoneNumber);
            customers.add(newCustomer);
            FileManager.saveCustomers(customers);
            
            JOptionPane.showMessageDialog(null, "Customer added successfully!");
            
        } catch (Exception e) {
            System.err.println("[Controller] Error adding customer: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public void deleteCustomer(int id) {
        try {
            System.out.println("[Controller] Deleting customer ID: " + id);
            ArrayList<Customer> customers = FileManager.loadCustomers();
            boolean removed = customers.removeIf(customer -> customer.getId() == id);

            if (removed) {
                FileManager.saveCustomers(customers);
                JOptionPane.showMessageDialog(null, "Customer deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Customer not found!");
            }
        } catch (Exception e) {
            System.err.println("[Controller] Error deleting customer: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public void updateCustomer(int id, String name, String phoneNumber) {
        try {
            System.out.println("[Controller] Updating customer ID: " + id);
            ArrayList<Customer> customers = FileManager.loadCustomers();
            boolean found = false;

            for (Customer customer : customers) {
                if (customer.getId() == id) {
                    customer.setName(name);
                    customer.setPhoneNumber(phoneNumber);
                    found = true;
                    break;
                }
            }

            if (found) {
                FileManager.saveCustomers(customers);
                JOptionPane.showMessageDialog(null, "Customer updated successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Customer not found!");
            }
        } catch (Exception e) {
            System.err.println("[Controller] Error updating customer: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public ArrayList<Customer> getAllCustomers() {
        try {
            ArrayList<Customer> customers = FileManager.loadCustomers();
            return customers;
        } catch (Exception e) {
            System.err.println("[Controller] Error loading customers: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Customer searchCustomer(String name) {
        try {
            ArrayList<Customer> customers = getAllCustomers();
            for (Customer customer : customers) {
                if (customer.getName().equalsIgnoreCase(name)) {
                    return customer;
                }
            }
            return null;
        } catch (Exception e) {
            System.err.println("[Controller] Error searching customer: " + e.getMessage());
            return null;
        }
    }
    
    public Customer searchCustomerById(int id) {
        try {
            ArrayList<Customer> customers = getAllCustomers();
            for (Customer customer : customers) {
                if (customer.getId() == id) {
                    return customer;
                }
            }
            return null;
        } catch (Exception e) {
            System.err.println("[Controller] Error searching customer by ID: " + e.getMessage());
            return null;
        }
    }
    
    // ========== ORDER MANAGEMENT ==========
    
    public boolean makeOrder(int customerId, ArrayList<OrderItem> items, String instructions) {
        try {
            System.out.println("[Controller] Creating order for customer ID: " + customerId);
            
            // Check if customer exists
            Customer customer = searchCustomerById(customerId);
            if (customer == null) {
                JOptionPane.showMessageDialog(null, "Error: Customer not found!");
                return false;
            }
            
            // Check if items are provided
            if (items == null || items.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Error: Order must have at least one item!");
                return false;
            }
            
            // Calculate total amount
            double totalAmount = 0;
            for (OrderItem item : items) {
                double subtotal = item.getPrice() * item.getQuantity();
                totalAmount += subtotal;
            }
            
            // Generate order ID
            String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            
            // Create order
            Date orderDate = new Date();
            Order newOrder = new Order(orderId, customerId, orderDate, items, "PENDING", totalAmount);
            if (instructions != null && !instructions.trim().isEmpty()) {
                newOrder.setSpecialInstructions(instructions);
            }
            
            // Save order
            ArrayList<Order> orders = FileManager.loadOrders();
            orders.add(newOrder);
            FileManager.saveOrders(orders);
            
            // Update customer with order ID
            customer.addOrderId(orderId);
            int loyaltyPointsEarned = (int)(totalAmount / 10);
            if (loyaltyPointsEarned > 0) {
                customer.addLoyaltyPoints(loyaltyPointsEarned);
            }
            
            // Update customer in system
            ArrayList<Customer> customers = getAllCustomers();
            for (int i = 0; i < customers.size(); i++) {
                if (customers.get(i).getId() == customerId) {
                    customers.set(i, customer);
                    break;
                }
            }
            FileManager.saveCustomers(customers);
            
            System.out.println("[Controller] Order created successfully! Order ID: " + orderId);
            return true;
            
        } catch (Exception e) {
            System.err.println("[Controller] ERROR in makeOrder: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error creating order: " + e.getMessage());
            return false;
        }
    }
    
    public boolean cancelOrder(String orderId) {
        try {
            System.out.println("[Controller] Canceling order: " + orderId);
            ArrayList<Order> orders = FileManager.loadOrders();
            
            for (Order order : orders) {
                if (order.getOrderId().equals(orderId)) {
                    if (order.getStatus().equals("COMPLETED")) {
                        JOptionPane.showMessageDialog(null, "Cannot cancel a completed order!");
                        return false;
                    }
                    if (order.getStatus().equals("CANCELLED")) {
                        JOptionPane.showMessageDialog(null, "Order is already cancelled!");
                        return false;
                    }
                    
                    order.setStatus("CANCELLED");
                    FileManager.saveOrders(orders);
                    return true;
                }
            }
            
            JOptionPane.showMessageDialog(null, "Order not found!");
            return false;
            
        } catch (Exception e) {
            System.err.println("[Controller] Error cancelling order: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error cancelling order: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteOrder(String orderId) {
        try {
            System.out.println("[Controller] Deleting order: " + orderId);
            ArrayList<Order> orders = FileManager.loadOrders();
            
            // Find the order
            Order orderToDelete = null;
            for (Order order : orders) {
                if (order.getOrderId().equals(orderId)) {
                    orderToDelete = order;
                    break;
                }
            }
            
            if (orderToDelete == null) {
                JOptionPane.showMessageDialog(null, "Order not found!");
                return false;
            }
            
            // Delete the order
            boolean removed = orders.removeIf(order -> order.getOrderId().equals(orderId));
            if (removed) {
                FileManager.saveOrders(orders);
                
                // Remove order from customer's history
                Customer customer = searchCustomerById(orderToDelete.getCustomerId());
                if (customer != null) {
                    customer.removeOrderId(orderId);
                    ArrayList<Customer> customers = getAllCustomers();
                    for (int i = 0; i < customers.size(); i++) {
                        if (customers.get(i).getId() == customer.getId()) {
                            customers.set(i, customer);
                            break;
                        }
                    }
                    FileManager.saveCustomers(customers);
                }
                
                return true;
            }
            return false;
            
        } catch (Exception e) {
            System.err.println("[Controller] Error deleting order: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error deleting order: " + e.getMessage());
            return false;
        }
    }
    
    public Order getOrderById(String orderId) {
        try {
            ArrayList<Order> orders = FileManager.loadOrders();
            for (Order order : orders) {
                if (order.getOrderId().equals(orderId)) {
                    return order;
                }
            }
            return null;
        } catch (Exception e) {
            System.err.println("[Controller] Error getting order: " + e.getMessage());
            return null;
        }
    }
    
    public ArrayList<Order> getAllOrders() {
        try {
            ArrayList<Order> orders = FileManager.loadOrders();
            return orders;
        } catch (Exception e) {
            System.err.println("[Controller] Error loading orders: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public boolean updateOrderStatus(String orderId, String newStatus) {
        try {
            System.out.println("[Controller] Updating order status: " + orderId + " -> " + newStatus);
            ArrayList<Order> orders = FileManager.loadOrders();
            
            for (Order order : orders) {
                if (order.getOrderId().equals(orderId)) {
                    if (order.getStatus().equals("CANCELLED")) {
                        JOptionPane.showMessageDialog(null, "Cannot update a cancelled order!");
                        return false;
                    }
                    
                    // Add loyalty points when order is completed
                    if (newStatus.equals("COMPLETED") && !order.getStatus().equals("COMPLETED")) {
                        Customer customer = searchCustomerById(order.getCustomerId());
                        if (customer != null) {
                            int pointsEarned = (int)(order.getTotalAmount() / 10);
                            customer.addLoyaltyPoints(pointsEarned);
                            ArrayList<Customer> customers = getAllCustomers();
                            for (int i = 0; i < customers.size(); i++) {
                                if (customers.get(i).getId() == customer.getId()) {
                                    customers.set(i, customer);
                                    break;
                                }
                            }
                            FileManager.saveCustomers(customers);
                        }
                    }
                    
                    order.setStatus(newStatus);
                    FileManager.saveOrders(orders);
                    return true;
                }
            }
            
            JOptionPane.showMessageDialog(null, "Order not found!");
            return false;
            
        } catch (Exception e) {
            System.err.println("[Controller] Error updating order: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error updating order: " + e.getMessage());
            return false;
        }
    }
    
    public String getOrderDetails(String orderId) {
        try {
            Order order = getOrderById(orderId);
            if (order == null) {
                return "Order not found!";
            }
            
            StringBuilder details = new StringBuilder();
            details.append("=== ORDER DETAILS ===\n");
            details.append("Order ID: ").append(order.getOrderId()).append("\n");
            details.append("Customer ID: ").append(order.getCustomerId()).append("\n");
            details.append("Date: ").append(order.getOrderDate()).append("\n");
            details.append("Status: ").append(order.getStatus()).append("\n");
            details.append("Total: ").append(String.format("%.2f", order.getTotalAmount())).append(" EGP\n");
            details.append("Instructions: ").append(order.getSpecialInstructions() != null ? order.getSpecialInstructions() : "None").append("\n");
            
            if (!order.getItems().isEmpty()) {
                details.append("\n=== ORDER ITEMS ===\n");
                double total = 0;
                for (OrderItem item : order.getItems()) {
                    double subtotal = item.getPrice() * item.getQuantity();
                    details.append("• ").append(item.getQuantity()).append("x ")
                           .append(item.getName()).append(" (")
                           .append(item.getCategory()).append(")\n");
                    details.append("  Price: ").append(String.format("%.2f", item.getPrice())).append(" EGP each\n");
                    details.append("  Subtotal: ").append(String.format("%.2f", subtotal)).append(" EGP\n");
                    details.append("  ------------------------------\n");
                    total += subtotal;
                }
                details.append("\nTOTAL AMOUNT: ").append(String.format("%.2f", total)).append(" EGP\n");
            } else {
                details.append("\nNo items in this order\n");
            }
            
            return details.toString();
            
        } catch (Exception e) {
            System.err.println("[Controller] Error getting order details: " + e.getMessage());
            return "Error loading order details: " + e.getMessage();
        }
    }
    
    // ========== BILL MANAGEMENT ==========
    
    public String generateBill(String orderId, String paymentMethod) {
        try {
            System.out.println("[Controller] Generating bill for order: " + orderId);
            
            // Get order
            Order order = getOrderById(orderId);
            if (order == null) {
                return "Order not found!";
            }
            
            // Get customer
            Customer customer = searchCustomerById(order.getCustomerId());
            if (customer == null) {
                return "Customer not found!";
            }
            
            // Generate bill ID
            String billId = "BILL-" + System.currentTimeMillis();
            
            // Create bill
            Date billDate = new Date();
            Bill bill = new Bill(billId, orderId, customer.getId(), 
                                customer.getName(), customer.getPhoneNumber(),
                                billDate, order.getTotalAmount(), paymentMethod);
            
            // Save bill
            ArrayList<Bill> bills = FileManager.loadBills();
            bills.add(bill);
            FileManager.saveBills(bills);
            
            // Update order status to COMPLETED
            updateOrderStatus(orderId, "COMPLETED");
            
            return bill.getFormattedBill();
            
        } catch (Exception e) {
            System.err.println("[Controller] Error generating bill: " + e.getMessage());
            return "Error generating bill: " + e.getMessage();
        }
    }
    
    public ArrayList<Bill> getAllBills() {
        try {
            ArrayList<Bill> bills = FileManager.loadBills();
            return bills;
        } catch (Exception e) {
            System.err.println("[Controller] Error loading bills: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public Bill getBillById(String billId) {
        try {
            ArrayList<Bill> bills = FileManager.loadBills();
            for (Bill bill : bills) {
                if (bill.getBillId().equals(billId)) {
                    return bill;
                }
            }
            return null;
        } catch (Exception e) {
            System.err.println("[Controller] Error getting bill: " + e.getMessage());
            return null;
        }
    }
    
    public ArrayList<Bill> getBillsByCustomer(int customerId) {
        try {
            ArrayList<Bill> bills = FileManager.loadBills();
            ArrayList<Bill> customerBills = new ArrayList<>();
            
            for (Bill bill : bills) {
                if (bill.getCustomerId() == customerId) {
                    customerBills.add(bill);
                }
            }
            return customerBills;
        } catch (Exception e) {
            System.err.println("[Controller] Error getting customer bills: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public boolean deleteBill(String billId) {
        try {
            System.out.println("[Controller] Deleting bill: " + billId);
            ArrayList<Bill> bills = FileManager.loadBills();
            
            // Find the bill
            Bill billToDelete = null;
            for (Bill bill : bills) {
                if (bill.getBillId().equals(billId)) {
                    billToDelete = bill;
                    break;
                }
            }
            
            if (billToDelete == null) {
                JOptionPane.showMessageDialog(null, "Bill not found!");
                return false;
            }
            
            // Update associated order status back to SERVED
            Order order = getOrderById(billToDelete.getOrderId());
            if (order != null && order.getStatus().equals("COMPLETED")) {
                order.setStatus("SERVED");
                ArrayList<Order> orders = FileManager.loadOrders();
                for (int i = 0; i < orders.size(); i++) {
                    if (orders.get(i).getOrderId().equals(order.getOrderId())) {
                        orders.set(i, order);
                        break;
                    }
                }
                FileManager.saveOrders(orders);
            }
            
            // Delete the bill
            boolean removed = bills.removeIf(bill -> bill.getBillId().equals(billId));
            if (removed) {
                FileManager.saveBills(bills);
                return true;
            }
            return false;
            
        } catch (Exception e) {
            System.err.println("[Controller] Error deleting bill: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error deleting bill: " + e.getMessage());
            return false;
        }
    }
    
    public String getBillDetails(String billId) {
        try {
            Bill bill = getBillById(billId);
            if (bill == null) {
                return "Bill not found!";
            }
            
            // Get order details
            Order order = getOrderById(bill.getOrderId());
            
            StringBuilder details = new StringBuilder();
            details.append("=== BILL DETAILS ===\n");
            details.append("Bill ID: ").append(bill.getBillId()).append("\n");
            details.append("Order ID: ").append(bill.getOrderId()).append("\n");
            details.append("Customer: ").append(bill.getCustomerName())
                   .append(" (ID: ").append(bill.getCustomerId()).append(")\n");
            details.append("Phone: ").append(bill.getPhoneNumber()).append("\n");
            details.append("Date: ").append(bill.getBillDate()).append("\n");
            details.append("Payment Method: ").append(bill.getPaymentMethod()).append("\n");
            
            if (order != null && !order.getItems().isEmpty()) {
                details.append("\n=== ORDER ITEMS ===\n");
                for (OrderItem item : order.getItems()) {
                    double subtotal = item.getPrice() * item.getQuantity();
                    details.append("• ").append(item.getQuantity()).append("x ")
                           .append(item.getName()).append(" (")
                           .append(item.getCategory()).append(")\n");
                    details.append("  Price: ").append(String.format("%.2f", item.getPrice())).append(" EGP each\n");
                    details.append("  Subtotal: ").append(String.format("%.2f", subtotal)).append(" EGP\n");
                    details.append("  ------------------------------\n");
                }
            }
            
            details.append("\n=== PAYMENT SUMMARY ===\n");
            details.append(String.format("Subtotal: %20.2f EGP\n", bill.getTotalAmount()));
            details.append(String.format("Tax (14%%): %19.2f EGP\n", bill.getTaxAmount()));
            details.append(String.format("Discount: %20.2f EGP\n", bill.getDiscountAmount()));
            details.append(String.format("Final Amount: %16.2f EGP\n", bill.getFinalAmount()));
            details.append("========================\n");
            
            return details.toString();
            
        } catch (Exception e) {
            System.err.println("[Controller] Error getting bill details: " + e.getMessage());
            return "Error loading bill details: " + e.getMessage();
        }
    }
    
    // ========== HELPER METHODS ==========
    
    public ArrayList<OrderItem> parseOrderItems(String itemsText) {
        ArrayList<OrderItem> items = new ArrayList<>();
        String[] lines = itemsText.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("//")) {
                continue;
            }
            
            String[] parts = line.split(",");
            if (parts.length >= 5) {
                try {
                    String itemId = parts[0].trim();
                    String name = parts[1].trim();
                    String category = parts[2].trim();
                    double price = Double.parseDouble(parts[3].trim());
                    int quantity = Integer.parseInt(parts[4].trim());
                    
                    OrderItem item = new OrderItem(itemId, name, category, price, quantity);
                    items.add(item);
                } catch (NumberFormatException e) {
                    System.err.println("[Controller] Invalid item format: " + line);
                }
            }
        }
        
        return items;
    }
    
    public String[] getStatusOptions() {
        return new String[]{"PENDING", "PREPARING", "READY", "SERVED", "COMPLETED", "CANCELLED"};
    }
    
    public String[] getPaymentMethods() {
        return new String[]{"CASH", "CARD", "MOBILE_PAYMENT"};
    }
}