package util;

import model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class FileManager {
    private static final String USERS_FILE = "src/database/users.txt";
    private static final String CUSTOMERS_FILE = "src/database/customers.txt";
    private static final String ORDERS_FILE = "src/database/orders.txt";
    private static final String BILLS_FILE = "src/database/bills.txt";
    private static final String OFFERS_FILE = "src/database/offers.txt";
    private static final String MARKETING_FILE = "src/database/marketing.txt";
    private static final String RULES_FILE = "src/database/loyalty_rules.txt";
    private static final String MEALS_FILE = "src/database/meals.txt";
    
    static {
        createDatabaseDirectory();
    }
    
    private static void createDatabaseDirectory() {
        File dbDir = new File("src/database");
        if (!dbDir.exists()) dbDir.mkdirs();
    }
    
    public static ArrayList<User> loadUsers() {
        ArrayList<User> users = new ArrayList<>();
        try {
            File file = new File(USERS_FILE);
            if (!file.exists()) {
                createDefaultUser();
                return loadUsers();
            }
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String id = parts[0];
                    String username = parts[1];
                    String password = parts[2];
                    String role = parts[3];
                    if (role.equals("ADMIN")) users.add(new Admin(id, username, password));
                    else if (role.equals("EMPLOYEE")) {
                        String jobTitle = parts.length > 4 ? parts[4] : "Employee";
                        double salary = parts.length > 5 ? Double.parseDouble(parts[5]) : 3000.0;
                        users.add(new Employee(id, username, password, jobTitle, salary));
                    }
                }
            }
            scanner.close();
        } catch (Exception e) {
            System.err.println("[FileManager] Error loading users: " + e.getMessage());
        }
        return users;
    }
    
    private static void createDefaultUser() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            writer.println("1,admin,admin123,ADMIN");
            writer.println("2,employee,emp123,EMPLOYEE,Manager,3500.0");
        } catch (IOException e) {
            System.err.println("[FileManager] Error creating default user: " + e.getMessage());
        }
    }
    
    public static void saveUsers(ArrayList<User> users) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (User user : users) {
                String line = user.getId() + "," + user.getUsername() + "," + user.getPassword() + "," + user.getRole();
                if (user instanceof Employee) {
                    Employee emp = (Employee) user;
                    line += "," + emp.getJobTitle() + "," + emp.getSalary();
                }
                writer.println(line);
            }
        } catch (IOException e) {
            System.err.println("[FileManager] Error saving users: " + e.getMessage());
        }
    }
    
    public static ArrayList<Customer> loadCustomers() {
        ArrayList<Customer> customers = new ArrayList<>();
        try {
            File file = new File(CUSTOMERS_FILE);
            if (!file.exists()) {
                file.createNewFile();
                return customers;
            }
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    try {
                        int id = Integer.parseInt(parts[0]);
                        String name = parts[1];
                        String phone = parts[2];
                        Customer customer = new Customer(id, name, phone);
                        
                        if (parts.length > 3) {
                            try {
                                int points = Integer.parseInt(parts[3]);
                                customer.setLoyaltyPoints(points);
                            } catch (NumberFormatException e) {}
                        }
                        
                        if (parts.length > 4 && !parts[4].isEmpty()) {
                            String[] orderIds = parts[4].split(";");
                            for (String orderId : orderIds) {
                                if (!orderId.trim().isEmpty()) customer.addOrderId(orderId.trim());
                            }
                        }
                        
                        if (parts.length > 5 && !parts[5].isEmpty()) {
                            String[] savedOffers = parts[5].split(";");
                            for (String offer : savedOffers) {
                                if (!offer.trim().isEmpty()) customer.addSavedOffer(offer.trim());
                            }
                        }
                        
                        if (parts.length > 6 && !parts[6].isEmpty()) {
                            String[] redeemedGifts = parts[6].split(";");
                            for (String gift : redeemedGifts) {
                                if (!gift.trim().isEmpty()) customer.addRedeemedGift(gift.trim());
                            }
                        }
                        
                        customers.add(customer);
                    } catch (NumberFormatException e) {
                        System.err.println("[FileManager] Invalid customer ID format: " + parts[0]);
                    }
                }
            }
            scanner.close();
        } catch (Exception e) {
            System.err.println("[FileManager] Error loading customers: " + e.getMessage());
        }
        return customers;
    }
    
    public static void saveCustomers(ArrayList<Customer> customers) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CUSTOMERS_FILE))) {
            for (Customer customer : customers) {
                StringBuilder line = new StringBuilder();
                line.append(customer.getId()).append(",")
                    .append(customer.getName()).append(",")
                    .append(customer.getPhoneNumber()).append(",")
                    .append(customer.getLoyaltyPoints()).append(",");
                
                if (customer.getOrderIds() != null && !customer.getOrderIds().isEmpty()) {
                    line.append(String.join(";", customer.getOrderIds()));
                }
                line.append(",");
                
                if (customer.getSavedOffers() != null && !customer.getSavedOffers().isEmpty()) {
                    line.append(String.join(";", customer.getSavedOffers()));
                }
                line.append(",");
                
                if (customer.getRedeemedGifts() != null && !customer.getRedeemedGifts().isEmpty()) {
                    line.append(String.join(";", customer.getRedeemedGifts()));
                }
                
                writer.println(line.toString());
            }
        } catch (IOException e) {
            System.err.println("[FileManager] Error saving customers: " + e.getMessage());
        }
    }
    
    public static ArrayList<Order> loadOrders() {
        ArrayList<Order> orders = new ArrayList<>();
        try {
            File file = new File(ORDERS_FILE);
            if (!file.exists()) {
                file.createNewFile();
                return orders;
            }
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                try {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 6) {
                        String orderId = parts[0];
                        int customerId = Integer.parseInt(parts[1]);
                        Date orderDate = new Date(Long.parseLong(parts[2]));
                        String status = parts[3];
                        String instructions = parts[4];
                        double totalAmount = Double.parseDouble(parts[5]);
                        ArrayList<OrderItem> items = new ArrayList<>();
                        Order order = new Order(orderId, customerId, orderDate, items, status, totalAmount);
                        order.setSpecialInstructions(instructions);
                        orders.add(order);
                    }
                } catch (Exception e) {
                    System.err.println("[FileManager] Error parsing order: " + e.getMessage());
                }
            }
            scanner.close();
        } catch (Exception e) {
            System.err.println("[FileManager] Error loading orders: " + e.getMessage());
        }
        return orders;
    }
    
    public static void saveOrders(ArrayList<Order> orders) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ORDERS_FILE))) {
            for (Order order : orders) {
                String line = order.getOrderId() + "|" + order.getCustomerId() + "|" + order.getOrderDate().getTime() + "|" + order.getStatus() + "|" + (order.getSpecialInstructions() != null ? order.getSpecialInstructions() : "") + "|" + order.getTotalAmount();
                writer.println(line);
            }
        } catch (IOException e) {
            System.err.println("[FileManager] Error saving orders: " + e.getMessage());
        }
    }
    
    public static ArrayList<Bill> loadBills() {
        ArrayList<Bill> bills = new ArrayList<>();
        try {
            File file = new File(BILLS_FILE);
            if (!file.exists()) {
                file.createNewFile();
                return bills;
            }
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                try {
                    String[] parts = line.split(",");
                    if (parts.length >= 7) {
                        String billId = parts[0];
                        String orderId = parts[1];
                        int customerId = Integer.parseInt(parts[2]);
                        String customerName = parts[3];
                        String phone = parts[4];
                        Date billDate = new Date(Long.parseLong(parts[5]));
                        double totalAmount = Double.parseDouble(parts[6]);
                        String paymentMethod = parts.length > 7 ? parts[7] : "CASH";
                        Bill bill = new Bill(billId, orderId, customerId, customerName, phone, billDate, totalAmount, paymentMethod);
                        bills.add(bill);
                    }
                } catch (Exception e) {
                    System.err.println("[FileManager] Error parsing bill: " + e.getMessage());
                }
            }
            scanner.close();
        } catch (Exception e) {
            System.err.println("[FileManager] Error loading bills: " + e.getMessage());
        }
        return bills;
    }
    
    public static void saveBills(ArrayList<Bill> bills) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BILLS_FILE))) {
            for (Bill bill : bills) {
                String line = bill.getBillId() + "," + bill.getOrderId() + "," + bill.getCustomerId() + "," + bill.getCustomerName() + "," + bill.getPhoneNumber() + "," + bill.getBillDate().getTime() + "," + bill.getFinalAmount() + "," + bill.getPaymentMethod();
                writer.println(line);
            }
        } catch (IOException e) {
            System.err.println("[FileManager] Error saving bills: " + e.getMessage());
        }
    }
    
    public static ArrayList<Offer> loadOffers() {
        ArrayList<Offer> offers = new ArrayList<>();
        try {
            File file = new File(OFFERS_FILE);
            if (!file.exists()) return offers;
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 3) offers.add(new Offer(parts[0], parts[1], parts[2]));
            }
            scanner.close();
        } catch (Exception e) {
            System.err.println("[FileManager] Error loading offers: " + e.getMessage());
        }
        return offers;
    }
    
    public static void saveOffers(ArrayList<Offer> offers) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(OFFERS_FILE))) {
            for (Offer offer : offers) writer.println(offer.getId() + "," + offer.getName() + "," + offer.getDiscount());
        } catch (IOException e) {
            System.err.println("[FileManager] Error saving offers: " + e.getMessage());
        }
    }
    
    public static void saveMarketingMessage(String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(MARKETING_FILE, true))) {
            writer.println(message);
        } catch (IOException e) {
            System.err.println("[FileManager] Error saving marketing message: " + e.getMessage());
        }
    }
    
    public static void saveLoyaltyRules(int points, String rewardName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RULES_FILE, true))) {
            writer.println(points + "," + rewardName);
        } catch (IOException e) {
            System.err.println("[FileManager] Error saving loyalty rules: " + e.getMessage());
        }
    }
    
    public static ArrayList<Meal> loadMeals() {
        ArrayList<Meal> meals = new ArrayList<>();
        try {
            File file = new File(MEALS_FILE);
            if (!file.exists()) return meals;
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;
                Meal meal = Meal.fromFileRecord(line);
                if (meal != null) meals.add(meal);
            }
            scanner.close();
        } catch (Exception e) {
            System.err.println("[FileManager] Error loading meals: " + e.getMessage());
        }
        return meals;
    }
    
    public static void saveMeals(List<Meal> meals) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(MEALS_FILE))) {
            for (Meal meal : meals) writer.println(meal.toFileRecord());
        } catch (IOException e) {
            System.err.println("[FileManager] Error saving meals: " + e.getMessage());
        }
    }
    
    public static int nextMealId(List<Meal> meals) {
        int max = 0;
        if (meals != null) {
            for (Meal meal : meals) max = Math.max(max, meal.getMealID());
        }
        return max + 1;
    }
}