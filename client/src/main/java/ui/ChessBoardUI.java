package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;

import static ui.EscapeSequences.*;

public class ChessBoardUI {

    public void drawBoard(ChessGame.TeamColor color, ChessBoard board, PrintStream out) {
        int[] rankOrder;
        int[] fileOrder;

        if (color == ChessGame.TeamColor.WHITE) {
            rankOrder = new int[]{8, 7, 6, 5, 4, 3, 2, 1};
            fileOrder = new int[]{1, 2, 3, 4, 5, 6, 7, 8};
        } else {
            rankOrder = new int[]{1, 2, 3, 4, 5, 6, 7, 8};
            fileOrder = new int[]{8, 7, 6, 5, 4, 3, 2, 1};
        }

        out.print(ERASE_SCREEN);
        resetConsoleStyle(out);
        out.println();

        drawFileHeaders(out, fileOrder);

        for (int rank : rankOrder) {
            printRankLabel(out, rank);
            for (int file : fileOrder) {
                setSquareColor(out, rank, file);
                String symbol = getPieceSymbol(board, rank, file);
                out.print(symbol);
            }
            resetConsoleStyle(out);
            printRankLabel(out, rank);
            out.println();
        }

        drawFileHeaders(out, fileOrder);

        resetConsoleStyle(out);
        out.println();
    }


    private void drawFileHeaders(PrintStream out, int[] fileOrder) {
        resetConsoleStyle(out);
        out.print("   ");
        for (int file : fileOrder) {
            char letter = (char) ('a' + (file - 1));
            out.print(" " + letter + " ");
        }
        out.print("   ");
        out.println();
    }

    private void printRankLabel(PrintStream out, int rank) {
        resetConsoleStyle(out);
        out.print(" " + rank + " ");
    }

    private void resetConsoleStyle(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private void setSquareColor(PrintStream out, int rank, int file) {
        boolean isLight = ((rank + file) % 2 == 0);
        if (isLight) {
            out.print(SET_BG_COLOR_LIGHT_GREY);
            out.print(SET_TEXT_COLOR_BLACK);
        } else {
            out.print(SET_BG_COLOR_DARK_GREY);
            out.print(SET_TEXT_COLOR_WHITE);
        }
    }

    private String getPieceSymbol(ChessBoard board, int rank, int file) {
        ChessPosition pos = new ChessPosition(rank, file);
        ChessPiece piece = board.getPiece(pos);

        if (piece == null) {
            return EMPTY;
        }

        boolean isWhite = piece.getTeamColor() == ChessGame.TeamColor.WHITE;

        return switch (piece.getPieceType()) {
            case KING -> isWhite ? WHITE_KING : BLACK_KING;
            case QUEEN -> isWhite ? WHITE_QUEEN : BLACK_QUEEN;
            case ROOK -> isWhite ? WHITE_ROOK : BLACK_ROOK;
            case BISHOP -> isWhite ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> isWhite ? WHITE_KNIGHT : BLACK_KNIGHT;
            case PAWN -> isWhite ? WHITE_PAWN : BLACK_PAWN;
        };
    }
}
