package dataaccess;

import model.UserData;

//UserData >> String username, String password, String email
public interface UserDAO {
    void createUser(UserData u) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;
}
