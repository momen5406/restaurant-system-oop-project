package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Meal {
    private static final String FIELD_DELIMITER = "\t";

    private int mealID;
    private String name;
    private double price;
    private String category;
    private String description;
    private boolean isAvailable;
    private String ingredients;
    private int prepTimeMinutes;

    public Meal(int mealID, String name, double price, String category, String description) {
        this(mealID, name, price, category, description, true, "", 0);
    }

    public Meal(int mealID, String name, double price, String category, String description,
                boolean isAvailable, String ingredients, int prepTimeMinutes) {
        this.mealID = mealID;
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
        this.isAvailable = isAvailable;
        this.ingredients = ingredients == null ? "" : ingredients;
        this.prepTimeMinutes = Math.max(prepTimeMinutes, 0);
    }

    public int getMealID() {
        return mealID;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public String getIngredients() {
        return ingredients;
    }

    public int getPrepTimeMinutes() {
        return prepTimeMinutes;
    }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
    }

    public void setPrice(double price) {
        if (price > 0) {
            this.price = price;
        }
    }

    public void setCategory(String category) {
        if (category != null && !category.trim().isEmpty()) {
            this.category = category.trim();
        }
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients == null ? "" : ingredients;
    }

    public void setPrepTimeMinutes(int prepTimeMinutes) {
        this.prepTimeMinutes = Math.max(prepTimeMinutes, 0);
    }

    public void updatePrice(double newPrice) {
        setPrice(newPrice);
    }

    public void toggleAvailability() {
        setAvailable(!isAvailable);
    }

    public void displayMeal() {
        System.out.println("+><+<>+><+<>+><+<>+><+");
        System.out.println("meal ID: " + mealID);
        System.out.println("nmae: " + name);
        System.out.println("price: $" + String.format("%.2f", price));
        System.out.println("category: " + category);
        System.out.println("description: " + description);
        System.out.println("available: " + (isAvailable ? "Yes" : "No"));
        System.out.println("       ==========     ");
    }

    @Override
    public String toString() {
        String availabilityLabel = isAvailable ? "[Available]" : "[Out of Stock]";
        return String.format("#%d %s - $%.2f (%s) %s", mealID, name, price, category, availabilityLabel);
    }

    public String getFormattedPrice() {
        return String.format("$%.2f", price);
    }

    public String toFileRecord() {
        String[] parts = new String[]{
                String.valueOf(mealID),
                safeField(name),
                String.valueOf(price),
                safeField(category),
                safeField(description),
                String.valueOf(isAvailable),
                safeField(ingredients),
                String.valueOf(prepTimeMinutes)
        };
        return String.join(FIELD_DELIMITER, parts);
    }

    public static Meal fromFileRecord(String line) {
        if (line == null || line.isEmpty()) {
            return null;
        }
        String[] parts = line.split(FIELD_DELIMITER, -1);
        if (parts.length < 8) {
            return null;
        }
        try {
            int id = Integer.parseInt(parts[0]);
            double parsedPrice = Double.parseDouble(parts[2]);
            boolean available = Boolean.parseBoolean(parts[5]);
            int prepTime = Integer.parseInt(parts[7]);
            return new Meal(
                    id,
                    parts[1],
                    parsedPrice,
                    parts[3],
                    parts[4],
                    available,
                    parts[6],
                    prepTime
            );
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static List<Meal> filterByName(List<Meal> meals, String query) {
        if (meals == null || query == null || query.isEmpty()) {
            return Collections.emptyList();
        }
        String needle = query.toLowerCase();
        List<Meal> results = new ArrayList<>();
        for (Meal meal : meals) {
            if (meal.getName().toLowerCase().contains(needle)) {
                results.add(meal);
            }
        }
        return results;
    }

    public static Meal findById(List<Meal> meals, int id) {
        if (meals == null) {
            return null;
        }
        for (Meal meal : meals) {
            if (meal.getMealID() == id) {
                return meal;
            }
        }
        return null;
    }

    public static List<Meal> filterByCategory(List<Meal> meals, String category) {
        if (meals == null || category == null || category.isEmpty()) {
            return Collections.emptyList();
        }
        String target = category.toLowerCase();
        List<Meal> results = new ArrayList<>();
        for (Meal meal : meals) {
            if (meal.getCategory().toLowerCase().equals(target)) {
                results.add(meal);
            }
        }
        return results;
    }

    public static List<Meal> filterByPriceRange(List<Meal> meals, double minPrice, double maxPrice) {
        if (meals == null) {
            return Collections.emptyList();
        }
        List<Meal> results = new ArrayList<>();
        for (Meal meal : meals) {
            double value = meal.getPrice();
            if (value >= minPrice && value <= maxPrice) {
                results.add(meal);
            }
        }
        return results;
    }

    public static List<Meal> sort(List<Meal> meals, Comparator<Meal> comparator) {
        if (meals == null || comparator == null) {
            return Collections.emptyList();
        }
        List<Meal> sorted = new ArrayList<>(meals);
        sorted.sort(comparator);
        return sorted;
    }

    public static Comparator<Meal> sortByNameAsc() {
        return Comparator.comparing(Meal::getName, String.CASE_INSENSITIVE_ORDER);
    }

    public static Comparator<Meal> sortByNameDesc() {
        return sortByNameAsc().reversed();
    }

    public static Comparator<Meal> sortByPriceAsc() {
        return Comparator.comparingDouble(Meal::getPrice);
    }

    public static Comparator<Meal> sortByPriceDesc() {
        return sortByPriceAsc().reversed();
    }

    public static Comparator<Meal> sortByCategory() {
        return Comparator.comparing(Meal::getCategory, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Meal::getName, String.CASE_INSENSITIVE_ORDER);
    }

    public static Comparator<Meal> sortByAvailability() {
        return Comparator.comparing(Meal::isAvailable).reversed()
                .thenComparing(Meal::getName, String.CASE_INSENSITIVE_ORDER);
    }

    public static Comparator<Meal> sortByPrepTime() {
        return Comparator.comparingInt(Meal::getPrepTimeMinutes)
                .thenComparing(Meal::getName, String.CASE_INSENSITIVE_ORDER);
    }

    private String safeField(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\n", " ").replace("\r", " ").replace(FIELD_DELIMITER, " ").trim();
    }
}

