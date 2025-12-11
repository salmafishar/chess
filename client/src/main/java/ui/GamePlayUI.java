package ui;

/*
    sends commands to sever through ws
    draw the current state of the chess board from the side the user is playing.

    Command >> Description
    Help >> Displays text informing the user what actions they can take.

    Redraw Chess Board >> Redraws the chess board upon the user’s request.

    Leave >> Removes the user from the game (whether they are playing or observing the game).
        The client transitions back to the Post-Login UI.

    Make Move >> Allow the user to input what move they want to make.
        The board is updated to reflect the result of the move,
        and the board automatically updates on all clients involved in the game.

    Resign >> Prompts the user to confirm they want to resign.
        If they do, the user forfeits the game and the game is over.
        Does not cause the user to leave the game.

    Highlight Legal Moves >> Allows the user to input the piece for which they want to highlight legal moves.
        The selected piece’s current square and all squares it can legally move to are highlighted.
        This is a local operation and has no effect on remote users’ screens.
 */

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import model.GameData;
import websocket.ServerMessageHandler;
import websocket.WebSocketFacade;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.HashSet;
import java.util.Set;

public class GamePlayUI implements ClientUI, ServerMessageHandler {
    private String authToken = null;
    private final WebSocketFacade ws;
    private ChessGame.TeamColor myColor;
    private ChessGame currentGame;
    private final int gameID;
    private boolean gameOver = false;

    /*
     store token, gameID, color and url
     create websocket
     connect
     */
    public GamePlayUI(String authToken, String serverUrl, int gameID, ChessGame.TeamColor myColor) throws Exception {
        this.authToken = authToken;
        this.myColor = myColor;
        this.gameID = gameID;
        this.ws = new WebSocketFacade(serverUrl, this);

        UserGameCommand cmd = new UserGameCommand(
                UserGameCommand.CommandType.CONNECT,
                authToken,
                gameID,
                null
        );
        ws.sendCommands(cmd);
    }


    @Override
    public String handle(String cmd, String[] params) {
        if (cmd.equalsIgnoreCase("help")) {
            return """
                    - move <from> <to>
                    - resign
                    - leave
                    - redraw
                    - highlight <square>
                    - help
                    """;
        }
        if (cmd.equalsIgnoreCase("redraw")) {
            return redrawChessBoard(params);
        }
        if (cmd.equalsIgnoreCase("leave")) {
            return leave(params);
        }
        if (cmd.equalsIgnoreCase("move")) {
            return makeMove(params);
        }
        if (cmd.equalsIgnoreCase("resign")) {
            return resign(params);
        }
        if (cmd.equalsIgnoreCase("highlight")) {
            return highlight(params);
        }
        return "Unknown command. Type 'help' to see available commands.";
    }

    private String redrawChessBoard(String[] params) {
        if (currentGame == null) {
            return "Game is not loaded. Try again";
        }
        ChessBoard board = currentGame.getBoard();
        new ChessBoardUI().drawBoard(myColor, board, System.out);
        return "Board has redrawn successfully.";
    }

    private String leave(String[] params) {
        try {
            UserGameCommand cmd = new UserGameCommand(
                    UserGameCommand.CommandType.LEAVE,
                    authToken,
                    gameID,
                    null
            );
            ws.sendCommands(cmd);
            return "left game";
        } catch (Exception ex) {
            return "error leaving game: " + ex.getMessage();
        }
    }

    private String makeMove(String[] params) {
        if (gameOver) {
            return "Game is over. No more moves can be made.";
        }
        if (currentGame == null) {
            return "game is not uploaded yet. please wait for the board to appear first.";
        }
        if (params.length != 2) {
            return "To make a move, use: move <from> <to>. example: move e3 e6.";
        }
        try {
            ChessPosition from = parseSquare(params[0]);
            ChessPosition to = parseSquare(params[1]);
            ChessMove move = new ChessMove(from, to, null); // no promotion yet.
            UserGameCommand cmd = new UserGameCommand(
                    UserGameCommand.CommandType.MAKE_MOVE,
                    authToken,
                    gameID,
                    move
            );
            ws.sendCommands(cmd);
            return "Successfully moved from " + params[0] + " to " + params[1];
        } catch (Exception e) {
            return "Error sending move: " + e.getMessage();
        }
    }

    private String resign(String[] params) {
        if (gameOver) {
            return "Game is already over.";
        }
        if (params.length == 0) {
            return """
                    This will forfeit the game and mark it as over.
                    If you're sure, type: resign confirm
                    """;
        }
        if (!params[0].equalsIgnoreCase("confirm")) {
            return "Resign cancelled.";
        }
        try {
            UserGameCommand cmd = new UserGameCommand(
                    UserGameCommand.CommandType.RESIGN,
                    authToken,
                    gameID,
                    null
            );
            ws.sendCommands(cmd);
            gameOver = true;
            return "You resigned. The game is now over.";
        } catch (Exception ex) {
            return "error resigning: " + ex.getMessage();
        }
    }

    private String positionToSquare(ChessPosition pos) {
        char col = (char) ('a' + pos.getColumn() - 1);
        char row = (char) ('0' + pos.getRow());
        return "" + col + row;
    }

    private String highlight(String[] params) {
        if (currentGame == null) {
            return "game is not uploaded yet. please wait for the board to appear first.";
        }
        if (params.length != 1) {
            return "To highlight legal moves, use: highlight <square>. example: highlight e2.";
        }
        try {
            ChessPosition from = parseSquare(params[0]);
            var piece = currentGame.getBoard().getPiece(from);
            if (piece == null) {
                return "There is no piece on " + params[0] + ".";
            }

            var moves = currentGame.validMoves(from);
            if (moves == null || moves.isEmpty()) {
                return "No legal moves available for the piece on " + params[0] + ".";
            }
            ChessBoard board = currentGame.getBoard();
            Set<ChessPosition> highlights = new HashSet<>();
            highlights.add(from);
            for (var m : moves) {
                highlights.add(m.getEndPosition());
            }
            new ChessBoardUI().drawBoard(myColor, board, highlights, System.out);
            StringBuilder sb = new StringBuilder();
            sb.append("Legal moves from ").append(params[0]).append(": ");
            boolean first = true;
            for (var m : moves) {
                if (!first) {
                    sb.append(", ");
                }
                first = false;
                sb.append(positionToSquare(m.getEndPosition()));
            }
            sb.append(".");
            return sb.toString();
        } catch (Exception e) {
            return "Error highlighting moves: " + e.getMessage();
        }
    }

    /*
    parsing moves: foe each position,
    convert each square file and rank to row and col
     */

    private ChessPosition parseSquare(String s) {
        if (s == null || s.length() != 2) {
            throw new IllegalArgumentException("Please make sure your square is valid");
        }
        char col = Character.toLowerCase(s.charAt(0));
        char row = s.charAt(1);

        if (col < 'a' || col > 'h') {
            throw new IllegalArgumentException("invalid column");
        }
        if (row < '1' || row > '8') {
            throw new IllegalArgumentException("invalid row");
        }
        int r = Character.getNumericValue(row);
        int c = col - 'a' + 1;
        return new ChessPosition(r, c);
    }
    /*
    Command >> Required Fields >> Description
    LOAD_GAME >> game (can be any type, just needs to be called game) >> Used by the server to send the current game state to a client.
                            When a client receives this message, it will redraw the chess board.
    ERROR >> String errorMessage >>This message is sent to a client when it sends an invalid command.
        The message must include the word Error.
    NOTIFICATION >> String message >> This is a message meant to inform a player when another player made an action.
     */

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                GameData game = message.getGame();
                this.currentGame = game.game();
                ChessBoard board = currentGame.getBoard();
                new ChessBoardUI().drawBoard(myColor, board, System.out);
            }
            case ERROR -> System.out.println(message.getErrorMessage());
            case NOTIFICATION -> {
                String msg = message.getMessage();
                System.out.println(msg);
            }
        }
    }
}
