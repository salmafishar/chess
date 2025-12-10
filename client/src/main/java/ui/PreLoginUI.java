package ui;


import requests.LoginRequest;
import requests.RegisterRequest;

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
    public String handle(String cmd, String[] params) throws Exception {
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

    public String register(String[] params) throws Exception {
        if (params.length != 3) {
            return "To register, please type in: register <USERNAME> <PASSWORD> <EMAIL>";
        }
        String name = params[0];
        String password = params[1];
        String email = params[2];
        try {
            var registerRequest = new RegisterRequest(name, password, email);
            var register = server.register(registerRequest);
            repl.switchToPostLogin();
            repl.postLogin.setAuthToken(register.authToken());
            return String.format("You are now logged in. Your username is %s", register.username());
        } catch (Exception e) {
            String msg = e.getMessage().toLowerCase();
            if (msg.contains("already taken")) {
                return "register failed: that username is already taken.";
            }
            if (msg.contains("bad request")) {
                return "register failed: missing or invalid fields.";
            }
            return "register failed: " + msg;
        }
    }

    // saved username >> login tyjo 21pi 21pi@by.ed
    //register star star2 star2@gstar.com
    // register salma sal 21@.edu
    //register star2 star22 star@sr
    //salmaa salx yeah@yeah
    // name password
    public String login(String[] params) {
        if (params.length != 2) {
            return "To login, please type in: login <USERNAME> <PASSWORD>";
        }
        String name = params[0];
        String password = params[1];
        try {
            var loginRequest = new LoginRequest(name, password);
            var login = server.login(loginRequest);
            repl.switchToPostLogin();
            repl.postLogin.setAuthToken(login.authToken());
            repl.switchToPostLogin();
            return String.format("Welcome back %s.", login.username());
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg != null && msg.toLowerCase().contains("unauthorized")) {
                return "login failed: wrong username or password.";
            }
            return "login failed: " + msg;
        }
    }
}