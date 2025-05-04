package Projekt.TanuloOsveny.controller;

import Projekt.TanuloOsveny.model.GameSession;
import Projekt.TanuloOsveny.model.User;
import Projekt.TanuloOsveny.repository.GameSessionRepository;
import Projekt.TanuloOsveny.service.GameService;
import Projekt.TanuloOsveny.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Controller
public class UserController {

    private final UserService userService;
    private final GameSessionRepository gameSessionRepository;
    private final GameService gameService;

    @Autowired
    public UserController(UserService userService, GameSessionRepository gameSessionRepository,
                          GameService gameService) {
        this.userService = userService;
        this.gameSessionRepository = gameSessionRepository;
        this.gameService = gameService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result,
                               @RequestParam("confirmPassword") String confirmPassword,
                               Model model) {

        // Ellenőrizzük a jelszó egyezőségét
        if (!user.getPassword().equals(confirmPassword)) {
            result.rejectValue("password", "error.user", "A jelszavak nem egyeznek!");
        }

        // Ellenőrizzük, hogy a felhasználónév már létezik-e
        if (userService.findByUsername(user.getUsername()) != null) {
            result.rejectValue("username", "error.user", "Ez a felhasználónév már foglalt!");
        }

        if (result.hasErrors()) {
            return "register";
        }

        userService.registerUser(user);
        return "redirect:/login?registered";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/profile")
    public String showUserProfile(@AuthenticationPrincipal User user, Model model) {
        // Statisztikák lekérése
        List<GameSession> userSessions = gameSessionRepository.findByUserOrderByStartTimeDesc(user);
        Double avgScore = gameSessionRepository.getAverageScoreByUserId(user.getId());
        Long correctAnswers = gameSessionRepository.getCorrectAnswersCountByUserId(user.getId());
        Long totalAnswers = gameSessionRepository.getTotalAnswersCountByUserId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("sessions", userSessions);
        model.addAttribute("avgScore", avgScore != null ? avgScore : 0);
        model.addAttribute("correctAnswers", correctAnswers);
        model.addAttribute("totalAnswers", totalAnswers);
        model.addAttribute("correctPercentage", totalAnswers > 0 ? (correctAnswers * 100.0 / totalAnswers) : 0);

        return "profile";
    }

    @GetMapping("/profile/edit")
    public String showEditProfileForm(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "edit-profile";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@AuthenticationPrincipal User currentUser,
                                @Valid @ModelAttribute("user") User user,
                                BindingResult result) {

        if (result.hasErrors()) {
            return "edit-profile";
        }

        // Az ID-t és a jelszót nem frissítjük itt
        currentUser.setFullName(user.getFullName());
        currentUser.setAge(user.getAge());
        currentUser.setEducationLevel(user.getEducationLevel());

        userService.updateUser(currentUser);

        return "redirect:/profile?updated";
    }

    @GetMapping("/user/game/new")
    public String newGame(@AuthenticationPrincipal User user) {
        // Bejelentkezett felhasználó esetén
        if (user != null) {
            Projekt.TanuloOsveny.model.Game game = gameService.createNewGame(user);
            return "redirect:/game/" + game.getId();
        }

        // Vendég felhasználó esetén
        Projekt.TanuloOsveny.model.Game game = gameService.createNewGame();
        return "redirect:/game/" + game.getId();
    }

    @GetMapping("/profile/game/{sessionId}")
    public String viewGameSession(@AuthenticationPrincipal User user,
                                  @PathVariable Long sessionId,
                                  Model model) {

        GameSession session = gameSessionRepository.findById(sessionId).orElse(null);

        if (session == null || !session.getUser().getId().equals(user.getId())) {
            return "redirect:/profile";
        }

        model.addAttribute("session", session);
        model.addAttribute("attempts", session.getChallengeAttempts());

        return "game-session-details";
    }

    @GetMapping("/user/game/choose-level")
    public String showLevelSelection(@AuthenticationPrincipal User user, Model model) {
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("levels", User.EducationLevel.values());
        model.addAttribute("currentLevel", user.getEducationLevel());

        return "choose-level";
    }

    @PostMapping("/user/game/choose-level")
    public String setGameLevel(@AuthenticationPrincipal User user,
                               @RequestParam("level") User.EducationLevel level) {

        if (user == null) {
            return "redirect:/login";
        }

        // Ideiglenesen frissítjük a felhasználó szintjét
        user.setEducationLevel(level);
        userService.updateUser(user);

        return "redirect:/user/game/new";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("passwordForm", new PasswordChangeForm());
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@AuthenticationPrincipal User user,
                                 @Valid @ModelAttribute("passwordForm") PasswordChangeForm form,
                                 BindingResult result) {

        if (!userService.checkPassword(user, form.getCurrentPassword())) {
            result.rejectValue("currentPassword", "error.password", "A jelenlegi jelszó hibás!");
        }

        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.password", "Az új jelszavak nem egyeznek!");
        }

        if (result.hasErrors()) {
            return "change-password";
        }

        userService.changePassword(user, form.getNewPassword());
        return "redirect:/profile?passwordChanged";
    }

    // Jelszó változtatás adatok
    public static class PasswordChangeForm {
        private String currentPassword;
        private String newPassword;
        private String confirmPassword;

        // Getterek és setterek
        public String getCurrentPassword() {
            return currentPassword;
        }

        public void setCurrentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }
}