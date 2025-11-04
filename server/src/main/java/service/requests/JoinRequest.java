package service.requests;

public record JoinRequest(String authToken, Integer gameID, String playerColor) {
}