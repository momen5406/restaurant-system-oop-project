package util;

import model.Admin;
import model.Employee;
import model.Offer;
import model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class FileManager {
    private static final String USERS_FILE = "src/database/users.txt";

    // Load Users from file
    public static ArrayList<User> loadUsers() {
        ArrayList<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);

        if ( !file.exists() ) return users;

        try (Scanner input = new Scanner(file)) {
            while ( input.hasNextLine() ) {
                String line = input.nextLine();
                String[] data = line.split(",");

                String id = data[0];
                String username = data[1];
                String password = data[2];
                String role = data[3];

                if ( role.equals("ADMIN") ) {
                    users.add(new Admin(id, username, password));
                } else if ( role.equals("EMPLOYEE") ) {
                    String jobTitle = data[4];
                    double salary = Double.parseDouble(data[5]);
                    users.add(new Employee(id, username, password, jobTitle, salary));
                }
            }
            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found: " + e.getMessage());
        }
        return users;
    }

    // Write users to file
    public static void saveUsers(ArrayList<User> users) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE));

            for ( User user: users ) {
                String line = user.getId() + "," + user.getUsername() + "," + user.getPassword() + "," + user.getRole();
                if (user instanceof Employee) {
                    Employee emp = (Employee) user;
                    line += "," + emp.getJobTitle() + "," + emp.getSalary();
                 }
                pw.println(line);
            }
            pw.close();
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    // Save offers inside offers.txt
    private static final String OFFERS_FILE = "src/database/offers.txt";

    public static ArrayList<Offer> loadOffers() {
        ArrayList<Offer> offers = new ArrayList<>();
        File file = new File(OFFERS_FILE);

        if ( !file.exists() ) return offers;

        try (Scanner input = new Scanner(file)) {
            while ( input.hasNextLine() ) {
                String line = input.nextLine();
                String[] data = line.split(",");

                String id = data[0];
                String offerName = data[1];
                String offerDiscount = data[2];

                offers.add(new Offer(id, offerName, offerDiscount));
            }
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found: " + e.getMessage());
        }
        return offers;
    }

    public static void saveOffers(ArrayList<Offer> offers) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(OFFERS_FILE));

            for ( Offer offer: offers ) {
                String line = offer.getId() + "," + offer.getName() + "," + offer.getDiscount();
                pw.println(line);
            }
            pw.close();
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    private static final String MARKETING_FILE = "src/database/marketing.txt";

    public static void saveMarketingMessage(String message) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(MARKETING_FILE, true))) {
            pw.println(message);
        } catch (Exception e) {
            System.out.println("Error Saving marketing: " + e.getMessage());
        }
    }

    private static final String RULES_FILE = "src/database/loyalty_rules.txt";

    public static void saveLoyaltyRules(int points, String rewardName) {
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(RULES_FILE))) {
            pw.println(points + "," + rewardName);
        } catch (Exception e) {
            System.out.println("Error saving rules: " + e.getMessage());
        }
    }


    private static final String CUSTOMERS_FILE = "src/database/customers.txt";

    // TODO: Load Customers Method

    // TODO: Save Customers Method
}
