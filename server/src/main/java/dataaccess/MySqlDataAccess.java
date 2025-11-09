package dataaccess;

import dataaccess.doas.AuthDAO;
import dataaccess.doas.GameDAO;
import dataaccess.doas.UserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static dataaccess.DatabaseManager.createTables;

public class MySqlDataAccess implements DataAccess {
    private final UserDAO userDAO = new mySQLUserDAO();
    private final AuthDAO authDAO = new mySQLAuthDAO();
    private final GameDAO gameDAO = new mySQLGameDAO();

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
                }
            } catch (SQLException e) {
                throw new DataAccessException("failed to get token", e);
            }
            return null;
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
            return 0;
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
    }

    public MySqlDataAccess() throws DataAccessException {
        createTables();
    }

    private final String[] clearStatements = {
            "SET FOREIGN_KEY_CHECKS=0",
            "TRUNCATE TABLE auth",
            "TRUNCATE TABLE game",
            "TRUNCATE TABLE `user`",
            "SET FOREIGN_KEY_CHECKS=1"
    };

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
}
