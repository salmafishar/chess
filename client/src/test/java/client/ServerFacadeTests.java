package client;

import org.junit.jupiter.api.*;
import server.Server;
import service.requests.RegisterRequest;
import ui.ServerFacade;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

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
        var result = facade.register(req);
        var req2 = new RegisterRequest("salma", "pass", "email2@byu.edu");
        assertThrows(Exception.class, () -> facade.register(req2));
    }
}
