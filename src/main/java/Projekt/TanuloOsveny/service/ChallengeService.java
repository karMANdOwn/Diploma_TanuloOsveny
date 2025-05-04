package Projekt.TanuloOsveny.service;

import Projekt.TanuloOsveny.model.Challenge;
import Projekt.TanuloOsveny.model.User;
import com.opencsv.CSVReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.util.*;

@Service
public class ChallengeService {

    private final Map<User.EducationLevel, List<Challenge>> challengeMap = new HashMap<>();
    private final Random random = new Random();

    public ChallengeService() {
        loadChallenges();
    }

    private void loadChallenges() {
        challengeMap.put(User.EducationLevel.GRADE_1, loadFromCsv("challenges_grade1.csv"));
        challengeMap.put(User.EducationLevel.GRADE_2_4, loadFromCsv("challenges_grade2_4.csv"));
        challengeMap.put(User.EducationLevel.GRADE_5_8, loadFromCsv("challenges_grade5_8.csv"));
    }

    private List<Challenge> loadFromCsv(String filename) {
        List<Challenge> loadedChallenges = new ArrayList<>();

        try (
                CSVReader reader = new CSVReader(new InputStreamReader(
                        new ClassPathResource(filename).getInputStream(), "UTF-8"))
        ) {
            String[] line;
            reader.readNext(); // fejléc átugrása

            while ((line = reader.readNext()) != null) {
                if (line.length < 11) {
                    System.err.println("Hibás sor (" + filename + "): " + Arrays.toString(line));
                    continue;
                }

                Challenge challenge = new Challenge();
                challenge.setId(Long.parseLong(line[0]));
                challenge.setType(Challenge.ChallengeType.valueOf(line[1]));
                challenge.setQuestion(line[2]);
                challenge.setImageUrl(line[3].isEmpty() ? null : line[3]);
                challenge.setSoundUrl(line[4].isEmpty() ? null : line[4]);
                challenge.setCorrectAnswer(line[5]);

                List<String> options = new ArrayList<>();
                for (int i = 6; i <= 9; i++) {
                    options.add(line[i]);
                }
                challenge.setOptions(options);

                challenge.setHint(line[10]);

                loadedChallenges.add(challenge);
            }

        } catch (Exception e) {
            System.err.println("Hiba a CSV betöltésekor: " + filename + " – " + e.getMessage());
        }

        return loadedChallenges;
    }

    public Challenge getRandomChallenge(User.EducationLevel level) {
        List<Challenge> challenges = challengeMap.getOrDefault(level, Collections.emptyList());
        if (challenges.isEmpty()) {
            throw new RuntimeException("Nincs feladvány ehhez a szinthez: " + level);
        }
        return challenges.get(random.nextInt(challenges.size()));
    }

    public Challenge getRandomChallenge() {
        return getRandomChallenge(User.EducationLevel.GRADE_1);
    }

    public List<Challenge> getChallengesByLevel(User.EducationLevel level) {
        return new ArrayList<>(challengeMap.getOrDefault(level, Collections.emptyList()));
    }

    public List<Challenge> getAllChallenges() {
        List<Challenge> all = new ArrayList<>();
        for (List<Challenge> list : challengeMap.values()) {
            all.addAll(list);
        }
        return all;
    }
}
