package dataaccess;

import dataaccess.doas.AuthDAO;
import dataaccess.doas.GameDAO;
import dataaccess.doas.UserDAO;

public interface DataAccess {
    void clear() throws DataAccessException;

    UserDAO users();

    AuthDAO auths();

    GameDAO games();
}
