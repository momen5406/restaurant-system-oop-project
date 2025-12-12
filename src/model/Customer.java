package model;



import java.util.*;

public class Customer {

    private int id;
    private String name;
    private String phoneNumber;

    private int loyaltyPoints;                   
    private ArrayList<Order> orderHistory;     
    private ArrayList<String> savedOffers;      

    public Customer(int id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;

        this.loyaltyPoints = 0;                   
        this.orderHistory = new ArrayList<>();
        this.savedOffers = new ArrayList<>();
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void addLoyaltyPoints(int points) {
        this.loyaltyPoints += points;
    }

    public ArrayList<Order> getOrderHistory() {
        return orderHistory;
    }

    public void addOrder(Order order) {
        this.orderHistory.add(order);
    }

    public ArrayList<String> getSavedOffers() {
        return savedOffers;
    }

    public void addOffer(String offer) {
        this.savedOffers.add(offer);
    }

    @Override
    public String toString() {
        return "Customer ID: " + id +
                ", Name: " + name +
                ", Phone: " + phoneNumber +
                ", Loyalty Points: " + loyaltyPoints;
    }
}


