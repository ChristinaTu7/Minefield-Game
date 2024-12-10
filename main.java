import java.util.Random;
import java.util.Scanner;

/*
 * Provided in this class is the neccessary code to get started with your game's implementation
 * You will find a while loop that should take your minefield's gameOver() method as its conditional
 * Then you will prompt the user with input and manipulate the data as before in project 2
 *
 * Things to Note:
 * 1. Think back to project 1 when we asked our user to give a shape. In this project we will be asking the user to provide a mode. Then create a minefield accordingly
 * 2. You must implement a way to check if we are playing in debug mode or not.
 * 3. When working inside your while loop think about what happens each turn. We get input, user our methods, check their return values. repeat.
 * 4. Once while loop is complete figure out how to determine if the user won or lost. Print appropriate statement.
 */

public class main {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    int rows = 0;
    int cols = 0;
    int mines = 0;
    // Asking for game mode
    System.out.println("Enter game mode (normal/debug): ");
    String mode = scanner.next();
    boolean debugMode = "debug".equalsIgnoreCase(mode);

    // Asking for game parameters
    System.out.println("Please select level difficulty: 1, 2, 3");
    int level = scanner.nextInt();

    // Initialize the minefield
    if(level == 1) {
      rows = 5;
      cols = 5;
      mines = 5;
    }
    else if(level == 2) {
      rows = 9;
      cols = 9;
      mines = 12;
    }
    else if(level == 3) { //interesting, the third level doesn't work when displaying
      rows = 20;
      cols = 20;
      mines = 40;
    }
    // the main ends after it displays the board and doesn't go through the game
    Minefield minefield = new Minefield(rows, cols, mines);

    minefield.evaluateField();

    minefield.revealStartingArea(0, 0);

    // Initial display of the minefield
    if (debugMode) {
      minefield.debug();
    } else {
      System.out.println("Initial Minefield:");
      System.out.println(minefield);
    }

    // Game loop
    while (!minefield.gameOver()) {
      // Displaying the minefield
      if (debugMode) {
        minefield.debug();
      } else {
        System.out.println("Current Minefield:");
        System.out.println(minefield);
      }

      // Getting user input for the next move
      System.out.println("Enter your move (row, column, and 'true' for a flag or 'false' otherwise): ");
      int row = scanner.nextInt();
      int col = scanner.nextInt();
      boolean isFlag = scanner.nextBoolean();

      // Process the move
      boolean hitMine = minefield.guess(row, col, isFlag);
      if (hitMine) {
        minefield.debug();
        System.out.println("Ope gurl, you hit a mine! Nice try :)");
        break; // Exit the loop if a mine is hit
      }

      // Check if the player has won
      if (minefield.playerWon()) {
        System.out.println("Congratulations gurlie! You won!");
        break; // Exit the loop if all non-mine cells are revealed
      }
    }

    scanner.close();
  }
}