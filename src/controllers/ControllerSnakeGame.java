package controllers;

import model.InputMap;
import model.SnakeGame;
import states.*;
import strategies.Strategy;
import view.PanelSnakeGame;

public class ControllerSnakeGame extends AbstractController {
    private State state;

    public ControllerSnakeGame(int maxTurn, Strategy strategy, String mapPath) throws Exception {
        InputMap map = new InputMap(mapPath);
        this.game = new SnakeGame(maxTurn, map.getStart_snakes(), map.getStart_items(), map.get_walls(), map.getSizeX(), map.getSizeY(), strategy);
        this.state = new RestartState(game);
    }

    public SnakeGame getGame() { return (SnakeGame) this.game; }
    public State getState() { return this.state; }

    public void restart() {
        this.state.restart();
        this.state = new RestartState(this.game);
    }

    public void step() {
        this.state.step();

        if (!(this.state instanceof StepState))
            this.state = new StepState(this.game);

        if (!game.gameContinue())
            this.state = new EndState(game);
    }

    public void play() {
        this.state.play();
        this.state = new PlayState(this.game);
    }

    public void pause() {
        this.game.pause();
        this.state = new PauseState(game);
    }

    public void setSpeed(int speed) {
        this.game.setSpeed(speed);
    }
}
