package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String name;
    private String phoneNumber;
    private int loyaltyPoints;
    private ArrayList<String> orderIds;
    private ArrayList<String> savedOffers;

    public Customer(int id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.loyaltyPoints = 0;
        this.orderIds = new ArrayList<>();
        this.savedOffers = new ArrayList<>();
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public int getLoyaltyPoints() { return loyaltyPoints; }
    
    public ArrayList<String> getOrderIds() { 
        if (this.orderIds == null) {
            this.orderIds = new ArrayList<>();
        }
        return orderIds; 
    }
    
    public ArrayList<String> getSavedOffers() { 
        if (this.savedOffers == null) {
            this.savedOffers = new ArrayList<>();
        }
        return savedOffers; 
    }

    public void setName(String name) { this.name = name; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }

    public void addLoyaltyPoints(int points) { this.loyaltyPoints += points; }
    public void deductLoyaltyPoints(int points) {
        if (this.loyaltyPoints >= points) {
            this.loyaltyPoints -= points;
        }
    }

    public void addOrderId(String orderId) {
        ArrayList<String> ids = getOrderIds(); // Use getter to ensure not null
        if (!ids.contains(orderId)) {
            ids.add(orderId);
        }
    }

    public void removeOrderId(String orderId) { 
        if (this.orderIds != null) {
            orderIds.remove(orderId); 
        }
    }
    
    public void addSavedOffer(String offer) { 
        if (this.savedOffers == null) {
            this.savedOffers = new ArrayList<>();
        }
        savedOffers.add(offer); 
    }
    
    public void removeSavedOffer(String offer) { 
        if (this.savedOffers != null) {
            savedOffers.remove(offer); 
        }
    }

    @Override
    public String toString() {
        return "Customer ID: " + id +
               ", Name: " + name +
               ", Phone: " + phoneNumber +
               ", Loyalty Points: " + loyaltyPoints +
               ", Orders: " + getOrderIds().size();
    }
}