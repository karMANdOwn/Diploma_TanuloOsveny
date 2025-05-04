package Projekt.TanuloOsveny.model;

public class BoardField {
    private int position;
    private FieldType type;
    private Challenge challenge;
    private String imageUrl;

    // Default constructor
    public BoardField() {
    }

    // Full constructor
    public BoardField(int position, FieldType type, Challenge challenge, String imageUrl) {
        this.position = position;
        this.type = type;
        this.challenge = challenge;
        this.imageUrl = imageUrl;
    }

    // Getters and setters
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public enum FieldType {
        START("Kezdés"),
        NORMAL("Normál mező"),
        CHALLENGE("Feladat mező"),
        BONUS("Bónusz mező"),
        FINISH("Cél");

        private final String displayName;

        FieldType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}