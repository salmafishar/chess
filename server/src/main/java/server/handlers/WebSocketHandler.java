package server.handlers;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import io.javalin.websocket.*;
import model.GameData;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler, WsErrorHandler {

    private final GameService gameService;
    private final DataAccess dao;
    private final Gson gson = new Gson();

    // gameID >> list of websocket connections in that game
    private final Map<Integer, List<WsContext>> gameConnections = new ConcurrentHashMap<>();

    public WebSocketHandler(GameService gameService, DataAccess dao) {
        this.gameService = gameService;
        this.dao = dao;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("WS CONNECT " + ctx.sessionId());
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            System.out.println("WS MSG RAW: " + ctx.message());

            UserGameCommand cmd = gson.fromJson(ctx.message(), UserGameCommand.class);
            if (cmd == null || cmd.getCommandType() == null) {
                throw new RuntimeException("Invalid command from client");
            }

            switch (cmd.getCommandType()) {
                case CONNECT -> handleConnectCommand(ctx, cmd);
                case MAKE_MOVE -> handleMoveCommand(ctx, cmd);
                case LEAVE -> handleLeaveCommand(ctx, cmd);
                case RESIGN -> handleResignCommand(ctx, cmd);
            }

        } catch (Exception e) {
            System.out.println("WS onMessage ERROR: " + e.getMessage());
            ServerMessage err = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            err.setErrorMessage("SERVER ERROR: " + e.getMessage());
            ctx.send(gson.toJson(err));
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("WS CLOSE " + ctx.sessionId());
        removeCtxFromAllGames(ctx);
    }

    @Override
    public void handleError(WsErrorContext ctx) {
        System.out.println("WS ERROR: " + ctx.error());
    }

    private void handleConnectCommand(WsContext ctx, UserGameCommand cmd) {
        int gameID = cmd.getGameID();
        String auth = cmd.getAuthToken();

        try {
            GameData data = gameService.getGame(auth, gameID);

            var authData = dao.auths().getAuth(auth);
            String username = (authData != null) ? authData.username() : null;

            List<WsContext> list = gameConnections.get(gameID);
            if (list == null) {
                list = new ArrayList<>();
                gameConnections.put(gameID, list);
            }
            list.add(ctx);

            ServerMessage load = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            load.setGame(data);
            ctx.send(gson.toJson(load));

            ServerMessage note = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            note.setMessage((username != null ? username : "a player") + " joined the game.");
            broadcastExcept(gameID, note, ctx);

        } catch (Exception e) {
            ServerMessage err = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            err.setErrorMessage("CONNECT ERROR: " + e.getMessage());
            ctx.send(gson.toJson(err));
        }
    }

    private void handleMoveCommand(WsContext ctx, UserGameCommand cmd) {
        int gameID = cmd.getGameID();
        String auth = cmd.getAuthToken();

        try {
            gameService.makeMove(auth, gameID, cmd.getMove());

            GameData updated = gameService.getGame(auth, gameID);

            ServerMessage load = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            load.setGame(updated);
            broadcast(gameID, load);
            ServerMessage note = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            note.setMessage("A player made a move.");
            broadcastExcept(gameID, note, ctx);
            ChessGame game = updated.game();
            ChessGame.TeamColor toMove = game.getTeamTurn();
            ChessGame.TeamColor winner =
                    (toMove == ChessGame.TeamColor.WHITE)
                            ? ChessGame.TeamColor.BLACK
                            : ChessGame.TeamColor.WHITE;

            if (game.isInCheckmate(toMove)) {
                ServerMessage gameOver = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                gameOver.setMessage("Game over: " + winner + " wins by checkmate.");
                broadcast(gameID, gameOver);
            } else if (game.isInStalemate(toMove)) {
                ServerMessage gameOver = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
                gameOver.setMessage("Game over: stalemate.");
                broadcast(gameID, gameOver);
            }

        } catch (Exception e) {
            ServerMessage err = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            err.setErrorMessage("MOVE ERROR: " + e.getMessage());
            ctx.send(gson.toJson(err));
        }
    }

    private void handleResignCommand(WsContext ctx, UserGameCommand cmd) {
        int gameID = cmd.getGameID();
        String auth = cmd.getAuthToken();

        try {
            gameService.resign(auth, gameID);

            ServerMessage note = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            note.setMessage("A player resigned.");
            broadcast(gameID, note);

        } catch (Exception e) {
            ServerMessage err = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            err.setErrorMessage("RESIGN ERROR: " + e.getMessage());
            ctx.send(gson.toJson(err));
        }
    }

    private void handleLeaveCommand(WsContext ctx, UserGameCommand cmd) {
        int gameID = cmd.getGameID();

        removeCtxFromGame(gameID, ctx);

        ServerMessage note = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        note.setMessage("A player left the game.");
        broadcast(gameID, note);
    }

    private void broadcast(int gameID, ServerMessage msg) {
        String json = gson.toJson(msg);
        List<WsContext> list = gameConnections.getOrDefault(gameID, List.of());
        for (WsContext c : list) {
            c.send(json);
        }
    }

    private void broadcastExcept(int gameID, ServerMessage msg, WsContext exclude) {
        String json = gson.toJson(msg);
        List<WsContext> list = gameConnections.getOrDefault(gameID, List.of());
        for (WsContext c : list) {
            if (!c.equals(exclude)) {
                c.send(json);
            }
        }
    }

    private void removeCtxFromGame(int gameID, WsContext ctx) {
        List<WsContext> list = gameConnections.get(gameID);
        if (list != null) {
            list.remove(ctx);
            if (list.isEmpty()) {
                gameConnections.remove(gameID);
            }
        }
    }

    private void removeCtxFromAllGames(WsContext ctx) {
        for (List<WsContext> list : gameConnections.values()) {
            list.remove(ctx);
        }
    }
}
