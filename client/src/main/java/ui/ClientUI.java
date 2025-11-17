package ui;

import dataaccess.DataAccessException;

public interface ClientUI {
    String handle(String cmd, String[] params) throws DataAccessException;
}
