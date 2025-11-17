package ui;

/*
- preLogin UI: after registration, we automatically enter the signed in state. No need to log in.

- UI requirements: printing readable errors, make sure the code doesn't crash. Make sure to handle invalid inputs.
 */
public class PreLoginUI implements ClientUI {

    public PreLoginUI(ServerFacade server, Repl repl) {
    }

    @Override
    public String handle(String cmd, String[] params) throws DataAccessException {
        // name, password, email
        if (cmd.equalsIgnoreCase("register")) {
            return register(params);
        }
        if (cmd.equalsIgnoreCase("login")) {
            return "blah";
        }
        if (cmd.equalsIgnoreCase("quit")) {
            return "blah";
        }
        if (cmd.equalsIgnoreCase("help")) {
            return "blah";
        }
        return "";
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
        return String.format("You are now logged in. Your username is %s", register.username());
    }
}
