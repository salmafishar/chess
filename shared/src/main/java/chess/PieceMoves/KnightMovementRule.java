package chess.PieceMoves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*
Knights move in an L shape, moving 2 squares in one direction and 1 square in the other direction.
 Knights are the only piece that can ignore pieces in the in-between squares (they can "jump" over other pieces).
They can move to squares occupied by an enemy piece and capture the enemy piece, or to unoccupied squares.
 */
public class KnightMovementRule extends BaseMovement {
    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int r = position.getRow();
        int c = position.getColumn();
        ChessPiece me = at(board, r, c);
        ChessGame.TeamColor color = me.getTeamColor();
        int[][] directions = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1}
                , {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        addForwardMove(position, board, directions, r, c, color, moves);
        return moves;
    }
}
