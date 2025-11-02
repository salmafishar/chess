package chess.PieceMoves;

import chess.*;

import java.util.Collection;
import java.util.ArrayList;
/*
    Where I'll keep the methods used for all the chess pieces
    Needs an <<in>> method to make sure we're in bounds
    <<at>> checks what pos the piece is in now.
    <<moveTo>> gets the piece's pos, checks it the next move will be in bound and empty and then moves
 */

abstract class BaseMovement implements MovementRule {
    public abstract Collection<ChessMove> moves(ChessBoard board, ChessPosition position);

    protected boolean in(int r, int c) {
        return r >= 1 && r <= 8 && c >= 1 && c <= 8;
    }


    protected ChessPiece at(ChessBoard board, int r, int c) {
        return board.getPiece(new ChessPosition(r, c));
    }


    protected void addForwardMove(ChessPosition currentPosition, ChessBoard board, int[][] move, int r, int c, ChessGame.TeamColor me, ArrayList<ChessMove> moves) {
        for (int[] m : move) {
            int rF = r + m[0];
            int cF = c + m[1];
            if (!in(rF, cF)) {
                continue;
            }
            ChessPiece newPosition = at(board, rF, cF);
            if (newPosition == null || newPosition.getTeamColor() != me) {
                moves.add(new ChessMove(currentPosition, new ChessPosition(rF, cF), null));
            }
        }
    }


    protected void addSlidingMove(ChessPosition currentPosition, ChessBoard board, int[][] move, int r, int c, ChessGame.TeamColor me, ArrayList<ChessMove> moves) {
        for (int[] m : move) {
            int rF = r + m[0];
            int cF = c + m[1];
            while (in(rF, cF)) {
                ChessPiece newPosition = at(board, rF, cF);
                if (newPosition == null) {
                    moves.add(new ChessMove(currentPosition, new ChessPosition(rF, cF), null));
                } else {
                    if (newPosition.getTeamColor() != me) {
                        moves.add(new ChessMove(currentPosition, new ChessPosition(rF, cF), null));
                    }
                    break;
                }
                rF += m[0];
                cF += m[1];
            }
        }
    }
}
