package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public record ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChessPiece that)) {
            return false;
        }
        return this.getTeamColor() == that.getTeamColor()
                && this.getPieceType() == that.getPieceType();
    }

    private boolean in(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    private ChessPiece at(ChessBoard board, int row, int col) {
        return board.getPiece(new ChessPosition(row, col));
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {

        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        final int r0 = myPosition.row();
        final int c0 = myPosition.getColumn();
        final ChessGame.TeamColor me = this.getTeamColor();

        // for KNIGHTS :  (L-shape)
        if (this.getPieceType() == PieceType.KNIGHT) {
            int[][] move = {
                    {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                    {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
            };
            for (int[] m : move) {
                int r = r0 + m[0];
                int c = c0 + m[1];
                if (!in(r, c)) {
                    continue;
                }
                ChessPiece occ = at(board, r, c);
                if (occ == null || occ.getTeamColor() != me) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(r, c), null));
                }
            }
        }
        // for KINGS: move 1 square in any direction
        if (this.getPieceType() == PieceType.KING) {
            int[][] move = {
                    {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                    {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
            };
            for (int[] m : move) {
                int r = r0 + m[0], c = c0 + m[1];
                if (!in(r, c)) {
                    continue;
                }
                ChessPiece occ = at(board, r, c);
                if (occ == null || occ.getTeamColor() != me) {
                    moves.add(new ChessMove(myPosition, new ChessPosition(r, c), null));
                }
            }
        }
        // BISHOP: slide on diagonals
        if (this.getPieceType() == PieceType.BISHOP) {
            int[][] dirs = {
                    {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
            };
            for (int[] d : dirs) {
                int r = r0 + d[0], c = c0 + d[1];
                while (in(r, c)) {
                    ChessPiece occ = at(board, r, c);
                    ChessPosition to = new ChessPosition(r, c);
                    if (occ == null) {
                        moves.add(new ChessMove(myPosition, to, null));
                    } else {
                        if (occ.getTeamColor() != me) {
                            moves.add(new ChessMove(myPosition, to, null));
                        }
                        break;
                    }
                    r += d[0];
                    c += d[1];
                }
            }
        }

        // ROOK: slide orthogonally
        if (this.getPieceType() == PieceType.ROOK) {
            int[][] dirs = {
                    {1, 0}, {-1, 0}, {0, 1}, {0, -1}
            };
            for (int[] d : dirs) {
                int r = r0 + d[0], c = c0 + d[1];
                while (in(r, c)) {
                    ChessPiece occ = at(board, r, c);
                    ChessPosition to = new ChessPosition(r, c);
                    if (occ == null) {
                        moves.add(new ChessMove(myPosition, to, null));
                    } else {
                        if (occ.getTeamColor() != me) {
                            moves.add(new ChessMove(myPosition, to, null));
                        }
                        break;
                    }
                    r += d[0];
                    c += d[1];
                }
            }
        }

        // QUEEN: rook + bishop directions
        if (this.getPieceType() == PieceType.QUEEN) {
            int[][] dirs = {
                    {1, 1}, {1, -1}, {-1, 1}, {-1, -1},
                    {1, 0}, {-1, 0}, {0, 1}, {0, -1}
            };
            for (int[] d : dirs) {
                int r = r0 + d[0], c = c0 + d[1];
                while (in(r, c)) {
                    ChessPiece occ = at(board, r, c);
                    ChessPosition to = new ChessPosition(r, c);
                    if (occ == null) {
                        moves.add(new ChessMove(myPosition, to, null));
                    } else {
                        if (occ.getTeamColor() != me) {
                            moves.add(new ChessMove(myPosition, to, null));
                        }
                        break;
                    }
                    r += d[0];
                    c += d[1];
                }
            }
        }

        // PAWN
        if (this.getPieceType() == PieceType.PAWN) {
            int startRow = (me == ChessGame.TeamColor.WHITE) ? 2 : 7;
            int direction = (me == ChessGame.TeamColor.WHITE) ? +1 : -1;

            // one square forward
            int r1 = r0 + direction;
            if (in(r1, c0) && at(board, r1, c0) == null) {
                ChessPosition to = new ChessPosition(r1, c0);
                if ((me == ChessGame.TeamColor.WHITE && r1 == 8) || (me == ChessGame.TeamColor.BLACK && r1 == 1)) {
                    moves.add(new ChessMove(myPosition, to, PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, to, PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, to, PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, to, PieceType.KNIGHT));
                } else {
                    moves.add(new ChessMove(myPosition, to, null));
                }
            }
            // two squares forward
            int r2 = r0 + 2 * direction;
            if (r0 == startRow && in(r2, c0) &&
                    at(board, r1, c0) == null && at(board, r2, c0) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(r2, c0), null));
            }
            // diagonal captures
            int r = r0 + direction;
            // left
            int cl = c0 - 1;
            if (in(r, cl)) {
                ChessPiece occ = at(board, r, cl);
                if (occ != null && occ.getTeamColor() != me) {
                    ChessPosition to = new ChessPosition(r, cl);
                    if ((me == ChessGame.TeamColor.WHITE && r == 8) || (me == ChessGame.TeamColor.BLACK && r == 1)) {
                        moves.add(new ChessMove(myPosition, to, PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, to, PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, to, PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, to, PieceType.KNIGHT));
                    } else {
                        moves.add(new ChessMove(myPosition, to, null));
                    }
                }
            }
            // right
            int cr = c0 + 1;
            if (in(r, cr)) {
                ChessPiece occ = at(board, r, cr);
                if (occ != null && occ.getTeamColor() != me) {
                    ChessPosition to = new ChessPosition(r, cr);
                    if ((me == ChessGame.TeamColor.WHITE && r == 8) ||
                            (me == ChessGame.TeamColor.BLACK && r == 1)) {
                        moves.add(new ChessMove(myPosition, to, PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, to, PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, to, PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, to, PieceType.KNIGHT));
                    } else {
                        moves.add(new ChessMove(myPosition, to, null));
                    }
                }
            }
        }
        return moves;
    }
}