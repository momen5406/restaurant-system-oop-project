package model;

import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Bill implements Serializable {
    private String billId;
    private String orderId;
    private int customerId;
    private String customerName;
    private String phoneNumber;
    private Date billDate;
    private double totalAmount;
    private double taxAmount;
    private double discountAmount;
    private double finalAmount;
    private String paymentMethod;

    // Constructor without discount
    public Bill(String billId, String orderId, int customerId, String customerName, 
                String phoneNumber, Date billDate, double totalAmount, String paymentMethod) {
        this.billId = billId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.billDate = billDate;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.discountAmount = 0.0;
        calculateAmounts();
    }

    // Constructor with discount
    public Bill(String billId, String orderId, int customerId, String customerName, 
                String phoneNumber, Date billDate, double totalAmount, 
                String paymentMethod, double discountAmount) {
        this.billId = billId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.billDate = billDate;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.discountAmount = discountAmount;
        calculateAmounts();
    }

    // Getters and Setters
    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
        calculateAmounts();
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
        calculateAmounts();
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // Helper methods
    private void calculateAmounts() {
        // Calculate tax (14% in Egypt)
        taxAmount = totalAmount * 0.14;
        
        // Calculate final amount
        finalAmount = totalAmount + taxAmount - discountAmount;
        
        // Ensure final amount is not negative
        if (finalAmount < 0) {
            finalAmount = 0;
        }
    }

    // Format date for display
    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(billDate);
    }

    // Get formatted bill for display/printing
    public String getFormattedBill() {
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("           RESTAURANT BILL              \n");
        sb.append("=========================================\n");
        sb.append(String.format("Bill ID:      %s\n", billId));
        sb.append(String.format("Order ID:     %s\n", orderId));
        sb.append(String.format("Date:         %s\n", getFormattedDate()));
        sb.append(String.format("Customer:     %s (ID: %d)\n", customerName, customerId));
        sb.append(String.format("Phone:        %s\n", phoneNumber));
        sb.append(String.format("Payment:      %s\n", paymentMethod));
        sb.append("=========================================\n");
        sb.append(String.format("Subtotal:     %20.2f EGP\n", totalAmount));
        sb.append(String.format("Tax (14%%):    %20.2f EGP\n", taxAmount));
        sb.append(String.format("Discount:     %20.2f EGP\n", discountAmount));
        sb.append("-----------------------------------------\n");
        sb.append(String.format("Total:        %20.2f EGP\n", finalAmount));
        sb.append("=========================================\n");
        sb.append("          THANK YOU FOR VISITING!       \n");
        sb.append("=========================================\n");
        
        return sb.toString();
    }

    // Get short summary
    public String getSummary() {
        return String.format("Bill %s - %s - %.2f EGP", billId, getFormattedDate(), finalAmount);
    }

    @Override
    public String toString() {
        return "Bill{" +
                "billId='" + billId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", billDate=" + billDate +
                ", totalAmount=" + totalAmount +
                ", taxAmount=" + taxAmount +
                ", discountAmount=" + discountAmount +
                ", finalAmount=" + finalAmount +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}