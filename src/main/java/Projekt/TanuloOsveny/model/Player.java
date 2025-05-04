package Projekt.TanuloOsveny.model;

public class Player {
    private String name;
    private int position;
    private int score;

    // Default constructor
    public Player() {
        this.position = 0;
        this.score = 0;
    }

    // Full constructor
    public Player(String name, int position, int score) {
        this.name = name;
        this.position = position;
        this.score = score;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void increaseScore(int points) {
        this.score += points;
    }
}