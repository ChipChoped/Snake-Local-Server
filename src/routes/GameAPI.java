package routes;

import org.json.JSONObject;
import utils.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GameAPI {
    private static final String URL = "http://localhost:8080/Snake/rest/game";

    public static Response saveGamePUT(int userID, Boolean won, int score, String date) throws IOException {
        java.net.URL obj = new URL(URL + "/save");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        String jsonInputString = "{\"user_id\": " + userID + ", \"won\": " + won + ", \"score\": " + score + ", \"date\": \"" + date + "\"}";

        try(OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = con.getResponseCode();
        String responseLine = null;
        JSONObject jsonResponse = new JSONObject();

        if (responseCode == HttpURLConnection.HTTP_CREATED) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                if (!response.isEmpty())
                    jsonResponse = new JSONObject(response.toString());

                return new Response(con.getResponseCode(), jsonResponse);
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

                return new Response(con.getResponseCode(), jsonResponse);
            }
        }

    }
}
