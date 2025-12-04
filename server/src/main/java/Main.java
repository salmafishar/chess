import server.Server;

public class Main {
    static void main(String[] args) {
        Server server = new Server();
        server.run(8080);
        System.out.println("♕ 240 Chess Server");
    }
}