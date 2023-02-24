import org.json.JSONObject;
import routes.UserAPI;
import utils.Response;

import java.net.*;
import java.io.*;

import static functions.LogIn.logIn;

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

            System.out.println(in.readLine());

            logIn(in, out);

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}