package ui;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.requests.*;
import service.results.*;

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
register, etc... directly communicate with HDP handlers

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

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder().
                uri(URI.create(serverUrl + path)).
                method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private HttpRequest buildRequestAuth(String method, String path, Object body, String token) {
        var request = HttpRequest.newBuilder().
                uri(URI.create(serverUrl + path)).method(method, makeRequestBody(body));
        if (token != null) {
            request.setHeader("Authorization", token);
        } else {
            return buildRequest(method, path, body);
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

    public LoginResult login(LoginRequest request) throws DataAccessException {
        var req = buildRequest("POST", "/session", request);
        var res = sendRequest(req);
        return handleResponse(res, LoginResult.class);
    }

    public LogoutResult logout(String token) throws DataAccessException {
        var req = buildRequestAuth("DELETE", "/session", null, token);
        var res = sendRequest(req);
        handleResponse(res, null);
        return new LogoutResult();
    }

    public ListResult list(ListRequest request) throws DataAccessException {
        var req = buildRequestAuth("GET", "/game", null, request.authToken());
        var res = sendRequest(req);
        return handleResponse(res, ListResult.class);
    }

    public CreateResult create(String token, String gameName) throws DataAccessException {
        CreateRequest body = new CreateRequest(token, gameName);
        var req = buildRequestAuth("POST", "/game", body, token);
        var res = sendRequest(req);
        return handleResponse(res, CreateResult.class);
    }

    public void join(String token, Integer gameID, String playerColor) throws DataAccessException {
        JoinRequest body = new JoinRequest(token, gameID, playerColor);
        var req = buildRequestAuth("PUT", "/game", body, token);
        var res = sendRequest(req);
        handleResponse(res, null);
    }

}