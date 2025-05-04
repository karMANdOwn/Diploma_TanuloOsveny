package Projekt.TanuloOsveny.controller;

import Projekt.TanuloOsveny.model.Game;
import Projekt.TanuloOsveny.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/game/{gameId}")
    public String getGame(@PathVariable String gameId, Model model) {
        Game game = gameService.getGame(gameId);
        if (game == null) {
            return "redirect:/";
        }
        model.addAttribute("game", game);
        return "game";
    }

    @GetMapping("/game/{gameId}/roll")
    @ResponseBody
    public Game rollDice(@PathVariable String gameId, @RequestParam(required = false) Integer value) {
        if (value != null) {
            gameService.rollDice(gameId, value);
        } else {
            gameService.rollDice(gameId);
        }
        return gameService.getGame(gameId);
    }

    @GetMapping("/game/{gameId}/move")
    @ResponseBody
    public Map<String, Object> movePlayer(@PathVariable String gameId) {
        Game game = gameService.getGame(gameId);
        int previousPosition = game.getPlayer().getPosition();

        gameService.movePlayer(gameId);

        Game updatedGame = gameService.getGame(gameId);
        int newPosition = updatedGame.getPlayer().getPosition();

        Map<String, Object> response = new HashMap<>();
        response.put("previousPosition", previousPosition);
        response.put("newPosition", newPosition);
        response.put("gameState", updatedGame);

        return response;
    }

    @PostMapping("/game/{gameId}/select-answer")
    @ResponseBody
    public Game selectAnswer(@PathVariable String gameId, @RequestParam String answer) {
        gameService.setSelectedAnswer(gameId, answer);
        return gameService.getGame(gameId);
    }

    @PostMapping("/game/{gameId}/answer")
    @ResponseBody
    public boolean answerChallenge(@PathVariable String gameId) {
        return gameService.checkSelectedAnswer(gameId);
    }

    @GetMapping("/game/new")
    public String newGame(HttpSession session) {
        Game game = gameService.createNewGame();
        session.setAttribute("gameId", game.getId());
        return "redirect:/game/" + game.getId();
    }

    // Új végpont a játék befejezéséhez
    @GetMapping("/game/{gameId}/finish")
    public String finishGame(@PathVariable String gameId) {
        Game game = gameService.getGame(gameId);
        if (game != null) {
            gameService.finishGame(gameId);
        }
        return "redirect:/profile";
    }
}