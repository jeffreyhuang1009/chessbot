package View;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;
import java.awt.*;
import Model.ChessPiece;
import Model.ChessBoard;
import java.awt.Point;
import java.util.List;


public class ChessBoardView extends JPanel {
    private ChessBoard chessBoard;
    public static final int GRID_SIZE = 8;
    private static final int SQUARE_SIZE = 100; // Size of each square in pixels
    private JPanel[][] squares = new JPanel[GRID_SIZE][GRID_SIZE];


    public ChessBoardView(ChessBoard chessBoard) {
        this.chessBoard = chessBoard;
        setLayout(new GridLayout(GRID_SIZE, GRID_SIZE));
        initializeBoard();
        initializePieces();

        // Set the preferred size of the ChessBoardView
        setPreferredSize(new Dimension(GRID_SIZE * SQUARE_SIZE, GRID_SIZE * SQUARE_SIZE));
    }

    public JPanel[][] getSquares() {
        return squares;
    }


    private void initializeBoard() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                squares[i][j] = new JPanel();
                squares[i][j].setPreferredSize(new Dimension(SQUARE_SIZE, SQUARE_SIZE));
                if ((i + j) % 2 == 0) {
                    squares[i][j].setBackground(Color.decode("#F0D9B5"));
                } else {
                    squares[i][j].setBackground(Color.decode("#B58863"));
                }
                add(squares[i][j]);
                // No mouse listener is added here
            }
        }
    }


    private void initializePieces() {
        Font chessFont = new Font("Serif", Font.BOLD, 48);

        // Initialize black pieces
        String[] blackPieces = {"♜", "♞", "♝", "♛", "♚", "♝", "♞", "♜"};
        for (int i = 0; i < GRID_SIZE; i++) {
            JLabel pieceLabel = new JLabel(blackPieces[i], SwingConstants.CENTER);
            pieceLabel.setFont(chessFont);
            squares[0][i].add(pieceLabel);

            JLabel pawnLabel = new JLabel("♟︎", SwingConstants.CENTER);
            pawnLabel.setFont(chessFont);
            squares[1][i].add(pawnLabel);
        }

        // Initialize white pieces
        String[] whitePieces = {"♖", "♘", "♗", "♕", "♔", "♗", "♘", "♖"};
        for (int i = 0; i < GRID_SIZE; i++) {
            JLabel pieceLabel = new JLabel(whitePieces[i], SwingConstants.CENTER);
            pieceLabel.setFont(chessFont);
            squares[7][i].add(pieceLabel);

            JLabel pawnLabel = new JLabel("♙", SwingConstants.CENTER);
            pawnLabel.setFont(chessFont);
            squares[6][i].add(pawnLabel);
        }
    }
}
