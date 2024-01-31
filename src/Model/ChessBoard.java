package Model;
import java.util.List;
import java.util.ArrayList;

public class ChessBoard {
    private ChessPiece[][] board;
    private LastMove lastMove;
    private boolean whiteKingMoved = false;
    private boolean whiteRookKingSideMoved = false;
    private boolean whiteRookQueenSideMoved = false;
    private boolean blackKingMoved = false;
    private boolean blackRookKingSideMoved = false;
    private boolean blackRookQueenSideMoved = false;
    private boolean hasWhiteCastled = false;
    private boolean hasBlackCastled = false;
    private List<Move> moveHistory;


    public ChessBoard() {
        board = new ChessPiece[8][8];
        resetBoard();
        moveHistory = new ArrayList<>();
    }

    public ChessPiece[][] getBoard() {
        return this.board;
    }
    public List<Move> getMoveHistory() { return moveHistory; }

    public boolean hasWhiteCastled() { return hasWhiteCastled; }

    public boolean hasBlackCastled() {
        return hasBlackCastled;
    }


    private void resetBoard() {
        // Set up Black pieces
        board[0] = new ChessPiece[] {ChessPiece.BLACK_ROOK, ChessPiece.BLACK_KNIGHT, ChessPiece.BLACK_BISHOP, ChessPiece.BLACK_QUEEN,
                ChessPiece.BLACK_KING, ChessPiece.BLACK_BISHOP, ChessPiece.BLACK_KNIGHT, ChessPiece.BLACK_ROOK};
        for (int i = 0; i < 8; i++) {
            board[1][i] = ChessPiece.BLACK_PAWN;
        }

        // Set up empty squares
        for (int i = 2; i <= 5; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = ChessPiece.EMPTY;
            }
        }

        // Set up White pieces
        for (int i = 0; i < 8; i++) {
            board[6][i] = ChessPiece.WHITE_PAWN;
        }
        board[7] = new ChessPiece[] {ChessPiece.WHITE_ROOK, ChessPiece.WHITE_KNIGHT, ChessPiece.WHITE_BISHOP, ChessPiece.WHITE_QUEEN,
                ChessPiece.WHITE_KING, ChessPiece.WHITE_BISHOP, ChessPiece.WHITE_KNIGHT, ChessPiece.WHITE_ROOK};
    }

    // Method to get a piece from the board
    public ChessPiece getPiece(int row, int col) { return board[row][col]; }

    // Method to set a piece on the board
    public void setPiece(int row, int col, ChessPiece piece) {
        board[row][col] = piece;
    }

// Method to check if the piece is the opposite color
    private boolean isOppositeColor(ChessPiece piece1, ChessPiece piece2) {
        return (piece1.toString().startsWith("WHITE") && piece2.toString().startsWith("BLACK")) ||
                (piece1.toString().startsWith("BLACK") && piece2.toString().startsWith("WHITE"));
    }


    // One big method to check if a move is valid
    public boolean isValidMove(ChessPiece[][] board, int startX, int startY, int endX, int endY) {
        // Check if the start and end positions are within the board
        if (!isWithinBoard(startX, startY) || !isWithinBoard(endX, endY)) return false;

        ChessPiece movingPiece = getPiece(startY, startX);
        if (movingPiece == ChessPiece.EMPTY ||
                (getPiece(endY, endX) != ChessPiece.EMPTY && !isOppositeColor(movingPiece, getPiece(endY, endX)))) {
            return false;
        }

        boolean isWhiteKing = movingPiece.toString().startsWith("WHITE");

        // Make a copy of the board and perform the move
        ChessPiece[][] boardCopy = new ChessPiece[8][8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(board[i], 0, boardCopy[i], 0, 8);
        }
        boardCopy[endY][endX] = boardCopy[startY][startX];
        boardCopy[startY][startX] = ChessPiece.EMPTY;

        // Check if the move leaves the king in check
        if (isKingInCheck(boardCopy, isWhiteKing)) return false;

        // For king-specific moves
        if (movingPiece == ChessPiece.WHITE_KING || movingPiece == ChessPiece.BLACK_KING) {
            return moveKing(board, startX, startY, endX, endY);
        }

        // Non-king piece move validation
        return isLegalNonKingMove(board, startX, startY, endX, endY);
    }


    private boolean isLegalNonKingMove(ChessPiece[][] board, int startX, int startY, int endX, int endY) {
        // Similar logic to isValidMove but only for non-king pieces
        // Do not check for king's check status here
        ChessPiece movingPiece = board[startY][startX];

        switch (movingPiece) {
            case WHITE_PAWN:
            case BLACK_PAWN:
                return movePawn(board, startX, startY, endX, endY);
            case WHITE_KNIGHT:
            case BLACK_KNIGHT:
                return moveKnight(board, startX, startY, endX, endY);
            case WHITE_BISHOP:
            case BLACK_BISHOP:
                return moveBishop(board, startX, startY, endX, endY);
            case WHITE_ROOK:
            case BLACK_ROOK:
                return moveRook(board, startX, startY, endX, endY);
            case WHITE_QUEEN:
            case BLACK_QUEEN:
                return moveQueen(board, startX, startY, endX, endY);
            // No case for the king as this method is specifically for non-king moves
            default:
                return false;
        }
    }

    // Method to check if a square is under attack by any of the legal moves of the opponent pieces
    private boolean isSquareUnderAttack(ChessPiece[][] board, int x, int y, boolean isWhite) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = board[i][j];
                if (piece != ChessPiece.EMPTY && isOppositeColor(piece, isWhite ? ChessPiece.WHITE_KING : ChessPiece.BLACK_KING)) {
                    if (isLegalNonKingMove(board, j, i, x, y)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Method to locate the king and check if that square is under attack
    public boolean isKingInCheck(ChessPiece[][] board, boolean isWhite) {
        // Find the king
        int kingX = -1;
        int kingY = -1;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((isWhite && board[i][j] == ChessPiece.WHITE_KING) ||
                        (!isWhite && board[i][j] == ChessPiece.BLACK_KING)) {
                    kingX = j;
                    kingY = i;
                    // Once the king is found, check if it's square is under attack
                    return isSquareUnderAttack(board, kingX, kingY, isWhite);
                }
            }
        }
        // Return false if the king's position is not found (shouldn't happen in a valid board)
        return false;
    }


    private boolean movePawn(ChessPiece[][] board, int startX, int startY, int endX, int endY) {
        ChessPiece movingPiece = board[startY][startX];
        // Check for forward movement without capturing for white and black pawns
        if (startX == endX && board[endY][endX] == ChessPiece.EMPTY) {
            if (movingPiece == ChessPiece.WHITE_PAWN) {
                if (startY - endY == 1 || (startY == 6 && endY == 4 && board[5][startX] == ChessPiece.EMPTY)) {
                    return true;
                }
            } else if (movingPiece == ChessPiece.BLACK_PAWN) {
                if (endY - startY == 1 || (startY == 1 && endY == 3 && board[2][startX] == ChessPiece.EMPTY)) {
                    return true;
                }
            }
        }
        // Check for diagonal capturing
        if (Math.abs(startX - endX) == 1 && ((movingPiece == ChessPiece.WHITE_PAWN && startY - endY == 1) || (movingPiece == ChessPiece.BLACK_PAWN && endY - startY == 1))) {
            if (isOppositeColor(movingPiece, board[endY][endX])) {
                return true;
            }
        }

        // En passant capture logic
        if (lastMove != null &&
                Math.abs(lastMove.startY - lastMove.endY) == 2 && // Two-square move in the last move
                Math.abs(startX - endX) == 1 && // Diagonal move
                ((movingPiece == ChessPiece.WHITE_PAWN && startY == 3 && endY == 2) ||
                        (movingPiece == ChessPiece.BLACK_PAWN && startY == 4 && endY == 5))) {
            // Check if the last move was made by an adjacent opposing pawn
            if ((movingPiece == ChessPiece.WHITE_PAWN && lastMove.piece == ChessPiece.BLACK_PAWN && lastMove.endY == 3) ||
                    (movingPiece == ChessPiece.BLACK_PAWN && lastMove.piece == ChessPiece.WHITE_PAWN && lastMove.endY == 4)) {
                // Check if the pawn is capturing en passant
                if ((lastMove.endX == endX - 1 || lastMove.endX == endX + 1) && lastMove.startY == startY) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean moveKnight(ChessPiece[][] board, int startX, int startY, int endX, int endY) {
        if ((Math.abs(startX - endX) == 2 && Math.abs(startY - endY) == 1) ||
                (Math.abs(startX - endX) == 1 && Math.abs(startY - endY) == 2)) {
            return true;
        }
        return false;
    }

    private boolean moveBishop(ChessPiece[][] board, int startX, int startY, int endX, int endY) {
        // Check if there is anything in the way
        if (Math.abs(startX - endX) == Math.abs(startY - endY)) {

            if (startX < endX && startY < endY) {
                for (int i = 1; i < Math.abs(startX - endX); i++) {
                    //System.out.println("A Checking position: (" + (startX + i) + "," + (startY + i) + ")");
                    if (startX + i < 8 && startY + i < 8 && board[startY + i][startX + i] != ChessPiece.EMPTY) {
                        return false;
                    }
                }
            }

            else if (startX > endX && startY < endY) {
                for (int i = 1; i < Math.abs(startX - endX); i++) {
                    //System.out.println("B Checking position: (" + (startX + i) + "," + (startY - i) + ")");
                    if (startX - i >= 0 && startY + i < 8 && board[startY + i][startX - i] != ChessPiece.EMPTY) {
                        return false;
                    }
                }
            }

            else if (startX < endX && startY > endY) {
                for (int i = 1; i < Math.abs(startX - endX); i++) {
                    //System.out.println("C Checking position: (" + (startX - i) + "," + (startY + i) + ")");
                    if (startX + i < 8 && startY - i >= 0 && board[startY - i][startX + i] != ChessPiece.EMPTY) {
                        return false;
                    }
                }
            }

            else if (startX > endX && startY > endY) {
                for (int i = 1; i < Math.abs(startX - endX); i++) {
                    //System.out.println("D Checking position: (" + (startX - i) + "," + (startY - i) + ")");
                    if (startX - i >= 0 && startY - i >= 0 && board[startY - i][startX - i] != ChessPiece.EMPTY) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    private boolean moveRook(ChessPiece[][] board, int startX, int startY, int endX, int endY) {
        // check if there is anything in the way
        if (startX == endX) {
            // Check if the rook is moving up
            if (startY < endY) {
                for (int i = 1; i < Math.abs(startY - endY); i++) {
                    if (board[startY + i][startX] != ChessPiece.EMPTY) {
                        return false;
                    }
                }
            }
            // Check if the rook is moving down
            else if (startY > endY) {
                for (int i = 1; i < Math.abs(startY - endY); i++) {
                    if (board[startY-i][startX] != ChessPiece.EMPTY) {
                        return false;
                    }
                }
            }
            return true;
        }
        else if (startY == endY) {
            // Check if the rook is moving right
            if (startX < endX) {
                for (int i = 1; i < Math.abs(startX - endX); i++) {
                    if (board[startY][startX+i] != ChessPiece.EMPTY) {
                        return false;
                    }
                }
            }
            // Check if the rook is moving left
            else if (startX > endX) {
                for (int i = 1; i < Math.abs(startX - endX); i++) {
                    if (board[startY][startX-i] != ChessPiece.EMPTY) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    private boolean moveQueen(ChessPiece[][] board, int startX, int startY, int endX, int endY) {
        // check if there is anything in the way
        // diagonal movement
        if (Math.abs(startX - endX) == Math.abs(startY - endY)) {

            if (startX < endX && startY < endY) {
                for (int i = 1; i < Math.abs(startX - endX); i++) {
                    //System.out.println("A Checking position: (" + (startX + i) + "," + (startY + i) + ")");
                    if (startX + i < 8 && startY + i < 8 && board[startY + i][startX + i] != ChessPiece.EMPTY) {
                        return false;
                    }
                }
            }

            else if (startX > endX && startY < endY) {
                for (int i = 1; i < Math.abs(startX - endX); i++) {
                    //System.out.println("B Checking position: (" + (startX + i) + "," + (startY - i) + ")");
                    if (startX - i >= 0 && startY + i < 8 && board[startY + i][startX - i] != ChessPiece.EMPTY) {
                        return false;
                    }
                }
            }

            else if (startX < endX && startY > endY) {
                for (int i = 1; i < Math.abs(startX - endX); i++) {
                    //System.out.println("C Checking position: (" + (startX - i) + "," + (startY + i) + ")");
                    if (startX + i < 8 && startY - i >= 0 && board[startY - i][startX + i] != ChessPiece.EMPTY) {
                        return false;
                    }
                }
            }

            else if (startX > endX && startY > endY) {
                for (int i = 1; i < Math.abs(startX - endX); i++) {
                    //System.out.println("D Checking position: (" + (startX - i) + "," + (startY - i) + ")");
                    if (startX - i >= 0 && startY - i >= 0 && board[startY - i][startX - i] != ChessPiece.EMPTY) {
                        return false;
                    }
                }
            }
            return true;
        }

        // up down movement
        else if (startX == endX) {
            // Check if the queen is moving up
            if (startY < endY) {
                for (int i = 1; i < Math.abs(startY - endY); i++) {
                    if (board[startY+i][startX] != ChessPiece.EMPTY) {
                        return false;
                    }
                }
            }
            // Check if the queen is moving down
            else if (startY > endY) {
                for (int i = 1; i < Math.abs(startY - endY); i++) {
                    if (board[startY-i][startX] != ChessPiece.EMPTY) {
                        return false;
                    }
                }
            }
            return true;
        }

        // left right movement
        else if (startY == endY) {
            // Check if the queen is moving right
            if (startX < endX) {
                for (int i = 1; i < Math.abs(startX - endX); i++) {
                    if (board[startY][startX+i] != ChessPiece.EMPTY) {
                        return false;
                    }
                }
            }
            // Check if the queen is moving left
            else if (startX > endX) {
                for (int i = 1; i < Math.abs(startX - endX); i++) {
                    if (board[startY][startX-i] != ChessPiece.EMPTY) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false   ;
    }

    private boolean moveKing(ChessPiece[][] board, int startX, int startY, int endX, int endY) {
        // Check for castling first
        if (Math.abs(startX - endX) == 2 && startY == endY) {
            return canCastle(board, startX, startY, endX, endY);
        }

        // Check if the move is one square in any direction
        if (Math.abs(startX - endX) <= 1 && Math.abs(startY - endY) <= 1) {
            // Check for adjacent kings
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int checkX = endX + i;
                    int checkY = endY + j;
                    if (isWithinBoard(checkX, checkY)) {
                        // If the adjacent square contains the opponent's king, do not allow the move
                        if ((board[startY][startX] == ChessPiece.WHITE_KING && board[checkY][checkX] == ChessPiece.BLACK_KING) ||
                                (board[startY][startX] == ChessPiece.BLACK_KING && board[checkY][checkX] == ChessPiece.WHITE_KING)) {
                            return false;
                        }
                    }
                }
            }
            return true; // Move is valid if no adjacent opponent king is found
        }
        return false; // Move is invalid if it's more than one square in any direction
    }

    private boolean isWithinBoard(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    // Check if a pawn reaches the end of the board
    public boolean isPawnPromotion(int startX, int startY, int endX, int endY) {
        ChessPiece piece = getPiece(startY, startX);
        return (piece == ChessPiece.WHITE_PAWN && endY == 0) || (piece == ChessPiece.BLACK_PAWN && endY == 7);
    }

    private ChessPiece chooseAIPromotionPiece() {
        // This method decides what piece a black pawn should be promoted to.
        // The simplest strategy is to always promote to a queen, but you can implement more complex logic if desired.
        return ChessPiece.BLACK_QUEEN;
    }

    public void movePiece(int startX, int startY, int endX, int endY) {
        ChessPiece movingPiece = getPiece(startY, startX);
        ChessPiece capturedPiece = getPiece(endY, endX);


        // Check for pawn promotion
        if (isPawnPromotion(startX, startY, endX, endY)) {
            if (movingPiece == ChessPiece.BLACK_PAWN) {
                // For AI-controlled black pawns
                ChessPiece promotedPiece = chooseAIPromotionPiece();
                setPiece(endY, endX, promotedPiece); // Set the promoted piece

                Move promotionMove = new Move(startX, startY, endX, endY, movingPiece);
                promotionMove.setPawnPromotion(true);
                promotionMove.setPromotionPiece(promotedPiece);
                moveHistory.add(promotionMove);

            } else {
                // This else block is for the human player's white pawns
                // The actual promotion piece will be set in the ChessController
                // For now, just remove the pawn from the board
                setPiece(endY, endX, ChessPiece.EMPTY);
            }
            setPiece(startY, startX, ChessPiece.EMPTY); // Remove the pawn from its original position
            return; // Exit the method to avoid double setting the piece
        }

        Move move = new Move(startX, startY, endX, endY, movingPiece);

        // Check for castling
        if (movingPiece == ChessPiece.WHITE_KING || movingPiece == ChessPiece.BLACK_KING) {
            if (canCastle(board, startX, startY, endX, endY)) {
                // Update flags for king movement
                if (movingPiece == ChessPiece.WHITE_KING) {
                    whiteKingMoved = true;
                    hasWhiteCastled = true;
                } else {
                    blackKingMoved = true;
                    hasBlackCastled = true;
                }
                // Perform the castling move
                int rookStartX = (endX == 6) ? 7 : 0; // Rook's original position
                int rookEndX = (endX == 6) ? 5 : 3; // Rook's new position after castling

                // Move the rook
                ChessPiece rook = getPiece(startY, rookStartX);
                setPiece(startY, rookEndX, rook);
                setPiece(startY, rookStartX, ChessPiece.EMPTY);

                // Update flags for rook movement
                if (rookStartX == 0) {
                    if (movingPiece == ChessPiece.WHITE_KING) whiteRookQueenSideMoved = true;
                    if (movingPiece == ChessPiece.BLACK_KING) blackRookQueenSideMoved = true;
                } else if (rookStartX == 7) {
                    if (movingPiece == ChessPiece.WHITE_KING) whiteRookKingSideMoved = true;
                    if (movingPiece == ChessPiece.BLACK_KING) blackRookKingSideMoved = true;
                }

                // Move the king
                setPiece(endY, endX, movingPiece);
                setPiece(startY, startX, ChessPiece.EMPTY);

                // Update the last move and return to avoid further processing
                lastMove = new LastMove(startX, startY, endX, endY, movingPiece);
                move.setCastlingMove(true);
                moveHistory.add(move);
                return;
            }
        }

        // Update flags for non-castling king and rook movements
        if (movingPiece == ChessPiece.WHITE_KING) whiteKingMoved = true;
        if (movingPiece == ChessPiece.BLACK_KING) blackKingMoved = true;
        if (movingPiece == ChessPiece.WHITE_ROOK) {
            if (startX == 0) whiteRookQueenSideMoved = true;
            if (startX == 7) whiteRookKingSideMoved = true;
        }
        if (movingPiece == ChessPiece.BLACK_ROOK) {
            if (startX == 0) blackRookQueenSideMoved = true;
            if (startX == 7) blackRookKingSideMoved = true;
        }

        // Perform the move for non-castling situations
        setPiece(endY, endX, movingPiece);
        setPiece(startY, startX, ChessPiece.EMPTY);

        // Check for en passant capture
        if (movingPiece == ChessPiece.WHITE_PAWN || movingPiece == ChessPiece.BLACK_PAWN) {
            if (Math.abs(startY - endY) == 1 && Math.abs(startX - endX) == 1 && getPiece(endY, endX) == ChessPiece.EMPTY) {
                // Remove the captured pawn in en passant
                setPiece(startY, endX, ChessPiece.EMPTY);
                // Add en passant move to move history
                move.setEnPassantMove(true);
                move.setCapturedPiece(capturedPiece);
                moveHistory.add(move);
                return;
            }
        }

        // Update the last move
        lastMove = new LastMove(startX, startY, endX, endY, movingPiece);
        if (capturedPiece != ChessPiece.EMPTY) {
            move.setCapturedPiece(capturedPiece);
        }
        moveHistory.add(move);
    }

    private boolean canCastle(ChessPiece[][] board, int startX, int startY, int endX, int endY){
        ChessPiece movingPiece = board[startY][startX];

        // Check if the king is currently in check
        if (isSquareUnderAttack(board, startX, startY, movingPiece == ChessPiece.WHITE_KING)) {
            return false;
        }

        // Check if the piece is a king and it's the first move for the king
        if (!((movingPiece == ChessPiece.WHITE_KING && !whiteKingMoved) || (movingPiece == ChessPiece.BLACK_KING && !blackKingMoved))) {
            return false;
        }

        // Check for castling move (two squares horizontally)
        if (Math.abs(startX - endX) != 2 || startY != endY) {
            return false;
        }

        // Path clearance and not under attack
        int direction = (endX > startX) ? 1 : -1;
        for (int x = startX + direction; x != endX; x += direction) {
            if (board[startY][x] != ChessPiece.EMPTY || isSquareUnderAttack(board, x, startY, movingPiece == ChessPiece.WHITE_KING)) {
                return false;
            }
        }

        // Rook movement check
        boolean rookMoved = (movingPiece == ChessPiece.WHITE_KING) ?
                ((direction == 1) ? whiteRookKingSideMoved : whiteRookQueenSideMoved) :
                ((direction == 1) ? blackRookKingSideMoved : blackRookQueenSideMoved);

        if (rookMoved) {
            return false;
        }

        return true;
    }

    public boolean isCheckmate(boolean isWhite) {
        // First, check if the king is in check
        if (!isKingInCheck(board, isWhite)) {
            return false;
        }

        // Check for any valid moves for all pieces of the current player
        for (int startY = 0; startY < 8; startY++) {
            for (int startX = 0; startX < 8; startX++) {
                ChessPiece piece = board[startY][startX];
                if (piece != ChessPiece.EMPTY && ((isWhite && piece.toString().startsWith("WHITE")) || (!isWhite && piece.toString().startsWith("BLACK")))) {
                    for (int endY = 0; endY < 8; endY++) {
                        for (int endX = 0; endX < 8; endX++) {
                            if (isValidMove(board, startX, startY, endX, endY)) {
                                return false;  // Found a valid move, so it's not checkmate
                            }
                        }
                    }
                }
            }
        }

        // If no valid moves are found, then it's checkmate
        return true;
    }

    public boolean isStalemate(boolean isWhite) {
        // First, check if the king is in check. If yes, it's not a stalemate.
        if (isKingInCheck(board, isWhite)) {
            return false;
        }

        // Check for any legal moves for all pieces of the current player
        for (int startY = 0; startY < 8; startY++) {
            for (int startX = 0; startX < 8; startX++) {
                ChessPiece piece = board[startY][startX];
                if (piece != ChessPiece.EMPTY && ((isWhite && piece.toString().startsWith("WHITE")) || (!isWhite && piece.toString().startsWith("BLACK")))) {
                    for (int endY = 0; endY < 8; endY++) {
                        for (int endX = 0; endX < 8; endX++) {
                            if (isValidMove(board, startX, startY, endX, endY)) {
                                return false;  // Found a legal move, so it's not stalemate
                            }
                        }
                    }
                }
            }
        }

        // If no legal moves are found and the king is not in check, then it's stalemate
        return true;
    }

    public boolean isGameOver() {
        // Check if either side is in checkmate or stalemate
        return isCheckmate(true) || isCheckmate(false) || isStalemate(true) || isStalemate(false);
    }




    private class LastMove {
        int startX, startY, endX, endY;
        ChessPiece piece;

        LastMove(int startX, int startY, int endX, int endY, ChessPiece piece) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.piece = piece;
        }
    }

    public List<Move> getAllPossibleMoves(boolean isWhiteTurn) {
        List<Move> possibleMoves = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board[row][col];
                if ((piece.toString().startsWith("WHITE") && isWhiteTurn) ||
                        (piece.toString().startsWith("BLACK") && !isWhiteTurn)) {
                    possibleMoves.addAll(getMovesForPiece(board, piece, row, col));
                }
            }
        }
        return possibleMoves;
    }

    private List<Move> getMovesForPiece(ChessPiece[][] board, ChessPiece piece, int row, int col) {
        List<Move> validMoves = new ArrayList<>();
        for (int targetRow = 0; targetRow < 8; targetRow++) {
            for (int targetCol = 0; targetCol < 8; targetCol++) {
                if (isValidMove(board, col, row, targetCol, targetRow)) {
                    Move move = new Move(col, row, targetCol, targetRow, piece);
                    validMoves.add(move);

                    // Handle special moves (e.g., pawn promotion, en passant, castling)
                    // by setting additional properties on the move object if necessary
                }
            }
        }
        return validMoves;
    }




    public void applyMove(Move move) {
        int startX = move.getStartX();
        int startY = move.getStartY();
        int endX = move.getEndX();
        int endY = move.getEndY();

        // Check for a capture at the destination square
        ChessPiece capturedPiece = this.board[endY][endX];
        if (capturedPiece != ChessPiece.EMPTY) {
            move.setCapturedPiece(capturedPiece);
        }

        ChessPiece movedPiece = this.board[startY][startX];
        this.board[endY][endX] = movedPiece;
        this.board[startY][startX] = ChessPiece.EMPTY;

        if (move.isPawnPromotion()) {
            // Promote the pawn to the specified piece
            this.board[endY][endX] = move.getPromotionPiece();
        }

        if (move.isEnPassantMove()) {
            // Remove the captured pawn in en passant
            int pawnRow = movedPiece.toString().startsWith("WHITE") ? 3 : 4;
            this.board[pawnRow][endX] = ChessPiece.EMPTY;
        }

        if (move.isCastlingMove()) {
            // Handle rook movement in castling
            int rookStartX = (endX == 6) ? 7 : 0;
            int rookEndX = (endX == 6) ? 5 : 3;
            ChessPiece rook = this.board[startY][rookStartX];
            this.board[startY][rookEndX] = rook;
            this.board[startY][rookStartX] = ChessPiece.EMPTY;

            // Update castling flags
            if (movedPiece == ChessPiece.WHITE_KING) {
                hasWhiteCastled = true;
            } else if (movedPiece == ChessPiece.BLACK_KING) {
                hasBlackCastled = true;
            }
        }


    }


    public void undoMove(Move move) {
        int startX = move.getStartX();
        int startY = move.getStartY();
        int endX = move.getEndX();
        int endY = move.getEndY();

        // Restore the moved piece to its original position
        ChessPiece movedPiece = this.board[endY][endX];
        this.board[startY][startX] = movedPiece;

        // Reset the target square
        this.board[endY][endX] = move.getCapturedPiece() == null ? ChessPiece.EMPTY : move.getCapturedPiece();

        if (move.isPawnPromotion()) {
            // Revert pawn promotion (assume pawn was the original piece)
            this.board[startY][startX] = movedPiece.toString().startsWith("WHITE") ? ChessPiece.WHITE_PAWN : ChessPiece.BLACK_PAWN;
        }

        if (move.isEnPassantMove()) {
            // Restore the captured pawn in en passant
            int pawnRow = movedPiece.toString().startsWith("WHITE") ? 3 : 4;
            this.board[pawnRow][endX] = move.getCapturedPiece();
        }

        if (move.isCastlingMove()) {
            // Handle rook movement in reverse for castling
            int rookStartX = (endX == 6) ? 7 : 0;
            int rookEndX = (endX == 6) ? 5 : 3;
            ChessPiece rook = this.board[startY][rookEndX];
            this.board[startY][rookStartX] = rook;
            this.board[startY][rookEndX] = ChessPiece.EMPTY;
            if (move.getMovedPiece() == ChessPiece.WHITE_KING) {
                hasWhiteCastled = false;
            } else if (move.getMovedPiece() == ChessPiece.BLACK_KING) {
                hasBlackCastled = false;
            }

        }
    }
}




// alpha beta pruning, order the moves based on how likely they are to be good moves
