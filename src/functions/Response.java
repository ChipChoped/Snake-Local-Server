package functions;

import controllers.ControllerSnakeGame;
import model.SnakeGame;
import org.json.JSONArray;
import org.json.JSONObject;
import strategies.InteractiveStrategy;
import utils.Snake;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.util.Observable;
import java.util.Observer;

import static behaviors.Behaviors.NORMAL;
import static routes.GameAPI.saveGamePUT;
import static routes.UserAPI.logOutPOST;
import static routes.UserAPI.loginPOST;

public class Response {
    @SuppressWarnings("unchecked")
    public static int logIn(BufferedReader in, PrintStream out, JSONObject jsonRequest) throws IOException {
        utils.Response response = loginPOST((String) jsonRequest.get("username"), (String) jsonRequest.get("password"));
        JSONObject jsonResponse = new JSONObject();

        if (response.code() == HttpURLConnection.HTTP_OK) {
            jsonResponse.put("type", "return-log-in");
            jsonResponse.put("id", response.content().getInt("id"));
            out.println(jsonResponse);

            return response.content().getInt("id");
        }
        else {
            jsonResponse.put("type", "error");
            jsonResponse.put("message", response.content().get("message"));
            out.println(jsonResponse);

            return -1;
        }
    }

    public static boolean logOut(BufferedReader in, PrintStream out, int ID) throws IOException {
        utils.Response response = logOutPOST(ID);

        JSONObject jsonResponse = new JSONObject();

        if (response.code() == HttpURLConnection.HTTP_OK) {
            jsonResponse.put("type", "return-log-out");
            out.println(jsonResponse);

            return true;
        }
        else {
            jsonResponse.put("type", "error");
            jsonResponse.put("message", response.content().get("message"));
            out.println(jsonResponse);

            return false;
        }
    }

    public static boolean saveGame(BufferedReader in, PrintStream out, JSONObject jsonRequest) throws IOException {
        utils.Response response = saveGamePUT(jsonRequest.getInt("user-id"), jsonRequest.getBoolean("won"), jsonRequest.getInt("score"), (String) jsonRequest.get("date"));

        JSONObject jsonResponse = new JSONObject();

        if (response.code() == HttpURLConnection.HTTP_CREATED) {
            jsonResponse.put("type", "return-save-game");
            out.println(jsonResponse);

            return true;
        }
        else {
            jsonResponse.put("type", "error");
            jsonResponse.put("message", response.content().get("message"));
            out.println(jsonResponse);

            return false;
        }
    }
}
