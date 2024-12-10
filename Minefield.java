import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Minefield {

  private int numRows;
  private int numCols;
  private Cell[][] field;
  private int totalMines;
  private int totalFlags;
  private boolean[][] mineLocations; //keeps track of mines in the field to prevent duplicate mines
  private boolean[][] revealedCells;
  public static final String ANSI_YELLOW_BRIGHT = "\u001B[33;1m";
  public static final String ANSI_YELLOW = "\u001B[33m";
  public static final String ANSI_BLUE_BRIGHT = "\u001b[34;1m";
  public static final String ANSI_BLUE = "\u001b[34m";
  public static final String ANSI_RED_BRIGHT = "\u001b[31;1m";
  public static final String ANSI_RED = "\u001b[31m";
  public static final String ANSI_GREEN = "\u001b[32m";
  public static final String ANSI_PURPLE = "\u001b[35m";
  public static final String ANSI_CYAN = "\u001b[36m";
  public static final String ANSI_WHITE_BACKGROUND = "\u001b[47m";
  public static final String ANSI_PURPLE_BACKGROUND = "\u001b[45m";
  public static final String ANSI_GREY_BACKGROUND = "\u001b[0m";
  public static final String ANSI_RESET = "\u001B[0m";

  /*
   * Class Variable Section
   *
   */

  /*Things to Note:
   * Please review ALL files given before attempting to write these functions.
   * Understand the Cell.java class to know what object our array contains and what methods you can utilize
   * Understand the StackGen.java class to know what type of stack you will be working with and methods you can utilize
   * Understand the QGen.java class to know what type of queue you will be working with and methods you can utilize
   */


  /**
   * Minefield
   * <p>
   * Build a 2-d Cell array representing your minefield. Constructor
   *
   * @param rows    Number of rows.
   * @param columns Number of columns.
   * @param flags   Number of flags, should be equal to mines
   */
  public Minefield(int rows, int columns, int flags) {
    this.numRows = rows;
    this.numCols = columns;
    this.field = new Cell[rows][columns];
    this.totalMines = flags;
    this.mineLocations = new boolean[rows][columns];

    // Initialize each Cell in the field with default values
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        field[i][j] = new Cell(false, "-");
      }
    }
  }

  /**
   * evaluateField
   *
   * @function: Evaluate entire array. When a mine is found check the surrounding adjacent tiles. If
   * another mine is found during this check, increment adjacent cells status by 1.
   */
  public void evaluateField() {
    int rows = field.length;
    int cols = field[0].length;

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        Cell currentCell = field[i][j];

        if ("M".equals(currentCell.getStatus())) {
          incrementAdjacentCells(i, j);
        }
      }
    }
  }

  private void incrementAdjacentCells(int row, int col) {
    int[] dRows = {-1, -1, -1, 0, 0, 1, 1, 1};
    int[] dCols = {-1, 0, 1, -1, 1, -1, 0, 1};

    for (int d = 0; d < 8; d++) {
      int adjRow = row + dRows[d];
      int adjCol = col + dCols[d];

      if (isValidCell(adjRow, adjCol) && !"M".equals(field[adjRow][adjCol].getStatus())) {
        int count = getCount(adjRow, adjCol);
        count++;
        field[adjRow][adjCol].setStatus(String.valueOf(count));
      }
    }
  }

  private boolean isValidCell(int row, int col) {
    return row >= 0 && col >= 0 && row < field.length && col < field[0].length;
  }

  private boolean isValidCell2(int row, int col, int rows, int cols) {
    return row >= 0 && col >= 0 && row < rows && col < cols;
  }

  private int getCount(int row, int col) {
    String status = field[row][col].getStatus();
    try {
      return "-".equals(status) ? 0 : Integer.parseInt(status);
    } catch (NumberFormatException e) {
      // Handle the case where status is not a number
      return 0; // or some other appropriate default or error handling
    }
  }


  /**
   * createMines
   * <p>
   * Randomly generate coordinates for possible mine locations. If the coordinate has not already
   * been generated and is not equal to the starting cell set the cell to be a mine. utilize
   * rand.nextInt()
   *
   * @param x     Start x, avoid placing on this square.
   * @param y     Start y, avoid placing on this square.
   * @param mines Number of mines to place.
   */
  public void createMines(int x, int y, int mines) {
    int rows = field.length;
    int cols = field[0].length;
    Random rand = new Random();

    // Ensure that the number of mines is less than the total cells minus one (for the starting cell)
    if (mines >= rows * cols - 1) {
      throw new IllegalArgumentException("Too many mines");
    }

    int minesPlaced = 0;
    while (minesPlaced < mines) {
      int randomX = rand.nextInt(rows);
      int randomY = rand.nextInt(cols);

      // Check if the random coordinate is valid for placing a mine
      if (randomX != x || randomY != y) {
        Cell currentCell = field[randomX][randomY];
        if (!currentCell.getRevealed() && !"M".equals(currentCell.getStatus())) {
          currentCell.setStatus("M");
          minesPlaced++;
        }
      }
    }
  }


  /**
   * guess
   * <p>
   * Check if the guessed cell is inbounds (if not done in the Main class). Either place a flag on
   * the designated cell if the flag boolean is true or clear it. If the cell has a 0 call the
   * revealZeroes() method or if the cell has a mine end the game. At the end reveal the cell to the
   * user.
   *
   * @param x    The x value the user entered.
   * @param y    The y value the user entered.
   * @param flag A boolean value that allows the user to place a flag on the corresponding square.
   * @return boolean Return false if guess did not hit mine or if flag was placed, true if mine
   * found.
   */
  public boolean guess(int x, int y, boolean flag) {
    x -= 1;
    y -= 1;

    if (x < 0 || y < 0 || x >= numRows || y >= numCols) {
      System.out.println("Guessed cell out of bounds!");
      return false;
    }

    Cell guessedCell = field[x][y];

    if (guessedCell.getRevealed()) {
      // Cell is already revealed, no action needed
      return false;
    }

    if (flag) {
      // Toggle flag status
      if ("F".equals(guessedCell.getStatus())) {
        guessedCell.setStatus("-");
        totalFlags--;
      } else {
        // Check if there are enough flags remaining to place
        if (totalFlags > 0) {
          guessedCell.setStatus("F");
          totalFlags--;
        } else {
          System.out.println("No more flags remaining!");
        }
      }
    } else {
      if ("M".equals(guessedCell.getStatus())) {
        // Player hits a mine, handle game over
        handleGameOver();
        return true;
      } else if ("0".equals(guessedCell.getStatus())) {
        // Cell has 0, call revealZeroes
        revealZeroes(x, y);
      }
    }

    guessedCell.setRevealed(true);

    // Check if the player has won (all non-mine cells revealed)
    if (playerWon()) {
      handleGameWin();
    }

    return false; // Guess didn't hit a mine or flag was placed/removed
  }

  private void handleGameOver() {
    // Reveal all mines and update the game state as needed
    int rows = field.length;
    int cols = field[0].length;

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        Cell currentCell = field[i][j];
        if ("M".equals(currentCell.getStatus())) {
          currentCell.setRevealed(true);
        }
      }
    }
    // Handle other game over actions such as displaying a message or stopping the game
    System.out.println("Ope gurlie you hit a mine. Nice Try :)");
  }


  private void handleGameWin() {
    // Check if the player has revealed all non-mine cells
    int rows = field.length;
    int cols = field[0].length;
    int totalRevealedCells = 0;

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        Cell currentCell = field[i][j];
        if (!"M".equals(currentCell.getStatus()) && currentCell.getRevealed()) {
          totalRevealedCells++;
        }
      }
    }

    if (totalRevealedCells == (rows * cols) - totalMines) {
      // The player has won, handle the game win actions here
      // This can include displaying a victory message, stopping the game, etc.
      return;
    }
  }


  /**
   * gameOver
   * <p>
   * Ways a game of Minesweeper ends: 1. player guesses a cell with a mine: game over -> player
   * loses 2. player has revealed the last cell without revealing any mines -> player wins
   *
   * @return boolean Return false if game is not over and squares have yet to be revealed, otheriwse
   * return true.
   */
  public boolean gameOver() {
    // Check if any cell with a mine has been revealed - indicating the player has lost
    for (int i = 0; i < field.length; i++) {
      for (int j = 0; j < field[i].length; j++) {
        Cell cell = field[i][j];
        if (cell != null) {
          String status = cell.getStatus();
          if ("M".equals(status) && cell.getRevealed()) {
            // Logic when a revealed mine is found
            return true;
          }
        }
      }
    }
    // Check if all non-mine cells have been revealed - indicating the player has won
    int nonMineCellsRevealed = 0;
    int totalNonMineCells = 0;
    for (int i = 0; i < field.length; i++) {
      for (int j = 0; j < field[i].length; j++) {
        Cell cell = field[i][j];
        if (cell != null && !"M".equals(cell.getStatus())) {
          totalNonMineCells++;
          if (cell.getRevealed()) {
            nonMineCellsRevealed++;
          }
        }
      }
    }

    if (nonMineCellsRevealed == totalNonMineCells) {
      // All non-mine cells have been revealed - game over
      return true;
    }
    // Game is not over yet as there are still squares that have yet to be revealed
    return false;
  }

  public boolean playerWon() {
    int totalRevealedCells = 0;

    for (int i = 0; i < field.length; i++) {
      for (int j = 0; j < field[0].length; j++) {
        Cell currentCell = field[i][j];

        if (currentCell != null) {
          // Count only non-mine cells that are revealed
          if (!"M".equals(currentCell.getStatus()) && currentCell.getRevealed()) {
            totalRevealedCells++;
          }
        }
      }
    }

    // Player wins if all non-mine cells are revealed
    return totalRevealedCells == (field.length * field[0].length) - totalMines;
  }


  /**
   * Reveal the cells that contain zeroes that surround the inputted cell. Continue revealing
   * 0-cells in every direction until no more 0-cells are found in any direction. Utilize a STACK to
   * accomplish this.
   * <p>
   * This method should follow the psuedocode given in the lab writeup. Why might a stack be useful
   * here rather than a queue?
   *
   * @param x The x value the user entered.
   * @param y The y value the user entered.
   */
  public void revealZeroes(int x, int y) {
    int rows = field.length;
    int cols = field[0].length;

    // A boolean array to keep track of visited cells
    boolean[][] visited = new boolean[rows][cols];

    // Using Stack to implement depth-first search
    Stack1Gen<int[]> stack = new Stack1Gen<>();
    stack.push(new int[]{x, y});

    while (!stack.isEmpty()) {
      int[] current = stack.pop();
      int currentX = current[0];
      int currentY = current[1];

      // Check if the cell is within bounds and not visited
      if (!isValidCell2(currentX, currentY, rows, cols) || visited[currentX][currentY]) {
        continue;
      }

      Cell currentCell = field[currentX][currentY];
      visited[currentX][currentY] = true;  // Mark the cell as visited

      // If the current cell is not a mine and is not revealed, reveal it
      if (!"M".equals(currentCell.getStatus()) && !currentCell.getRevealed()) {
        currentCell.setRevealed(true);

        // If the current cell's status is "0", add its non-visited neighbors to the stack
        if ("0".equals(currentCell.getStatus())) {
          int[] dx = {-1, 0, 1, 0};
          int[] dy = {0, -1, 0, 1};

          for (int i = 0; i < 4; i++) {
            int nextX = currentX + dx[i];
            int nextY = currentY + dy[i];
            if (isValidCell2(nextX, nextY, rows, cols) && !visited[nextX][nextY]) {
              stack.push(new int[]{nextX, nextY});
            }
          }
        }
      }
    }
  }



  /**
   * revealStartingArea
   * <p>
   * On the starting move only reveal the neighboring cells of the inital cell and continue
   * revealing the surrounding concealed cells until a mine is found. Utilize a QUEUE to accomplish
   * this.
   * <p>
   * This method should follow the psuedocode given in the lab writeup. Why might a queue be useful
   * for this function?
   *
   * @param x The x value the user entered.
   * @param y The y value the user entered.
   */
  public void revealStartingArea(int x, int y) {
    int rows = field.length;
    int cols = field[0].length;

    Queue<int[]> queue = new LinkedList<>();
    queue.offer(new int[]{x, y});

    while (!queue.isEmpty()) {
      int[] current = queue.poll();
      int currentX = current[0];
      int currentY = current[1];

      Cell currentCell = field[currentX][currentY];

      currentCell.setRevealed(true);

      if ("M".equals(currentCell.getStatus())) {
        break;
      }

      int[] dx = {-1, 0, 1, 0};
      int[] dy = {0, -1, 0, 1};

      for (int i = 0; i < 4; i++) {
        int nextX = currentX + dx[i];
        int nextY = currentY + dy[i];

        if (isValidCell2(nextX, nextY, rows, cols) && !field[nextX][nextY].getRevealed()) {
          queue.offer(new int[]{nextX, nextY});
        }
      }
    }
  }

  /**
   * For both printing methods utilize the ANSI colour codes provided!
   * <p>
   * <p>
   * <p>
   * <p>
   * <p>
   * debug
   *
   * @function This method should print the entire minefield, regardless if the user has guessed a
   * square. *This method should print out when debug mode has been selected.
   */
  public void debug() {
    int rows = field.length;
    int cols = field[0].length;

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        Cell currentCell = field[i][j];
        String status = currentCell.getStatus();

        if ("M".equals(status)) {
          // Red for mines
          System.out.print(ANSI_RED + "M" + ANSI_RESET + " ");
        } else if ("F".equals(status)) {
          // Purple for flags
          System.out.print(ANSI_PURPLE + "F" + ANSI_RESET + " ");
        } else if (currentCell.getRevealed()) {
          // Handle revealed cells
          switch (status) {
            case "0":
              // White background for empty revealed cell
              System.out.print(ANSI_WHITE_BACKGROUND + " " + ANSI_RESET + " ");
              break;
            case "1":
              // Blue for cells with 1 adjacent mine
              System.out.print(ANSI_BLUE + status + ANSI_RESET + " ");
              break;
            // Add cases for other numbers as needed
            default:
              // Default color for other numbers
              System.out.print(status + " ");
              break;
          }
        } else {
          // Grey background for unrevealed cells
          System.out.print(ANSI_GREY_BACKGROUND + " " + ANSI_RESET + " ");
        }
      }
      System.out.println(); // Move to the next row
    }
  }

  /**
   * toString
   *
   * @return String The string that is returned only has the squares that has been revealed to the
   * user or that the user has guessed.
   */
  public String toString() {
    StringBuilder output = new StringBuilder();
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numCols; j++) {
        Cell currentCell = field[i][j];
        if (currentCell != null) {
          if (currentCell.getRevealed() || "F".equals(currentCell.getStatus())) {
            output.append(currentCell.getStatus()).append(" ");
          } else {
            output.append("- ");
          }
        } else {
          output.append("X "); // Placeholder for uninitialized cells
        }
      }
      output.append("\n");
    }
    return output.toString();
  }
}