package View;

import Model.ChessBoard;
import Controller.ChessController;
import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    private static final int GRID_SIZE = 8; // Size of the chessboard (8x8)
    private static final int SQUARE_SIZE = 100; // Size of each square in pixels

    public GameWindow() {
        setTitle("Chess Bot");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ChessBoard chessBoard = new ChessBoard();

        // Create an instance of ChessBoardView and add it to the window
        ChessBoardView chessBoardView = new ChessBoardView(chessBoard);

        new ChessController(chessBoard, chessBoardView);

        add(chessBoardView);

        pack(); // Adjusts the window size based on its contents
        setLocationRelativeTo(null); // Center the window
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameWindow window = new GameWindow();
            window.setVisible(true);
        });
    }
}
