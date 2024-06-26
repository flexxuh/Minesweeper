import edu.princeton.cs.algs4.Queue;

import java.util.*;

//agene001
public class Minefield {
    /**
     * Global Section
     */
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE_BRIGHT = "\u001b[34;1m";
    public static final String ANSI_BLUE = "\u001b[34m";
    public static final String ANSI_RED_BRIGHT = "\u001b[31;1m";
    public static final String ANSI_RED = "\u001b[31m";
    public static final String ANSI_GREEN = "\u001b[32m";
    public static final String ANSI_GREY_BG = "\u001b[0m";
    /**
     * Constructor
     *
     * @param rows       Number of rows.
     * @param columns    Number of columns.
     * @param flags      Number of flags, should be equal to mines
     */
    static int rows;
    static int cols;
    int flags;
    Cell[] board;

    public Minefield(int rows, int columns, int flags) {
        //initzializer
        this.rows = rows;
        cols = columns;
        this.flags = flags;

        board = new Cell[rows * cols + 1];
        int h2 = (cols * 2);
        int max = cols * 4;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                board[XYto1D(j, i)] = new Cell(false, "0");
            }
        }

    }
    public Minefield(int rows, int columns, int flags, Set<List<Integer>> mines) {
        //initzializer
        this.rows = rows;
        cols = columns;
        this.flags = flags;

        board = new Cell[rows * cols + 1];
        int h2 = (cols * 2);
        int max = cols * 4;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                board[XYto1D(j, i)] = new Cell(false, "0");
            }
        }
        for (List<Integer> mine : mines) {
            board[XYto1D(mine.get(0),mine.get(1))].setStatus("M");
        }
        evaluateField();

    }

    /**
     * Updates the surrounding cells' status based on the number of adjacent mines.
     * For each cell on the board that is a mine, this method checks the adjacent cells
     * and increments their status by 1 if they are not a mine.
     */
    void evaluateField() {
        //used to keep track of mines surronding
        int count;
        //goes through all and checks that adjacent pieces are in bounds and mine
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[XYto1D(i,j)].getStatus().equalsIgnoreCase("M")) {
                    // checks surronding mines
                    for (int isub = i - 1; isub < i + 2; isub++) {
                        for (int jsub = j - 1; jsub < j + 2; jsub++) {
                            if (i != isub || j != jsub) {
                                if (isub < rows && isub >= 0 && jsub < cols && jsub >= 0) {
                                    Cell cell = board[XYto1D(isub, jsub)];
                                    try {
                                        cell.setStatus(String.valueOf(Integer.parseInt(cell.getStatus()) + 1));
                                    } catch (Exception ignored) {

                                    }
                                }
                                ;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Converts the 2D coordinates (x, y) to a 1D coordinate.
     *
     * @param y The y coordinate.
     * @param x The x coordinate.
     * @return The corresponding 1D coordinate.
     */
    public int XYto1D(int y, int x) {
        return (y * cols) + x;
    }

    /**
     * Converts a one-dimensional index to a two-dimensional coordinate (x, y).
     *
     * @param d The one-dimensional index.
     * @return An array containing the x and y coordinates.
     */
    private int[] D1toXY(int d) {
        int y = d % cols;
        int x = d / cols;
        return new int[]{x, y};
    }

    /**
     * Calculates the number of nearby hidden cells.
     *
     * @param cell The coordinates of the cell.
     *             The first element is the x-coordinate and the second element is the y-coordinate.
     * @return The number of nearby hidden cells.
     */
    public int nearbyHidden(int[] cell) {
        int count = 0;
        for (int i = cell[0] - 1; i < cell[0] + 2; i++) {
            for (int j = cell[1] - 1; j < cell[1] + 2; j++) {
                if (cell[0] != i || cell[1] != j) {
                    if (0 <= i && i < rows && 0 <= j && j < rows) {

                        if (!getCell(XYto1D(i, j)).getRevealed()) {
                            count++;
                        }
                    }
                }
            }
        }
        return count;

    }

    /**
     * createMines
     *
     * @param x     Start x, avoid placing on this square.
     * @param y     Start y, avoid placing on this square.
     * @param mines Number of mines to place.
     */

    public void createMines(int x, int y, int mines) {

        int a;
        int b;
        Random rand = new Random();
        while (mines > 0) {
            int upper = rows;
            a = rand.nextInt(upper);
            b = rand.nextInt(upper);
            //checks if mine is placed on starting coordinate
            if ((a == x && b == y) || (a + 1 == x && b == y) || (a - 1 == x && b == y) || (a + 1 == x && b + 1 == y) || (a + 1 == x && b - 1 == y) || (a - 1 == x && b + 1 == y) || (a - 1 == x && b - 1 == y) || (a == x && b + 1 == y) || (a == x && b - 1 == y)) {
            } else {
                board[XYto1D(b, a)].setStatus("M");
                mines--;

            }
        }
        evaluateField();
    }


    public Cell getCell(int x) {
        return board[x];
    }

    /**
     * Updates the status of a cell on the game board based on user input.
     *
     * @param x    The x coordinate of the cell.
     * @param y    The y coordinate of the cell.
     * @param flag True if the user wants to flag/unflag the cell, false otherwise.
     * @return True if the user guessed a mine, false otherwise.
     */
    public boolean guess(int x, int y, boolean flag) {
//        System.out.println("X val: "+x+" Y val: "+y);
        if (x <= cols && y <= rows && x >= 0 && y >= 0) {
            if (flag) {
                if (flags > 0) {
                    if (board[XYto1D(x, y)].getStatus().startsWith("F")) {
                        String tt = board[XYto1D(x, y)].getStatus().replace("F", "");
                        board[XYto1D(x, y)].setStatus(tt);
                        board[XYto1D(x, y)].setRevealed(false);
                        flags++;
                        return false;
                    } else {
                        board[XYto1D(x, y)].setStatus("F" + board[XYto1D(x, y)].getStatus());
                        board[XYto1D(x, y)].setRevealed(true);
                        flags--;
                        return false;
                    }
                }
            } else if (board[XYto1D(x, y)].getStatus().equals("0")) {
                revealZeroes(x, y);
                return false;
            } else if (board[XYto1D(x, y)].getStatus().toLowerCase().equals("m"))
                return true;
            else {
                board[XYto1D(x, y)].setRevealed(true);
            }

        }
        return false;
    }

    /**
     * gameOver
     *
     * @return boolean Return false if game is not over and squares have yet to be revealed, otherwise return true.
     */
    public boolean gameOver() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[XYto1D(j, i)].getStatus() == "M") {
                    if (!board[XYto1D(j, i)].getRevealed()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * revealField
     * <p>
     * This method should follow the pseudocode given.
     * Why might a stack be useful here rather than a queue?
     *
     * @param x The x value the user entered.
     * @param y The y value the user entered.
     */
    public void revealZeroes(int x, int y) {
//        printMinefield();
//        printDebugMinefield();
        Stack<int[]> zeros = new Stack<>();
        int[] t = new int[2];
        t[0] = y;
        t[1] = x;
        zeros.push(t);
        while (!zeros.isEmpty()) {
            int[] temp = zeros.pop();
//                        System.out.println("search node is ("+temp[0] + ", " + temp[1]+") : with status "+ board[XYto1D(temp[0], temp[1])].getStatus());

            if (board[XYto1D(temp[0], temp[1])].getStatus().equals("0") && !board[XYto1D(temp[0], temp[1])].getRevealed()) {
                board[XYto1D(temp[0], temp[1])].setRevealed(true);
                for (int i = temp[0] - 1; i < temp[0] + 2; i++) {
                    for (int j = temp[1] - 1; j < temp[1] + 2; j++) {
                        if (i != temp[0] || j != temp[1]) {
                            if (i < rows && i >= 0 && j < cols && j >= 0) {
//                        System.out.println("search node is ("+i + ", " + j+") : with status "+ board[XYto1D(temp[0], temp[1])].getStatus());

                                if (!board[XYto1D(i, j)].getStatus().equals("M")) {
                                    if (board[XYto1D(i, j)].getStatus().equals("0")) {
                                        zeros.push(new int[]{i, j});

                                    } else {
                                        revealMines(i, j);
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    /**
     * revealMines
     * <p>
     * This method should follow the psuedocode given.
     * Why might a queue be useful for this function?
     *
     * @param x The x value the user entered.
     * @param y The y value the user entered.
     */
    public void revealMines(int x, int y) {
        //initializes queue with x and y in it
        Queue<int[]> queue = new Queue<>();
        int[] t = new int[2];
        t[0] = y;
        t[1] = x;
        queue.enqueue(t);

        //System.out.println(num);
        //continues until length is 0
        while (!queue.isEmpty()) {
            int[] check = queue.dequeue();
            if (!board[XYto1D(check[1], check[0])].getStatus().equalsIgnoreCase("M") && !board[XYto1D(check[1], check[0])].getRevealed()) {

                // if zero reveal zeros
                if (!board[XYto1D(check[1], check[0])].getStatus().equals("0")) {
//                    this.revealZeroes(check[1], check[0]);
                    board[XYto1D(check[1], check[0])].setRevealed(true);
                    int[] add = new int[2];
                    add[0] = check[0];
                    add[1] = check[1];
                    queue.enqueue(add);
                } else {
                    for (int i = check[0] - 1; i < check[0] + 2; i++) {
                        for (int j = check[1] - 1; j < check[1] + 2; j++) {
                            if (i != check[0] || j != check[1]) {
                                if (i < rows && i >= 0 && j < cols && j >= 0) {

                                    queue.enqueue(new int[]{i, j});

                                }
                            }
                        }
                    }
                }

                board[XYto1D(check[1], check[0])].setRevealed(true);
            }

            //break if mine
            else
                continue;

        }
    }

    public void debug() {
        //sets all boards to revealed
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[XYto1D(j, i)].setStatus("0");
                board[XYto1D(j, i)].setRevealed(true);
            }
        }
    }

    /**
     * revealStart
     *
     * @param x The x value the user entered.
     * @param y The y value the user entered.
     */
    public void revealStart(int x, int y) {
        this.revealZeroes(x, y);

//        this.revealZeroes(x, y);
    }

    /**
     * printMinefield
     *
     * @fuctnion This method should print the entire minefield, regardless if the user has guessed a square.
     * *This method should print out when debug mode has been selected.
     */
    public void printMinefield() {
        for (int i = 0; i < rows; i++) {
            if (i < 10) {
                if (i == rows - 1) System.out.println("  " + i);
                else System.out.print("   " + i + "     ");
            } else {
                if (i == rows - 1) System.out.println("  " + i);
                else System.out.print("   " + i + "    ");

            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (j == 0) {
                    if (i < 10) System.out.print(" " + i + " ");
                    else System.out.print(i + " ");
                }
                if (board[XYto1D(i, j)].getRevealed()) {
                    if (board[XYto1D(i, j)].getStatus().equals("1"))
                        System.out.print(ANSI_RED + " " + board[XYto1D(i, j)].getStatus() + "       " + ANSI_GREY_BG);
                    else if (board[XYto1D(i, j)].getStatus().equals("2"))
                        System.out.print(ANSI_YELLOW + " " + board[XYto1D(i, j)].getStatus() + "       " + ANSI_GREY_BG);
                    else if (board[XYto1D(i, j)].getStatus().equals("3"))
                        System.out.print(ANSI_BLUE_BRIGHT + " " + board[XYto1D(i, j)].getStatus() + "       " + ANSI_GREY_BG);
                    else if (board[XYto1D(i, j)].getStatus().equals("4"))
                        System.out.print(ANSI_RED_BRIGHT + " " + board[XYto1D(i, j)].getStatus() + "       " + ANSI_GREY_BG);
                    else if (board[XYto1D(i, j)].getStatus().equals("5"))
                        System.out.print(ANSI_BLUE + " " + board[XYto1D(i, j)].getStatus() + "       " + ANSI_GREY_BG);
                    else if (board[XYto1D(i, j)].getStatus().equals("6"))
                        System.out.print(ANSI_GREEN + " " + board[XYto1D(i, j)].getStatus() + "       " + ANSI_GREY_BG);
                    else System.out.print(" " + board[XYto1D(i, j)].getStatus() + "       " + ANSI_GREY_BG);
                } else System.out.print(" -       ");
            }
            System.out.println();
            System.out.println();
        }
    }

    public void printDebugMinefield() {
        for (int i = 0; i < rows; i++) {
            if (i < 10) {
                if (i == rows - 1) System.out.println("  " + i);
                else System.out.print("   " + i + "     ");
            } else {
                if (i == rows - 1) System.out.println("  " + i);
                else System.out.print("   " + i + "    ");

            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (j == 0) {
                    if (i < 10) System.out.print(" " + i + " ");
                    else System.out.print(i + " ");
                }
                if (board[XYto1D(i, j)].getStatus().equals("1"))
                    System.out.print(ANSI_RED + " " + board[XYto1D(i, j)].getStatus() + "       " + ANSI_GREY_BG);
                else if (board[XYto1D(i, j)].getStatus().equals("2"))
                    System.out.print(ANSI_YELLOW + " " + board[XYto1D(i, j)].getStatus() + "       " + ANSI_GREY_BG);
                else if (board[XYto1D(i, j)].getStatus().equals("3"))
                    System.out.print(ANSI_BLUE_BRIGHT + " " + board[XYto1D(i, j)].getStatus() + "       " + ANSI_GREY_BG);
                else if (board[XYto1D(i, j)].getStatus().equals("4"))
                    System.out.print(ANSI_RED_BRIGHT + " " + board[XYto1D(i, j)].getStatus() + "       " + ANSI_GREY_BG);
                else if (board[XYto1D(i, j)].getStatus().equals("5"))
                    System.out.print(ANSI_BLUE + " " + board[XYto1D(i, j)].getStatus() + "       " + ANSI_GREY_BG);
                else if (board[XYto1D(i, j)].getStatus().equals("6"))
                    System.out.print(ANSI_GREEN + " " + board[XYto1D(i, j)].getStatus() + "       " + ANSI_GREY_BG);
                else System.out.print(" " + board[XYto1D(i, j)].getStatus() + "       " + ANSI_GREY_BG);
//                } else System.out.print(" -       ");
            }
            System.out.println();
            System.out.println();
        }
    }

    /**
     * toString
     *
     * @return String The string that is returned only has the squares that has been revealed to the user or that the user has guessed.
     */
//    public String toString() {
//        String rev = "";
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < cols; j++) {
//                if (board[XYto1D(i, j)].getRevealed()) {
//                    rev += board[XYto1D(i, j)].getStatus();
//                } else
//                    rev += "- ";
//            }
//            rev += "\n";
//        }
//        return rev;
//    }
}
