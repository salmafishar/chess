package dataaccess;

import model.*;

import java.util.*;

public class MemoryDataAccess implements DataAccess {
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<String, AuthData> auths = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextGameId = 1;

    @Override
    public void clear() throws DataAccessException {
        users.clear();
        auths.clear();
        games.clear();
        nextGameId = 1;
    }

    @Override
    public void createUser(UserData u) throws DataAccessException {
        if (users.containsKey(u.username())) {
            throw new DataAccessException("Username Already exists");
        }
        users.put(u.username(), u);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var user = users.get(username);
        if (user == null) {
            throw new DataAccessException("Username not found");
        }
        return user;
    }

    @Override
    public void createAuth(AuthData token) throws DataAccessException {
        auths.put(token.authToken(), token);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        var auth = auths.get(authToken);
        if (auth == null) {
            throw new DataAccessException("Unauthorized");
        }
        return auth;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        auths.remove(authToken);
    }

    @Override
    public int createGame(GameData game) throws DataAccessException {
        int id = nextGameId++;
        var newGame = new GameData(id, game.whiteUsername(),
                game.blackUsername(), game.gameName());
        games.put(id, newGame);
        return id;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        var game = games.get(gameID);
        if (game == null) {
            throw new DataAccessException("Game not found");
        }
        return game;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (!games.containsKey(game.gameID())) {
            throw new DataAccessException("Game not found");
        }
        games.put(game.gameID(), game);
    }
}
