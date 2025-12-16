package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String phoneNumber;
    private int loyaltyPoints;
    private ArrayList<String> orderIds;  // Changed from Order objects to String IDs
    private ArrayList<String> savedOffers;

    public Customer(int id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.loyaltyPoints = 0;
        this.orderIds = new ArrayList<>();
        this.savedOffers = new ArrayList<>();
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public int getLoyaltyPoints() { return loyaltyPoints; }
    public ArrayList<String> getOrderIds() { return orderIds; }
    public ArrayList<String> getSavedOffers() { return savedOffers; }
    
    // Setters
    public void setName(String name) { this.name = name; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }  // ADD THIS METHOD!
    
    // Add methods
    public void addLoyaltyPoints(int points) { this.loyaltyPoints += points; }
    public void deductLoyaltyPoints(int points) { 
        if (this.loyaltyPoints >= points) {
            this.loyaltyPoints -= points; 
        }
    }
    
    public void addOrderId(String orderId) { 
        if (!orderIds.contains(orderId)) {
            orderIds.add(orderId); 
        }
    }
    
    public void removeOrderId(String orderId) { orderIds.remove(orderId); }
    public void addSavedOffer(String offer) { savedOffers.add(offer); }
    public void removeSavedOffer(String offer) { savedOffers.remove(offer); }

    @Override
    public String toString() {
        return "Customer ID: " + id +
               ", Name: " + name +
               ", Phone: " + phoneNumber +
               ", Loyalty Points: " + loyaltyPoints +
               ", Orders: " + orderIds.size();
    }
}