package controller;

import model.Customer;
import model.User;
import util.FileManager;

import java.util.ArrayList;

public class CustomerController {
    public static Customer login(String phone) {
        ArrayList<Customer> allCustomers = FileManager.loadCustomers();

        for ( Customer customer: allCustomers ) {
            if ( customer.getPhoneNumber().equals(phone) ) {
                return customer;
            }
        }
        return null;
    }



    // TODO: A method to check if they have enough points to add a gift to thier profile
}