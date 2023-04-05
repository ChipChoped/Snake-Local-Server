package functions;

import behaviors.Behavior;
import behaviors.Behaviors;
import controllers.ControllerSnakeGame;
import model.SnakeGame;
import org.json.JSONArray;
import org.json.JSONObject;
import strategies.InteractiveStrategy;
import utils.AgentAction;
import utils.Snake;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

import static java.time.LocalDateTime.now;
import static routes.GameAPI.saveGamePUT;

public class OnlineGameManager implements Observer {
    BufferedReader in;
    PrintStream out;
    ControllerSnakeGame controller;
    SnakeGame game;

    public OnlineGameManager(Socket socket) {
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private void initGame(ControllerSnakeGame controller) throws Exception {
        SnakeGame game = controller.getGame();
        Snake snake = game.getSnakes().get(0);

        JSONObject jsonResponse = new JSONObject();

        jsonResponse.put("type", "return-start-game");
        gameToJSON(in, out,jsonResponse, game);

        out.println(jsonResponse);
        System.out.println("???");
    }

    public void startGame(int userID) throws Exception {
        this.controller =  new ControllerSnakeGame(100, new InteractiveStrategy(), System.getProperty("user.dir") + "/layout/aloneNoWall.lay");
        this.game = controller.getGame();
        JSONObject jsonRequest;

        game.initializeGame();
        game.addObserver(this);
        initGame(controller);

        response:
        while (game.gameContinue()) {
            String input = in.readLine();

            jsonRequest = new JSONObject(input);

            switch ((String) jsonRequest.get("type")) {
                case "play":
                    controller.play();
                    break;
                case "restart":
                    controller.restart();
                    initGame(controller);
                    break;
                case "pause":
                    controller.pause();
                    break;
                case "next-action":
                    game.getNextMoves().set(0, AgentAction.valueOf(jsonRequest.get("next-action").toString()));
                    break;
                case "exit":
                    break response;
            }
        }

        if (!game.gameContinue())
            saveGamePUT(userID, game.isWon(), game.getScore(), String.valueOf(now()));
    }

    private void gameToJSON(BufferedReader in, PrintStream out, JSONObject jsonResponse, SnakeGame game) {
        jsonResponse.put("game-over", game.isGameOver());
        jsonResponse.put("map-height", game.getSizeY());
        jsonResponse.put("map-width", game.getSizeX());
        jsonResponse.put("score", game.getScore());
        jsonResponse.put("turn", game.getTurn());

        Snake snake = game.getSnakes().get(0);

        JSONArray headXY = new JSONArray();
        headXY.put(snake.getPositions().get(0).getX());
        headXY.put(snake.getPositions().get(0).getY());

        JSONArray body = new JSONArray();

        for (int i = 1; i < snake.getPositions().size(); i++) {
            JSONArray bodyPartXY = new JSONArray();
            bodyPartXY.put(snake.getPositions().get(i).getX());
            bodyPartXY.put(snake.getPositions().get(i).getY());

            JSONArray bodyPart = new JSONArray();
            bodyPart.put(bodyPartXY);

            body.put(bodyPart);
        }

        JSONObject snake_ = new JSONObject();
        snake_.put("head", headXY);
        snake_.put("body", body);

        Boolean isSick = false;
        Boolean isInvincible = false;

        switch (snake.getBehavior().getBehaviorType()) {
            case NORMAL:
                break;
            case SICK:
                isSick = true;
                break;
            case INVINCIBLE:
                isInvincible = true;
                break;
        }

        snake_.put("isSick", isSick);
        snake_.put("isInvincible", isInvincible);

        String lastAction = null;

        switch (snake.getLastAction()) {
            case MOVE_UP:
                lastAction = "up";
                break;
            case MOVE_DOWN:
                lastAction = "down";
                break;
            case MOVE_LEFT:
                lastAction = "left";
                break;
            case MOVE_RIGHT:
                lastAction = "right";
                break;
        }

        snake_.put("last-action", lastAction);

        jsonResponse.put("snake", snake_);

        JSONArray items = new JSONArray();

        for (int i = 0; i < game.getItems().size(); i++) {
            JSONArray itemXY = new JSONArray();
            itemXY.put(game.getItems().get(i).getX());
            itemXY.put(game.getItems().get(i).getY());

            JSONObject item = new JSONObject();
            item.put("coordinates", itemXY);

            String type = null;

            switch (game.getItems().get(i).getItemType()) {
                case APPLE:
                    type = "apple";
                    break;
                case BOX:
                    type = "box";
                    break;
                case SICK_BALL:
                    type = "sick";
                    break;
                case INVINCIBILITY_BALL:
                    type = "invincibility";
                    break;
            }

            item.put("type", type);
            items.put(item);
        }

        jsonResponse.put("items", items);

        System.out.println(jsonResponse);
    }

    @Override
    public void update(Observable o, Object arg) {
        SnakeGame game = (SnakeGame) o;
        System.out.println(game.getTurn());

        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("type", "update-game");
        gameToJSON(in, out, jsonResponse, game);
        out.println(jsonResponse);
    }
}