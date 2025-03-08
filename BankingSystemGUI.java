package sudoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class BankingSystemGUI extends JFrame {
    private double savings;
    private double spendings;
    private String password;
    private final double lowBalanceThreshold = 50.0;
    private final ArrayList<String> transactionHistory = new ArrayList<>();
    private JTextArea historyArea;
    private int passwordRow;
    private int passwordCol;
    public BankingSystemGUI() {
        setTitle("Advanced Banking Management System");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Load account data or initialize it for the first time
        loadAccountData();

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Main Banking Tab
        JPanel bankingPanel = new JPanel(new BorderLayout());

        // Balance Display
        JLabel balanceLabel = new JLabel(getBalanceText());
        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        balanceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        balanceLabel.setOpaque(true);
        balanceLabel.setBackground(new Color(230, 240, 255));
        bankingPanel.add(balanceLabel, BorderLayout.NORTH);

        // Action Panel
        JPanel actionPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        actionPanel.setBackground(new Color(240, 255, 240));
        JTextField amountField = new JTextField();
        JComboBox<String> accountType = new JComboBox<>(new String[]{"Savings", "Spendings"});

        JButton addButton = new JButton("Add Money");
        JButton withdrawButton = new JButton("Withdraw Money");
        JButton transferButton = new JButton("Transfer Money");
        JButton resetButton = new JButton("Reset Balances");

        actionPanel.add(new JLabel("Amount:"));
        actionPanel.add(amountField);
        actionPanel.add(new JLabel("Account:"));
        actionPanel.add(accountType);
        actionPanel.add(addButton);
        actionPanel.add(withdrawButton);
        actionPanel.add(transferButton);
        actionPanel.add(resetButton);

        bankingPanel.add(actionPanel, BorderLayout.CENTER);

        // Reinitialize System Button
        JButton reinitializeButton = new JButton("Reinitialize System");
        bankingPanel.add(reinitializeButton, BorderLayout.SOUTH);

        tabbedPane.addTab("Banking", bankingPanel);

        // Transaction History Tab
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        JScrollPane historyScrollPane = new JScrollPane(historyArea);
        historyScrollPane.setBorder(BorderFactory.createTitledBorder("Transaction History"));
        historyPanel.add(historyScrollPane, BorderLayout.CENTER);
        historyPanel.setBackground(new Color(255, 250, 240));
        tabbedPane.addTab("Transaction History", historyPanel);

        // Update history area now that it's initialized
        updateTransactionHistory();

        add(tabbedPane, BorderLayout.CENTER);

        // Button Actions
        addButton.addActionListener(e -> processTransaction(amountField, accountType, balanceLabel, true));
        withdrawButton.addActionListener(e -> processTransaction(amountField, accountType, balanceLabel, false));
        transferButton.addActionListener(e -> processTransfer(amountField, accountType, balanceLabel));
        resetButton.addActionListener(e -> resetBalances(balanceLabel));
        reinitializeButton.addActionListener(e -> reinitializeSystem());
        
        setVisible(true);

    }
    private void processTransaction(JTextField amountField, JComboBox<String> accountType, JLabel balanceLabel, boolean isAddition) {
        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Enter a positive amount.");
                return;
            }
            String account = accountType.getSelectedItem().toString();
            if (isAddition) {
                if (account.equals("Savings")) {
                    savings += amount;
                } else {
                    spendings += amount;
                }
                logTransaction("Added $" + amount + " to " + account);
            } else { // Withdrawal
                if (account.equals("Savings") && savings >= amount) {
                    savings -= amount;
                    logTransaction("Withdrew $" + amount + " from Savings");
                } else if (account.equals("Spendings") && spendings >= amount) {
                    spendings -= amount;
                    logTransaction("Withdrew $" + amount + " from Spendings");
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient funds for withdrawal!");
                    return;
                }
            }
            saveAccountData();
            balanceLabel.setText(getBalanceText());
            amountField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount!");
        }
    }

    private void processTransfer(JTextField amountField, JComboBox<String> accountType, JLabel balanceLabel) {
        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Enter a positive amount.");
                return;
            }
            String source = accountType.getSelectedItem().toString();
            if (source.equals("Savings") && savings >= amount) {
                savings -= amount;
                spendings += amount;
                logTransaction("Transferred $" + amount + " from Savings to Spendings");
            } else if (source.equals("Spendings") && spendings >= amount) {
                spendings -= amount;
                savings += amount;
                logTransaction("Transferred $" + amount + " from Spendings to Savings");
            } else {
                JOptionPane.showMessageDialog(this, "Insufficient funds for transfer!");
                return;
            }
            saveAccountData();
            balanceLabel.setText(getBalanceText());
            amountField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount!");
        }
    }

    private void resetBalances(JLabel balanceLabel) {
        savings = 0;
        spendings = 0;
        logTransaction("Balances reset to $0");
        saveAccountData();
        balanceLabel.setText(getBalanceText());
        JOptionPane.showMessageDialog(this, "Balances reset to $0!");
    }
    
   public String getPassword() {
    	return password;
    }

   private void initializePasswordGrid() {
       JDialog gridDialog = new JDialog(this, "Select Password Position", true);
       gridDialog.setLayout(new GridLayout(9, 9));
       gridDialog.setSize(400, 400);

       for (int row = 0; row < 9; row++) {
           for (int col = 0; col < 9; col++) {
               JButton button = new JButton();
               button.setPreferredSize(new Dimension(40, 40));
               int selectedRow = row;
               int selectedCol = col;

               button.addActionListener(e -> {
                   passwordRow = selectedRow;
                   passwordCol = selectedCol;
                   JOptionPane.showMessageDialog(gridDialog, "Password position set to Row: " + passwordRow + ", Col: " + passwordCol);
                   gridDialog.dispose();
               });

               gridDialog.add(button);
           }
       }

       gridDialog.setLocationRelativeTo(this);
       gridDialog.setVisible(true);
   }

   public int getPasswordRow() {
       return passwordRow;
   }

   public int getPasswordCol() {
       return passwordCol;
   }


   private void reinitializeSystem() {
       String inputPassword = JOptionPane.showInputDialog(this, "Enter your password to reinitialize:");
       if (inputPassword != null && inputPassword.equals(password)) {
           int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to reinitialize the system? All data will be lost.", "Confirmation", JOptionPane.YES_NO_OPTION);
           if (confirmation == JOptionPane.YES_OPTION) {
               initializeAccountData();
               JOptionPane.showMessageDialog(this, "System reinitialized successfully!");

               // Close the current window and open a new instance
               this.dispose();
               SwingUtilities.invokeLater(BankingSystemGUI::new);
           }
       } else {
           JOptionPane.showMessageDialog(this, "Incorrect password!");
       }
   }
    private void logTransaction(String message) {
        transactionHistory.add(message);
        updateTransactionHistory();
    }

    private void updateTransactionHistory() {
        if (historyArea != null) {
            StringBuilder historyText = new StringBuilder();
            for (String transaction : transactionHistory) {
                historyText.append(transaction).append("\n");
            }
            historyArea.setText(historyText.toString());
        }
    }

    private String getBalanceText() {
        StringBuilder warning = new StringBuilder("<html>");
        if (savings < lowBalanceThreshold) {
            warning.append("<span style='color:red;'>Savings balance is low!</span><br>");
        }
        if (spendings < lowBalanceThreshold) {
            warning.append("<span style='color:red;'>Spendings balance is low!</span><br>");
        }
        warning.append(String.format("Savings: $%.2f | Spendings: $%.2f</html>", savings, spendings));
        return warning.toString();
    }

    private void loadAccountData() {
        File file = new File("account_data.txt");
        if (!file.exists()) {
            initializeAccountData();
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            password = reader.readLine();
            passwordRow = Integer.parseInt(reader.readLine());
            passwordCol = Integer.parseInt(reader.readLine());            
            savings = Double.parseDouble(reader.readLine());
            spendings = Double.parseDouble(reader.readLine());
            String line;
            while ((line = reader.readLine()) != null) {
                transactionHistory.add(line);
            }
        } catch (IOException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error loading account data. Initializing to $0.");
            initializeAccountData();
        }
    }

    private void saveAccountData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("account_data.txt"))) {
            writer.println(password);
            writer.println(passwordRow);
            writer.println(passwordCol);
            writer.println(savings);
            writer.println(spendings);
            for (String transaction : transactionHistory) {
                writer.println(transaction);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving account data.");
        }
    }

    private void initializeAccountData() {
        password = promptForPassword("Set your banking system password:");
        initializePasswordGrid();
        savings = promptForInitialAmount("Enter your initial savings amount:");
        spendings = promptForInitialAmount("Enter your initial spendings amount:");
        logTransaction("Initialized Savings: $" + savings);
        logTransaction("Initialized Spendings: $" + spendings);
        saveAccountData();
    }

    private String promptForPassword(String message) {
        while (true) {
            String input = JOptionPane.showInputDialog(this, message);
            if (input == null || input.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password cannot be empty!");
            } else {
                return input;
            }
        }
    }

    private double promptForInitialAmount(String message) {
        while (true) {
            String input = JOptionPane.showInputDialog(this, message);
            if (input == null) {
                System.exit(0); // Exit if user cancels
            }
            try {
                double amount = Double.parseDouble(input);
                if (amount >= 0) {
                    return amount;
                }
                JOptionPane.showMessageDialog(this, "Enter a non-negative amount.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Enter a numeric value.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BankingSystemGUI::new);
    }
}
