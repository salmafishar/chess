package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard myBoard;
    private TeamColor teamTurn;

    public ChessGame() {
        myBoard = new ChessBoard();
        myBoard.resetBoard();
        teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {

        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = myBoard.getPiece(startPosition);
        // null if no piece
        if (piece == null) {
            return null;
        }
        var test = piece.pieceMoves(myBoard, startPosition);
        var allowed = new java.util.ArrayList<ChessMove>();
        if (test == null || test.isEmpty()) {
            return allowed;
        }

        for (ChessMove m : test) {
            ChessBoard temp = myBoard.clone();
            temp.addPiece(startPosition, null);

            ChessPiece moved;
            if (piece.getPieceType() == ChessPiece.PieceType.PAWN && m.getPromotionPiece() != null) {
                moved = new ChessPiece(piece.getTeamColor(), m.getPromotionPiece());
            } else {
                moved = new ChessPiece(piece.getTeamColor(), piece.getPieceType());
            }
            temp.addPiece(m.getEndPosition(), moved);
            if (!isOnBoard(piece.getTeamColor(), temp)) {
                allowed.add(m);
            }
        }
        return allowed;
    }

    private boolean isOnBoard(TeamColor teamColor, ChessBoard board) {
        ChessPosition kingPos = findKingOnBoard(teamColor, board);
        if (kingPos == null) {
            return false;
        }
        TeamColor opp = teamColor;
        if (teamColor == TeamColor.WHITE) {
            opp = TeamColor.BLACK;
        } else if (teamColor == TeamColor.BLACK) {
            opp = TeamColor.WHITE;
        }

        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition position = new ChessPosition(r, c);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == opp) {
                    for (ChessMove m : piece.pieceMoves(board, position)) {
                        if (m.getEndPosition().equals(kingPos)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private ChessPosition findKingOnBoard(TeamColor teamColor, ChessBoard board) {
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition position = new ChessPosition(r, c);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return position;
                }
            }
        }
        return null;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // remove the piece from `from`, place it at `to`, checking promotion, flipping teamTurn
        ChessPosition from = move.getStartPosition();
        ChessPosition to = move.getEndPosition();
        ChessPiece movingPiece = myBoard.getPiece(from);

        if (movingPiece == null) {
            throw new InvalidMoveException("no piece at start");
        }
        if (movingPiece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("not your turn!");
        }
        Collection<ChessMove> moves = validMoves(from);
        if (!moves.contains(move)) {
            throw new InvalidMoveException("illegal move!");
        }
        myBoard.addPiece(from, null);
        ChessPiece placed = movingPiece;
        if (movingPiece.getPieceType() == ChessPiece.PieceType.PAWN
                && move.getPromotionPiece() != null
                && ((movingPiece.getTeamColor() == TeamColor.WHITE && to.getRow() == 8)
                || (movingPiece.getTeamColor() == TeamColor.BLACK && to.getRow() == 1))) {
            placed = new ChessPiece(movingPiece.getTeamColor(), move.getPromotionPiece());
        }
        myBoard.addPiece(to, placed);
        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    private TeamColor opponent(TeamColor c) {
        return (c == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    private boolean squareAttacked(ChessBoard board, ChessPosition target, TeamColor by) {
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                var pos = new ChessPosition(r, c);
                var p = board.getPiece(pos);
                if (p != null && p.getTeamColor() == by) {
                    var moves = p.pieceMoves(board, pos);
                    if (moves != null) {
                        for (var m : moves) {
                            if (m.getEndPosition().equals(target)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessGame chessGame)) {
            return false;
        }
        return Objects.equals(myBoard, chessGame.myBoard) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(myBoard, teamTurn);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "myBoard=" + myBoard +
                ", teamTurn=" + teamTurn +
                '}';
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */

    public boolean isInCheck(TeamColor teamColor) {
        var king = findKingOnBoard(teamColor, myBoard);
        if (king == null) {
            return false;
        }
        return squareAttacked(myBoard, king, opponent(teamColor));
    }

    private boolean hasAnyLegalMove(TeamColor color) {
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                var pos = new ChessPosition(r, c);
                var p = myBoard.getPiece(pos);
                if (p != null && p.getTeamColor() == color) {
                    var mv = validMoves(pos);
                    if (mv != null && !mv.isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //The king is in check: It is being attacked by an opponent's piece.
        //No legal escape for the king: The king cannot move to a safe square.
        //No blocking or capturing the attacker: The attacking piece cannot be captured, and no other piece can be moved to block the attack.
        return isInCheck(teamColor) && !hasAnyLegalMove(teamColor);
    }


    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && !hasAnyLegalMove(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.myBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return myBoard;
    }
}
