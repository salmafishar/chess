package ui;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.requests.RegisterRequest;
import service.results.RegisterResult;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;


/*

call the APIs implemented in the server
mirror the api on the server,
register.. directly communicate with HDP handlers

 it needs to know the host name and port number
 has build request, send request, handle response
 start with ServerFacade and make sure its running, then continue phase 5

 */
public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }


    /*
      request: name, pass ,email
      result: name, token
      URL path  -> 	/user
      HTTP Method ->  POST
     */
    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        var req = buildRequest("POST", "/user", request);
        var res = sendRequest(req);
        return handleResponse(res, RegisterResult.class);
    }


    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder().
                uri(URI.create(serverUrl + path)).
                method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest req) throws DataAccessException {
        try {
            return client.send(req, BodyHandlers.ofString());
        } catch (Exception e) {
            throw new DataAccessException(DataAccessException.Code.ServerError, e.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> res, Class<T> resClass) throws DataAccessException {
        var status = res.statusCode();
        if (!isSuccessful(status)) {
            var body = res.body();
            if (body != null && !body.isBlank()) {
                try {
                    throw DataAccessException.fromJson(body);
                } catch (DataAccessException e) {
                    throw e;
                } catch (Exception ex) {
                    throw new DataAccessException(
                            DataAccessException.fromHttpStatusCode(status),
                            "Request failed (" + status + "): " + body
                    );
                }
            }
        }
        if (resClass != null) {
            return new Gson().fromJson(res.body(), resClass);
        }
        return null;
    }

    public void clear() throws DataAccessException {
        var req = buildRequest("DELETE", "/db", null);
        var res = sendRequest(req);
        handleResponse(res, null);
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        var req = buildRequest("POST", "/user", request);
        var res = sendRequest(req);
        return handleResponse(res, RegisterResult.class);
    }

    /*
    request: name, pass
    result: name, token
    URL path	-> /session
    HTTP Method ->	POST

     */
    public LoginRequest login(LoginRequest request) throws DataAccessException {
        var req = buildRequest("POST", "/session", request);
        var res = sendRequest(req);
        return handleResponse(res, LoginRequest.class);
    }

    /*
    request: token
    result: {}
    URL path	-> /session
    HTTP Method ->	DELETE
     */
    public LogoutResult logout(String token) throws DataAccessException {
        var req = buildRequestAuth("DELETE", "/session", null, token);
        var res = sendRequest(req);
        handleResponse(res, null);
        return new LogoutResult();
    }
    /*
    request: token
    result: List<GameData> games
    URL path	-> /game
    HTTP Method ->	GET
     */

    public ListResult list(ListRequest request) throws DataAccessException {
        var req = buildRequestAuth("GET", "/game", null, request.authToken());
        var res = sendRequest(req);
        return handleResponse(res, ListRequest.class);
    }

    /*
    request: token, gameName
    result: gameID
    URL path	-> /game
    HTTP Method ->	POST
     */
    public CreateRequest create(CreateRequest request) throws DataAccessException {
        var req = buildRequest("POST", "/game", request);
        var res = sendRequest(req);
        return handleResponse(res, CreateRequest.class);
    }

    /*
    request: token, id ,color
    result:
    URL path	-> /game
    HTTP Method ->	PUT
     */
    public JoinRequest join(JoinRequest request) throws DataAccessException {
        var req = buildRequest("PUT", "/game", request);
        var res = sendRequest(req);
        return handleResponse(res, JoinRequest.class);
    }

}

/*
- preLogin UI: after registration, we automatically enter the signed in state. No need to log in.
- postLogin UI: make listGame numbering dependant of the game IDs
- gamePlay UI: make sure the board is printed correctly.
- UI requirements: printing readable errors, make sure the code doesn't crash. Make sure to handle invalid inputs.
 */