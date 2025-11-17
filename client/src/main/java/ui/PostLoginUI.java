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
}

