package service;
/*
Each service method receives a Request object containing all the information it needs to do its work.
After performing its purpose, it returns a corresponding Result object containing the output of the method.
 */

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.GameData;
import requests.CreateRequest;
import requests.JoinRequest;
import requests.ListRequest;
import results.CreateResult;
import results.JoinResult;
import results.ListResult;

import java.util.ArrayList;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    // Note that whiteUsername and blackUsername may be null.
    // request -> authToken
    // result -> java.util.Collection<model.GameData> games
    public ListResult listGames(ListRequest request) throws DataAccessException {
        dataAccess.auths().getAuth(request.authToken());
        var allGames = dataAccess.games().listGames();
        var summaries = new ArrayList<GameData>();
        for (var game : allGames) {
            summaries.add(new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(),
                    game.gameName(), game.game()));
        }
        return new ListResult(summaries);
    }

    public CreateResult createGame(CreateRequest request) throws DataAccessException {
        if (dataAccess.auths().getAuth(request.authToken()) == null) {
            throw new DataAccessException("unauthorized");
        }
        var name = request.gameName();
        if (name == null) {
            throw new DataAccessException("bad request");
        }
        int id = dataAccess.games().createGame(new GameData(0, null, null,
                name, new ChessGame()));
        return new CreateResult(id);
    }

    public void joinGame(JoinRequest request) throws DataAccessException {
        if (request == null) {
            throw new DataAccessException("bad request");
        }
        var t = request.authToken();
        if (t == null) {
            throw new DataAccessException("unauthorized");
        }
        var auth = dataAccess.auths().getAuth(request.authToken());
        if (auth == null) {
            throw new DataAccessException("unauthorized");
        }
        var username = auth.username();
        if (request.gameID() == null) {
            throw new DataAccessException("bad request");
        }
        var g = dataAccess.games().getGame(request.gameID());
        if (g == null) {
            throw new DataAccessException("bad request");
        }
        String color = request.playerColor();
        if (color == null) {

            throw new DataAccessException("bad request");
        }
        color = color.toUpperCase();
        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            throw new DataAccessException("bad request");
        }
        if (color.equals("WHITE")) {
            g = new GameData(g.gameID(), username, g.blackUsername(), g.gameName(), g.game());
        } else {
            if (g.blackUsername() != null) {
                throw new DataAccessException("already taken");
            }
            g = new GameData(g.gameID(), g.whiteUsername(), username, g.gameName(), g.game());
        }
        dataAccess.games().updateGame(g);
        new JoinResult();
    }
}
