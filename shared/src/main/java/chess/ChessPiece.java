package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {

        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new java.util.ArrayList<>();

        if (this.getPieceType() != PieceType.BISHOP) {
            return moves;
        }

        int r0 = myPosition.getRow();
        int c0 = myPosition.getColumn();

        int[][] directions = {
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for (int[] d : directions) {
            int dr = d[0];
            int dc = d[1];

            int r = r0 + dr;
            int c = c0 + dc;
            while (r >= 1 && r <= 8 && c >= 1 && c <= 8) {
                ChessPosition nextPos = new ChessPosition(r, c);
                ChessPiece occ = board.getPiece(nextPos);

                if (occ == null) {
                    moves.add(new ChessMove(myPosition, nextPos, null));
                    r += dr;
                    c += dc;
                } else {
                    if (occ.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, nextPos, null));
                    }
                }
                break;
            }
        }
    }
    return moves;
}