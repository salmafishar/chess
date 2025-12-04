package service;
/*
Each service method receives a Request object containing all the information it needs to do its work.
After performing its purpose, it returns a corresponding Result object containing the output of the method.
 */

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import results.LoginResult;
import results.LogoutResult;
import results.RegisterResult;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }


    //takes a name and a password, if it matches, returns a 200 response with username and authToken
    // if it doesn't ,match a name, 3 error messages
    public RegisterResult register(RegisterRequest req) throws DataAccessException {
        if (req == null || req.username() == null ||
                req.email() == null || req.password() == null) {
            throw new DataAccessException("bad request");
        }
        String hashedPassword = BCrypt.hashpw(req.password(), BCrypt.gensalt());
        dataAccess.users().createUser(new UserData(req.username(), hashedPassword, req.email()));
        String t = java.util.UUID.randomUUID().toString();
        dataAccess.auths().createAuth(new AuthData(t, req.username()));
        return new RegisterResult(req.username(), t);
    }


    public LoginResult login(LoginRequest req) throws DataAccessException {
        if (req == null || req.username() == null || req.password() == null) {
            throw new DataAccessException("bad request");
        }
        var u = dataAccess.users().getUser((req.username()));
        if (u == null) {
            throw new DataAccessException("unauthorized");
        }
        boolean comparePassword = BCrypt.checkpw(req.password(), u.password());

        if (!comparePassword) {
            throw new DataAccessException("unauthorized");
        }
        String t = java.util.UUID.randomUUID().toString();
        dataAccess.auths().createAuth(new AuthData(t, req.username()));
        return new LoginResult(req.username(), t);
    }

    public LogoutResult logout(LogoutRequest req) throws DataAccessException {
        if (req.authToken() == null) {
            throw new DataAccessException("unauthorized");
        }
        var auth = dataAccess.auths().getAuth(req.authToken());
        if (auth == null) {
            throw new DataAccessException("unauthorized");
        }
        dataAccess.auths().deleteAuth(req.authToken());
        return new LogoutResult();
    }
}
