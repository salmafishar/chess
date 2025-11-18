package ui;

/*
- postLogin UI: make listGame numbering dependant of the game IDs
- gamePlay UI: make sure the board is printed correctly.
- UI requirements: printing readable errors, make sure the code doesn't crash. Make sure to handle invalid inputs.

 */
public class PostLoginUI implements ClientUI {
    private final ServerFacade server;
    private final Repl repl;

    public PostLoginUI(ServerFacade server, Repl repl) {
        this.server = server;
        this.repl = repl;
    }

    @Override
    public String handle(String cmd, String[] params) throws DataAccessException {

        if (cmd.equalsIgnoreCase("logout")) {
            return logout(params);
        }
        if (cmd.equalsIgnoreCase("quit")) {
            return "quit";
        }
        if (cmd.equalsIgnoreCase("help")) {
            return """
                    - create <GameName>
                    - list
                    - observe <GameID>
                    - logout
                    - quit
                    - help
                    """;
        }
        return "Unknown command. Type 'help' to see available commands.";
    }

    // token
    public String logout(String[] params) throws DataAccessException {
        var logout = server.logout(authToken);
        repl.switchToPreLogin();
        authToken = null;
        return "You are now logged out.";
    }

    // String authToken, String gameName
    public String create(String[] params) throws DataAccessException {
        if (params.length != 1) {
            return "To create a game, please type in: create <GameName>";
        }
        String gameName = params[0];
        var create = server.create(authToken, gameName);
        return String.format("Now, you have created the game %s. The game ID is %d.\n" +
                "To join the game, please type in join <GameID> <WHITE | BLACK>", gameName, create.gameID());


    }

    // String authToken,
    public String list(String[] params) throws DataAccessException {
        var listRequest = new ListRequest(authToken);
        var list = server.list(listRequest);
        var games = list.games();
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
    public String join(String[] params) throws DataAccessException {
        if (params.length != 2) {
            return "To join a game, please type in: join <GameID> <WHITE | BLACK>";
        }
        if (listGames == null || listGames.isEmpty()) {
            return "You need to list your games first. To do so, type list";
        }
        int index;
        try {
            index = Integer.parseInt(params[0]);
        } catch (Exception e) {
            return "The gameID must be an integer";
        }
        if (index < 1 || index > listGames.size()) {
            return "Invalid gameID. Type list to see the available games";
        }
        var game = listGames.get(index - 1);
        int gameID = game.gameID();
        String color = params[1].toUpperCase();
        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            return "Color must be WHITE or BLACK. Example: join 1 white";
        }
        server.join(authToken, gameID, color);
        return String.format("You joined %s as %s", game.gameName(), color.toLowerCase());
    }

    public String observe(String[] params) throws DataAccessException {
        if (params.length != 1) {
            return "To observe a game, please type in: observe <GameID>;";
        }
        if (listGames == null || listGames.isEmpty()) {
            return "You need to list your games first. To do so, type list";
        }
        int index;
        try {
            index = Integer.parseInt(params[0]);
        } catch (Exception e) {
            return "The gameID must be an integer";
        }
        if (index < 1 || index > listGames.size()) {
            return "Invalid gameID. Type list to see the available games";
        }
        var game = listGames.get(index - 1);
        int gameID = game.gameID();

        server.join(authToken, gameID, null);
        return String.format("You now are observing %s ", game.gameName());
    }
}

