package Projekt.TanuloOsveny.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "challenge_attempts")
public class ChallengeAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_session_id")
    private GameSession gameSession;

    @Column(nullable = false)
    private Long challengeId;

    @Enumerated(EnumType.STRING)
    private Challenge.ChallengeType challengeType;

    @Column(nullable = false)
    private LocalDateTime attemptTime = LocalDateTime.now();

    @Column(nullable = false)
    private String userAnswer;

    @Column(nullable = false)
    private boolean correct;

    @Column(nullable = false)
    private int attemptCount = 1;

    private int secondsToSolve;

    private boolean usedHint = false;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GameSession getGameSession() {
        return gameSession;
    }

    public void setGameSession(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public Challenge.ChallengeType getChallengeType() {
        return challengeType;
    }

    public void setChallengeType(Challenge.ChallengeType challengeType) {
        this.challengeType = challengeType;
    }

    public LocalDateTime getAttemptTime() {
        return attemptTime;
    }

    public void setAttemptTime(LocalDateTime attemptTime) {
        this.attemptTime = attemptTime;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }

    public int getSecondsToSolve() {
        return secondsToSolve;
    }

    public void setSecondsToSolve(int secondsToSolve) {
        this.secondsToSolve = secondsToSolve;
    }

    public boolean isUsedHint() {
        return usedHint;
    }

    public void setUsedHint(boolean usedHint) {
        this.usedHint = usedHint;
    }

    // Növeli a próbálkozások számát
    public void incrementAttemptCount() {
        this.attemptCount++;
    }
}