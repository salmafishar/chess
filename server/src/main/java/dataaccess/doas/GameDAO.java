package dataaccess.doas;

import dataaccess.DataAccessException;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    int createGame(GameData g) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void updateGame(GameData g) throws DataAccessException;
}
