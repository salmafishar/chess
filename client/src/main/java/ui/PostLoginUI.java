package ui;

import chess.ChessGame;
import model.GameData;
import requests.ListRequest;

public class PostLoginUI implements ClientUI {
    private final ServerFacade server;
    private final Repl repl;
    private String authToken = null;
    private java.util.List<GameData> listGames;

    public PostLoginUI(ServerFacade server, Repl repl) {
        this.server = server;
        this.repl = repl;
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    @Override
    public String handle(String cmd, String[] params) throws Exception {

        if (cmd.equalsIgnoreCase("logout")) {
            return logout(params);
        }
        if (cmd.equalsIgnoreCase("quit")) {
            return "quit";
        }
        if (cmd.equalsIgnoreCase("create")) {
            return create(params);
        }
        if (cmd.equalsIgnoreCase("list")) {
            return list(params);
        }
        if (cmd.equalsIgnoreCase("join")) {
            return join(params);
        }
        if (cmd.equalsIgnoreCase("observe")) {
            return observe(params);
        }
        if (cmd.equalsIgnoreCase("help")) {
            return """
                    - create <GameName>
                    - list
                    - join <GameID> <WHITE | BLACK>
                    - observe <GameID>
                    - logout
                    - quit
                    - help
                    """;
        }
        return "Unknown command. Type 'help' to see available commands.";
    }

    // token
    public String logout(String[] params) {
        repl.switchToPreLogin();
        authToken = null;
        return "You are now logged out.";
    }

    // String authToken, String gameName
    public String create(String[] params) throws Exception {
        if (params.length != 1) {
            return "To create a game, please type in: create <GameName>";
        }
        String gameName = params[0];
        var create = server.create(authToken, gameName);
        return String.format("Now, you have created the game `%s`. The game ID is %d.\n" +
                "To join the game, please list all the games using `list`,\n then type in join <GameID> <WHITE | BLACK>", gameName, create.gameID());
    }

    // String authToken,
    public String list(String[] params) throws Exception {
        var listRequest = new ListRequest(authToken);
        var list = server.list(listRequest);
        var games = list.games();
        listGames = games;
        if (games == null || games.isEmpty()) {
            return "There were no games found";
        }
        int index = 1;
        var sb = new StringBuilder();
        for (var g : games) {
            sb.append(String.format(
                    "%d. %s (white: %s, black: %s)\n",
                    index++,
                    g.gameName(),
                    g.whiteUsername() == null ? "-" : g.whiteUsername(),
                    g.blackUsername() == null ? "-" : g.blackUsername()
            ));
        }
        return sb.toString();
    }

    // token, id ,color
    public String join(String[] params) throws Exception {
        if (params.length != 2) {
            return "To join a game, please type in: join <GameID> <WHITE | BLACK>";
        }
        if (listGames == null || listGames.isEmpty()) {
            return "You need to list your games first. To do so, type list";
        }
        GameData game;
        try {
            game = getGameByIndex(params[0]);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return e.getMessage();
        }
        int gameID = game.gameID();
        String color = params[1].toUpperCase();
        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            return "Color must be WHITE or BLACK. Example: join 1 white";
        }
        server.join(authToken, gameID, color);
        ChessGame.TeamColor myColor = color.equals("WHITE")
                ? ChessGame.TeamColor.WHITE
                : ChessGame.TeamColor.BLACK;
        String serverUrl = server.getServerURL();
        var gameplay = new GamePlayUI(authToken, serverUrl, gameID, myColor);
        repl.switchToGamePlay(gameplay);
        return "Entering game...";
    }

    public String observe(String[] params) throws Exception {
        if (params.length != 1) {
            return "To observe a game, please type in: observe <GameID>;";
        }
        if (listGames == null || listGames.isEmpty()) {
            return "You need to list your games first. To do so, type list";
        }
        GameData game;
        try {
            game = getGameByIndex(params[0]);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return e.getMessage();
        }
        int gameID = game.gameID();
        ChessGame.TeamColor observerColor = ChessGame.TeamColor.WHITE;
        String serverUrl = server.getServerURL();
        var gameplay = new GamePlayUI(authToken, serverUrl, gameID, observerColor);
        repl.switchToGamePlay(gameplay);
        return "Entering game as observer...";
    }

    private GameData getGameByIndex(String indexStr) {
        if (listGames == null || listGames.isEmpty()) {
            throw new IllegalStateException("You need to list your games first. To do so, type list");
        }

        int index;
        try {
            index = Integer.parseInt(indexStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The gameID must be an integer");
        }

        if (index < 1 || index > listGames.size()) {
            throw new IllegalArgumentException("Invalid gameID. Type list to see the available games");
        }

        return listGames.get(index - 1);
    }
}

