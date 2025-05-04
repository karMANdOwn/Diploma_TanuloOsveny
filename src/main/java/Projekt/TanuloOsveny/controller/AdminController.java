package Projekt.TanuloOsveny.controller;

import Projekt.TanuloOsveny.model.ChallengeAttempt;
import Projekt.TanuloOsveny.model.GameSession;
import Projekt.TanuloOsveny.model.User;
import Projekt.TanuloOsveny.repository.GameSessionRepository;
import Projekt.TanuloOsveny.service.ChallengeService;
import Projekt.TanuloOsveny.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final UserService userService;
    private final GameSessionRepository gameSessionRepository;
    private final ChallengeService challengeService;

    @Autowired
    public AdminController(UserService userService, GameSessionRepository gameSessionRepository,
                           ChallengeService challengeService) {
        this.userService = userService;
        this.gameSessionRepository = gameSessionRepository;
        this.challengeService = challengeService;
    }

    @GetMapping
    public String adminDashboard(Model model) {
        // Statisztikai adatok összegyűjtése
        List<User> allUsers = userService.getAllUsers();
        long totalUsers = allUsers.size();
        long totalCompletedGames = gameSessionRepository.findByCompletedTrue().size();

        // Felhasználók korosztály szerinti eloszlása
        Map<User.EducationLevel, Long> educationLevelDistribution = allUsers.stream()
                .collect(Collectors.groupingBy(User::getEducationLevel, Collectors.counting()));

        // Átlagos pontszámok korosztály szerint
        List<Object[]> avgScoresByLevel = gameSessionRepository.getAverageScoreByEducationLevel();
        Map<User.EducationLevel, Double> avgScoreMap = new HashMap<>();

        for (Object[] row : avgScoresByLevel) {
            User.EducationLevel level = (User.EducationLevel) row[0];
            Double avgScore = (Double) row[1];
            avgScoreMap.put(level, avgScore);
        }

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalCompletedGames", totalCompletedGames);
        model.addAttribute("educationLevelDistribution", educationLevelDistribution);
        model.addAttribute("avgScoresByLevel", avgScoreMap);

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @GetMapping("/users/{userId}")
    public String viewUserDetails(@PathVariable Long userId, Model model) {
        User user = userService.getUserById(userId);
        List<GameSession> userSessions = gameSessionRepository.findByUserOrderByStartTimeDesc(user);

        // Statisztikák számítása
        double avgScore = gameSessionRepository.getAverageScoreByUserId(userId) != null ?
                gameSessionRepository.getAverageScoreByUserId(userId) : 0;

        double avgTimeToSolve = gameSessionRepository.getAverageTimeToSolveByUserId(userId) != null ?
                gameSessionRepository.getAverageTimeToSolveByUserId(userId) : 0;

        long correctAnswers = gameSessionRepository.getCorrectAnswersCountByUserId(userId);
        long totalAnswers = gameSessionRepository.getTotalAnswersCountByUserId(userId);
        double correctPercentage = totalAnswers > 0 ? (correctAnswers * 100.0 / totalAnswers) : 0;

        model.addAttribute("user", user);
        model.addAttribute("sessions", userSessions);
        model.addAttribute("avgScore", avgScore);
        model.addAttribute("avgTimeToSolve", avgTimeToSolve);
        model.addAttribute("correctAnswers", correctAnswers);
        model.addAttribute("totalAnswers", totalAnswers);
        model.addAttribute("correctPercentage", correctPercentage);

        return "admin/user-details";
    }

    @GetMapping("/sessions")
    public String listGameSessions(Model model) {
        List<GameSession> sessions = gameSessionRepository.findAll();
        model.addAttribute("sessions", sessions);
        return "admin/sessions";
    }

    @GetMapping("/sessions/{sessionId}")
    public String viewSessionDetails(@PathVariable Long sessionId, Model model) {
        GameSession session = gameSessionRepository.findById(sessionId).orElse(null);

        if (session == null) {
            return "redirect:/admin/sessions";
        }

        List<ChallengeAttempt> attempts = session.getChallengeAttempts();

        model.addAttribute("session", session);
        model.addAttribute("attempts", attempts);

        return "admin/session-details";
    }

    @GetMapping("/challenges")
    public String listChallenges(Model model) {
        model.addAttribute("challenges", challengeService.getAllChallenges());
        return "admin/challenges";
    }
}