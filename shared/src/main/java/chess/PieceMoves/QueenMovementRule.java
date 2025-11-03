package chess.PieceMoves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;


/*
move in straight lines and diagonals as far as there is open space.
If there is an enemy piece at the end of the line, they may move to that position and capture the enemy piece.
 */
public class QueenMovementRule extends BaseMovement {
    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int r = position.getRow();
        int c = position.getColumn();
        ChessPiece me = at(board, r, c);
        ChessGame.TeamColor color = me.getTeamColor();
        int[][] directions = {
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };
        addSlidingMove(position, board, directions, r, c, color, moves);
        return moves;
    }
}
