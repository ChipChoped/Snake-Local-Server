package routes;

import org.json.JSONObject;
import utils.Response;
import utils.UserIDToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UserAPI {
    private static final String URL = "http://localhost:8080/Snake/rest/user";

    public static Response loginPOST(String username, String password) throws IOException {
        URL obj = new URL(URL + "/log-in");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        String jsonInputString = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";

        return getResponse(con, jsonInputString);
    }

    public static Response logOutPOST(UserIDToken userIDToken) throws IOException {
        URL obj = new URL(URL + "/log-out");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Authorization", userIDToken.token());
        con.setDoOutput(true);

        String jsonInputString = "{\"id\": " + userIDToken.ID() + "}";

        return getResponse(con, jsonInputString);
    }

    private static Response getResponse(HttpURLConnection con, String jsonInputString) throws IOException {
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = con.getResponseCode();
        String responseLine = null;
        JSONObject jsonResponse = new JSONObject();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                if (!response.isEmpty())
                    jsonResponse = new JSONObject(response.toString());

                return new Response(con.getResponseCode(), jsonResponse, con.getHeaderField("Authorization"));
            }
        }
        else {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                if (!response.isEmpty())
                    jsonResponse = new JSONObject(response.toString());

                return new Response(con.getResponseCode(), jsonResponse, null);
            }
        }
    }
}