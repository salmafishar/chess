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

public class WebSocketFacade {
}
