package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

/*
Pawns normally may move forward one square if that square is unoccupied,
though if it is the first time that pawn is being moved,
it may be moved forward 2 squares (provided both squares are unoccupied).
Pawns cannot capture forward, but instead capture forward diagonally (1 square forward and 1 square sideways).
They may only move diagonally like this if capturing an enemy piece.
When a pawn moves to the end of the board (row 8 for white and row 1 for black),
they get promoted and are replaced with the player's choice of Rook, Knight, Bishop, or Queen
(they cannot stay a Pawn or become King).
 */
public class PawnMovementRule extends BaseMovement {
    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int r = position.getRow();
        int c = position.getColumn();
        ChessPiece me = at(board, r, c);
        if (me == null) {
            return moves;
        }

        ChessGame.TeamColor color = me.getTeamColor();
        int direction;
        int startRow;
        int promotionRow;
        // one square forward
        if (color == ChessGame.TeamColor.WHITE) {
            direction = 1;
            startRow = 2;
            promotionRow = 8;
        } else {
            direction = -1;
            startRow = 7;
            promotionRow = 1;
        }
        // one square forward + promotion
        int r1 = r + direction;
        if (in(r1, c) && at(board, r1, c) == null) {
            if (r1 == promotionRow) {
                moves.add(new ChessMove(position, new ChessPosition(r1, c), ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(position, new ChessPosition(r1, c), ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(position, new ChessPosition(r1, c), ChessPiece.PieceType.BISHOP));
                moves.add(new ChessMove(position, new ChessPosition(r1, c), ChessPiece.PieceType.QUEEN));
            } else {
                moves.add(new ChessMove(position, new ChessPosition(r1, c), null));
            }
        }
        // 2 squares forward
        int r2 = r + 2 * direction;
        if (r == startRow && in(r2, c) && at(board, r1, c) == null
                && at(board, r2, c) == null) {
            moves.add(new ChessMove(position, new ChessPosition(r2, c), null));
        }
        // capturing
        int cLeft = c - 1;
        int cRight = c + 1;
        if (in(r1, cLeft)) {
            ChessPiece targetL = at(board, r1, cLeft);
            if (targetL != null && targetL.getTeamColor() != color) {
                if (r1 == promotionRow) {
                    moves.add(new ChessMove(position, new ChessPosition(r1, cLeft), ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(position, new ChessPosition(r1, cLeft), ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(position, new ChessPosition(r1, cLeft), ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(position, new ChessPosition(r1, cLeft), ChessPiece.PieceType.QUEEN));
                } else {
                    moves.add(new ChessMove(position, new ChessPosition(r1, cLeft), null));
                }
            }
        }
        if (in(r1, cRight)) {
            ChessPiece targetR = at(board, r1, cRight);
            if (targetR != null && targetR.getTeamColor() != color) {
                if (r1 == promotionRow) {
                    moves.add(new ChessMove(position, new ChessPosition(r1, cRight), ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(position, new ChessPosition(r1, cRight), ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(position, new ChessPosition(r1, cRight), ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(position, new ChessPosition(r1, cRight), ChessPiece.PieceType.QUEEN));
                } else {
                    moves.add(new ChessMove(position, new ChessPosition(r1, cRight), null));
                }
            }
        }
        return moves;
    }
}
