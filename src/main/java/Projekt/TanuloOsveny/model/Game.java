package Projekt.TanuloOsveny.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Game {
    private String id;
    private Player player;
    private List<BoardField> board;
    private int currentDiceValue;
    private GameState gameState;
    private String selectedAnswer; // Új mező a kiválasztott válasz tárolásához

    public Game() {
        this.id = UUID.randomUUID().toString();
        this.player = new Player();
        this.board = new ArrayList<>();
        this.gameState = GameState.WAITING_FOR_ROLL;
    }

    public void movePlayer(int steps) {
        // Biztosítjuk, hogy a lépések száma 1 és 6 között legyen
        steps = Math.min(Math.max(steps, 1), 6);

        // Kiszámoljuk az új pozíciót, de nem engedjük, hogy túllépje a tábla méretét
        int newPosition = Math.min(player.getPosition() + steps, board.size() - 1);
        player.setPosition(newPosition);

        // Frissítjük a játék állapotát az új pozíció alapján
        BoardField currentField = board.get(newPosition);
        if (currentField.getType() == BoardField.FieldType.CHALLENGE ||
                currentField.getType() == BoardField.FieldType.BONUS) {
            gameState = GameState.CHALLENGE;
            selectedAnswer = null; // Reset selected answer when entering a new challenge
        } else if (currentField.getType() == BoardField.FieldType.FINISH) {
            gameState = GameState.FINISHED;
        } else {
            gameState = GameState.WAITING_FOR_ROLL;
        }

        // Debug üzenet a konzolon
        System.out.println("Game: Játékos lépett: " + steps + " mezőt, új pozíció: " + newPosition);
    }

    public boolean answerChallenge(String answer) {
        BoardField currentField = board.get(player.getPosition());
        if (currentField.getType() != BoardField.FieldType.CHALLENGE &&
                currentField.getType() != BoardField.FieldType.BONUS) {
            return false;
        }

        Challenge challenge = currentField.getChallenge();
        boolean isCorrect = challenge.getCorrectAnswer().equalsIgnoreCase(answer.trim());

        if (isCorrect) {
            gameState = GameState.WAITING_FOR_ROLL;
            // Ha bónusz mezőn vagyunk, dupla pontot adunk
            if (currentField.getType() == BoardField.FieldType.BONUS) {
                player.increaseScore(20); // Dupla pont
            } else {
                player.increaseScore(10); // Normál pont
            }
        }

        return isCorrect;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public List<BoardField> getBoard() {
        return board;
    }

    public void setBoard(List<BoardField> board) {
        this.board = board;
    }

    public int getCurrentDiceValue() {
        return currentDiceValue;
    }

    public void setCurrentDiceValue(int currentDiceValue) {
        this.currentDiceValue = currentDiceValue;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(String selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public enum GameState {
        WAITING_FOR_ROLL,
        WAITING_FOR_MOVE,
        CHALLENGE,
        FINISHED
    }
}