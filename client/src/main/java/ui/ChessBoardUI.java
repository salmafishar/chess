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

        return switch (piece.getTeamColor()) {
            case WHITE -> getWhiteSymbol(piece);
            case BLACK -> getBlackSymbol(piece);
        };
    }

    private String getWhiteSymbol(ChessPiece piece) {
        return switch (piece.getPieceType()) {
            case KING -> WHITE_KING;
            case QUEEN -> WHITE_QUEEN;
            case ROOK -> WHITE_ROOK;
            case BISHOP -> WHITE_BISHOP;
            case KNIGHT -> WHITE_KNIGHT;
            case PAWN -> WHITE_PAWN;
        };
    }

    private String getBlackSymbol(ChessPiece piece) {
        return switch (piece.getPieceType()) {
            case KING -> BLACK_KING;
            case QUEEN -> BLACK_QUEEN;
            case ROOK -> BLACK_ROOK;
            case BISHOP -> BLACK_BISHOP;
            case KNIGHT -> BLACK_KNIGHT;
            case PAWN -> BLACK_PAWN;
        };
    }
}
