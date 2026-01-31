import java.sql.*;
import java.util.Scanner;

public class ATMSystem {
    static final String DB_URL = "jdbc:mysql://localhost:3306/atm";
    static final String DB_USER = "root";
    static final String DB_PASS = "Patil@17";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter your Debit Card Number: ");
        String debitCardNo = sc.nextLine();

        char continueChoice;

        do {
            System.out.println("\n--- ATM Menu ---");
            System.out.println("1. Check Balance");
            System.out.println("2. Withdraw");
            System.out.println("3. Change PIN");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();
            sc.nextLine(); // Consume newline

            System.out.print("Enter your PIN: ");
            String enteredPin = sc.nextLine();

            if (verifyPin(debitCardNo, enteredPin)) {
                switch (choice) {
                    case 1:
                        checkBalance(debitCardNo);
                        break;
                    case 2:
                        withdraw(debitCardNo, sc);
                        break;
                    case 3:
                        changePin(debitCardNo, sc);
                        break;
                    default:
                        System.out.println("Invalid option selected.");
                }
            } else {
                System.out.println("Incorrect PIN.");
            }

            System.out.print("Do you want to perform another transaction? (y/n): ");
            continueChoice = sc.next().charAt(0);
            sc.nextLine(); // consume leftover newline

        } while (continueChoice == 'y' || continueChoice == 'Y');

        System.out.println("Thank you for using the ATM.");
        sc.close();
    }

    static boolean verifyPin(String card, String pin) {
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT * FROM users WHERE debit_card_no = ? AND pin = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, card);
            pst.setString(2, pin);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    static void checkBalance(String card) {
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT balance FROM users WHERE debit_card_no = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, card);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                System.out.println("Your balance is: " + rs.getDouble("balance"));
            } else {
                System.out.println("Account not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void withdraw(String card, Scanner sc) {
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT balance FROM users WHERE debit_card_no = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, card);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                System.out.print("Enter amount to withdraw: ₹");
                double amount = sc.nextDouble();
                sc.nextLine(); // consume newline

                if (amount > 0 && amount <= balance) {
                    double newBalance = balance - amount;
                    PreparedStatement updatePst = con.prepareStatement(
                        "UPDATE users SET balance = ? WHERE debit_card_no = ?"
                    );
                    updatePst.setDouble(1, newBalance);
                    updatePst.setString(2, card);
                    updatePst.executeUpdate();
                    System.out.println("Withdrawal successful. New balance: ₹" + newBalance);
                } else {
                    System.out.println("Insufficient balance or invalid amount.");
                }
            } else {
                System.out.println("Account not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void changePin(String card, Scanner sc) {
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            System.out.print("Enter new 4-digit PIN: ");
            String newPin = sc.nextLine();

            if (newPin.matches("\\d{4}")) {
                PreparedStatement pst = con.prepareStatement(
                    "UPDATE users SET pin = ? WHERE debit_card_no = ?"
                );
                pst.setString(1, newPin);
                pst.setString(2, card);
                pst.executeUpdate();
                System.out.println("PIN changed successfully.");
            } else {
                System.out.println("PIN must be a 4-digit number.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
