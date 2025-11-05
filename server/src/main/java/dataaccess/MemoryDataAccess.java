package dataaccess;

import model.*;

import java.util.*;

public class MemoryDataAccess implements DataAccess {
    private final Map<String, UserData> users = new HashMap<>();
    private final UserDAO userDAO = new MemoryUserDAO(users);
    private final Map<String, AuthData> auths = new HashMap<>();
    private final AuthDAO authDAO = new MemoryAuthDAO(auths);
    private final Map<Integer, GameData> games = new HashMap<>();
    private final GameDOA gameDOA = new MemoryGamehDAO(games);
    private static int nextGameId = 1;


    @Override
    public void clear() throws DataAccessException {
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
    public GameDOA games() {
        return gameDOA;
    }

    private static class MemoryUserDAO implements UserDAO {
        private final Map<String, UserData> store;

        MemoryUserDAO(Map<String, UserData> store) {
            this.store = store;
        }

        @Override
        public void createUser(UserData u) throws DataAccessException {
            if (store.containsKey(u.username())) {
                throw new DataAccessException("Username Already exists");
            }
            store.put(u.username(), u);
        }

        @Override
        public UserData getUser(String username) throws DataAccessException {
            return store.get(username);
        }
    }

    private static class MemoryAuthDAO implements AuthDAO {
        private final Map<String, AuthData> store;

        private MemoryAuthDAO(Map<String, AuthData> auth) {
            this.store = auth;
        }

        @Override
        public void createAuth(AuthData a) throws DataAccessException {
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
        public void deleteAuth(String token) throws DataAccessException {
            store.remove(token);
        }
    }

    private static class MemoryGamehDAO implements GameDOA {
        private final Map<Integer, GameData> store;

        private MemoryGamehDAO(Map<Integer, GameData> store) {
            this.store = store;
        }

        @Override
        public int createGame(GameData g) throws DataAccessException {
            int id = nextGameId++;
            var newGame = new GameData(id, g.whiteUsername(),
                    g.blackUsername(), g.gameName());
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
        public Collection<GameData> listGames() throws DataAccessException {
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
