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
    private ArrayList<String> redeemedGifts;

    public Customer(int id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.loyaltyPoints = 0;
        this.orderIds = new ArrayList<>();
        this.savedOffers = new ArrayList<>();
        this.redeemedGifts = new ArrayList<>();
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
    
    public ArrayList<String> getRedeemedGifts() { 
        if (this.redeemedGifts == null) {
            this.redeemedGifts = new ArrayList<>();
        }
        return redeemedGifts; 
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
        ArrayList<String> ids = getOrderIds();
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
    
    public void addRedeemedGift(String gift) { 
        if (this.redeemedGifts == null) {
            this.redeemedGifts = new ArrayList<>();
        }
        redeemedGifts.add(gift); 
    }
    
    public void removeRedeemedGift(String gift) { 
        if (this.redeemedGifts != null) {
            redeemedGifts.remove(gift); 
        }
    }
    
    public int getGiftsCount() {
        return getRedeemedGifts().size();
    }

    @Override
    public String toString() {
        return "Customer ID: " + id +
               ", Name: " + name +
               ", Phone: " + phoneNumber +
               ", Loyalty Points: " + loyaltyPoints +
               ", Orders: " + getOrderIds().size() +
               ", Gifts Redeemed: " + getGiftsCount();
    }
}