package AI;

import Model.ChessBoard;
import Model.ChessPiece;

public class BoardEvaluator {
    private static final int PAWN_VALUE = 1;
    private static final int KNIGHT_VALUE = 3;
    private static final int BISHOP_VALUE = 3;
    private static final int ROOK_VALUE = 5;
    private static final int QUEEN_VALUE = 9;
    private static final double CASTLING_COMPLETED_BONUS = 50;

    private static final double CHECK_BONUS = 0.5;
    private static final double CHECKMATE_BONUS = 900;

    // Other methods...

    public int evaluate(ChessBoard board, boolean isWhiteToMove) {
        ChessPiece[][] boardState = board.getBoard();
        int whiteEval = countMaterial(boardState, true);
        int blackEval = countMaterial(boardState, false);

        // Add positional evaluation
        whiteEval += evaluatePositionalFactors(board, true);
        blackEval += evaluatePositionalFactors(board, false);

        // Evaluate check and checkmate scenarios
        if (board.isKingInCheck(boardState, false)) {  // Checking if black king is in check
            whiteEval += CHECK_BONUS;
        }
        if (board.isCheckmate(false)) {  // Checking if black is in checkmate
            whiteEval += CHECKMATE_BONUS;
        }
        if (board.isKingInCheck(boardState, true)) {  // Checking if white king is in check
            blackEval += CHECK_BONUS;
        }
        if (board.isCheckmate(true)) {  // Checking if white is in checkmate
            blackEval += CHECKMATE_BONUS;
        }

        int evaluation = whiteEval - blackEval;
        //int perspective = isWhiteToMove ? 1 : -1;

        return evaluation;
    }

    private int evaluatePositionalFactors(ChessBoard board, boolean isWhite) {
        int score = 0;

        // Reward for completing castling
        if (isWhite && board.hasWhiteCastled() || !isWhite && board.hasBlackCastled()) {
            score += CASTLING_COMPLETED_BONUS;
        }

        // Rest of your positional evaluation logic...

        return score;
    }


    private int countMaterial(ChessPiece[][] board, boolean isWhite) {
        int material = 0;
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                ChessPiece piece = board[row][col];
                if (piece.toString().startsWith(isWhite ? "WHITE" : "BLACK")) {
                    material += getValueOfPiece(piece);
                }
            }
        }
        return material;
    }

    private int getValueOfPiece(ChessPiece piece) {
        switch (piece) {
            case WHITE_PAWN, BLACK_PAWN:
                return PAWN_VALUE;
            case WHITE_KNIGHT, BLACK_KNIGHT:
                return KNIGHT_VALUE;
            case WHITE_BISHOP, BLACK_BISHOP:
                return BISHOP_VALUE;
            case WHITE_ROOK, BLACK_ROOK:
                return ROOK_VALUE;
            case WHITE_QUEEN, BLACK_QUEEN:
                return QUEEN_VALUE;
            default:
                return 0;
        }
    }


}
