package ui;


public interface ClientUI {
    String handle(String cmd, String[] params) throws Exception;
}
