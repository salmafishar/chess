package ui;
/*
first thing you see is some help options listed
evaluate the command
print the output based on the command

3 REPLs:
1. pre-login
2. post
3. gameplay
as they exit the loop the go back to the previous ones.

separate the code for each loop

ui.repl:
prints out the prompt we want to give, reads the command the user gives by using Scanner
we go to a loop and stay in it until they write quit.


 */


import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final ServerFacade server;
    private final PreLoginUI preLogin;
    private final PostLoginUI postLogin;
    private UIMode state = UIMode.PreLogin;

    public Repl(String serverURL) {
        this.server = new ServerFacade(serverURL);
        this.preLogin = new PreLoginUI(server, this);
        this.postLogin = new PostLoginUI(server, this);
    }

    public void switchToPostLogin() {
        state = UIMode.PostLogin;
    }

    public void switchToPreLogin() {
        state = UIMode.PreLogin;
    }

    public void run() {
        System.out.println(SET_TEXT_BOLD + SET_TEXT_ITALIC + " ♛ Welcome to a fun visual Chess Game ♛ ");
        System.out.println(" Type help to get started and learn how to play.");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (true) {
            System.out.print("\n" + ERASE_LINE + ">>> " + SET_TEXT_COLOR_WHITE);
            String line = scanner.nextLine();
            result = eval(line);
            if ("quit".equalsIgnoreCase(result) || "q".equalsIgnoreCase(result)) {
                break;
            } else {
                System.out.print(SET_BG_COLOR_BLUE + result);
            }
        }
    }

    public String eval(String input) {

        ClientUI ui = (state == UIMode.PreLogin) ? preLogin : postLogin;
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (cmd.equals("quit") || cmd.equals("q")) {
                return "quit";
            }
            if ("help".equals(cmd)) {
                return help();
            }
            return ui.handle(cmd, params);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String help() {
        if (state == UIMode.PreLogin) {
            return """
                    - register <USERNAME> <PASSWORD> <EMAIL>
                    - login <USERNAME> <PASSWORD>
                    - quit
                    - help
                    """;
        }
        return """
                - create <GameName>
                - list
                - observe <GameID>
                - logout
                - quit
                - help
                """;
    }

    enum UIMode {
        PreLogin,
        PostLogin
    }
}
