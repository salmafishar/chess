package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.CreateRequest;
import requests.JoinRequest;
import requests.ListRequest;
import requests.RegisterRequest;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    MemoryDataAccess dao;
    GameService game;
    UserService user;

    @BeforeEach
    void clear() {
        dao = new MemoryDataAccess();
        user = new UserService(dao);
        game = new GameService(dao);
        dao.clear();
    }

    @Test
    void listGamesOk() throws DataAccessException {
        var registerReq = user.register(new RegisterRequest("sal", "21", "s@al21"));
        var token = registerReq.authToken();
        game.createGame(new CreateRequest(token, "game1"));
        var list = game.listGames(new ListRequest(token)).games();
        assertEquals(1, list.size());
    }

    @Test
    void listGamesFailed() throws DataAccessException {
        var registerReq = user.register(new RegisterRequest("sal", "21", "s@al21"));
        var token = registerReq.authToken();
        var name = registerReq.username();
        game.createGame(new CreateRequest(token, "game1"));
        game.createGame(new CreateRequest(token, "game2"));
        DataAccessException ex = assertThrows(DataAccessException.class, () ->
                game.listGames(new ListRequest(name)));
        assertEquals("Unauthorized", ex.getMessage());
    }

    @Test
    void createGameOK() throws DataAccessException {
        var registerReq = user.register(new RegisterRequest("sal", "21", "s@al21"));
        var token = registerReq.authToken();
        var list = game.listGames(new ListRequest(token)).games();
        assertEquals(0, list.size());
        for (var g : list) {
            assertNotNull(g);
        }
    }

    @Test
    void createGameFailed() {
        DataAccessException ex = assertThrows(DataAccessException.class, () ->
                game.createGame(new CreateRequest("", "")));
        assertEquals("Unauthorized", ex.getMessage());
    }

    @Test
    void joinGameOk() throws DataAccessException {
        var registerReq = user.register(new RegisterRequest("sal", "21", "s@al21"));
        var token = registerReq.authToken();
        var createReq = game.createGame(new CreateRequest(token, "game1"));
        var gameID = createReq.gameID();
        var req = new JoinRequest(token, gameID, "WHITE");
        game.joinGame(req);
        var list = game.listGames(new ListRequest(token)).games();
        GameData found = null;
        for (var g : list) {
            if (g.gameID() == gameID) {
                found = g;
                break;
            }
        }
        assertNotNull(found);
        assertEquals("sal", found.whiteUsername());
    }

    @Test
    void joinGameFailed() {
        DataAccessException ex = assertThrows(DataAccessException.class, () ->
                game.joinGame(new JoinRequest("", 0, "WHITE")));
        assertEquals("Unauthorized", ex.getMessage());
    }
}