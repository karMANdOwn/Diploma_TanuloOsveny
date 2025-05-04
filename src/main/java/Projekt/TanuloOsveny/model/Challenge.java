package Projekt.TanuloOsveny.model;

import java.util.ArrayList;
import java.util.List;

public class Challenge {
    private Long id;
    private ChallengeType type;
    private String question;
    private String imageUrl;
    private String soundUrl;
    private String correctAnswer;
    private List<String> options = new ArrayList<>();
    private String hint;
    private User.EducationLevel educationLevel; // Új mező: oktatási szint

    // Default constructor
    public Challenge() {
    }

    // Full constructor
    public Challenge(Long id, ChallengeType type, String question, String imageUrl,
                     String soundUrl, String correctAnswer, List<String> options, String hint,
                     User.EducationLevel educationLevel) {
        this.id = id;
        this.type = type;
        this.question = question;
        this.imageUrl = imageUrl;
        this.soundUrl = soundUrl;
        this.correctAnswer = correctAnswer;
        this.options = options;
        this.hint = hint;
        this.educationLevel = educationLevel;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ChallengeType getType() {
        return type;
    }

    public void setType(ChallengeType type) {
        this.type = type;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSoundUrl() {
        return soundUrl;
    }

    public void setSoundUrl(String soundUrl) {
        this.soundUrl = soundUrl;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public void addOption(String option) {
        this.options.add(option);
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public User.EducationLevel getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(User.EducationLevel educationLevel) {
        this.educationLevel = educationLevel;
    }

    public enum ChallengeType {
        COUNTING_FRUITS("Gyümölcsök számolása"),
        COUNTING_ANIMALS("Állatok számolása"),
        COMPARE_NUMBERS("Számok összehasonlítása"),
        ANIMAL_SOUND("Állathang felismerése"),
        BASIC_ADDITION("Egyszerű összeadás"),
        BASIC_SUBTRACTION("Egyszerű kivonás"),
        COLOR_RECOGNITION("Színek felismerése"),
        SHAPE_RECOGNITION("Formák felismerése"),
        BASIC_MULTIPLICATION("Egyszerű szorzás"),
        BASIC_DIVISION("Egyszerű osztás"),
        WORD_PROBLEM("Szöveges feladat"),
        MATH_SEQUENCE("Számsorozat kiegészítése"),
        GRAMMAR("Nyelvtani feladat"),
        GEOGRAPHY("Földrajzi kérdés"),
        HISTORY("Történelmi kérdés"),
        SCIENCE("Természettudományi kérdés");

        private final String displayName;

        ChallengeType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}