package controller;

import model.Employee;
import model.Offer;
import model.User;
import util.FileManager;

import java.util.ArrayList;

public class AdminController {
    // Employees Management
    // Add
    public void addEmployee(String id, String username, String password, String job, double salary) {
        ArrayList<User> users = FileManager.loadUsers();
        Employee newEmp = new Employee(id, username, password, job, salary);
        users.add(newEmp);
        FileManager.saveUsers(users);
        System.out.println("Employee Added successfully");
    }

    // Delete
    public void deleteEmployee(String id) {
        ArrayList<User> users = FileManager.loadUsers();
        boolean removed = users.removeIf(user -> user.getId().equals(id) && user instanceof Employee);

        if ( removed ) {
            FileManager.saveUsers(users);
            System.out.println("Employee deleted Successfully");
        } else {
            System.out.println("Employee not found.");
        }
    }

    // Update
    public void updateEmployee(String id, String job, double salary) {
        ArrayList<User> users = FileManager.loadUsers();
        boolean found = false;

        for ( User user: users ) {
            if ( user.getId().equals(id) && user instanceof Employee ) {
                Employee emp = (Employee) user;
                emp.setJobTitle(job);
                emp.setSalary(salary);
                found = true;
                break;
            }
        }

        if ( found ) {
            FileManager.saveUsers(users);
            System.out.println("Employee updated Successfully!");
        } else {
            System.out.println("Employee not found.");
        }
    }

    // List
    public ArrayList<Employee> getAllEmployees() {
        ArrayList<User> users = FileManager.loadUsers();
        ArrayList<Employee> employees = new ArrayList<>();

        for ( User user: users ) {
            if ( user instanceof Employee ) {
                employees.add((Employee) user);
            }
        }
        return employees;
    }

    // Search
    public Employee searchEmployee(String username) {
        ArrayList<User> users = FileManager.loadUsers();

        for ( User user: users ) {
            if ( user instanceof Employee ) {
                if ( user.getUsername().equals(username) ) {
                    return (Employee) user;
                }
            }
        }
        return null;
    }

    // Offers
    // Add
    public void addOffer(String id, String offerName, String discount) {
        ArrayList<Offer> offers = FileManager.loadOffers();
        Offer newOffer = new Offer(id, offerName, discount);
        offers.add(newOffer);
        FileManager.saveOffers(offers);
    }

    // Delete
    public void deleteOffer(String id) {
        ArrayList<Offer> offers = FileManager.loadOffers();
        boolean removed = offers.removeIf(offer -> offer.getId().equals(id));

        if ( removed ) {
            FileManager.saveOffers(offers);
            System.out.println("Offer deleted Successfully");
        } else {
            System.out.println("Offer not found.");
        }
    }

    // TODO: MANAGE MEALS (ADD, DELETE, UPDATE, LIST, SEARCH) LIKE EMPLOYEES ☝️

    // TODO: CUSTOMER REPORT LIKE EMPLOYEE REPORT 👇

    // Employee Report
    public String EmployeesReport() {
        ArrayList<User> users = FileManager.loadUsers();
        int numberOfEmployees = 0;
        double totalSalary = 0;
        int chefs = 0;
        int waiters = 0;

        for ( User user: users ) {
            if ( user instanceof Employee ) {
                numberOfEmployees++;
                totalSalary += ((Employee) user).getSalary();
                Employee e = (Employee) user;
                if ( "Chef".equalsIgnoreCase(e.getJobTitle()) ) chefs++;
                if ( "Waiter".equalsIgnoreCase(e.getJobTitle()) ) waiters++;
            }
        }
        return "REPORT: Restaurant has " + numberOfEmployees + " Employees, Total Salary: " + totalSalary + "EGP, team consist of " + chefs  + " Chefs and " +  waiters + " Waiters";
    }

    // TODO: Uncomment after finishing the loadCustomers method in FileManager.java
//    public String CustomersReport() {
//        ArrayList<Customer> customers = FileManager.loadCustomers();
//        int total = customers.size();
//
//        return "REPORT: Total Registered Customers: " + total;
//    }
}
