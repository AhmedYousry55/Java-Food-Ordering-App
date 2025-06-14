import java.io.Serializable;
import java.util.*;

public class Order implements Serializable {
    private int orderId;
    private List<FoodItem> items;
    private Date date;

    public Order(int orderId, List<FoodItem> items) {
        this.orderId = orderId;
        this.items = new ArrayList<>(items);
        this.date = new Date();
    }

    public int getOrderId() { return orderId; }
    public List<FoodItem> getItems() { return items; }
    public Date getDate() { return date; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order #").append(orderId).append(" [").append(date).append("]: ");
        double total = 0;
        for (FoodItem item : items) {
            sb.append(item.getName()).append(" ($").append(item.getPrice()).append("), ");
            total += item.getPrice();
        }
        sb.append("Total: $").append(String.format("%.2f", total));
        return sb.toString();
    }
}