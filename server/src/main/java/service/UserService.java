package service;
/*
Each service method receives a Request object containing all the information it needs to do its work.
After performing its purpose, it returns a corresponding Result object containing the output of the method.
 */

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.requests.*;
import service.results.*;

import java.util.UUID;


public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clear() throws DataAccessException {
        dataAccess.clear();
    }

    //takes a name and a password, if it matches, returns a 200 response with username and authToken
    // if it doesn't ,match a name, 3 error messages
    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        if (request == null || request.username() == null ||
                request.email() == null || request.password() == null) {
            throw new DataAccessException("bad request");
        }
        dataAccess.createUser(new UserData(request.username(), request.password(), request.email()));
        String token = java.util.UUID.randomUUID().toString();
        dataAccess.createAuth(new AuthData(token, request.username()));
        return new RegisterResult(request.username(), token);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        if (request == null || request.username() == null || request.password() == null) {
            throw new DataAccessException("bad request");
        }
        var user = dataAccess.getUser(request.username());
        if (!user.password().equals(request.password())) {
            throw new DataAccessException("unauthorized");
        }
        String token = java.util.UUID.randomUUID().toString();
        dataAccess.createAuth(new AuthData(token, request.username()));
        return new LoginResult(request.username(), token);
    }

    public void logout(LogoutRequest logoutRequest) {
    }
}
