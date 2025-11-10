package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

class MySqlDataAccessTest {

    @Test
    void clear() throws DataAccessException {
        var dao = new MySqlDataAccess();
        dao.clear();
        dao.users().createUser(new UserData("sa", "21|-/", "wo@wo.com"));
        dao.auths().createAuth(new AuthData("NoJS", "sa"));
        dao.games().createGame(new GameData(0, null, null, "coolGame"));
        dao.clear();
        assertTrue(dao.games().listGames().isEmpty());
        assertNull(dao.auths().getAuth("NoJS"));
        assertNull(dao.users().getUser("sa"));
    }

    @Test
    void createUser() throws Exception {
        var dao = new MySqlDataAccess();
        dao.clear();
        dao.users().createUser(new UserData("salma", "231|-|", "s@woo.com"));
        var user = dao.users().getUser("salma");
        assertNotNull(user);
        assertEquals("salma", user.username());
    }

    @Test
    void createUserFailed() throws Exception {
        var dao = new MySqlDataAccess();
        dao.clear();
        dao.users().createUser(new UserData("salma", "231|-|", "s@woo.com"));
        var ex = assertThrows(DataAccessException.class, () -> dao.users().createUser(new
                UserData("salma", "21|-?", "s@dho.com")));
        assertEquals("username already exists", ex.getMessage().toLowerCase());
    }

    @Test
    void createAuth() throws Exception {
        var dao = new MySqlDataAccess();
        dao.clear();
        dao.users().createUser(new UserData("sal", "231|-|", "s@woo.com"));
        dao.auths().createAuth(new AuthData("te21", "sal"));
        var auth = dao.auths().getAuth("te21");
        var user = dao.users().getUser("sal");
        assertNotNull(auth);
        assertEquals(user.username(), auth.username());
    }

    @Test
    void createAuthFailed() throws Exception {
        var dao = new MySqlDataAccess();
        dao.clear();
        dao.users().createUser(new UserData("sal", "231|-|", "s@woo.com"));
        dao.auths().createAuth(new AuthData("te21", "sal"));
        var ex = assertThrows(DataAccessException.class, () -> dao.auths().createAuth(
                new AuthData("te21", "sam")));
        assertEquals("Token already exists", ex.getMessage());

    }

    @Test
    void deleteAuthPass() throws Exception {
        var dao = new MySqlDataAccess();
        dao.clear();
        dao.users().createUser(new UserData("sal", "231|-|", "s@woo.com"));
        dao.auths().createAuth(new AuthData("te21", "sal"));
        dao.auths().deleteAuth("te21");
        var auth = dao.auths().getAuth("te21");
        assertNull(auth);
    }

    @Test
    void deleteAuthFail() throws Exception {
        var dao = new MySqlDataAccess();
        dao.clear();
        dao.users().createUser(new UserData("sal", "231|-|", "s@woo.com"));
        dao.auths().createAuth(new AuthData("te21", "sal"));
        var ex = assertThrows(DataAccessException.class, () -> dao.auths().deleteAuth("te2q"));
        assertEquals("Token is not deleted", ex.getMessage());
    }

    @Test
    void createGamePass() throws Exception {
        var dao = new MySqlDataAccess();
        dao.clear();
        int gameID = dao.games().createGame(new GameData(0,
                null, null, "game1"));
        assertTrue(gameID > 0);
    }

    @Test
    void createGameFail() throws Exception {
        var dao = new MySqlDataAccess();
        dao.clear();
        dao.users().createUser(new UserData("sal", "231|-|", "s@woo.com"));
        var ex = assertThrows(DataAccessException.class, () ->
                dao.games().createGame(new GameData(0, "ghost", null, "g2")));
        assertEquals("Failed to create game", ex.getMessage());
    }

    @Test
    void getGamePass() throws Exception {
        var dao = new MySqlDataAccess();
        dao.clear();
        int gameID = dao.games().createGame(new GameData(0,
                null, null, "game1"));
        GameData g = dao.games().getGame(gameID);
        assertEquals("game1", g.gameName());

    }

    @Test
    void getGameFail() throws Exception {
        var dao = new MySqlDataAccess();
        dao.clear();
        var ex = assertThrows(DataAccessException.class, () -> dao.games().getGame(9999));
        assertEquals("game not found", ex.getMessage());
    }

    @Test
    void listGamesPass() throws Exception {
        var dao = new MySqlDataAccess();
        dao.clear();
        dao.games().createGame(new GameData(0,
                null, null, "game1"));
        dao.games().createGame(new GameData(0,
                null, null, "game2"));

        var games = dao.games().listGames();
        assertEquals(2, games.size());
    }

    @Test
    void listGamesFail() throws Exception {
        var dao = new MySqlDataAccess();
        dao.clear();
        dao.games().createGame(new GameData(0,
                null, null, "game1"));
        var games = dao.games().listGames();
        assertEquals(1, games.size());
        var ex = assertThrows(DataAccessException.class, () -> dao.games().createGame(
                new GameData(0, "woo", null, "nope")));
        assertEquals("Failed to create game", ex.getMessage());
    }

    @Test
    void updateGamePass() throws Exception {
        var dao = new MySqlDataAccess();
        dao.clear();
        int id = dao.games().createGame(new GameData(0, null, null, "game1"));
        dao.users().createUser(new UserData("woo",
                "pw", "woo@example.com"));
        dao.games().updateGame(new GameData(id,
                "woo", null, "game1"));
        var g = dao.games().getGame(id);
        assertEquals(g.whiteUsername(), "woo");
    }

    @Test
    void updateGameFail() throws Exception {
        var dao = new MySqlDataAccess();
        dao.clear();
        int id = dao.games().createGame(new GameData(0, null, null, "game1"));
        var ex = assertThrows(DataAccessException.class, () -> dao.games().updateGame(new GameData(id,
                "woo", null, "game1")));
        assertEquals("failed to update game", ex.getMessage());
    }
}