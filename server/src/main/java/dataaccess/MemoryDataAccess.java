package dataaccess;

import dataaccess.doas.AuthDAO;
import dataaccess.doas.GameDAO;
import dataaccess.doas.UserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    private static int nextGameId = 1;
    private final Map<String, UserData> users = new HashMap<>();
    private final UserDAO userDAO = new MemoryUserDAO(users);
    private final Map<String, AuthData> auths = new HashMap<>();
    private final AuthDAO authDAO = new MemoryAuthDAO(auths);
    private final Map<Integer, GameData> games = new HashMap<>();
    private final GameDAO gameDAO = new MemoryGameDAO(games);

    @Override
    public void clear() {
        users.clear();
        auths.clear();
        games.clear();
        nextGameId = 1;
    }

    @Override
    public UserDAO users() {
        return userDAO;
    }

    @Override
    public AuthDAO auths() {
        return authDAO;
    }

    @Override
    public GameDAO games() {
        return gameDAO;
    }

    private record MemoryUserDAO(Map<String, UserData> store) implements UserDAO {

        @Override
        public void createUser(UserData u) throws DataAccessException {
            if (store.containsKey(u.username())) {
                throw new DataAccessException("Username Already exists");
            }
            store.put(u.username(), u);
        }

        @Override
        public UserData getUser(String username) {
            return store.get(username);
        }
    }

    private record MemoryAuthDAO(Map<String, AuthData> store) implements AuthDAO {

        @Override
        public void createAuth(AuthData a) {
            store.put(a.authToken(), a);
        }

        @Override
        public AuthData getAuth(String token) throws DataAccessException {
            var auth = store.get(token);
            if (auth == null) {
                throw new DataAccessException("Unauthorized");
            }
            return store.get(token);
        }

        @Override
        public void deleteAuth(String token) {
            store.remove(token);
        }
    }

    private record MemoryGameDAO(Map<Integer, GameData> store) implements GameDAO {

        @Override
        public int createGame(GameData g) {
            int id = nextGameId++;
            var newGame = new GameData(id, g.whiteUsername(),
                    g.blackUsername(), g.gameName(), g.game());
            store.put(id, newGame);
            return id;
        }

        @Override
        public GameData getGame(int gameID) throws DataAccessException {
            var game = store.get(gameID);
            if (game == null) {
                throw new DataAccessException("Game not found");
            }
            return game;
        }

        @Override
        public Collection<GameData> listGames() {
            var out = new ArrayList<GameData>();
            for (var g : store.values()) {
                if (g != null) {
                    out.add(g);
                }
            }
            return out;
        }

        @Override
        public void updateGame(GameData g) throws DataAccessException {
            if (!store.containsKey(g.gameID())) {
                throw new DataAccessException("Game not found");
            }
            store.put(g.gameID(), g);
        }
    }

}
