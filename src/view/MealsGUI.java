package view;

import controller.AdminController;
import model.Meal;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class MealsGUI extends JFrame {
    private AdminController adminController;
    
    // Meal management
    private DefaultTableModel mealTableModel;
    private JTable mealTable;
    private JTextField mealNameField;
    private JTextField mealPriceField;
    private JTextField mealCategoryField;
    private JTextArea mealDescriptionArea;
    private JTextField mealIngredientsField;
    private JTextField mealPrepTimeField;
    private JCheckBox mealAvailabilityCheck;

    private JTextField editMealNameField;
    private JTextField editMealPriceField;
    private JTextField editMealCategoryField;
    private JTextArea editMealDescriptionArea;
    private JTextField editMealIngredientsField;
    private JTextField editMealPrepTimeField;
    private JCheckBox editMealAvailabilityCheck;
    private JLabel editMealIdLabel;
    private JButton undoDeleteMealButton;

    private Meal lastDeletedMeal;
    private int lastDeletedMealIndex = -1;
    private boolean suppressAvailabilityEvents = false;

    public MealsGUI() {
        adminController = new AdminController();
        setTitle("Restaurant | Manage Meals");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeComponents();
        setVisible(true);
    }

    private void initializeComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Meal table at the top
        String[] columns = {"ID", "Name", "Price", "Category", "Available", "Prep Time", "Ingredients", "Description"};
        mealTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) return Boolean.class;
                return Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                // allow toggling availability directly
                return column == 4;
            }
        };
        mealTable = new JTable(mealTableModel);
        mealTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mealTable.setAutoCreateRowSorter(true);

        // Listen for availability toggles
        mealTableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (suppressAvailabilityEvents) return;
                if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 4) {
                    int row = e.getFirstRow();
                    if (row >= 0) {
                        int modelRow = mealTable.convertRowIndexToModel(row);
                        int id = (int) mealTableModel.getValueAt(modelRow, 0);
                        boolean available = (boolean) mealTableModel.getValueAt(modelRow, 4);
                        adminController.toggleMealAvailability(id, available);
                        JOptionPane.showMessageDialog(MealsGUI.this, "Availability updated.");
                    }
                }
            }
        });

        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sortPanel.setBorder(BorderFactory.createTitledBorder("Sort Options"));
        JComboBox<String> sortCombo = new JComboBox<>(new String[]{
            "Sort: Name A-Z",
            "Sort: Name Z-A",
            "Sort: Price Low-High",
            "Sort: Price High-Low",
            "Sort: Category",
            "Sort: Availability",
            "Sort: Prep Time"
        });
        sortCombo.addActionListener(e -> refreshMealTable(getComparator(sortCombo.getSelectedIndex())));
        sortPanel.add(sortCombo);

        JScrollPane mealScroll = new JScrollPane(mealTable);
        mealScroll.setBorder(BorderFactory.createTitledBorder("Meals List"));
        mealScroll.setPreferredSize(new Dimension(800, 250));

        // Tabs for Add/Edit below the table
        JTabbedPane mealTabs = new JTabbedPane();
        mealTabs.add("Add Meal", createAddMealTab());
        mealTabs.add("Edit/Delete", createEditMealTab());

        // Layout: Sort panel at top, table in center, tabs at bottom
        mainPanel.add(sortPanel, BorderLayout.NORTH);
        mainPanel.add(mealScroll, BorderLayout.CENTER);
        mainPanel.add(mealTabs, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        refreshMealTable(getComparator(0));
    }

    private Comparator<Meal> getComparator(int index) {
        switch (index) {
            case 1:
                return Meal.sortByNameDesc();
            case 2:
                return Meal.sortByPriceAsc();
            case 3:
                return Meal.sortByPriceDesc();
            case 4:
                return Meal.sortByCategory();
            case 5:
                return Meal.sortByAvailability();
            case 6:
                return Meal.sortByPrepTime();
            default:
                return Meal.sortByNameAsc();
        }
    }

    private JPanel createAddMealTab() {
        JPanel addPanel = new JPanel(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        mealNameField = new JTextField();
        mealPriceField = new JTextField();
        mealCategoryField = new JTextField();
        mealDescriptionArea = new JTextArea(3, 20);
        mealDescriptionArea.setLineWrap(true);
        mealDescriptionArea.setWrapStyleWord(true);
        mealIngredientsField = new JTextField();
        mealPrepTimeField = new JTextField();
        mealAvailabilityCheck = new JCheckBox("Available", true);

        form.add(new JLabel("Name:")); form.add(mealNameField);
        form.add(new JLabel("Price:")); form.add(mealPriceField);
        form.add(new JLabel("Category:")); form.add(mealCategoryField);
        form.add(new JLabel("Ingredients:")); form.add(mealIngredientsField);
        form.add(new JLabel("Prep Time (min):")); form.add(mealPrepTimeField);
        form.add(new JLabel("Description:")); form.add(new JScrollPane(mealDescriptionArea));
        form.add(new JLabel("Status:")); form.add(mealAvailabilityCheck);

        JButton addMealButton = new JButton("Add Meal");
        addMealButton.addActionListener(e -> handleAddMeal());

        addPanel.add(form, BorderLayout.CENTER);
        addPanel.add(addMealButton, BorderLayout.SOUTH);
        return addPanel;
    }

    private JPanel createEditMealTab() {
        JPanel editPanel = new JPanel(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        editMealIdLabel = new JLabel("No meal selected");
        editMealNameField = new JTextField();
        editMealPriceField = new JTextField();
        editMealCategoryField = new JTextField();
        editMealIngredientsField = new JTextField();
        editMealPrepTimeField = new JTextField();
        editMealDescriptionArea = new JTextArea(3, 20);
        editMealDescriptionArea.setLineWrap(true);
        editMealDescriptionArea.setWrapStyleWord(true);
        editMealAvailabilityCheck = new JCheckBox("Available", true);

        form.add(new JLabel("Selected Meal ID:")); form.add(editMealIdLabel);
        form.add(new JLabel("Name:")); form.add(editMealNameField);
        form.add(new JLabel("Price:")); form.add(editMealPriceField);
        form.add(new JLabel("Category:")); form.add(editMealCategoryField);
        form.add(new JLabel("Ingredients:")); form.add(editMealIngredientsField);
        form.add(new JLabel("Prep Time (min):")); form.add(editMealPrepTimeField);
        form.add(new JLabel("Description:")); form.add(new JScrollPane(editMealDescriptionArea));
        form.add(new JLabel("Status:")); form.add(editMealAvailabilityCheck);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton updateButton = new JButton("Save Changes");
        JButton deleteButton = new JButton("Delete Meal");
        undoDeleteMealButton = new JButton("Undo Delete");
        undoDeleteMealButton.setEnabled(false);

        updateButton.addActionListener(e -> handleUpdateMeal());
        deleteButton.addActionListener(e -> handleDeleteMeal());
        undoDeleteMealButton.addActionListener(e -> handleUndoDeleteMeal());

        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(undoDeleteMealButton);

        mealTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = mealTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = mealTable.convertRowIndexToModel(selectedRow);
                    Meal meal = buildMealFromRow(modelRow);
                    populateEditFields(meal);
                }
            }
        });

        editPanel.add(form, BorderLayout.CENTER);
        editPanel.add(buttons, BorderLayout.SOUTH);
        return editPanel;
    }

    private void handleAddMeal() {
        try {
            String name = mealNameField.getText();
            double price = Double.parseDouble(mealPriceField.getText());
            String category = mealCategoryField.getText();
            String ingredients = mealIngredientsField.getText();
            int prepTime = mealPrepTimeField.getText().isEmpty() ? 0 : Integer.parseInt(mealPrepTimeField.getText());
            String description = mealDescriptionArea.getText();
            boolean available = mealAvailabilityCheck.isSelected();

            adminController.addMeal(name, price, category, description, available, ingredients, prepTime);
            refreshMealTable(getComparator(0));
            clearAddMealForm();
            JOptionPane.showMessageDialog(this, "Meal added successfully.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price and Prep Time must be numeric.");
        }
    }

    private void handleUpdateMeal() {
        if (editMealIdLabel.getText() == null || editMealIdLabel.getText().isEmpty() || !editMealIdLabel.getText().matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Please select a meal to edit.");
            return;
        }
        try {
            int id = Integer.parseInt(editMealIdLabel.getText());
            String name = editMealNameField.getText();
            double price = Double.parseDouble(editMealPriceField.getText());
            String category = editMealCategoryField.getText();
            String ingredients = editMealIngredientsField.getText();
            int prepTime = editMealPrepTimeField.getText().isEmpty() ? 0 : Integer.parseInt(editMealPrepTimeField.getText());
            String description = editMealDescriptionArea.getText();
            boolean available = editMealAvailabilityCheck.isSelected();

            Meal updated = new Meal(id, name, price, category, description, available, ingredients, prepTime);
            boolean saved = adminController.updateMeal(updated);
            if (saved) {
                refreshMealTable(getComparator(0));
                JOptionPane.showMessageDialog(this, "Meal updated successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Meal not found.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price and Prep Time must be numeric.");
        }
    }

    private void handleDeleteMeal() {
        int selectedRow = mealTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a meal to delete.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this meal?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        int modelRow = mealTable.convertRowIndexToModel(selectedRow);
        Meal target = buildMealFromRow(modelRow);
        boolean deleted = adminController.deleteMeal(target.getMealID());
        if (deleted) {
            lastDeletedMeal = target;
            lastDeletedMealIndex = modelRow;
            undoDeleteMealButton.setEnabled(true);
            refreshMealTable(getComparator(0));
            JOptionPane.showMessageDialog(this, "Meal deleted.");
        } else {
            JOptionPane.showMessageDialog(this, "Meal not found.");
        }
    }

    private void handleUndoDeleteMeal() {
        if (lastDeletedMeal == null) {
            return;
        }
        adminController.restoreMeal(lastDeletedMeal);
        refreshMealTable(getComparator(0));
        undoDeleteMealButton.setEnabled(false);
        JOptionPane.showMessageDialog(this, "Meal restored.");
    }

    private Meal buildMealFromRow(int modelRow) {
        int id = (int) mealTableModel.getValueAt(modelRow, 0);
        String name = (String) mealTableModel.getValueAt(modelRow, 1);
        double price = Double.parseDouble(mealTableModel.getValueAt(modelRow, 2).toString());
        String category = (String) mealTableModel.getValueAt(modelRow, 3);
        boolean available = (boolean) mealTableModel.getValueAt(modelRow, 4);
        int prepTime = Integer.parseInt(mealTableModel.getValueAt(modelRow, 5).toString());
        String ingredients = (String) mealTableModel.getValueAt(modelRow, 6);
        String description = (String) mealTableModel.getValueAt(modelRow, 7);
        return new Meal(id, name, price, category, description, available, ingredients, prepTime);
    }

    private void populateEditFields(Meal meal) {
        if (meal == null) return;
        editMealIdLabel.setText(String.valueOf(meal.getMealID()));
        editMealNameField.setText(meal.getName());
        editMealPriceField.setText(String.valueOf(meal.getPrice()));
        editMealCategoryField.setText(meal.getCategory());
        editMealIngredientsField.setText(meal.getIngredients());
        editMealPrepTimeField.setText(String.valueOf(meal.getPrepTimeMinutes()));
        editMealDescriptionArea.setText(meal.getDescription());
        editMealAvailabilityCheck.setSelected(meal.isAvailable());
    }

    private void refreshMealTable(Comparator<Meal> comparator) {
        suppressAvailabilityEvents = true;
        mealTableModel.setRowCount(0);
        List<Meal> meals = adminController.getAllMeals();
        if (comparator != null) {
            meals = Meal.sort(meals, comparator);
        }
        for (Meal meal : meals) {
            mealTableModel.addRow(new Object[]{
                meal.getMealID(),
                meal.getName(),
                meal.getPrice(),
                meal.getCategory(),
                meal.isAvailable(),
                meal.getPrepTimeMinutes(),
                meal.getIngredients(),
                meal.getDescription()
            });
        }
        suppressAvailabilityEvents = false;
    }

    private void clearAddMealForm() {
        mealNameField.setText("");
        mealPriceField.setText("");
        mealCategoryField.setText("");
        mealDescriptionArea.setText("");
        mealIngredientsField.setText("");
        mealPrepTimeField.setText("");
        mealAvailabilityCheck.setSelected(true);
    }
}

