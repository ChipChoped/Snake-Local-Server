package functions;

import org.json.JSONObject;
import routes.UserAPI;
import utils.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;

import static routes.UserAPI.logOutPOST;
import static routes.UserAPI.loginPOST;

public class Logs {
    @SuppressWarnings("unchecked")
    public static boolean logIn(BufferedReader in, PrintStream out, JSONObject jsonRequest) throws IOException {
        Response response = loginPOST((String) jsonRequest.get("username"), (String) jsonRequest.get("password"));
        JSONObject jsonResponse = new JSONObject();

        if (response.code() == HttpURLConnection.HTTP_OK) {
            jsonResponse.put("type", "return-log-in");
            jsonResponse.put("id", response.content().getInt("id"));
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

    public static boolean logOut(BufferedReader in, PrintStream out, int ID) throws IOException {
        Response response = logOutPOST(ID);

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
}
