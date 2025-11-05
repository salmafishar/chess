package chess;

import chess.moves.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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
        if (this.getPieceType() == PieceType.KNIGHT) {
            return new KnightMovementRule().moves(board, myPosition);
        }
        if (this.getPieceType() == PieceType.KING) {
            return new KingMovementRule().moves(board, myPosition);
        }
        if (this.getPieceType() == PieceType.BISHOP) {
            return new BishopMovementRule().moves(board, myPosition);
        }
        if (this.getPieceType() == PieceType.ROOK) {
            return new RookMovementRule().moves(board, myPosition);
        }
        if (this.getPieceType() == PieceType.QUEEN) {
            return new QueenMovementRule().moves(board, myPosition);
        }
        if (this.getPieceType() == PieceType.PAWN) {
            return new PawnMovementRule().moves(board, myPosition);
        }
        return new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessPiece piece)) {
            return false;
        }
        return pieceColor == piece.pieceColor && type == piece.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }
}