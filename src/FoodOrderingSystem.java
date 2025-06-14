import java.io.*;
import java.util.*;

public class FoodOrderingSystem {
    private Map<String, User> users;
    private Map<Integer, FoodItem> menu;
    private int nextFoodId;
    private int nextOrderId;

    public static final String USERS_FILE = "users.dat";
    public static final String MENU_FILE = "menu.dat";

    public FoodOrderingSystem() {
        users = new HashMap<>();
        menu = new HashMap<>();
        nextFoodId = 1;
        nextOrderId = 1;
        loadFromDisk();
    }

    // User Management
    public boolean register(String username, String password) {
        if (users.containsKey(username)) return false;
        users.put(username, new User(username, password));
        saveUsers();
        return true;
    }

    public User login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.checkPassword(password)) return user;
        return null;
    }

    // Menu Management
    public void addMenuItem(String name, String desc, double price) {
        FoodItem item = new FoodItem(nextFoodId++, name, desc, price);
        menu.put(item.getId(), item);
        saveMenu();
    }

    public boolean removeMenuItem(int id) {
        if (menu.remove(id) != null) {
            saveMenu();
            return true;
        }
        return false;
    }

    public Collection<FoodItem> getMenu() {
        return menu.values();
    }

    public FoodItem getMenuItem(int id) {
        return menu.get(id);
    }

    // Order Management
    public boolean placeOrder(User user, List<FoodItem> cart) {
        if (cart.isEmpty()) return false;
        double total = cart.stream().mapToDouble(FoodItem::getPrice).sum();
        boolean paid = PaymentSimulator.simulatePayment(total);
        if (paid) {
            Order order = new Order(nextOrderId++, cart);
            user.addOrder(order);
            saveUsers();
            return true;
        }
        return false;
    }

    // Admin: View all orders
    public List<Order> getAllOrders() {
        List<Order> result = new ArrayList<>();
        for (User user : users.values()) {
            result.addAll(user.getOrderHistory());
        }
        return result;
    }

    // Persistence
    private void saveUsers() {
        try {
            PersistenceManager.saveObject(users, USERS_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveMenu() {
        try {
            PersistenceManager.saveObject(menu, MENU_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFromDisk() {
        try {
            File fMenu = new File(MENU_FILE);
            File fUsers = new File(USERS_FILE);
            if (fMenu.exists()) {
                menu = (HashMap<Integer, FoodItem>) PersistenceManager.loadObject(MENU_FILE);
                nextFoodId = menu.keySet().stream().mapToInt(i -> i).max().orElse(0) + 1;
            } else {
                // Seed menu if not found
                addMenuItem("Burger", "Beef patty with cheese", 5.99);
                addMenuItem("Pizza", "Margherita classic", 8.49);
                addMenuItem("Pasta", "Creamy Alfredo", 7.25);
                addMenuItem("Salad", "Caesar Salad", 4.50);
                addMenuItem("Soda", "Chilled cola drink", 1.50);
            }
            if (fUsers.exists()) {
                users = (HashMap<String, User>) PersistenceManager.loadObject(USERS_FILE);
                // find max order id for all users
                int maxOrder = 0;
                for (User u : users.values()) {
                    for (Order o : u.getOrderHistory()) {
                        maxOrder = Math.max(maxOrder, o.getOrderId());
                    }
                }
                nextOrderId = maxOrder + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}