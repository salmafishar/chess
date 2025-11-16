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

processing the command:
print prompt-read teh command - eval() the command, this will print have a result. we print this.

the client class:
tye one that knows how to execute the command.
have three client classed(pre,post and gameplay) each implements the ui.repl class.

 */


public class repl {
    private final ServerFacade server;

    public repl(String serverURL) {
        server = new ServerFacade(serverURL);
    }

}
