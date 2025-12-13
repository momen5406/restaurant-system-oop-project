package model;

public class Offer {
    private String id;
    private String name;
    private String discount;

    public Offer(String id, String name, String discount) {
        this.id = id;
        this.name = name;
        this.discount = discount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
