package Projekt.TanuloOsveny.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game_sessions")
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String gameId;

    @Column(nullable = false)
    private LocalDateTime startTime = LocalDateTime.now();

    private LocalDateTime endTime;

    @Column(nullable = false)
    private int finalScore = 0;

    @Enumerated(EnumType.STRING)
    private User.EducationLevel educationLevel;

    @Column(nullable = false)
    private boolean completed = false;

    @OneToMany(mappedBy = "gameSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChallengeAttempt> challengeAttempts = new ArrayList<>();

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public int getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(int finalScore) {
        this.finalScore = finalScore;
    }

    public User.EducationLevel getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(User.EducationLevel educationLevel) {
        this.educationLevel = educationLevel;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public List<ChallengeAttempt> getChallengeAttempts() {
        return challengeAttempts;
    }

    public void setChallengeAttempts(List<ChallengeAttempt> challengeAttempts) {
        this.challengeAttempts = challengeAttempts;
    }

    public void addChallengeAttempt(ChallengeAttempt attempt) {
        challengeAttempts.add(attempt);
        attempt.setGameSession(this);
    }

    // Játékmenet időtartamának kiszámítása (percben)
    public int getDurationMinutes() {
        if (endTime == null) {
            return 0;
        }
        return (int) java.time.Duration.between(startTime, endTime).toMinutes();
    }
}