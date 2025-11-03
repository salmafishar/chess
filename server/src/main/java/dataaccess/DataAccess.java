package dataaccess;

import model.*;

import java.util.Collection;

public interface DataAccess {
    void clear() throws DataAccessException;

    void createUser(UserData u) throws DataAccessException;

    UserData getUser(UserData u) throws DataAccessException;

    void createGame(String username) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void updateGame(GameData g) throws DataAccessException;

    void createAuth(AuthData token) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;
}
