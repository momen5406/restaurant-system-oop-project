package util;

import model.Admin;
import model.Employee;
<<<<<<< Updated upstream
import model.Offer;
=======
import model.Meal;
>>>>>>> Stashed changes
import model.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
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

<<<<<<< Updated upstream
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
        try (PrintWriter pw = new PrintWriter(new FileWriter(RULES_FILE, true))) {
            pw.println(points + "," + rewardName);
        } catch (Exception e) {
            System.out.println("Error saving rules: " + e.getMessage());
        }
    }


    private static final String CUSTOMERS_FILE = "src/database/customers.txt";

    // TODO: Load Customers Method

    // TODO: Save Customers Method
=======
    // Meals persistence
    private static final String MEALS_FILE = "src/database/meals.txt";

    public static ArrayList<Meal> loadMeals() {
        ArrayList<Meal> meals = new ArrayList<>();
        File file = new File(MEALS_FILE);
        if (!file.exists()) {
            return meals;
        }
        try (Scanner input = new Scanner(file)) {
            while (input.hasNextLine()) {
                Meal meal = Meal.fromFileRecord(input.nextLine());
                if (meal != null) {
                    meals.add(meal);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Meals file not found: " + e.getMessage());
        }
        return meals;
    }

    public static void saveMeals(List<Meal> meals) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(MEALS_FILE))) {
            for (Meal meal : meals) {
                pw.println(meal.toFileRecord());
            }
        } catch (IOException e) {
            System.out.println("Error saving meals: " + e.getMessage());
        }
    }

    public static int nextMealId(List<Meal> meals) {
        int max = 0;
        if (meals != null) {
            for (Meal meal : meals) {
                max = Math.max(max, meal.getMealID());
            }
        }
        return max + 1;
    }
>>>>>>> Stashed changes
}
