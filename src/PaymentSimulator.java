import javax.swing.*;

public class PaymentSimulator {
    public static boolean simulatePayment(double amount) {
        String paidStr = JOptionPane.showInputDialog(null,
                String.format("Your total is $%.2f\nEnter payment amount:", amount),
                "Payment", JOptionPane.QUESTION_MESSAGE);
        if (paidStr == null) return false;
        try {
            double paid = Double.parseDouble(paidStr);
            if (paid >= amount) {
                JOptionPane.showMessageDialog(null,
                        String.format("Payment successful! Change: $%.2f", paid - amount));
                return true;
            } else {
                JOptionPane.showMessageDialog(null,
                        "Insufficient payment. Order not completed.");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input. Payment cancelled.");
            return false;
        }
    }
}