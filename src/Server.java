import behaviors.NormalBehavior;
import functions.OnlineGameManager;
import functions.Response;
import model.InputMap;
import model.SnakeGame;
import org.json.JSONObject;
import strategies.InteractiveStrategy;
import utils.UserIDToken;

import java.net.*;
import java.io.*;

import static functions.Response.*;

public class Server extends Thread {
    final static int port = 9632;
    private final Socket socket;

    public static void main(String[] args) {
        try {
            ServerSocket socketServeur = new ServerSocket(port);
            System.out.println("Lancement du serveur");

            while (true) {
                Socket socketClient = socketServeur.accept();
                Server server = new Server(socketClient);
                server.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Server(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintStream out = new PrintStream(socket.getOutputStream());
            boolean connected = true;
            boolean online = false;
            JSONObject jsonRequest;
            UserIDToken userIDToken = new UserIDToken(-1, null);

            System.out.println(in.readLine());

            response:
            while (connected) {
                while (!online) {
                    jsonRequest = new JSONObject(in.readLine());

                    if (jsonRequest.get("type").equals("disconnect"))
                        break response;
                    else if (jsonRequest.get("type").equals("log-in")) {
                        userIDToken = logIn(in, out, jsonRequest);
                        online = userIDToken.ID() != -1;
                    }
                }

                jsonRequest = new JSONObject(in.readLine());
                System.out.println(userIDToken);

                switch ((String) jsonRequest.get("type")) {
                    case "start-game":
                        OnlineGameManager manager = new OnlineGameManager(socket);
                        manager.startGame(userIDToken);
                        break;
                    case "log-out":
                        logOut(socket, in, out, userIDToken);
                        online = false;
                        break;
                    case "disconnect":
                        connected = false;
                        break;
                }
            }

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}