package util;

import model.Admin;
import model.Employee;
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

    public static void saveOffer(String offer) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(OFFERS_FILE))) {
            pw.println(offer);
        } catch (IOException e) {
            System.out.println("Error saving offer: " + e.getMessage());
        }
    }


    private static final String CUSTOMERS_FILE = "src/database/customers.txt";

    // TODO: Load Customers Method

    // TODO: Save Customers Method
}
