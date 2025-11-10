package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.doas.AuthDAO;
import dataaccess.doas.GameDAO;
import dataaccess.doas.UserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;

import static dataaccess.DatabaseManager.createTables;

public class MySqlDataAccess implements DataAccess {
    private final UserDAO userDAO = new mySQLUserDAO();
    private final AuthDAO authDAO = new mySQLAuthDAO();
    private final GameDAO gameDAO = new mySQLGameDAO();
    private final String[] clearStatements = {
            "SET FOREIGN_KEY_CHECKS=0",
            "TRUNCATE TABLE auth",
            "TRUNCATE TABLE game",
            "TRUNCATE TABLE `user`",
            "SET FOREIGN_KEY_CHECKS=1"
    };

    public MySqlDataAccess() throws DataAccessException {
        createTables();
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.createStatement()) {
            for (String stmt : clearStatements) {
                statement.executeUpdate(stmt);
            }
            System.out.println("The tables have been cleared");
        } catch (SQLException ex) {
            throw new DataAccessException("failed to clear database", ex);
        }
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

    private static class mySQLUserDAO implements UserDAO {

        @Override
        public void createUser(UserData u) throws DataAccessException {
            String sql = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
            try (var conn = DatabaseManager.getConnection();
                 var ps = conn.prepareStatement(sql)) {
                ps.setString(1, u.username());
                ps.setString(2, u.password());
                ps.setString(3, u.email());
                ps.executeUpdate();
            } catch (java.sql.SQLIntegrityConstraintViolationException duplicate) {
                throw new DataAccessException("Username Already exists", duplicate);
            } catch (SQLException e) {
                throw new DataAccessException("Error creating user", e);
            }
        }

        @Override
        public UserData getUser(String username) throws DataAccessException {
            String sql = "SELECT username, password, email FROM user WHERE username = ?";
            try (var conn = DatabaseManager.getConnection();
                 var ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("email")
                        );
                    }
                }
            } catch (SQLException e) {
                throw new DataAccessException("failed to get user", e);
            }
            return null;
        }
    }

    private static class mySQLAuthDAO implements AuthDAO {
        @Override
        public void createAuth(AuthData a) throws DataAccessException {
            String sql = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
            try (var conn = DatabaseManager.getConnection();
                 var ps = conn.prepareStatement(sql)) {
                ps.setString(1, a.authToken());
                ps.setString(2, a.username());
                ps.executeUpdate();
            } catch (java.sql.SQLIntegrityConstraintViolationException duplicate) {
                throw new DataAccessException("Token already exists", duplicate);
            } catch (SQLException e) {
                throw new DataAccessException("failed to create auth", e);
            }
        }

        @Override
        public AuthData getAuth(String token) throws DataAccessException {
            String sql = "SELECT authToken, username FROM auth WHERE authToken = ?";
            try (var conn = DatabaseManager.getConnection();
                 var ps = conn.prepareStatement(sql)) {
                ps.setString(1, token);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(
                                rs.getString("authToken"),
                                rs.getString("username")
                        );
                    }
                    throw new DataAccessException("Unauthorized");
                }
            } catch (SQLException e) {
                throw new DataAccessException("failed to get token", e);
            }
        }

        @Override
        public void deleteAuth(String string) throws DataAccessException {
            String sql = "DELETE FROM auth WHERE authToken = ?";
            try (var conn = DatabaseManager.getConnection();
                 var ps = conn.prepareStatement(sql)) {
                ps.setString(1, string);
                var rows = ps.executeUpdate();
                if (rows == 0) {
                    throw new DataAccessException("Token is not deleted");
                }
            } catch (SQLException e) {
                throw new DataAccessException("failed to delete token", e);
            }
        }
    }

    private static class mySQLGameDAO implements GameDAO {
        @Override
        public int createGame(GameData g) throws DataAccessException {
            String sql = "INSERT INTO game (whiteUsername," +
                    " blackUsername, gameName, game) VALUES  (?, ?, ?, ?)";
            try (var conn = DatabaseManager.getConnection();
                 var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                if (g.whiteUsername() == null) {
                    ps.setNull(1, Types.VARCHAR);
                } else {
                    ps.setString(1, g.whiteUsername());
                }
                if (g.blackUsername() == null) {
                    ps.setNull(2, Types.VARCHAR);
                } else {
                    ps.setString(2, g.blackUsername());
                }
                ps.setString(3, g.gameName());
                var game = new Gson().toJson(new ChessGame());
                ps.setString(4, game);
                ps.executeUpdate();
                try (var rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
                throw new DataAccessException("failed to get generated gameID");
            } catch (SQLException e) {
                throw new DataAccessException("Failed to create game", e);
            }
        }

        @Override
        public GameData getGame(int gameID) throws DataAccessException {
            String sql = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game WHERE gameID = ?";
            try (var conn = DatabaseManager.getConnection();
                 var ps = conn.prepareStatement(sql)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt("gameID");
                        String white = rs.getString("whiteUsername");
                        String black = rs.getString("blackUsername");
                        String name = rs.getString("gameName");
                        return new GameData(id, white, black, name);
                    }
                    throw new DataAccessException("game not found");
                }
            } catch (SQLException e) {
                throw new DataAccessException("failed to get game", e);
            }
        }

        @Override
        public Collection<GameData> listGames() throws DataAccessException {
            final var sql = "SELECT gameID, whiteUsername, blackUsername, gameName FROM game";
            var out = new ArrayList<GameData>();
            try (var conn = DatabaseManager.getConnection();
                 var ps = conn.prepareStatement(sql);
                 var rs = ps.executeQuery()) {

                while (rs.next()) {
                    int id = rs.getInt("gameID");
                    String white = rs.getString("whiteUsername");
                    String black = rs.getString("blackUsername");
                    String name = rs.getString("gameName");
                    out.add(new GameData(id, white, black, name));
                }
                return out;
            } catch (SQLException e) {
                throw new DataAccessException("failed to list games", e);
            }
        }

        @Override
        public void updateGame(GameData g) throws DataAccessException {
            if (g == null) {
                throw new DataAccessException("null game data");
            }
            final var sql = """
                    UPDATE game
                    SET whiteUsername = ?, blackUsername = ?, gameName = ?
                    WHERE gameID = ?
                    """;
            try (var conn = DatabaseManager.getConnection();
                 var ps = conn.prepareStatement(sql)) {
                if (g.whiteUsername() == null) {
                    ps.setNull(1, Types.VARCHAR);
                } else {
                    ps.setString(1, g.whiteUsername());
                }
                if (g.blackUsername() == null) {
                    ps.setNull(2, Types.VARCHAR);
                } else {
                    ps.setString(2, g.blackUsername());
                }
                ps.setString(3, g.gameName());
                ps.setInt(4, g.gameID());
                int updated = ps.executeUpdate();
                if (updated == 0) {
                    throw new DataAccessException("game not found");
                }
            } catch (SQLException e) {
                throw new DataAccessException("failed to update game", e);
            }
        }
    }
}
