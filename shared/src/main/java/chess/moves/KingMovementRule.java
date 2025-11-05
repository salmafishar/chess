package chess.moves;
/*
square in any direction (including diagonal)
to either a position occupied by an enemy piece,
or to an unoccupied position
A player is not allowed to make any move that would allow the opponent to capture their King.
If your King is in danger of being captured on your turn,
you must make a move that removes your King from immediate danger.
 */

import chess.*;

import java.util.ArrayList;
import java.util.Collection;


public class KingMovementRule extends BaseMovement {
    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int r = position.getRow();
        int c = position.getColumn();
        ChessPiece me = at(board, r, c);
        ChessGame.TeamColor color = me.getTeamColor();
        int[][] directions = {
                {1, 0}, {0, 1}, {-1, 0}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };
        addForwardMove(position, board, directions, r, c, color, moves);
        return moves;
    }
}
