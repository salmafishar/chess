package chess.piece_moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

/*
Bishops move in diagonal lines as far as there is open space.
If there is an enemy piece at the end of the diagonal, the bishop may move to that position and capture the enemy piece.


 */
public class BishopMovementRule extends BaseMovement {
    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int r = position.getRow();
        int c = position.getColumn();
        ChessPiece me = at(board, r, c);
        ChessGame.TeamColor color = me.getTeamColor();
        int[][] directions = {
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1},
        };
        addSlidingMove(position, board, directions, r, c, color, moves);
        return moves;
    }
}
