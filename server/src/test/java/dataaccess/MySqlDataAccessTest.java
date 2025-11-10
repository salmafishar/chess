package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

class MySqlDataAccessTest {

    @Test
    void clear() throws Exception {
//        var dao = new MySqlDataAccess();
//        dao.users().createUser(new UserData("sma", "21|-/", "wo@wo.com"));
//        dao.auths().createAuth(new AuthData("NowJS", "sma"));
//        dao.games().createGame(new GameData(21, "white", "black", "coolGame"));
//        dao.clear();
//        assertTrue(dao.games().listGames().isEmpty());
//        assertNull(dao.auths().getAuth("NowJS"));
//        assertNull(dao.users().getUser("sma"));
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

}