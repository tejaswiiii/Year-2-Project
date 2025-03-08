package sudoku;

import java.util.*;

public class Sudoku {
    private int[][] board;
    private int[][] fixedBoard;
    public boolean[][] fixed;
    private int size;

    public Sudoku(int size) {
        this.size = size;
        board = new int[size][size];
        fixedBoard = new int[size][size];
        fixed = new boolean[size][size];
        generatePuzzle();
    }

    public void generatePuzzle() {
        Random random = new Random();
        int cellsToFill = size * size / 4; // Adjust number of pre-filled cells
        for (int i = 0; i < cellsToFill; i++) {
            int row = random.nextInt(size);
            int col = random.nextInt(size);
            int num = random.nextInt(size) + 1;
            if (isValid(row, col, num)) {
                board[row][col] = num;
                fixedBoard[row][col] = num;
                fixed[row][col] = true;
            }
        }
    }
    public boolean isValid(int row, int col, int num) {
    	if (num > size) {
    		return false;
    	}
        for (int i = 0; i < size; i++) {
            if (board[row][i] == num || board[i][col] == num) {
                return false;
            }
        }

        int boxSize = (int) Math.sqrt(size);
        int boxRowStart = (row / boxSize) * boxSize;
        int boxColStart = (col / boxSize) * boxSize;

        for (int i = 0; i < boxSize; i++) {
            for (int j = 0; j < boxSize; j++) {
                if (board[boxRowStart + i][boxColStart + j] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    // New: Validate the entire grid
    public boolean isGridValid() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int num = board[row][col];
                if (num != 0) {
                    board[row][col] = 0; // Temporarily clear the cell
                    if (!isValid(row, col, num)) {
                        board[row][col] = num; // Restore the cell
                        return false;
                    }
                    board[row][col] = num; // Restore the cell
                }
            }
        }
        return true;
    }
    public void resetToFixedValues() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                board[row][col] = fixedBoard[row][col];
            }
        }
    }
    
    public boolean solve() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board[row][col] == 0) {
                    for (int num = 1; num <= size; num++) {
                        if (isValid(row, col, num)) {
                            board[row][col] = num;
                            if (solve()) return true;
                            board[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoardCell(int row, int col, int value) {
        board[row][col] = value;
    }

    public int getSize(Object s) {
    	if(s == "Easy") {
    		size = 4;
    	}
    	else if (s == "Medium") {
    		size = 6;
    	}
    	else if (s == "Hard") {
    		size = 9;
    	}
    	return size;
    }    
}