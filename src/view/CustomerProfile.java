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
    private JButton joinLoyaltyBtn;

    public CustomerProfile(Customer customer) {
        this.customer = customer;
        setTitle("Customer Profile - " + customer.getName());
        setSize(700, 550);
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
        add(tabbedPane, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void createProfileTab() {
        JPanel profilePanel = new JPanel(new GridLayout(6, 2, 10, 10));
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
        
        JButton claimOfferBtn = new JButton("Claim Offer");
        claimOfferBtn.addActionListener(e -> {
            int selectedIndex = offersList.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < offers.size()) {
                Offer selectedOffer = offers.get(selectedIndex);
                boolean success = CustomerController.claimOffer(customer.getId(), selectedOffer.getId());
                if (success) {
                    customer = CustomerController.login(customer.getPhoneNumber());
                    savedOffersLabel.setText(String.valueOf(customer.getSavedOffers().size()));
                    JOptionPane.showMessageDialog(this, "Offer claimed: " + selectedOffer.getName());
                    updateOffersTab();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select an offer to claim");
            }
        });
        
        JPanel offersButtonPanel = new JPanel();
        offersButtonPanel.add(claimOfferBtn);
        availableOffersPanel.add(offersScroll, BorderLayout.CENTER);
        availableOffersPanel.add(offersButtonPanel, BorderLayout.SOUTH);

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

        JPanel savedOffersPanel = new JPanel(new BorderLayout());
        savedOffersPanel.setBorder(BorderFactory.createTitledBorder("Your Saved Offers"));
        JTextArea savedOffersArea = new JTextArea(8, 50);
        savedOffersArea.setEditable(false);
        savedOffersArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane savedOffersScroll = new JScrollPane(savedOffersArea);
        updateSavedOffersArea(savedOffersArea);
        savedOffersPanel.add(savedOffersScroll, BorderLayout.CENTER);

        offersPanel.add(topPanel, BorderLayout.CENTER);
        offersPanel.add(savedOffersPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Offers", offersPanel);
    }

    private void updateSavedOffersArea(JTextArea savedOffersArea) {
        StringBuilder savedOffersText = new StringBuilder();
        for (String offer : customer.getSavedOffers()) {
            savedOffersText.append("• ").append(offer).append("\n");
        }
        if (customer.getSavedOffers().isEmpty()) {
            savedOffersText.append("No saved offers");
        }
        savedOffersArea.setText(savedOffersText.toString());
    }

    private void updateOffersTab() {
        int offersTabIndex = 3;
        if (tabbedPane.getTabCount() > offersTabIndex) {
            JPanel offersPanel = (JPanel) tabbedPane.getComponentAt(offersTabIndex);
            JPanel savedOffersPanel = (JPanel) ((JPanel) offersPanel.getComponent(0)).getComponent(1);
            JScrollPane savedOffersScroll = (JScrollPane) savedOffersPanel.getComponent(0);
            JTextArea savedOffersArea = (JTextArea) savedOffersScroll.getViewport().getView();
            updateSavedOffersArea(savedOffersArea);
        }
    }
}