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

import chess.ChessGame;
import dataaccess.DataAccessException;
import websocket.ServerMessageHandler;
import websocket.WebSocketFacade;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

public class GamePlayUI implements ClientUI, ServerMessageHandler {
    private String authToken = null;
    private final WebSocketFacade ws;
    private ChessGame.TeamColor myColor;
    private ChessGame currentGame;
    private final int gameID;
    private final String serverUrl;

    /*
     store token, gameID, color and url
     create websocket
     connect
     */
    public GamePlayUI(String authToken, String serverUrl, int gameID, ChessGame.TeamColor myColor) throws DataAccessException {
        this.authToken = authToken;
        this.myColor = myColor;
        this.gameID = gameID;
        this.serverUrl = serverUrl;
        this.ws = new WebSocketFacade(serverUrl, this);

        UserGameCommand cmd = new UserGameCommand(
                UserGameCommand.CommandType.CONNECT, authToken, gameID
        );
        ws.SendCommands(cmd);
    }


    @Override
    public String handle(String cmd, String[] params) throws Exception {
        if (cmd.equalsIgnoreCase("help")) {
            return """
                    - move <from> <to>
                    - resign
                    - leave
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
        return null;
    }

    private String leave(String[] params) {
        return null;
    }

    private String makeMove(String[] params) {
        return null;
    }

    private String resign(String[] params) {
        return null;
    }

    private String highlight(String[] params) {
        return null;
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
        if (row < '1' || row > '1') {
            throw new IllegalArgumentException("invalid row");
        }
        int r = Character.getNumericValue(row);
        int c = col - 'a' + 1;
        return new ChessPosition(r, c);
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                GameData game = message.getGame();
                this.currentGame = game.game();
                ChessBoard board = currentGame.getBoard();
                new ChessBoardUI().drawBoard(myColor, board, System.out);
            }
            case ERROR -> System.out.println(message.getError());
            case NOTIFICATION -> {
                String msg = message.getMessage();
                System.out.println(msg);
            }
        }
    }
}
