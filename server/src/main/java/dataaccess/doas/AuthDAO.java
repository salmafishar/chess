package dataaccess.doas;

import dataaccess.DataAccessException;
import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData a) throws DataAccessException;

    AuthData getAuth(String token) throws DataAccessException;

    void deleteAuth(String string) throws DataAccessException;
}
