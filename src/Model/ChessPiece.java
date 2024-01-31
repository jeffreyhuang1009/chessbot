package Model;

public enum ChessPiece {
    // White pieces
    WHITE_PAWN("♙", "WHITE_PAWN"),
    WHITE_KNIGHT("♘", "WHITE_KNIGHT"),
    WHITE_BISHOP("♗", "WHITE_BISHOP"),
    WHITE_ROOK("♖", "WHITE_ROOK"),
    WHITE_QUEEN("♕", "WHITE_QUEEN"),
    WHITE_KING("♔", "WHITE_KING"),

    // Black pieces
    BLACK_PAWN("♟︎", "BLACK_PAWN"),
    BLACK_KNIGHT("♞", "BLACK_KNIGHT"),
    BLACK_BISHOP("♝", "BLACK_BISHOP"),
    BLACK_ROOK("♜", "BLACK_ROOK"),
    BLACK_QUEEN("♛", "BLACK_QUEEN"),
    BLACK_KING("♚", "BLACK_KING"),

    // Empty space
    EMPTY(" ", "EMPTY");

    private final String icon;
    private final String name;

    ChessPiece(String icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
