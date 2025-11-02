package chess.PieceMoves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
/*
move in straight lines as far as there is open space.
If there is an enemy piece at the end of the line, rooks may move to that position and capture the enemy piece.

 */

public class RookMovementRule extends BaseMovement {
    @Override
    public Collection<ChessMove> moves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int r = position.getRow();
        int c = position.getColumn();
        ChessPiece me = at(board, r, c);
        ChessGame.TeamColor color = me.getTeamColor();
        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };
        addForwardMove(position, board, directions, r, c, color, moves);
        return moves;
    }
}
