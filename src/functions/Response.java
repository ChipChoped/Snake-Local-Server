package functions;

import org.json.JSONObject;
import utils.UserIDToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.Socket;

import static routes.UserAPI.logOutPOST;
import static routes.UserAPI.loginPOST;

public class Response {
    @SuppressWarnings("unchecked")
    public static UserIDToken logIn(BufferedReader in, PrintStream out, JSONObject jsonRequest) throws IOException {
        utils.Response response = loginPOST((String) jsonRequest.get("username"), (String) jsonRequest.get("password"));
        JSONObject jsonResponse = new JSONObject();

        if (response.code() == HttpURLConnection.HTTP_OK) {
            jsonResponse.put("type", "return-log-in");
            jsonResponse.put("id", response.content().getInt("id"));
            out.println(jsonResponse);

            return new UserIDToken(response.content().getInt("id"), response.header());
        }
        else {
            jsonResponse.put("type", "error");
            jsonResponse.put("message", response.content().get("message"));
            out.println(jsonResponse);

            return new UserIDToken(-1, null);
        }
    }

    public static void logOut(PrintStream out, UserIDToken userIDToken) throws IOException {
        utils.Response response = logOutPOST(userIDToken);

        JSONObject jsonResponse = new JSONObject();

        if (response.code() == HttpURLConnection.HTTP_OK) {
            jsonResponse.put("type", "return-log-out");
            out.println(jsonResponse);
        }
        else {
            jsonResponse.put("type", "error");
            jsonResponse.put("message", response.content().get("message"));
            out.println(jsonResponse);
        }
    }
}
