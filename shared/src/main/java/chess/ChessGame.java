package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame{

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
        var pseudo = piece.pieceMoves(myBoard,startPosition);
        var legal = new java.util.ArrayList<ChessMove>(pseudo.size());

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
        // remove the piece from `from`, place it at `to`, checking promotion, flipping teamTurn
        ChessPosition from = move.startPosition();
        ChessPosition to = move.endPosition();
        ChessPiece movingPiece = myBoard.getPiece(from);

        if (movingPiece == null) {
            throw new InvalidMoveException("no piece at start");
        }
        if (movingPiece.getTeamColor() !=teamTurn){
            throw new InvalidMoveException("not your turn!");
        }
        Collection<ChessMove> moves = validMoves(from);
        if (!moves.contains(move)){throw new InvalidMoveException("illegal move!");}
        myBoard.addPiece(from,null);
        // checking if it's a promotion
        if (movingPiece.getPieceType()== ChessPiece.PieceType.PAWN){
            if (move.promotionPiece() != null){
               if((movingPiece.getTeamColor() == TeamColor.WHITE && to.row()== 8)
               ||(movingPiece.getTeamColor() == TeamColor.BLACK && to.row()== 1)){
                   myBoard.addPiece(to,new ChessPiece(movingPiece.getTeamColor(),move.promotionPiece()));
               }
            }
            else {myBoard.addPiece(to,movingPiece);}
        }
        else if (movingPiece.getPieceType()!= ChessPiece.PieceType.PAWN){
            myBoard.addPiece(to,movingPiece);
        }
        if (teamTurn == TeamColor.WHITE){teamTurn = TeamColor.BLACK;}
        else if (teamTurn == TeamColor.BLACK){teamTurn = TeamColor.WHITE;}
    }
    // method to get the location of the king to see if it's in check
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
                        if(m.endPosition().equals(kingPos)){return true;}
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
        if (!isInCheck(teamColor)){return false;}
        for (int r = 1; r <=8; r++){
            for (int c=1; c<=8; c++){
                ChessPosition pos =new ChessPosition(r,c);
                ChessPiece p = myBoard.getPiece(pos);
                if(p!= null && p.getTeamColor()== teamColor){
                    var moves = validMoves(pos);
                    if(moves!= null && !moves.isEmpty()){return false;}
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)){return false;}
        for (int r = 1; r <=8; r++){
            for (int c=1; c<=8; c++){
                ChessPosition pos =new ChessPosition(r,c);
                ChessPiece p = myBoard.getPiece(pos);
                if(p!= null && p.getTeamColor()== teamColor){
                    var moves = validMoves(pos);
                    if(moves!= null && !moves.isEmpty()){return false;}
                }
            }
        }
        return true;
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
    @Override
    public int hashCode() {
        if (teamTurn == null){return 0;}
        if (myBoard == null){return 0;}
        return 31 * (myBoard.hashCode()) + teamTurn.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null){return false;}
        if (obj == this){return true;}
        if (!(obj instanceof ChessGame otherGame)){return false;}
        return (this.myBoard.equals(otherGame.myBoard)
        && this.teamTurn.equals(otherGame.teamTurn));
    }

    @Override
    public String toString() {
        return String.format("Team turn: %s, Board: %s", teamTurn, myBoard);
    }
}
