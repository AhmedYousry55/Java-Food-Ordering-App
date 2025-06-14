import java.io.Serializable;

public class FoodItem implements Serializable {
    private int id;
    private String name;
    private String description;
    private double price;

    public FoodItem(int id, String name, String description, double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return String.format("%d. %s ($%.2f) - %s", id, name, price, description);
    }
}