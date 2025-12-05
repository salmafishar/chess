package websocket;
/*
what the root client will do:
    1. Connect >> open the connection
    2. Send messages to the server: connect, make-move, leave, resign.
    Every command is JSON object that'll be turned to a string, sent though ws.
--
what the server sends:
    1. Load-game >> redraw board
    2. Notification >> displays messages and updates
    3. Send error messages
parse > switch > handle
--
When a user joins a game:
     1. Chooses game using ID
     2. client opens websocket
     3. client sends connect
     4. server responds >> load game: to client, notification: to everyone.
     5. gameplay loop: move, resign, leave, highlight, redraw. >> translated using ws.
     6. when a user moves:
        - server updates the game.
        - server sends load game to all.
        - server sends notification to everyone but mover
        - check/ checkmate.
--
Closing websocket:
    when user sends "leave":
        - server notifies others
        - connection gets closed.
        - move back to post-login UI

    when user sends "resign":
        - game ends
        - connection stays open
 */

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    ServerMessageHandler notificationHandler;

    // unused
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    // connect
    public WebSocketFacade(String url, ServerMessageHandler notificationHandler) throws DataAccessException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            // setting a message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String string) {
                    ServerMessageHandler notification = new Gson().fromJson(string, ServerMessageHandler.class);
                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | URISyntaxException | IOException e) {
            throw new DataAccessException(DataAccessException.Code.ServerError, e.getMessage());
        }
    }
}
