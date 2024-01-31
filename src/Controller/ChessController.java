package Controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import Model.ChessBoard;
import Model.ChessPiece;
import View.ChessBoardView;
import AI.ChessAI;
import Model.Move;


public class ChessController {
    private ChessBoard chessBoard;
    private ChessBoardView chessBoardView;
    private boolean pieceSelected = false;
    private int selectedRow = -1, selectedCol = -1;
    private boolean isPlayersTurn = true;

    public ChessController(ChessBoard chessBoard, ChessBoardView chessBoardView) {
        this.chessBoard = chessBoard;
        this.chessBoardView = chessBoardView;
        addMouseListeners();
    }

    private void addMouseListeners() {
        MyMouseListener listener = new MyMouseListener();
        for (int i = 0; i < ChessBoardView.GRID_SIZE; i++) {
            for (int j = 0; j < ChessBoardView.GRID_SIZE; j++) {
                chessBoardView.getSquares()[i][j].addMouseListener(listener);
            }
        }
    }

    private class MyMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (!isPlayersTurn) return;

            JPanel clickedSquare = (JPanel) e.getComponent();
            int row = -1, col = -1;
            for (int i = 0; i < ChessBoardView.GRID_SIZE; i++) {
                for (int j = 0; j < ChessBoardView.GRID_SIZE; j++) {
                    if (chessBoardView.getSquares()[i][j] == clickedSquare) {
                        row = i; col = j; break;
                    }
                }
            }

            handlePieceSelection(row, col);
        }
    }

    private void handlePieceSelection(int row, int col) {
        ChessPiece clickedPiece = chessBoard.getPiece(row, col);
        ChessPiece[][] currentBoardState = chessBoard.getBoard();

        if (pieceSelected) {
            if (selectedRow == row && selectedCol == col) {
                // Reselecting the same piece, so reset the selection
                resetSelection();
            } else if (chessBoard.isValidMove(currentBoardState, selectedCol, selectedRow, col, row)) {
                // Valid move, so move the piece and reset the selection
                moveSelectedPiece(row, col);
                resetSelection();
            } else {
                // Invalid move or selecting another piece, so reset the selection
                resetSelection();
                if (clickedPiece != null && clickedPiece.toString().startsWith("WHITE")) {
                    // Select the new piece if it's a white piece
                    selectPiece(row, col);
                }
            }
        } else if (clickedPiece != null && clickedPiece.toString().startsWith("WHITE")) {
            // If no piece is currently selected and the clicked piece is white, select it
            selectPiece(row, col);
        }
    }



    private void moveSelectedPiece(int newRow, int newCol) {
        ChessPiece movingPiece = chessBoard.getPiece(selectedRow, selectedCol);

        // Handle pawn promotion for white pawns
        if (chessBoard.isPawnPromotion(selectedCol, selectedRow, newCol, newRow) && movingPiece.toString().startsWith("WHITE")) {

            // Show promotion choice dialog for the human player
            ChessPiece newPiece = showPromotionChoice();
            // Move the pawn to the promotion square first
            chessBoard.movePiece(selectedCol, selectedRow, newCol, newRow);
            // Then promote the pawn
            chessBoard.setPiece(newRow, newCol, newPiece);
            Move promotionMove = new Move(selectedCol, selectedRow, newCol, newRow, movingPiece);
            promotionMove.setPawnPromotion(true);
            promotionMove.setPromotionPiece(newPiece);
            chessBoard.getMoveHistory().add(promotionMove);
        } else {
            // For all other moves
            chessBoard.movePiece(selectedCol, selectedRow, newCol, newRow);
        }

        updateBoardView();
        checkForEndGame();
        resetSelection();

        // Start AI move calculation in a new thread
        new Thread(() -> {
            switchTurn();
            if (!isPlayersTurn) {
                makeComputerMove();
            }
        }).start();
    }


    private ChessPiece showPromotionChoice() {
        Object[] options = {"Queen", "Rook", "Bishop", "Knight"};
        int choice = JOptionPane.showOptionDialog(null,
                "Choose piece for pawn promotion",
                "Pawn Promotion",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        switch (choice) {
            case 0: return ChessPiece.WHITE_QUEEN;
            case 1: return ChessPiece.WHITE_ROOK;
            case 2: return ChessPiece.WHITE_BISHOP;
            case 3: return ChessPiece.WHITE_KNIGHT;
            default: return ChessPiece.WHITE_QUEEN; // Default promotion to Queen
        }
    }







    private void switchTurn() {
        isPlayersTurn = !isPlayersTurn;
        if (!isPlayersTurn) makeComputerMove();
    }

    private void makeComputerMove() {

        // Create an instance of your Chess AI with a specific depth
        ChessAI ai = new ChessAI(4); // You can adjust the depth as needed

        // Determine if it's the white or black AI's turn

        // Get the best move from the AI
        Move bestMove = ai.findBestMove(chessBoard, isPlayersTurn);
        System.out.println("Best move!!!!1: " + bestMove);
        if (bestMove == null) {
            System.out.println("No valid move found!");
            // No valid move found. Check for checkmate or stalemate
            checkForEndGame();
            return; // Exit the method to avoid further execution
        }
        chessBoard.movePiece(bestMove.getStartX(), bestMove.getStartY(), bestMove.getEndX(), bestMove.getEndY());
        updateBoardView();
        checkForEndGame();
        switchTurn();
    }

    public void checkForEndGame() {
        if (chessBoard.isCheckmate(isPlayersTurn)) {
            JOptionPane.showMessageDialog(null, "Checkmate! " + (isPlayersTurn ? "Black" : "White") + " wins!");
            endGame();
        } else if (chessBoard.isStalemate(isPlayersTurn)) {
            JOptionPane.showMessageDialog(null, "Stalemate! It's a draw!");
            endGame();
        }
    }


    private void selectPiece(int row, int col) {
        resetPreviousHighlights();
        pieceSelected = true;
        selectedRow = row;
        selectedCol = col;
        highlightPossibleMoves(row, col);
        chessBoardView.getSquares()[row][col].setBorder(BorderFactory.createLineBorder(Color.RED, 3));
    }

    private void resetSelection() {
        pieceSelected = false;
        resetPreviousHighlights();
        selectedRow = -1;
        selectedCol = -1;
    }

    private void resetPreviousHighlights() {
        for (int i = 0; i < ChessBoardView.GRID_SIZE; i++) {
            for (int j = 0; j < ChessBoardView.GRID_SIZE; j++) {
                chessBoardView.getSquares()[i][j].setBorder(null);
            }
        }
    }

    private void highlightPossibleMoves(int row, int col) {
        ChessPiece[][] currentBoardState = chessBoard.getBoard();
        for (int i = 0; i < ChessBoardView.GRID_SIZE; i++) {
            for (int j = 0; j < ChessBoardView.GRID_SIZE; j++) {
                if (chessBoard.isValidMove(currentBoardState,col, row, j, i)) {
                    chessBoardView.getSquares()[i][j].setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
                }
            }
        }
    }

    private void updateBoardView() {
        Font chessFont = new Font("Serif", Font.BOLD, 48);
        for (int row = 0; row < ChessBoardView.GRID_SIZE; row++) {
            for (int col = 0; col < ChessBoardView.GRID_SIZE; col++) {
                JPanel square = chessBoardView.getSquares()[row][col];
                square.removeAll();
                ChessPiece piece = chessBoard.getPiece(row, col);
                if (piece != ChessPiece.EMPTY) {
                    JLabel pieceLabel = new JLabel(piece.getIcon(), SwingConstants.CENTER);
                    pieceLabel.setFont(chessFont);
                    square.add(pieceLabel);
                }
                square.revalidate();
                square.repaint();
            }
        }
    }

    private void endGame() {
        // Logic to end the game
        System.exit(0);
    }
    // Other methods can be added as needed
}
