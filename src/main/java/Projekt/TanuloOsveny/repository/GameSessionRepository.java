package Projekt.TanuloOsveny.repository;

import Projekt.TanuloOsveny.model.GameSession;
import Projekt.TanuloOsveny.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {

    GameSession findByGameId(String gameId);

    List<GameSession> findByUser(User user);

    List<GameSession> findByUserOrderByStartTimeDesc(User user);

    List<GameSession> findByCompletedTrue();

    @Query("SELECT g FROM GameSession g WHERE g.user.id = ?1 AND g.completed = true ORDER BY g.finalScore DESC")
    List<GameSession> findTopScoresByUserId(Long userId);

    @Query("SELECT AVG(g.finalScore) FROM GameSession g WHERE g.user.id = ?1 AND g.completed = true")
    Double getAverageScoreByUserId(Long userId);

    @Query("SELECT AVG(ca.secondsToSolve) FROM GameSession g JOIN g.challengeAttempts ca WHERE g.user.id = ?1 AND ca.correct = true")
    Double getAverageTimeToSolveByUserId(Long userId);

    @Query("SELECT COUNT(ca) FROM GameSession g JOIN g.challengeAttempts ca WHERE g.user.id = ?1 AND ca.correct = true")
    Long getCorrectAnswersCountByUserId(Long userId);

    @Query("SELECT COUNT(ca) FROM GameSession g JOIN g.challengeAttempts ca WHERE g.user.id = ?1")
    Long getTotalAnswersCountByUserId(Long userId);

    @Query("SELECT g.user.educationLevel, AVG(g.finalScore) FROM GameSession g WHERE g.completed = true GROUP BY g.user.educationLevel")
    List<Object[]> getAverageScoreByEducationLevel();
}