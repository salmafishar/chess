package dataaccess;

public interface DataAccess {
    void clear() throws DataAccessException;

    UserDAO users();

    AuthDAO auths();

    GameDOA games();
}
