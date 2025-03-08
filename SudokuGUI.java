package sudoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SudokuGUI extends JFrame {
    private JTextField[][] fields;
    private Sudoku sudoku;
    private BankingSystemGUI bank;
    private int size;
    private String password;
    private int passwordRow;
    private int passwordCol;

    private String[] difficulties = {"Easy", "Medium", "Hard"};
    private JComboBox<String> difficultySelector = new JComboBox<>(difficulties);

    public SudokuGUI(BankingSystemGUI bank) {
        this.bank = bank;
        this.password = bank.getPassword();
        this.passwordRow = bank.getPasswordRow();
        this.passwordCol = bank.getPasswordCol();
        setInitialDifficulty();
        initializeSudoku();

        setTitle("Sudoku");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel gridPanel = buildSudokuGrid();
        JPanel buttonPanel = buildButtonPanel();

        add(gridPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setInitialDifficulty() {
        String selectedDifficulty = (String) difficultySelector.getSelectedItem();
        if ("Easy".equals(selectedDifficulty)) {
            size = 4;
        } else if ("Medium".equals(selectedDifficulty)) {
            size = 6;
        } else {
            size = 9;
        }
    }

    private void initializeSudoku() {
        setInitialDifficulty(); // Update size based on selected difficulty
        do {
            sudoku = new Sudoku(size);
        } while (!sudoku.solve());
        sudoku.resetToFixedValues();
        fields = new JTextField[size][size];
    }

    private JPanel buildSudokuGrid() {
        JPanel gridPanel = new JPanel(new GridLayout(size, size));
        int[][] board = sudoku.getBoard();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                fields[row][col] = new JTextField();
                fields[row][col].setHorizontalAlignment(JTextField.CENTER);
                fields[row][col].setFont(new Font("SansSerif", Font.BOLD, 18));

                if (board[row][col] != 0) {
                    fields[row][col].setText(String.valueOf(board[row][col]));
                    fields[row][col].setEditable(false);
                    fields[row][col].setBackground(Color.CYAN);
                }
                gridPanel.add(fields[row][col]);
            }
        }
        return gridPanel;
    }

    private JPanel buildButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));

        JButton validateButton = new JButton("Validate");
        validateButton.addActionListener(new ValidateActionListener());
        buttonPanel.add(validateButton);

        JButton solveButton = new JButton("Solve");
        solveButton.addActionListener(new SolveActionListener());
        buttonPanel.add(solveButton);

        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(new NewGameActionListener());
        buttonPanel.add(newGameButton);

        buttonPanel.add(difficultySelector);
        return buttonPanel;
    }

    private void openBankingSystem() {
        if (bank == null) {
            bank = new BankingSystemGUI();
        }
        SwingUtilities.invokeLater(() -> bank.setVisible(true));
    }

    private class ValidateActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean isGridValid = true;
            StringBuilder enteredCode = new StringBuilder();
            boolean emptyCells = false;

            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    String text = fields[row][col].getText();
                    if (!text.isEmpty()) {
                        if (row == passwordRow && col == passwordCol) {
                            enteredCode.append(text);
                        }

                        try {
                            int num = Integer.parseInt(text);
                            sudoku.setBoardCell(row, col, num);
                        } catch (NumberFormatException ex) {
                            fields[row][col].setBackground(Color.RED);
                            isGridValid = false;
                        }
                    } else {
                        sudoku.setBoardCell(row, col, 0);
                        emptyCells = true;
                    }
                }
            }

            if ("Hard".equals(difficultySelector.getSelectedItem()) && enteredCode.toString().equals(password)) {
                JOptionPane.showMessageDialog(SudokuGUI.this, "Access granted to Banking System!");
                openBankingSystem();
                return;
            }

            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    String text = fields[row][col].getText();
                    if (!text.isEmpty()) {
                        try {
                            int num = Integer.parseInt(text);
                            sudoku.setBoardCell(row, col, 0);
                            if (!sudoku.isValid(row, col, num)) {
                                fields[row][col].setBackground(Color.RED);
                                isGridValid = false;
                            } else {
                                fields[row][col].setBackground(Color.GREEN);
                            }
                            sudoku.setBoardCell(row, col, num);
                        } catch (NumberFormatException ex) {
                            fields[row][col].setBackground(Color.RED);
                        }
                    } else {
                        fields[row][col].setBackground(Color.WHITE);
                    }
                }
            }

            if (isGridValid) {
                if (emptyCells) {
                    JOptionPane.showMessageDialog(SudokuGUI.this, "The Sudoku grid is valid but there are empty cells!");
                } else {
                    JOptionPane.showMessageDialog(SudokuGUI.this, "The Sudoku grid is valid!");
                }
            } else {
                JOptionPane.showMessageDialog(SudokuGUI.this, "The Sudoku grid has errors!");
            }
        }
    }

    private class SolveActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            sudoku.resetToFixedValues();
            if (sudoku.solve()) {
                updateGridWithSolution();
            } else {
                JOptionPane.showMessageDialog(SudokuGUI.this, "No solution exists!");
            }
        }

        private void updateGridWithSolution() {
            int[][] solvedBoard = sudoku.getBoard();

            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    if (!sudoku.fixed[row][col]) {
                        fields[row][col].setText(String.valueOf(solvedBoard[row][col]));
                        fields[row][col].setBackground(Color.LIGHT_GRAY);
                    }
                    fields[row][col].setEditable(false);
                }
            }
        }
    }

    private class NewGameActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            initializeSudoku();
            getContentPane().removeAll();

            JPanel gridPanel = buildSudokuGrid();
            JPanel buttonPanel = buildButtonPanel();

            add(gridPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);

            revalidate();
            repaint();
        }
    }
    public static void main(String[] args) {
    	SwingUtilities.invokeLater(() -> {
    		BankingSystemGUI bank = new BankingSystemGUI();
    		SudokuGUI sudoku = new SudokuGUI(bank);
    		sudoku.setVisible(true);
    		bank.setVisible(false);
    	});
    }
}