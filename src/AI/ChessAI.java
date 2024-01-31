package AI;
import Model.ChessBoard;
import Model.ChessPiece;
import Model.Move;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class ChessAI {

    private final int maxDepth;
    private BoardEvaluator boardEvaluator;
    private OpeningNode openingsTree;

    public ChessAI(int maxDepth) {
        this.maxDepth = maxDepth;
        this.boardEvaluator = new BoardEvaluator();
        buildOpeningsTree();
    }



    public Move findBestMove(ChessBoard board, boolean isWhite) {
        isWhite=false;
        Move openingMove = checkForOpening(board.getMoveHistory());
        if (openingMove != null) {
            return openingMove;
        }

        int bestValue = isWhite ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        Move bestMove = null;

        // Iterate through all possible moves
        for (Move move : board.getAllPossibleMoves(isWhite)) {
            System.out.println("Considering move: " + move);
            // Apply the move
            board.applyMove(move);

            // Evaluate the move using minimax
            int moveValue = minimax(board, maxDepth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, !isWhite);

            // Print the evaluated value of the move
            System.out.println("Evaluated value of move: " + move + " = " + moveValue);

            // Undo the move
            board.undoMove(move);

            // Update the best move if necessary
            if (isWhite && moveValue > bestValue || !isWhite && moveValue < bestValue) {
                bestValue = moveValue;
                bestMove = move;
            }
        }
        System.out.println("Best move chosen: " + bestMove);

        return bestMove;
    }


    private int minimax(ChessBoard board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (depth == 0 || board.isGameOver()) {
            return boardEvaluator.evaluate(board, maximizingPlayer);
        }

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : board.getAllPossibleMoves(true)) {
                board.applyMove(move);
                int eval = minimax(board, depth - 1, alpha, beta, false);
                board.undoMove(move);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break; // Alpha-Beta Pruning
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : board.getAllPossibleMoves(false)) {
                board.applyMove(move);
                int eval = minimax(board, depth - 1, alpha, beta, true);
                board.undoMove(move);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break; // Alpha-Beta Pruning
                }
            }
            return minEval;
        }
    }

    private Move checkForOpening(List<Move> moveHistory) {
        OpeningNode current = openingsTree;
        Random random = new Random();

        for (Move playedMove : moveHistory) {
            boolean moveFound = false;
            for (OpeningNode child : current.nextMoves) {
                if (child.move.equals(playedMove)) {
                    current = child;
                    moveFound = true;
                    break;
                }
            }
            if (!moveFound) {
                return null; // No matching opening found
            }
        }

        if (current.nextMoves.isEmpty()) {
            return null; // End of the opening sequence
        } else {
            // Return a random next move from the available options
            return current.nextMoves.get(random.nextInt(current.nextMoves.size())).move;
        }
    }





    private static class OpeningNode {
        Move move;
        List<OpeningNode> nextMoves;

        OpeningNode(Move move) {
            this.move = move;
            this.nextMoves = new ArrayList<>();
        }

        void addNextMove(OpeningNode nextMove) {
            nextMoves.add(nextMove);
        }
    }

    private void buildOpeningsTree() {
        openingsTree = new OpeningNode(null); // root node

        // Example: Adding Ruy Lopez
        OpeningNode e4 = new OpeningNode(new Move(4, 6, 4, 4, ChessPiece.WHITE_PAWN));
        OpeningNode e5 = new OpeningNode(new Move(4, 1, 4, 3, ChessPiece.BLACK_PAWN));
        OpeningNode nf3 = new OpeningNode(new Move(6, 7, 5, 5, ChessPiece.WHITE_KNIGHT));
        OpeningNode nc6 = new OpeningNode(new Move(1, 0, 2, 2, ChessPiece.BLACK_KNIGHT));
        OpeningNode bb5 = new OpeningNode(new Move(5, 7, 1, 3, ChessPiece.WHITE_BISHOP));

        OpeningNode c5 = new OpeningNode(new Move(2, 1, 2, 3, ChessPiece.BLACK_PAWN));
        OpeningNode d4 = new OpeningNode(new Move(3, 6, 3, 4, ChessPiece.WHITE_PAWN));
        OpeningNode cxd4 = new OpeningNode(new Move(2, 3, 3, 4, ChessPiece.BLACK_PAWN));
        OpeningNode nxd4 = new OpeningNode(new Move(5, 5, 3, 4, ChessPiece.WHITE_KNIGHT));
        OpeningNode g6 = new OpeningNode(new Move(6, 1, 6, 2, ChessPiece.BLACK_PAWN));
        OpeningNode nc3 = new OpeningNode(new Move(1, 7, 2, 5, ChessPiece.WHITE_KNIGHT));
        OpeningNode bg7 = new OpeningNode(new Move(5, 0, 6, 1, ChessPiece.BLACK_BISHOP));
        OpeningNode be3 = new OpeningNode(new Move(2, 7, 4, 5, ChessPiece.WHITE_BISHOP));
        OpeningNode nf6 = new OpeningNode(new Move(6, 0, 5, 2, ChessPiece.BLACK_KNIGHT));
        OpeningNode bc4 = new OpeningNode(new Move(5, 7, 2, 4, ChessPiece.WHITE_BISHOP));


        openingsTree.addNextMove(e4);
        e4.addNextMove(e5);
        e5.addNextMove(nf3);
        nf3.addNextMove(nc6);
        nc6.addNextMove(bb5);




        // Sicilian defense accelerated dragon
        e4.addNextMove(c5);
        c5.addNextMove(nf3);
        nf3.addNextMove(nc6);
        nc6.addNextMove(d4);
        d4.addNextMove(cxd4);
        cxd4.addNextMove(nxd4);
        nxd4.addNextMove(g6);
        g6.addNextMove(nc3);
        nc3.addNextMove(bg7);
        bg7.addNextMove(be3);
        be3.addNextMove(nf6);



        // Add more openings and variations here...
        /*
        *
        * e4
        * Modern defense
        * Pirc defense
        *
        *
        * French defense
        * Caro-Kann defense
        * King's Gambit
        * Sicilian defense
        * */
    }

}
