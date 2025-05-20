import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;

class Transaction {
    enum Type { INCOME, EXPENSE }

    private Type type;
    private LocalDate date;
    private String category;
    private double amount;

    public Transaction(Type type, LocalDate date, String category, double amount) {
        this.type = type;
        this.date = date;
        this.category = category;
        this.amount = amount;
    }

    public Type getType() { return type; }
    public LocalDate getDate() { return date; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }

    @Override
    public String toString() {
        return type + " | " + date + " | " + category + " | " + amount;
    }
}

public class ExpenseTracker {
    private static List<Transaction> transactions = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n===== Expense Tracker Menu =====");
            System.out.println("1. Add Income/Expense");
            System.out.println("2. View Monthly Summary");
            System.out.println("3. Load Data from File");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> addTransaction();
                case 2 -> viewMonthlySummary();
                case 3 -> loadDataFromFile();
                case 4 -> {
                    System.out.println("Exiting... Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void addTransaction() {
        System.out.print("Enter type (INCOME/EXPENSE): ");
        String typeStr = scanner.nextLine().toUpperCase();

        Transaction.Type type;
        try {
            type = Transaction.Type.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid type. Must be INCOME or EXPENSE.");
            return;
        }

        System.out.print("Enter date (yyyy-MM-dd): ");
        String dateStr = scanner.nextLine();
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            System.out.println("Invalid date format.");
            return;
        }

        System.out.print("Enter category: ");
        String category = scanner.nextLine();

        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // consume newline

        transactions.add(new Transaction(type, date, category, amount));
        System.out.println("Transaction added successfully!");
    }

    private static void viewMonthlySummary() {
        Map<Month, Double> incomeSummary = new HashMap<>();
        Map<Month, Double> expenseSummary = new HashMap<>();

        for (Transaction t : transactions) {
            Month month = t.getDate().getMonth();
            if (t.getType() == Transaction.Type.INCOME) {
                incomeSummary.put(month, incomeSummary.getOrDefault(month, 0.0) + t.getAmount());
            } else {
                expenseSummary.put(month, expenseSummary.getOrDefault(month, 0.0) + t.getAmount());
            }
        }

        System.out.println("\n===== Monthly Summary =====");
        for (Month month : Month.values()) {
            double income = incomeSummary.getOrDefault(month, 0.0);
            double expense = expenseSummary.getOrDefault(month, 0.0);
            if (income > 0 || expense > 0) {
                System.out.printf("%s -> Income: %.2f | Expense: %.2f | Net: %.2f\n",
                        month, income, expense, income - expense);
            }
        }
    }

    private static void loadDataFromFile() {
        System.out.print("Enter file path: ");
        String path = scanner.nextLine();

        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length != 4) continue;

                Transaction.Type type = Transaction.Type.valueOf(parts[0].toUpperCase());
                LocalDate date = LocalDate.parse(parts[1], formatter);
                String category = parts[2];
                double amount = Double.parseDouble(parts[3]);

                transactions.add(new Transaction(type, date, category, amount));
            }
            System.out.println("Data loaded successfully from file!");
        } catch (IOException e) {
            System.out.println("Error reading file.");
        } catch (Exception e) {
            System.out.println("Error parsing file content. Make sure the format is correct.");
        }
    }
}
