package view;

import model.Customer;
import model.Order;
import model.Offer;
import controller.CustomerController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class CustomerProfile extends JFrame {
    private Customer customer;
    private JTabbedPane tabbedPane;
    private JLabel pointsLabel;
    private JLabel ordersLabel;
    private JLabel savedOffersLabel;
    private JLabel giftsLabel;
    private JButton joinLoyaltyBtn;
    private DefaultListModel<String> giftsListModel;

    public CustomerProfile(Customer customer) {
        this.customer = customer;
        setTitle("Customer Profile - " + customer.getName());
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Customer Profile", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        createProfileTab();
        createOrdersTab();
        createProgramsTab();
        createOffersTab();
        createGiftsTab();
        add(tabbedPane, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void createProfileTab() {
        JPanel profilePanel = new JPanel(new GridLayout(7, 2, 10, 10));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        profilePanel.add(new JLabel("Customer ID:"));
        profilePanel.add(new JLabel(String.valueOf(customer.getId())));
        
        profilePanel.add(new JLabel("Name:"));
        profilePanel.add(new JLabel(customer.getName()));
        
        profilePanel.add(new JLabel("Phone:"));
        profilePanel.add(new JLabel(customer.getPhoneNumber()));
        
        profilePanel.add(new JLabel("Loyalty Points:"));
        pointsLabel = new JLabel(String.valueOf(customer.getLoyaltyPoints()));
        profilePanel.add(pointsLabel);
        
        profilePanel.add(new JLabel("Total Orders:"));
        int totalOrders = CustomerController.getCustomerOrders(customer.getId()).size();
        ordersLabel = new JLabel(String.valueOf(totalOrders));
        profilePanel.add(ordersLabel);
        
        profilePanel.add(new JLabel("Saved Offers:"));
        savedOffersLabel = new JLabel(String.valueOf(customer.getSavedOffers().size()));
        profilePanel.add(savedOffersLabel);
        
        profilePanel.add(new JLabel("Gifts Redeemed:"));
        int giftsCount = CustomerController.getCustomerGifts(customer.getId()).size();
        giftsLabel = new JLabel(String.valueOf(giftsCount));
        profilePanel.add(giftsLabel);
        
        tabbedPane.addTab("Profile", profilePanel);
    }

    private void createOrdersTab() {
        ArrayList<Order> orders = CustomerController.getCustomerOrders(customer.getId());
        String[] columns = {"Order ID", "Date", "Status", "Amount"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Order order : orders) {
            Object[] row = {
                order.getOrderId(),
                order.getFormattedDate(),
                order.getStatus(),
                String.format("$%.2f", order.getTotalAmount())
            };
            model.addRow(row);
        }

        JTable ordersTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel ordersPanel = new JPanel(new BorderLayout());
        ordersPanel.add(scrollPane, BorderLayout.CENTER);

        JLabel countLabel = new JLabel("Total Orders: " + orders.size());
        countLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        ordersPanel.add(countLabel, BorderLayout.SOUTH);

        tabbedPane.addTab("Orders", ordersPanel);
    }

    private void createProgramsTab() {
        JPanel programsPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        programsPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JPanel loyaltyPanel = new JPanel(new BorderLayout());
        loyaltyPanel.setBorder(BorderFactory.createTitledBorder("Loyalty Program"));

        JPanel loyaltyInfo = new JPanel(new GridLayout(3, 1, 5, 5));
        loyaltyInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        loyaltyInfo.add(new JLabel("Current Points: " + customer.getLoyaltyPoints()));
        loyaltyInfo.add(new JLabel("Get 100 points when you join"));
        loyaltyInfo.add(new JLabel("Redeem points for discounts"));

        joinLoyaltyBtn = new JButton("Join Loyalty Program");
        if (customer.getLoyaltyPoints() >= 100) {
            joinLoyaltyBtn.setText("Already Joined");
            joinLoyaltyBtn.setEnabled(false);
        }

        joinLoyaltyBtn.addActionListener(e -> {
            CustomerController.addLoyaltyPoints(customer.getId(), 100);
            customer.addLoyaltyPoints(100);
            pointsLabel.setText(String.valueOf(customer.getLoyaltyPoints()));
            joinLoyaltyBtn.setText("Already Joined");
            joinLoyaltyBtn.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Joined loyalty program! +100 bonus points!");
        });

        loyaltyPanel.add(loyaltyInfo, BorderLayout.CENTER);
        loyaltyPanel.add(joinLoyaltyBtn, BorderLayout.SOUTH);

        JPanel marketingPanel = new JPanel(new BorderLayout());
        marketingPanel.setBorder(BorderFactory.createTitledBorder("Marketing Program"));

        JPanel marketingContent = new JPanel(new BorderLayout());
        JLabel marketingInfo = new JLabel("Receive special offers and discounts");
        marketingInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton joinMarketingBtn = new JButton("Subscribe to Marketing");
        boolean isSubscribed = customer.getSavedOffers().contains("Marketing Subscriber");
        if (isSubscribed) {
            joinMarketingBtn.setText("Unsubscribe");
        }

        joinMarketingBtn.addActionListener(e -> {
            if (joinMarketingBtn.getText().equals("Subscribe to Marketing")) {
                boolean success = CustomerController.enrollInMarketing(customer.getId());
                if (success) {
                    customer.addSavedOffer("Marketing Subscriber");
                    joinMarketingBtn.setText("Unsubscribe");
                    savedOffersLabel.setText(String.valueOf(customer.getSavedOffers().size()));
                    JOptionPane.showMessageDialog(this, "Subscribed to marketing program!");
                }
            } else {
                customer.removeSavedOffer("Marketing Subscriber");
                joinMarketingBtn.setText("Subscribe to Marketing");
                savedOffersLabel.setText(String.valueOf(customer.getSavedOffers().size()));
                JOptionPane.showMessageDialog(this, "Unsubscribed from marketing program!");
            }
        });

        marketingContent.add(marketingInfo, BorderLayout.CENTER);
        marketingContent.add(joinMarketingBtn, BorderLayout.SOUTH);
        marketingPanel.add(marketingContent, BorderLayout.CENTER);

        programsPanel.add(loyaltyPanel);
        programsPanel.add(marketingPanel);

        tabbedPane.addTab("Programs", programsPanel);
    }

    private void createOffersTab() {
        JPanel offersPanel = new JPanel(new BorderLayout());
        offersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        JPanel availableOffersPanel = new JPanel(new BorderLayout());
        availableOffersPanel.setBorder(BorderFactory.createTitledBorder("Available Offers"));
        
        DefaultListModel<String> offersListModel = new DefaultListModel<>();
        ArrayList<Offer> offers = CustomerController.getAvailableOffers();
        for (Offer offer : offers) {
            offersListModel.addElement(offer.getId() + " - " + offer.getName() + " (" + offer.getDiscount() + "%)");
        }
        
        if (offers.isEmpty()) {
            offersListModel.addElement("No offers available");
        }
        
        JList<String> offersList = new JList<>(offersListModel);
        offersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane offersScroll = new JScrollPane(offersList);
        
        availableOffersPanel.add(offersScroll, BorderLayout.CENTER);

        JPanel marketingOffersPanel = new JPanel(new BorderLayout());
        marketingOffersPanel.setBorder(BorderFactory.createTitledBorder("Marketing Messages"));
        
        DefaultListModel<String> marketingListModel = new DefaultListModel<>();
        ArrayList<String> marketingMessages = CustomerController.getMarketingMessages();
        
        if (marketingMessages.isEmpty()) {
            marketingListModel.addElement("No marketing messages");
        } else {
            for (String message : marketingMessages) {
                if (!message.contains("Customer enrolled in marketing") && 
                    !message.contains("New customer registered")) {
                    marketingListModel.addElement(message);
                }
            }
        }
        
        if (marketingListModel.isEmpty()) {
            marketingListModel.addElement("No marketing messages from admin");
        }
        
        JList<String> marketingList = new JList<>(marketingListModel);
        marketingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane marketingScroll = new JScrollPane(marketingList);
        marketingOffersPanel.add(marketingScroll, BorderLayout.CENTER);

        topPanel.add(availableOffersPanel);
        topPanel.add(marketingOffersPanel);

        offersPanel.add(topPanel, BorderLayout.CENTER);

        tabbedPane.addTab("Offers", offersPanel);
    }

    private void createGiftsTab() {
        JPanel giftsPanel = new JPanel(new BorderLayout(10, 10));
        giftsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel pointsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel pointsInfo = new JLabel("Available Points: " + customer.getLoyaltyPoints());
        pointsInfo.setFont(new Font("Arial", Font.BOLD, 14));
        pointsPanel.add(pointsInfo);
        topPanel.add(pointsPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 15));

        JPanel availableGiftsPanel = new JPanel(new BorderLayout());
        availableGiftsPanel.setBorder(BorderFactory.createTitledBorder("Available Gifts"));

        DefaultListModel<String> giftsModel = new DefaultListModel<>();
        giftsModel.addElement("Free Coffee - 50 points");
        giftsModel.addElement("Free Soft Drink - 75 points");
        giftsModel.addElement("French Fries - 100 points");
        giftsModel.addElement("Garlic Bread - 120 points");
        giftsModel.addElement("Chicken Wings - 150 points");
        giftsModel.addElement("Caesar Salad - 180 points");
        giftsModel.addElement("Burger - 200 points");
        giftsModel.addElement("Pizza Slice - 250 points");
        giftsModel.addElement("Pasta Meal - 300 points");
        giftsModel.addElement("Steak Dinner - 500 points");
        giftsModel.addElement("Seafood Platter - 600 points");

        JList<String> giftsList = new JList<>(giftsModel);
        JScrollPane giftsScroll = new JScrollPane(giftsList);
        availableGiftsPanel.add(giftsScroll, BorderLayout.CENTER);

        JPanel redeemPanel = new JPanel(new BorderLayout());
        redeemPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        JButton redeemBtn = new JButton("Redeem Gift");
        redeemBtn.addActionListener(e -> {
            String selectedGift = giftsList.getSelectedValue();
            if (selectedGift == null) {
                JOptionPane.showMessageDialog(this, "Please select a gift to redeem!");
                return;
            }

            int pointsCost = 0;
            if (selectedGift.contains("50 points")) pointsCost = 50;
            else if (selectedGift.contains("75 points")) pointsCost = 75;
            else if (selectedGift.contains("100 points")) pointsCost = 100;
            else if (selectedGift.contains("120 points")) pointsCost = 120;
            else if (selectedGift.contains("150 points")) pointsCost = 150;
            else if (selectedGift.contains("180 points")) pointsCost = 180;
            else if (selectedGift.contains("200 points")) pointsCost = 200;
            else if (selectedGift.contains("250 points")) pointsCost = 250;
            else if (selectedGift.contains("300 points")) pointsCost = 300;
            else if (selectedGift.contains("500 points")) pointsCost = 500;
            else if (selectedGift.contains("600 points")) pointsCost = 600;

            if (customer.getLoyaltyPoints() < pointsCost) {
                JOptionPane.showMessageDialog(this, "Not enough points! You need " + pointsCost + " points.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, 
                "Redeem '" + selectedGift.split(" - ")[0] + "' for " + pointsCost + " points?",
                "Confirm Redemption", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = CustomerController.redeemGift(customer.getId(), 
                    selectedGift.split(" - ")[0], pointsCost);
                
                if (success) {
                    customer.addLoyaltyPoints(-pointsCost);
                    pointsLabel.setText(String.valueOf(customer.getLoyaltyPoints()));
                    pointsInfo.setText("Available Points: " + customer.getLoyaltyPoints());
                    
                    giftsListModel.addElement(selectedGift.split(" - ")[0] + " (Redeemed)");
                    int giftsCount = CustomerController.getCustomerGifts(customer.getId()).size();
                    giftsLabel.setText(String.valueOf(giftsCount));
                    
                    JOptionPane.showMessageDialog(this, "Gift redeemed successfully!");
                }
            }
        });
        redeemPanel.add(redeemBtn, BorderLayout.CENTER);
        availableGiftsPanel.add(redeemPanel, BorderLayout.SOUTH);

        JPanel redeemedGiftsPanel = new JPanel(new BorderLayout());
        redeemedGiftsPanel.setBorder(BorderFactory.createTitledBorder("Redeemed Gifts"));

        giftsListModel = new DefaultListModel<>();
        ArrayList<String> customerGifts = CustomerController.getCustomerGifts(customer.getId());
        for (String gift : customerGifts) {
            giftsListModel.addElement(gift + " (Redeemed)");
        }

        if (giftsListModel.isEmpty()) {
            giftsListModel.addElement("No gifts redeemed yet");
        }

        JList<String> redeemedList = new JList<>(giftsListModel);
        JScrollPane redeemedScroll = new JScrollPane(redeemedList);
        redeemedGiftsPanel.add(redeemedScroll, BorderLayout.CENTER);

        centerPanel.add(availableGiftsPanel);
        centerPanel.add(redeemedGiftsPanel);

        giftsPanel.add(topPanel, BorderLayout.NORTH);
        giftsPanel.add(centerPanel, BorderLayout.CENTER);

        tabbedPane.addTab("Gifts", giftsPanel);
    }
}