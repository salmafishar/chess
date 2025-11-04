package service;
/*
Each service method receives a Request object containing all the information it needs to do its work.
After performing its purpose, it returns a corresponding Result object containing the output of the method.
 */

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.GameData;
import service.requests.CreateGameRequest;
import service.requests.ListGameRequest;
import service.results.CreateGameResult;
import service.results.ListGamesResult;

import java.util.ArrayList;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    // Note that whiteUsername and blackUsername may be null.
    // request -> authToken
    // result -> java.util.Collection<model.GameData> games
    public ListGamesResult listGames(ListGameRequest request) throws DataAccessException {
        if (request.authToken() == null) {
            throw new DataAccessException("unauthorized");
        }
        dataAccess.getAuth(request.authToken());
        var allGames = dataAccess.listGames();
        var summaries = new ArrayList<GameData>();
        for (var g : allGames) {
            summaries.add(new GameData(g.gameID(), g.whiteUsername(), g.blackUsername(),
                    g.gameName()));
        }
        for (var s : summaries) {
            if (s == null) {
                throw new IllegalStateException("null game in summaries");
            }
        }
        return new ListGamesResult(summaries);
    }

    // request -> String authToken, String gameName
    // result -> int gameID
//    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException {
//        if (request == null || request.authToken() == null || request.gameName() == null) {
//            throw new DataAccessException("bad request");
//        }
//        var token = dataAccess.getAuth(request.authToken());
//        var name = dataAccess.getGame();
//        if (token == null) {
//            throw new DataAccessException("unauthorized");
//        }

    /// /        GameData gameData = dataAccess.game
    /// /        dataAccess.createGame(gameData);
//        return new CreateGameResult(gameData.gameID());
//    }
    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException {
        if (request.authToken() == null) {
            throw new DataAccessException("unauthorized");
        }
        if (dataAccess.getAuth(request.authToken()) == null) {
            throw new DataAccessException("unauthorized");
        }
        var name = request.gameName();
        if (name == null) {
            throw new DataAccessException("bad request");
        }
        int id = dataAccess.createGame(new GameData(0, null, null,
                name));
        return new CreateGameResult(id);
    }

}
