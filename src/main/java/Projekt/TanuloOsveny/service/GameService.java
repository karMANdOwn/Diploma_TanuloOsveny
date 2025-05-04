package Projekt.TanuloOsveny.service;

import Projekt.TanuloOsveny.model.*;
import Projekt.TanuloOsveny.repository.GameSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class GameService {

    private final ChallengeService challengeService;
    private final GameSessionRepository gameSessionRepository;
    private final Map<String, Game> activeGames = new HashMap<>();
    private final Random random = new Random();

    @Autowired
    public GameService(ChallengeService challengeService, GameSessionRepository gameSessionRepository) {
        this.challengeService = challengeService;
        this.gameSessionRepository = gameSessionRepository;
    }

    public Game createNewGame(User user) {
        Game game = new Game();
        game.setBoard(generateBoard(50, user.getEducationLevel())); // 50 mezős játéktábla
        activeGames.put(game.getId(), game);

        // Játékmenet mentése az adatbázisba
        GameSession gameSession = new GameSession();
        gameSession.setUser(user);
        gameSession.setGameId(game.getId());
        gameSession.setEducationLevel(user.getEducationLevel());
        gameSessionRepository.save(gameSession);

        return game;
    }

    // Visszafelé kompatibilitás miatt
    public Game createNewGame() {
        Game game = new Game();
        game.setBoard(generateBoard(50, null)); // 50 mezős alap játéktábla
        activeGames.put(game.getId(), game);
        return game;
    }

    public Game getGame(String gameId) {
        return activeGames.get(gameId);
    }

    // Új metódus: játék betöltése a GameSession adatokból, ha nincs az aktív játékok között
    public Game loadGameFromSession(String gameId) {
        // Ha a játék már aktív, akkor visszaadjuk
        Game existingGame = activeGames.get(gameId);
        if (existingGame != null) {
            return existingGame;
        }

        // Különben megpróbáljuk betölteni a GameSession alapján
        GameSession gameSession = gameSessionRepository.findByGameId(gameId);
        if (gameSession != null && !gameSession.isCompleted()) {
            // Új játék létrehozása a játékmenet adatai alapján
            Game game = new Game();
            game.setId(gameId);

            // Tábla inicializálása az adott nehézségi szintnek megfelelően
            game.setBoard(generateBoard(50, gameSession.getEducationLevel()));

            // Játék állapotának beállítása
            game.setGameState(Game.GameState.WAITING_FOR_ROLL);

            // Játékos pontszámának beállítása a GameSession alapján
            Player player = new Player();
            player.setScore(gameSession.getFinalScore());
            game.setPlayer(player);

            // Játék hozzáadása az aktív játékokhoz
            activeGames.put(gameId, game);

            return game;
        }

        return null;
    }

    // Véletlenszerű dobás
    public void rollDice(String gameId) {
        Game game = activeGames.get(gameId);
        if (game != null && game.getGameState() == Game.GameState.WAITING_FOR_ROLL) {
            int diceValue = random.nextInt(6) + 1;
            game.setCurrentDiceValue(diceValue);
            game.setGameState(Game.GameState.WAITING_FOR_MOVE);
        }
    }

    // Adott értékkel dobás (kliensoldali dobás esetén)
    public void rollDice(String gameId, int value) {
        Game game = activeGames.get(gameId);
        if (game != null && game.getGameState() == Game.GameState.WAITING_FOR_ROLL) {
            // Ellenőrizzük, hogy a kliensoldali dobás érvényes-e (1-6)
            int diceValue = Math.min(Math.max(value, 1), 6);
            game.setCurrentDiceValue(diceValue);
            game.setGameState(Game.GameState.WAITING_FOR_MOVE);
        }
    }

    public void movePlayer(String gameId) {
        Game game = activeGames.get(gameId);
        if (game != null && game.getGameState() == Game.GameState.WAITING_FOR_MOVE) {
            game.movePlayer(game.getCurrentDiceValue());
        }
    }

    // Feleletválasztós válasz beállítása
    public void setSelectedAnswer(String gameId, String answer) {
        Game game = activeGames.get(gameId);
        if (game != null && game.getGameState() == Game.GameState.CHALLENGE) {
            game.setSelectedAnswer(answer);
        }
    }

    // Kiválasztott válasz ellenőrzése
    public boolean checkSelectedAnswer(String gameId) {
        Game game = activeGames.get(gameId);
        if (game != null && game.getGameState() == Game.GameState.CHALLENGE && game.getSelectedAnswer() != null) {
            boolean isCorrect = game.answerChallenge(game.getSelectedAnswer());

            // Játékmenet statisztika mentése
            updateChallengeStatistics(gameId, game.getBoard().get(game.getPlayer().getPosition()).getChallenge(),
                    game.getSelectedAnswer(), isCorrect, false);

            return isCorrect;
        }
        return false;
    }

    // Régi válaszkezelő metódus, továbbra is támogatva a kompatibilitás miatt
    public boolean answerChallenge(String gameId, String answer) {
        Game game = activeGames.get(gameId);
        if (game != null && game.getGameState() == Game.GameState.CHALLENGE) {
            boolean isCorrect = game.answerChallenge(answer);

            // Játékmenet statisztika mentése
            updateChallengeStatistics(gameId, game.getBoard().get(game.getPlayer().getPosition()).getChallenge(),
                    answer, isCorrect, false);

            return isCorrect;
        }
        return false;
    }

    // Játék befejezése
    public void finishGame(String gameId) {
        Game game = activeGames.get(gameId);
        if (game != null && game.getGameState() == Game.GameState.FINISHED) {
            // Játékmenet lezárása az adatbázisban
            GameSession gameSession = gameSessionRepository.findByGameId(gameId);
            if (gameSession != null) {
                gameSession.setCompleted(true);
                gameSession.setEndTime(LocalDateTime.now());
                gameSession.setFinalScore(game.getPlayer().getScore());
                gameSessionRepository.save(gameSession);
            }
        }
    }

    // Segítség használatának rögzítése
    public void useHint(String gameId) {
        Game game = activeGames.get(gameId);
        if (game != null && game.getGameState() == Game.GameState.CHALLENGE) {
            // Játékmenet statisztika frissítése - segítség használata
            updateHintUsage(gameId);
        }
    }

    // Statisztika frissítése
    private void updateChallengeStatistics(String gameId, Challenge challenge, String userAnswer,
                                           boolean isCorrect, boolean usedHint) {
        GameSession gameSession = gameSessionRepository.findByGameId(gameId);
        if (gameSession != null && challenge != null) {
            ChallengeAttempt attempt = new ChallengeAttempt();
            attempt.setChallengeId(challenge.getId());
            attempt.setChallengeType(challenge.getType());
            attempt.setUserAnswer(userAnswer);
            attempt.setCorrect(isCorrect);
            attempt.setUsedHint(usedHint);

            gameSession.addChallengeAttempt(attempt);
            gameSessionRepository.save(gameSession);
        }
    }

    // Segítség használatának rögzítése
    private void updateHintUsage(String gameId) {
        GameSession gameSession = gameSessionRepository.findByGameId(gameId);
        if (gameSession != null && !gameSession.getChallengeAttempts().isEmpty()) {
            // Az utolsó kísérlet frissítése
            ChallengeAttempt lastAttempt = gameSession.getChallengeAttempts().get(
                    gameSession.getChallengeAttempts().size() - 1);
            lastAttempt.setUsedHint(true);
            gameSessionRepository.save(gameSession);
        }
    }

    private java.util.List<BoardField> generateBoard(int size, User.EducationLevel educationLevel) {
        java.util.List<BoardField> board = new java.util.ArrayList<>(size);

        // Start mező
        board.add(new BoardField(0, BoardField.FieldType.START, null, null));

        // Köztes mezők
        for (int i = 1; i < size - 1; i++) {
            if (i % 3 == 0) { // Minden harmadik mező feladat-mező
                Challenge challenge;
                if (educationLevel != null) {
                    challenge = challengeService.getRandomChallenge(educationLevel);
                } else {
                    challenge = challengeService.getRandomChallenge();
                }
                board.add(new BoardField(i, BoardField.FieldType.CHALLENGE, challenge, null));
            } else if (i % 7 == 0) { // Minden hetedik mező bónusz-mező dupla pontért
                Challenge bonusChallenge;
                if (educationLevel != null) {
                    bonusChallenge = challengeService.getRandomChallenge(educationLevel);
                } else {
                    bonusChallenge = challengeService.getRandomChallenge();
                }
                board.add(new BoardField(i, BoardField.FieldType.BONUS, bonusChallenge, null));
            } else {
                board.add(new BoardField(i, BoardField.FieldType.NORMAL, null, null));
            }
        }

        // Cél mező
        board.add(new BoardField(size - 1, BoardField.FieldType.FINISH, null, null));

        return board;
    }
}