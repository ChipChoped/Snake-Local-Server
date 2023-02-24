package functions;

import org.json.JSONObject;
import routes.UserAPI;
import utils.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;

public class LogIn {
    @SuppressWarnings("unchecked")
    public static int logIn(BufferedReader in, PrintStream out) throws IOException {
        String type = "";
        JSONObject jsonRequest = null;

        while (!type.equals("logins")) {
            String output = in.readLine();
            jsonRequest = new JSONObject(output);
            type = (String) jsonRequest.get("type");
        }

        Response response = UserAPI.loginPOST((String) jsonRequest.get("username"), (String) jsonRequest.get("password"));
        JSONObject jsonResponce = new JSONObject();

        if (response.code() == HttpURLConnection.HTTP_OK) {
            jsonResponce.put("type", "id");
            jsonResponce.put("id", response.content().getInt("id"));
            out.println(jsonResponce);

            return (int) response.content().get("id");
        }
        else {
            jsonResponce.put("type", "error");
            jsonResponce.put("message", response.content().get("message"));
            out.println(jsonResponce);

            return logIn(in, out);
        }
    }
}
