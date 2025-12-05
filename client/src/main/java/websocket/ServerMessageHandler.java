package websocket;

import websocket.messages.ServerMessage;

// takes the message the server sends
public interface ServerMessageHandler {
    void notify(ServerMessage message);
}

