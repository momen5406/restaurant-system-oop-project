package controller;

import model.Customer;
import model.Order;
import model.Offer;
import util.FileManager;
import java.util.ArrayList;

public class CustomerController {
    public static Customer login(String phone) {
        ArrayList<Customer> allCustomers = FileManager.loadCustomers();
        for (Customer customer : allCustomers) {
            if (customer.getPhoneNumber().equals(phone)) {
                return customer;
            }
        }
        return null;
    }

    public static boolean register(String name, String phone) {
        if (name == null || name.trim().isEmpty() || phone == null || phone.trim().isEmpty()) {
            return false;
        }
        
        ArrayList<Customer> allCustomers = FileManager.loadCustomers();
        for (Customer customer : allCustomers) {
            if (customer.getPhoneNumber().equals(phone)) {
                return false;
            }
        }
        
        int newId = 1;
        if (!allCustomers.isEmpty()) {
            int maxId = 0;
            for (Customer c : allCustomers) {
                if (c.getId() > maxId) {
                    maxId = c.getId();
                }
            }
            newId = maxId + 1;
        }
        
        Customer newCustomer = new Customer(newId, name, phone);
        newCustomer.setLoyaltyPoints(0);
        
        allCustomers.add(newCustomer);
        FileManager.saveCustomers(allCustomers);
        
        FileManager.saveMarketingMessage("New customer registered: " + name + " (" + phone + ")");
        
        return true;
    }

    public static ArrayList<Order> getCustomerOrders(int customerId) {
        ArrayList<Order> allOrders = FileManager.loadOrders();
        ArrayList<Order> customerOrders = new ArrayList<>();
        
        for (Order order : allOrders) {
            if (order.getCustomerId() == customerId) {
                customerOrders.add(order);
            }
        }
        
        return customerOrders;
    }

    public static boolean addLoyaltyPoints(int customerId, int points) {
        ArrayList<Customer> allCustomers = FileManager.loadCustomers();
        
        for (Customer customer : allCustomers) {
            if (customer.getId() == customerId) {
                customer.addLoyaltyPoints(points);
                FileManager.saveCustomers(allCustomers);
                return true;
            }
        }
        
        return false;
    }

    public static boolean enrollInMarketing(int customerId) {
        ArrayList<Customer> allCustomers = FileManager.loadCustomers();
        
        for (Customer customer : allCustomers) {
            if (customer.getId() == customerId) {
                FileManager.saveMarketingMessage("Customer enrolled in marketing: " + 
                    customer.getName() + " (ID: " + customerId + ")");
                return true;
            }
        }
        
        return false;
    }

    public static boolean enrollInLoyaltyProgram(int customerId) {
        ArrayList<Customer> allCustomers = FileManager.loadCustomers();
        
        for (Customer customer : allCustomers) {
            if (customer.getId() == customerId) {
                customer.addLoyaltyPoints(100);
                FileManager.saveCustomers(allCustomers);
                FileManager.saveLoyaltyRules(100, "Welcome bonus for loyalty program");
                return true;
            }
        }
        
        return false;
    }

    public static ArrayList<Offer> getAvailableOffers() {
        return FileManager.loadOffers();
    }
    
    public static boolean claimOffer(int customerId, String offerId) {
        ArrayList<Customer> allCustomers = FileManager.loadCustomers();
        ArrayList<Offer> allOffers = FileManager.loadOffers();
        
        for (Customer customer : allCustomers) {
            if (customer.getId() == customerId) {
                for (Offer offer : allOffers) {
                    if (offer.getId().equals(offerId)) {
                        customer.addSavedOffer(offer.getName() + " - " + offer.getDiscount());
                        FileManager.saveCustomers(allCustomers);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static ArrayList<String> getMarketingMessages() {
        ArrayList<String> messages = new ArrayList<>();
        try {
            java.io.File file = new java.io.File("src/database/marketing.txt");
            if (file.exists()) {
                java.util.Scanner scanner = new java.util.Scanner(file);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (!line.isEmpty()) {
                        messages.add(line);
                    }
                }
                scanner.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messages;
    }
}