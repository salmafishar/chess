package client;

import org.junit.jupiter.api.*;
import server.Server;
import requests.CreateRequest;
import requests.ListRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import ui.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    static ServerFacade facade;
    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);

    }

    /*
    write a test to clear database between each test, by using `@BeforeEach`
     */

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDB() throws Exception {
        facade.clear();
    }

    @Test
    public void registerSuccess() throws Exception {
        var req = new RegisterRequest("salma", "pass", "email@byu.edu");
        var result = facade.register(req);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.authToken());
        Assertions.assertEquals("salma", result.username());
    }

    @Test
    public void registerFail() throws Exception {
        var req = new RegisterRequest("salma", "pass", "email@byu.edu");
        facade.register(req);
        var req2 = new RegisterRequest("salma", "pass", "email2@byu.edu");
        assertThrows(Exception.class, () -> facade.register(req2));
    }

    @Test
    public void loginSuccess() throws Exception {
        var register = new RegisterRequest("salma", "pass", "email@byu.edu");
        facade.register(register);
        var req = new LoginRequest("salma", "pass");
        var result = facade.login(req);
        Assertions.assertEquals("salma", result.username());
    }

    @Test
    public void loginFail() {
        var req = new LoginRequest("salma", "pass");
        assertThrows(Exception.class, () -> facade.login(req));
    }

    @Test
    public void logoutSuccess() throws Exception {
        facade.register(new RegisterRequest("salma", "pass", "email@byu.edu"));
        var login = facade.login(new LoginRequest("salma", "pass"));
        var token = login.authToken();

        assertDoesNotThrow(() -> facade.logout(token));
    }

    @Test
    public void logoutFailInvalid() {
//        facade.register(new RegisterRequest("salma", "pass", "email@byu.edu"));
//        var login = facade.login(new LoginRequest("salma", "pass"));
//        var token = login.authToken();
        assertThrows(Exception.class, () -> facade.logout("token + 1"));
    }

    @Test
    public void listSuccess() throws Exception {
        facade.register(new RegisterRequest("salma", "pass", "email@byu.edu"));
        var login = facade.login(new LoginRequest("salma", "pass"));
        var token = login.authToken();
        var gameList = facade.list(new ListRequest(token));
        assertTrue(gameList.games().isEmpty());
    }

    @Test
    public void listFail() {
        assertThrows(Exception.class, () -> facade.list(new ListRequest("fakeToken")));
    }

    @Test
    public void createSuccess() throws Exception {
        facade.register(new RegisterRequest("salma", "pass", "email@byu.edu"));
        var login = facade.login(new LoginRequest("salma", "pass"));
        var token = login.authToken();

        var createReq = new CreateRequest(token, "game1");
        facade.create(token, createReq.gameName());
        var gameList = facade.list(new ListRequest(token));
        assertEquals(1, gameList.games().size());
    }

    @Test
    public void createFail() {
        assertThrows(Exception.class, () -> facade.create("fake", "blank"));
    }

    @Test
    public void joinSuccess() throws Exception {
        facade.register(new RegisterRequest("salma", "pass", "email@byu.edu"));
        var login = facade.login(new LoginRequest("salma", "pass"));
        var token = login.authToken();

        var createReq = new CreateRequest(token, "game1");
        var createRes = facade.create(token, createReq.gameName());
        facade.join(token, createRes.gameID(), "white");
        var gameList = facade.list(new ListRequest(token));
        Assertions.assertEquals("salma", gameList.games().getFirst().whiteUsername());
    }

    @Test
    public void joinFail() throws Exception {
        facade.register(new RegisterRequest("salma", "pass", "email@byu.edu"));
        var login = facade.login(new LoginRequest("salma", "pass"));
        var token = login.authToken();

        var createReq = new CreateRequest(token, "game1");
        var createRes = facade.create(token, createReq.gameName());
        assertThrows(Exception.class, () -> facade.join(token + 1, createRes.gameID(), "white"));
    }
}
