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
}