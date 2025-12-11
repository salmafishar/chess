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
import jakarta.websocket.*;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    ServerMessageHandler msgHandler;

    // unused
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    // connect
    public WebSocketFacade(String url, ServerMessageHandler msgHandler) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.msgHandler = msgHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            // setting a message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String string) {
                    ServerMessage message = new Gson().fromJson(string, ServerMessage.class);
                    msgHandler.notify(message);
                }
            });
        } catch (DeploymentException | URISyntaxException | IOException e) {
            throw new Exception("Failed to send WebSocket message: " + e.getMessage(), e);
        }
    }

    /*
    send messages TO sever:
    receives a UserGameCommand object
    builds a JSON string from it >> serializes it
    sends it to server through ws
     */
    public void sendCommands(UserGameCommand command) throws Exception {
        try {
            String json = new Gson().toJson(command);
            this.session.getBasicRemote().sendText(json);
        } catch (IOException e) {
            throw new Exception("Failed to send WebSocket message: " + e.getMessage(), e);
        }
    }
}
