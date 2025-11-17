package ui;

/*
- preLogin UI: after registration, we automatically enter the signed in state. No need to log in.

- UI requirements: printing readable errors, make sure the code doesn't crash. Make sure to handle invalid inputs.
 */
public class PreLoginUI implements ClientUI {

    public PreLoginUI(ServerFacade server, Repl repl) {
    }

    @Override
    public String handle(String cmd, String[] params) {
        return "";
    }
}
