import java.io.Serializable;
import java.util.*;

public class User implements Serializable {
    private String username;
    private String passwordHash;
    private LinkedList<Order> orderHistory = new LinkedList<>();

    public User(String username, String password) {
        this.username = username;
        this.passwordHash = Integer.toString(password.hashCode());
    }

    public String getUsername() { return username; }
    public boolean checkPassword(String password) {
        return passwordHash.equals(Integer.toString(password.hashCode()));
    }

    public void addOrder(Order order) {
        orderHistory.add(order);
    }

    public List<Order> getOrderHistory() {
        return orderHistory;
    }
}