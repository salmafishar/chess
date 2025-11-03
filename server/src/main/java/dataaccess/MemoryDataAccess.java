package dataaccess;

import model.*;

import java.util.*;

public class MemoryDataAccess implements DataAccess {
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<String, AuthData> auths = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        users.clear();
        auths.clear();
        games.clear();
    }

    @Override
    public void createUser(UserData u) throws DataAccessException {
        if (users.containsKey(u.username())) {
            throw new DataAccessException("Username Already exists");
        }
        users.put(u.username(), u);
    }

    @Override
    public UserData getUser(UserData u) throws DataAccessException {
        return null;
    }

    @Override
    public void createGame(String username) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void updateGame(GameData g) throws DataAccessException {

    }

    @Override
    public void createAuth(AuthData token) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }
}
