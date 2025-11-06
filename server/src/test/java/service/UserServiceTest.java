package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.Test;
import service.requests.LoginRequest;
import service.requests.LogoutRequest;
import service.requests.RegisterRequest;

import static org.junit.jupiter.api.Assertions.*;

//tests call service methods then assert results/exceptions

class UserServiceTest {
    MemoryDataAccess dao;
    UserService user;


    @Test
    void clear() {
        dao = new MemoryDataAccess();
        user = new UserService(dao);
        dao.clear();
    }

    @Test
        // Positive Test
        // returns auth token
    void registerOk() throws DataAccessException {
        clear();
        var req = new RegisterRequest("sal", "21", "s@al21");
        var res = user.register(req);
        assertNotNull(res.authToken());
        assertEquals("sal", res.username());
    }

    @Test
    void registerNameDuplicate() throws DataAccessException {
        clear();
        user.register(new RegisterRequest("sal", "21", "s@al21"));
        DataAccessException ex = assertThrows(DataAccessException.class, () ->
                user.register(new RegisterRequest("sal", "21", "s@al21")));
        assertEquals("Username Already exists", ex.getMessage());
    }

    @Test
    void loginOk() throws DataAccessException {
        clear();
        var registerReq = new RegisterRequest("sal", "21", "s@al21");
        user.register(registerReq);
        var req = new LoginRequest("sal", "21");
        var res = user.login(req);
        assertNotNull(res.authToken());
        assertEquals("sal", res.username());
    }

    @Test
    void loginFailed() throws DataAccessException {
        clear();
        var registerReq = new RegisterRequest("sal", "21", "s@al21");
        user.register(registerReq);
        var req = new LoginRequest("sal", "21");
        user.login(req);
        DataAccessException ex = assertThrows(DataAccessException.class, () ->
                user.login(new LoginRequest("sl", "21")));
        assertEquals("unauthorized", ex.getMessage());
    }

    @Test
    void logoutOk() throws DataAccessException {
        clear();
        var registerReq = user.register(new RegisterRequest("sal", "21", "s@al21"));
        var token = registerReq.authToken();
        var logoutReq = new LogoutRequest(token);
        user.logout(logoutReq);
        var ex = assertThrows(DataAccessException.class, () ->
                user.logout(logoutReq));
        assertEquals("Unauthorized", ex.getMessage());
    }

    @Test
    void logoutFailed() throws DataAccessException {
        clear();
        var registerReq = user.register(new RegisterRequest("sal", "21", "s@al21"));
        var name = registerReq.username();
        var ex = assertThrows(DataAccessException.class, () ->
                user.logout(new LogoutRequest(name)));
        assertEquals("Unauthorized", ex.getMessage());
    }
}