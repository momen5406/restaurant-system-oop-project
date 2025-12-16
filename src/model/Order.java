package model;

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;

public class Order implements Serializable {
    private String orderId;
    private int customerId;
    private Date orderDate;
    private ArrayList<OrderItem> items;
    private String status;
    private double totalAmount;
    private String specialInstructions;

    // Constructor
    public Order(String orderId, int customerId, Date orderDate, ArrayList<OrderItem> items, String status, double totalAmount) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.items = items != null ? items : new ArrayList<>();
        this.status = status;
        this.totalAmount = totalAmount;
        this.specialInstructions = "";
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public ArrayList<OrderItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<OrderItem> items) {
        this.items = items;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    // Helper methods
    public void addItem(OrderItem item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(item);
        calculateTotal();
    }

    public void removeItem(OrderItem item) {
        if (items != null) {
            items.remove(item);
            calculateTotal();
        }
    }

    private void calculateTotal() {
        totalAmount = 0;
        if (items != null) {
            for (OrderItem item : items) {
                totalAmount += item.getSubtotal();
            }
        }
    }

    public String getFormattedDate() {
        return orderDate.toString();
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", customerId=" + customerId +
                ", orderDate=" + orderDate +
                ", items=" + items.size() +
                ", status='" + status + '\'' +
                ", totalAmount=" + totalAmount +
                ", specialInstructions='" + specialInstructions + '\'' +
                '}';
    }
}