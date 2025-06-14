import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

public class FoodOrderingApp {
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin123";
    private static FoodOrderingSystem system = new FoodOrderingSystem();
    private static User currentUser = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FoodOrderingApp::showStartScreen);
    }

    private static void showStartScreen() {
        JFrame frame = new JFrame("Food Ordering App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        JButton btnRegister = new JButton("Register");
        JButton btnLogin = new JButton("User Login");
        JButton btnAdmin = new JButton("Admin Panel");
        JButton btnExit = new JButton("Exit");

        panel.add(new JLabel("Welcome to the Food Ordering App!", SwingConstants.CENTER));
        panel.add(btnRegister);
        panel.add(btnLogin);
        panel.add(btnAdmin);
        panel.add(btnExit);

        frame.setContentPane(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        btnRegister.addActionListener(e -> {
            frame.dispose();
            showRegistrationScreen();
        });
        btnLogin.addActionListener(e -> {
            frame.dispose();
            showLoginScreen();
        });
        btnAdmin.addActionListener(e -> {
            frame.dispose();
            showAdminLoginScreen();
        });
        btnExit.addActionListener(e -> System.exit(0));
    }

    private static void showRegistrationScreen() {
        String username = JOptionPane.showInputDialog(null, "Choose a username:");
        if (username == null) {
            showStartScreen();
            return;
        }
        String password = JOptionPane.showInputDialog(null, "Choose a password:");
        if (password == null) {
            showStartScreen();
            return;
        }
        boolean ok = system.register(username, password);
        JOptionPane.showMessageDialog(null,
                ok ? "Registration successful!" : "Username already exists.");
        showStartScreen();
    }

    private static void showLoginScreen() {
        String username = JOptionPane.showInputDialog(null, "Username:");
        if (username == null) {
            showStartScreen();
            return;
        }
        String password = JOptionPane.showInputDialog(null, "Password:");
        if (password == null) {
            showStartScreen();
            return;
        }
        User user = system.login(username, password);
        if (user != null) {
            currentUser = user;
            showUserSessionScreen();
        } else {
            JOptionPane.showMessageDialog(null, "Invalid credentials.");
            showStartScreen();
        }
    }

    private static void showUserSessionScreen() {
        JFrame frame = new JFrame("User Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 400);

        DefaultListModel<FoodItem> menuModel = new DefaultListModel<>();
        for (FoodItem fi : system.getMenu()) menuModel.addElement(fi);

        JList<FoodItem> menuList = new JList<>(menuModel);
        menuList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultListModel<FoodItem> cartModel = new DefaultListModel<>();
        JList<FoodItem> cartList = new JList<>(cartModel);

        JButton btnAdd = new JButton("Add to Cart");
        JButton btnRemove = new JButton("Remove from Cart");
        JButton btnOrder = new JButton("Place Order");
        JButton btnHistory = new JButton("Order History");
        JButton btnLogout = new JButton("Logout");

        JPanel panel = new JPanel(new BorderLayout());
        JPanel leftPanel = new JPanel(new BorderLayout());
        JPanel rightPanel = new JPanel(new BorderLayout());

        leftPanel.add(new JLabel("Menu"), BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(menuList), BorderLayout.CENTER);
        leftPanel.add(btnAdd, BorderLayout.SOUTH);

        rightPanel.add(new JLabel("Cart"), BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(cartList), BorderLayout.CENTER);
        JPanel rightSouth = new JPanel(new GridLayout(3,1));
        rightSouth.add(btnRemove);
        rightSouth.add(btnOrder);
        rightSouth.add(btnHistory);
        rightPanel.add(rightSouth, BorderLayout.SOUTH);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.CENTER);
        panel.add(btnLogout, BorderLayout.SOUTH);

        frame.setContentPane(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        btnAdd.addActionListener(e -> {
            FoodItem selected = menuList.getSelectedValue();
            if (selected != null) {
                cartModel.addElement(selected);
            }
        });

        btnRemove.addActionListener(e -> {
            FoodItem selected = cartList.getSelectedValue();
            if (selected != null) {
                cartModel.removeElement(selected);
            }
        });

        btnOrder.addActionListener(e -> {
            if (cartModel.getSize() == 0) {
                JOptionPane.showMessageDialog(frame, "Cart is empty!");
                return;
            }
            java.util.List<FoodItem> items = Collections.list(cartModel.elements());
            boolean ok = system.placeOrder(currentUser, items);
            if (ok) {
                JOptionPane.showMessageDialog(frame, "Order placed!");
                cartModel.clear();
            }
        });

        btnHistory.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            java.util.List<Order> history = currentUser.getOrderHistory();
            if (history.isEmpty()) {
                sb.append("No orders yet.");
            } else {
                for (Order order : history) {
                    sb.append(order).append("\n");
                }
            }
            JOptionPane.showMessageDialog(frame, sb.toString(), "Order History", JOptionPane.INFORMATION_MESSAGE);
        });

        btnLogout.addActionListener(e -> {
            frame.dispose();
            currentUser = null;
            showStartScreen();
        });
    }

    private static void showAdminLoginScreen() {
        String username = JOptionPane.showInputDialog(null, "Admin username:");
        if (username == null) {
            showStartScreen();
            return;
        }
        String password = JOptionPane.showInputDialog(null, "Admin password:");
        if (password == null) {
            showStartScreen();
            return;
        }
        if (ADMIN_USER.equals(username) && ADMIN_PASS.equals(password)) {
            showAdminPanel();
        } else {
            JOptionPane.showMessageDialog(null, "Invalid admin credentials.");
            showStartScreen();
        }
    }

    private static void showAdminPanel() {
        JFrame frame = new JFrame("Admin Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        DefaultListModel<FoodItem> menuModel = new DefaultListModel<>();
        for (FoodItem fi : system.getMenu()) menuModel.addElement(fi);

        JList<FoodItem> menuList = new JList<>(menuModel);

        JButton btnAdd = new JButton("Add Menu Item");
        JButton btnRemove = new JButton("Remove Menu Item");
        JButton btnOrders = new JButton("View All Orders");
        JButton btnLogout = new JButton("Logout");

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Menu"), BorderLayout.NORTH);
        panel.add(new JScrollPane(menuList), BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new GridLayout(1, 4));
        southPanel.add(btnAdd);
        southPanel.add(btnRemove);
        southPanel.add(btnOrders);
        southPanel.add(btnLogout);

        panel.add(southPanel, BorderLayout.SOUTH);

        frame.setContentPane(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        btnAdd.addActionListener(e -> {
            JTextField tfName = new JTextField();
            JTextField tfDesc = new JTextField();
            JTextField tfPrice = new JTextField();
            Object[] message = {
                    "Name:", tfName,
                    "Description:", tfDesc,
                    "Price:", tfPrice
            };
            int option = JOptionPane.showConfirmDialog(frame, message, "Add Menu Item", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    String name = tfName.getText();
                    String desc = tfDesc.getText();
                    double price = Double.parseDouble(tfPrice.getText());
                    system.addMenuItem(name, desc, price);
                    menuModel.clear();
                    for (FoodItem fi : system.getMenu()) menuModel.addElement(fi);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid input.");
                }
            }
        });

        btnRemove.addActionListener(e -> {
            FoodItem selected = menuList.getSelectedValue();
            if (selected != null) {
                system.removeMenuItem(selected.getId());
                menuModel.clear();
                for (FoodItem fi : system.getMenu()) menuModel.addElement(fi);
            }
        });

        btnOrders.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            java.util.List<Order> orders = system.getAllOrders();
            if (orders.isEmpty()) {
                sb.append("No orders yet.");
            } else {
                for (Order order : orders) {
                    sb.append(order).append("\n");
                }
            }
            JOptionPane.showMessageDialog(frame, sb.toString(), "All Orders", JOptionPane.INFORMATION_MESSAGE);
        });

        btnLogout.addActionListener(e -> {
            frame.dispose();
            showStartScreen();
        });
    }
}