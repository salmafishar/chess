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
    var row = pos.getRow()-1;
    var col =pos.getColumn()-1;
    return squares[row] [col];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int r = 0 ; r<8; r++){
            for( int c =0; c < 8; c++){
                squares [r][c] = null;
            }
        }
        for (int c = 1; c <= 8; c++) {
            addPiece(new ChessPosition(2, c),
                    new ChessPiece(ChessGame.TeamColor.WHITE,
                    ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(7, c),
                    new ChessPiece(ChessGame.TeamColor.BLACK,
                    ChessPiece.PieceType.PAWN));
        }
        ChessPiece.PieceType[] back = {
                ChessPiece.PieceType.ROOK,   ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN,  ChessPiece.PieceType.KING,
                ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK
        };
        for (int c = 1; c <= 8; c++) {
            addPiece(new ChessPosition(1, c),
                    new ChessPiece(ChessGame.TeamColor.WHITE, back[c-1]));
        }
        // black back rank on row 8
        for (int c = 1; c <= 8; c++) {
            addPiece(new ChessPosition(8, c),
                    new ChessPiece(ChessGame.TeamColor.BLACK, back[c-1]));
        }
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessBoard that)) return false;
        return java.util.Arrays.deepEquals(this.squares, that.squares);
    }

    @Override
    public int hashCode() {
        return java.util.Arrays.deepHashCode(this.squares);
    }
}
