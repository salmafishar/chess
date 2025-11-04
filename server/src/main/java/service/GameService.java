package service;
/*
Each service method receives a Request object containing all the information it needs to do its work.
After performing its purpose, it returns a corresponding Result object containing the output of the method.
 */

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.GameData;
import service.results.*;
import service.requests.*;

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
        dataAccess.getAuth(request.authToken());
        var allGames = dataAccess.listGames();
        var summaries = new ArrayList<GameData>();
        for (var game : allGames) {
            summaries.add(new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(),
                    game.gameName()));
        }
        return new ListResult(summaries);
    }

    public CreateResult createGame(CreateRequest request) throws DataAccessException {
        if (dataAccess.getAuth(request.authToken()) == null) {
            throw new DataAccessException("unauthorized");
        }
        var name = request.gameName();
        if (name == null) {
            throw new DataAccessException("bad request");
        }
        int id = dataAccess.createGame(new GameData(0, null, null,
                name));
        return new CreateResult(id);
    }

    public JoinResult joinGame(JoinRequest request) throws DataAccessException {
        var auth = dataAccess.getAuth(request.authToken());
        var username = auth.username();
        if (request.gameID() == null) {
            throw new DataAccessException("bad request");
        }
        var g = dataAccess.getGame(request.gameID());
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
            g = new GameData(g.gameID(), username, g.blackUsername(), g.gameName());
        } else {
            if (g.blackUsername() != null) {
                throw new DataAccessException("already taken");
            }
            g = new GameData(g.gameID(), g.whiteUsername(), username, g.gameName());
        }
        dataAccess.updateGame(g);
        return new JoinResult();
    }
}
