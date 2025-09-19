package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

     ChessPiece [] [] squares = new ChessPiece[8][8];
    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param pos where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition pos, ChessPiece piece){
    squares[pos.getRow()-1][pos.getColumn()-1] =piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param pos The pos to get the piece from
     * @return Either the piece at the pos, or null if no piece is at that
     * pos
     */
    public ChessPiece getPiece(ChessPosition pos) {
      return squares[pos.getRow()-1][pos.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        throw new RuntimeException("Not implemented");
    }
}
