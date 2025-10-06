package chess;

import java.util.Collection;

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
        if (piece == null){
            return null;
        }
        // valid moves:
        return piece.pieceMoves(myBoard,startPosition);

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        /**
         * 1.set basic from, to and movingPiece
         * 2.check null and teamTurn >> throw exceptions
         * 3. call validMoves
         */
        ChessPosition from = move.getStartPosition();
        ChessPosition to = move.getEndPosition();
        ChessPiece movingPiece = myBoard.getPiece(from);

        if (movingPiece == null) {
            throw new InvalidMoveException("no piece at start");
        }
        if (movingPiece.getTeamColor() !=teamTurn){
            throw new InvalidMoveException("not your turn!");
        }
        Collection<ChessMove> moves = validMoves(from);
        if (!moves.contains(move)){throw new InvalidMoveException("illegal move!");}

    }
    // method to get the location of teh king to see if it's in check
    private ChessPosition findKing(TeamColor teamColor){
        for (int r = 1; r <=8; r++){
            for (int c=1; c<=8; c++){
                ChessPiece piece = myBoard.getPiece(new ChessPosition(r,c));
                if (piece !=null && piece.getTeamColor() == teamColor &&piece.getPieceType()== ChessPiece.PieceType.KING){
                    return new ChessPosition(r,c);}
            }
        }
        return null;
    }
    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findKing(teamColor);
        if (kingPos ==null) {return false;}

        TeamColor opp = teamColor;
        if (teamColor == TeamColor.WHITE) {opp= TeamColor.BLACK;}
        else if (teamColor == TeamColor.BLACK){opp =TeamColor.WHITE;}

        for (int r = 1; r <=8; r++){
            for (int c=1; c<=8; c++){
                ChessPosition position = new ChessPosition(r,c);
                ChessPiece piece = myBoard.getPiece(position);
                if (piece!= null && piece.getTeamColor() == opp){
                    for (ChessMove m: piece.pieceMoves(myBoard, position)){
                        if(m.getEndPosition().equals(kingPos)){return true;}
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
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
