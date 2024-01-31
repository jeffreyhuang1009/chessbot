package Model;

import java.util.Objects;
public class Move {
    private final int startX, startY;
    private final int endX, endY;
    private final ChessPiece movedPiece;
    private ChessPiece capturedPiece;
    private ChessPiece promotionPiece;
    private boolean isCastlingMove = false;
    private boolean isEnPassantMove = false;
    private boolean isPawnPromotion = false;

    // Constructor for a standard move
    public Move(int startX, int startY, int endX, int endY, ChessPiece movedPiece) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.movedPiece = movedPiece;
        this.capturedPiece = null; // set this later if the move is a capture
    }

    // Getters
    public int getStartX() { return startX; }
    public int getStartY() { return startY; }
    public int getEndX() { return endX; }
    public int getEndY() { return endY; }
    public ChessPiece getMovedPiece() { return movedPiece; }
    public ChessPiece getCapturedPiece() { return capturedPiece; }
    public ChessPiece getPromotionPiece() { return promotionPiece;}
    public boolean isCastlingMove() { return isCastlingMove; }
    public boolean isEnPassantMove() { return isEnPassantMove; }
    public boolean isPawnPromotion() { return isPawnPromotion; }

    // Setters for special moves and captured piece
    public void setCapturedPiece(ChessPiece piece) { this.capturedPiece = piece; }
    public void setCastlingMove(boolean isCastling) { this.isCastlingMove = isCastling; }
    public void setEnPassantMove(boolean isEnPassant) { this.isEnPassantMove = isEnPassant; }
    public void setPawnPromotion(boolean isPromotion) { this.isPawnPromotion = isPromotion; }
    public void setPromotionPiece(ChessPiece promotionPiece) { this.promotionPiece = promotionPiece; }

    @Override
    public String toString() {
        // Enhanced toString to include special move information
        return "Move: " + movedPiece + " from " + startX + "," + startY + " to " + endX + "," + endY +
                (isCastlingMove ? " [Castling]" : "") +
                (isEnPassantMove ? " [En Passant]" : "") +
                (isPawnPromotion ? " [Pawn Promotion]" : "");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Move move = (Move) obj;
        return startX == move.startX &&
                startY == move.startY &&
                endX == move.endX &&
                endY == move.endY &&
                Objects.equals(movedPiece, move.movedPiece);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startX, startY, endX, endY, movedPiece);
    }

}
