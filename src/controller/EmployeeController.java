package controller;

import model.*;
import util.FileManager;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class EmployeeController {
    
    public boolean addCustomer(int id, String name, String phoneNumber) {
        try {
            ArrayList<Customer> customers = FileManager.loadCustomers();
            
            for (Customer customer : customers) {
                if (customer.getId() == id) {
                    JOptionPane.showMessageDialog(null, "Customer ID already exists!");
                    return false;
                }
            }
            
            Customer newCustomer = new Customer(id, name, phoneNumber);
            customers.add(newCustomer);
            FileManager.saveCustomers(customers);
            
            JOptionPane.showMessageDialog(null, "Customer added successfully!");
            return true;
            
        } catch (Exception e) {
            System.err.println("[Controller] Error adding customer: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCustomer(int id) {
        try {
            ArrayList<Customer> customers = FileManager.loadCustomers();
            boolean removed = customers.removeIf(customer -> customer.getId() == id);

            if (removed) {
                FileManager.saveCustomers(customers);
                JOptionPane.showMessageDialog(null, "Customer deleted successfully!");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Customer not found!");
                return false;
            }
        } catch (Exception e) {
            System.err.println("[Controller] Error deleting customer: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateCustomer(int id, String name, String phoneNumber) {
        try {
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
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Customer not found!");
                return false;
            }
        } catch (Exception e) {
            System.err.println("[Controller] Error updating customer: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            return false;
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
    
    public boolean makeOrder(int customerId, ArrayList<OrderItem> items, String instructions) {
        try {
            ArrayList<Customer> customers = FileManager.loadCustomers();
            Customer customer = null;
            int customerIndex = -1;
            
            for (int i = 0; i < customers.size(); i++) {
                if (customers.get(i).getId() == customerId) {
                    customer = customers.get(i);
                    customerIndex = i;
                    break;
                }
            }
            
            if (customer == null) {
                JOptionPane.showMessageDialog(null, "Error: Customer not found!");
                return false;
            }
            
            if (items == null || items.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Error: Order must have at least one item!");
                return false;
            }
            
            double totalAmount = 0;
            for (OrderItem item : items) {
                totalAmount += item.getPrice() * item.getQuantity();
            }
            
            String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            
            Date orderDate = new Date();
            Order newOrder = new Order(orderId, customerId, orderDate, items, "PENDING", totalAmount);
            if (instructions != null && !instructions.trim().isEmpty()) {
                newOrder.setSpecialInstructions(instructions);
            }
            
            ArrayList<Order> orders = FileManager.loadOrders();
            orders.add(newOrder);
            FileManager.saveOrders(orders);
            
            customer.addOrderId(orderId);
            
            int loyaltyPointsEarned = (int)(totalAmount / 10);
            if (loyaltyPointsEarned > 0) {
                customer.addLoyaltyPoints(loyaltyPointsEarned);
            }
            
            customers.set(customerIndex, customer);
            FileManager.saveCustomers(customers);
            
            JOptionPane.showMessageDialog(null, "Order created successfully! Order ID: " + orderId);
            return true;
            
        } catch (Exception e) {
            System.err.println("[Controller] ERROR in makeOrder: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error creating order: " + e.getMessage());
            return false;
        }
    }
    
    public boolean addCheesecakeGift(String orderId) {
        try {
            ArrayList<Order> orders = FileManager.loadOrders();
            
            for (Order order : orders) {
                if (order.getOrderId().equals(orderId)) {
                    if (order.getStatus().equals("CANCELLED") || order.getStatus().equals("COMPLETED")) {
                        JOptionPane.showMessageDialog(null, "Cannot add gift to " + order.getStatus().toLowerCase() + " order!");
                        return false;
                    }
                    
                    OrderItem cheesecake = new OrderItem("GIFT001", "Cheesecake (Gift)", "Dessert", 0.0, 1);
                    order.getItems().add(cheesecake);
                    
                    FileManager.saveOrders(orders);
                    
                    JOptionPane.showMessageDialog(null, "Free Cheesecake added to order!");
                    return true;
                }
            }
            
            JOptionPane.showMessageDialog(null, "Order not found!");
            return false;
            
        } catch (Exception e) {
            System.err.println("[Controller] Error adding cheesecake gift: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error adding gift: " + e.getMessage());
            return false;
        }
    }
    
    public boolean cancelOrder(String orderId) {
        try {
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
            ArrayList<Order> orders = FileManager.loadOrders();
            
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
            
            boolean removed = orders.removeIf(order -> order.getOrderId().equals(orderId));
            if (removed) {
                FileManager.saveOrders(orders);
                
                ArrayList<Customer> customers = FileManager.loadCustomers();
                for (Customer customer : customers) {
                    if (customer.getId() == orderToDelete.getCustomerId()) {
                        customer.removeOrderId(orderId);
                        break;
                    }
                }
                FileManager.saveCustomers(customers);
                
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
    
    public int getTotalOrderCount() {
        try {
            ArrayList<Order> orders = getAllOrders();
            return orders.size();
        } catch (Exception e) {
            System.err.println("[Controller] Error getting total order count: " + e.getMessage());
            return 0;
        }
    }
    
    public int getTotalItemCount() {
        try {
            ArrayList<Order> orders = getAllOrders();
            int totalItems = 0;
            for (Order order : orders) {
                if (order.getItems() != null) {
                    for (OrderItem item : order.getItems()) {
                        totalItems += item.getQuantity();
                    }
                }
            }
            return totalItems;
        } catch (Exception e) {
            System.err.println("[Controller] Error getting total item count: " + e.getMessage());
            return 0;
        }
    }
    
    public boolean updateOrderStatus(String orderId, String newStatus) {
        try {
            ArrayList<Order> orders = FileManager.loadOrders();
            
            for (Order order : orders) {
                if (order.getOrderId().equals(orderId)) {
                    if (order.getStatus().equals("CANCELLED")) {
                        JOptionPane.showMessageDialog(null, "Cannot update a cancelled order!");
                        return false;
                    }
                    
                    if (newStatus.equals("COMPLETED") && !order.getStatus().equals("COMPLETED")) {
                        ArrayList<Customer> customers = FileManager.loadCustomers();
                        for (Customer customer : customers) {
                            if (customer.getId() == order.getCustomerId()) {
                                int pointsEarned = (int)(order.getTotalAmount() / 10);
                                customer.addLoyaltyPoints(pointsEarned);
                                FileManager.saveCustomers(customers);
                                break;
                            }
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
            
            if (order.getItems() != null && !order.getItems().isEmpty()) {
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
                details.append("\nNo items found in this order\n");
            }
            
            return details.toString();
            
        } catch (Exception e) {
            System.err.println("[Controller] Error getting order details: " + e.getMessage());
            return "Error loading order details: " + e.getMessage();
        }
    }
    
    public String generateBill(String orderId, String paymentMethod) {
        try {
            Order order = getOrderById(orderId);
            if (order == null) {
                return "Order not found!";
            }
            
            Customer customer = searchCustomerById(order.getCustomerId());
            if (customer == null) {
                return "Customer not found!";
            }
            
            String billId = "BILL-" + System.currentTimeMillis();
            
            Date billDate = new Date();
            Bill bill = new Bill(billId, orderId, customer.getId(), 
                                customer.getName(), customer.getPhoneNumber(),
                                billDate, order.getTotalAmount(), paymentMethod);
            
            ArrayList<Bill> bills = FileManager.loadBills();
            bills.add(bill);
            FileManager.saveBills(bills);
            
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
            ArrayList<Bill> bills = FileManager.loadBills();
            
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
            
            if (order != null && order.getItems() != null && !order.getItems().isEmpty()) {
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
            } else {
                details.append("\nNo items found for this bill\n");
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