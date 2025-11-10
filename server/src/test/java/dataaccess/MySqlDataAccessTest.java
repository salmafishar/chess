package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MySqlDataAccessTest {
    private MySqlDataAccess dao;

    @BeforeEach
    void setup() throws Exception {
        dao = new MySqlDataAccess();
        dao.clear();
    }

    @Test
    void createUserPass() throws Exception {
        dao.users().createUser(new UserData("salma", "231|-|", "s@woo.com"));
        var user = dao.users().getUser("salma");
        assertNotNull(user);
        assertEquals("salma", user.username());
    }

    @Test
    void createUserFailed() throws Exception {
        dao.users().createUser(new UserData("salma", "231|-|", "s@woo.com"));
        var ex = assertThrows(DataAccessException.class, () -> dao.users().createUser(new
                UserData("salma", "21|-?", "s@dho.com")));
        assertTrue(ex.getMessage().toLowerCase().contains("already"), "message: " + ex.getMessage());
    }

    @Test
    void getUserPass() throws Exception {
        dao.users().createUser(new UserData("woo", "pw", "woo@example.com"));
        var u = dao.users().getUser("woo");
        assertNotNull(u);
        assertEquals("woo", u.username());
    }

    @Test
    void getUserFail() throws Exception {
        var u = dao.users().getUser("ghost");
        assertNull(u);
    }

    @Test
    void createAuthPass() throws Exception {
        dao.users().createUser(new UserData("sal", "231|-|", "s@woo.com"));
        dao.auths().createAuth(new AuthData("te21", "sal"));
        var auth = dao.auths().getAuth("te21");
        assertNotNull(auth);
        assertEquals("sal", auth.username());
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
    void getAuthPass() throws Exception {
        dao.users().createUser(new UserData("sam", "pw", "sam@example.com"));
        dao.auths().createAuth(new AuthData("tok123", "sam"));
        var a = dao.auths().getAuth("tok123");
        assertNotNull(a);
        assertEquals("sam", a.username());
    }

    @Test
    void getAuthFail() throws Exception {
        dao.users().createUser(new UserData("sal", "231|-|", "s@woo.com"));
        dao.auths().createAuth(new AuthData("te21", "sal"));
        var ex = assertThrows(DataAccessException.class, () -> dao.auths().getAuth("te1"));
        assertEquals("Unauthorized", ex.getMessage());
    }

    @Test
    void deleteAuthPass() throws Exception {
        dao.users().createUser(new UserData("sal", "231|-|", "s@woo.com"));
        dao.auths().createAuth(new AuthData("te21", "sal"));
        dao.auths().deleteAuth("te21");
        var ex = assertThrows(DataAccessException.class, () -> dao.auths().getAuth("te21"));
        assertEquals("Unauthorized", ex.getMessage());
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
        int gameID = dao.games().createGame(new GameData(0,
                null, null, "game1"));
        assertTrue(gameID > 0);
    }

    @Test
    void createGameFail() throws Exception {
        dao.users().createUser(new UserData("sal", "231|-|", "s@woo.com"));
        var ex = assertThrows(DataAccessException.class, () ->
                dao.games().createGame(new GameData(0, "ghost", null, "g2")));
        assertEquals("Failed to create game", ex.getMessage());
    }

    @Test
    void getGamePass() throws Exception {
        int gameID = dao.games().createGame(new GameData(0,
                null, null, "game1"));
        GameData g = dao.games().getGame(gameID);
        assertEquals("game1", g.gameName());

    }

    @Test
    void getGameFail() {
        var ex = assertThrows(DataAccessException.class, () -> dao.games().getGame(9999));
        assertEquals("game not found", ex.getMessage());
    }

    @Test
    void listGamesPass() throws Exception {
        dao.games().createGame(new GameData(0,
                null, null, "game1"));
        dao.games().createGame(new GameData(0,
                null, null, "game2"));

        var games = dao.games().listGames();
        assertEquals(2, games.size());
    }

    @Test
    void listGamesFail() throws Exception {
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
        int id = dao.games().createGame(new GameData(0, null, null, "game1"));
        dao.users().createUser(new UserData("woo",
                "pw", "woo@example.com"));
        dao.games().updateGame(new GameData(id,
                "woo", null, "game1"));
        var g = dao.games().getGame(id);
        assertEquals("woo", g.whiteUsername());
    }

    @Test
    void updateGameFail() throws Exception {
        int id = dao.games().createGame(new GameData(0, null, null, "game1"));
        var ex = assertThrows(DataAccessException.class, () -> dao.games().updateGame(new GameData(id,
                "woo", null, "game1")));
        assertEquals("failed to update game", ex.getMessage());
    }
}