package ui;

/*
- preLogin UI: after registration, we automatically enter the signed in state. No need to log in.

- UI requirements: printing readable errors, make sure the code doesn't crash. Make sure to handle invalid inputs.
 */
/*

When the user first opens your Chess client application they can execute any of the Prelogin commands.

Help	Displays text informing the user what actions they can take.
Quit	Exits the program.
Login	Prompts the user to input login information. Calls the server login API to login the user. When successfully logged in, the client should transition to the Postlogin UI.
Register	Prompts the user to input registration information. Calls the server register API to register and login the user. If successfully registered, the client should be logged in and transition to the Postlogin UI.

 */

import dataaccess.DataAccessException;
import service.requests.LoginRequest;
import service.requests.RegisterRequest;

/*
Things to test out:
1. # of args
2. types of args
3. rejected arg (like wrong password, wrong
 */
public class PreLoginUI implements ClientUI {
    private final ServerFacade server;
    private final Repl repl;

    public PreLoginUI(ServerFacade server, Repl repl) {
        this.server = server;
        this.repl = repl;
    }

    @Override
    public String handle(String cmd, String[] params) throws DataAccessException {
        // name, password, email
        if (cmd.equalsIgnoreCase("register")) {
            return register(params);
        }
        if (cmd.equalsIgnoreCase("login")) {
            return login(params);
        }
        if (cmd.equalsIgnoreCase("quit")) {
            return "quit";
        }
        if (cmd.equalsIgnoreCase("help")) {
            return """
                    - register <USERNAME> <PASSWORD> <EMAIL>
                    - login <USERNAME> <PASSWORD>
                    - quit
                    - help
                    """;
        }
        return "Unknown command. Type 'help' to see available commands.";
    }

    public String register(String[] params) throws DataAccessException {
        if (params.length != 3) {
            return "To register, please type in: register <USERNAME> <PASSWORD> <EMAIL>";
        }
        String name = params[0];
        String password = params[1];
        String email = params[2];
        var registerRequest = new RegisterRequest(name, password, email);
        var register = server.register(registerRequest);
        repl.switchToPostLogin();
        repl.postLogin.setAuthToken(register.authToken());
        return String.format("You are now logged in. Your username is %s", register.username());
    }
    // saved username >> login tyjo 21pi 21pi@by.ed

    // name password
    public String login(String[] params) throws DataAccessException {
        if (params.length != 2) {
            return "To login, please type in: login <USERNAME> <PASSWORD>";
        }
        String name = params[0];
        String password = params[1];
        var loginRequest = new LoginRequest(name, password);
        var login = server.login(loginRequest);
        repl.switchToPostLogin();
        repl.postLogin.setAuthToken(login.authToken());
        repl.switchToPostLogin();
        return String.format("Welcome back %s.", login.username());
    }
}
