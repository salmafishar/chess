package ui;

import com.google.gson.Gson;
import requests.*;
import results.*;

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
        var builder = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            builder.setHeader("Content-Type", "application/json");
        }
        return builder.build();
    }

    private HttpRequest buildRequestAuth(String method, String path, Object body, String token) {
        if (token == null || token.isBlank()) {
            return buildRequest(method, path, body);
        }

        var builder = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body))
                .setHeader("Authorization", token);

        if (body != null) {
            builder.setHeader("Content-Type", "application/json");
        }

        return builder.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request == null) {
            return BodyPublishers.noBody();
        }
        String json = new Gson().toJson(request);
        return BodyPublishers.ofString(json);
    }

    private HttpResponse<String> sendRequest(HttpRequest req) throws Exception {
        return client.send(req, BodyHandlers.ofString());
    }

    private <T> T handleResponse(HttpResponse<String> res, Class<T> resClass) throws Exception {
        int status = res.statusCode();

        if (!isSuccessful(status)) {
            String body = res.body();
            throw new Exception("Request failed (" + status + "): " + body);
        }
        if (resClass == null) {
            return null;
        }

        String body = res.body();
        return new Gson().fromJson(body, resClass);
    }


    public void clear() throws Exception {
        var req = buildRequest("DELETE", "/db", null);
        var res = sendRequest(req);
        handleResponse(res, null);
    }

    public RegisterResult register(RegisterRequest request) throws Exception {
        var req = buildRequest("POST", "/user", request);
        var res = sendRequest(req);
        return handleResponse(res, RegisterResult.class);
    }

    public LoginResult login(LoginRequest request) throws Exception {
        var req = buildRequest("POST", "/session", request);
        var res = sendRequest(req);
        return handleResponse(res, LoginResult.class);
    }

    public LogoutResult logout(String token) throws Exception {
        var req = buildRequestAuth("DELETE", "/session", null, token);
        var res = sendRequest(req);
        return handleResponse(res, LogoutResult.class);
    }

    public ListResult list(ListRequest request) throws Exception {
        var req = buildRequestAuth("GET", "/game", null, request.authToken());
        var res = sendRequest(req);
        return handleResponse(res, ListResult.class);
    }

    public CreateResult create(String token, String gameName) throws Exception {
        var body = new CreateRequest(token, gameName);
        var req = buildRequestAuth("POST", "/game", body, token);
        var res = sendRequest(req);
        return handleResponse(res, CreateResult.class);
    }

    public void join(String token, Integer gameID, String playerColor) throws Exception {
        var body = new JoinRequest(token, gameID, playerColor);
        var req = buildRequestAuth("PUT", "/game", body, token);
        var res = sendRequest(req);
        handleResponse(res, null);
    }

}