package wordle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import wordle.GuessResult.LetterResult;

public class EvilWordle {

    private SmartDictionary dict;

    public EvilWordle(int length) {
        dict = new SmartDictionary(App.ALL_WORDS).filterLength(length);
    }

    public String getWord() {
        if (dict.size() == 1) {
            return dict.getRandomWord();
        }
        return "< word unknown >";
    }

    public GuessResult guess(String guess) {
        String worstAnswer = getWorstAnswer(guess, dict);
        System.out.println("For guess '" +guess + "'' the worst answer was '" + worstAnswer + "'.");
        GuessResult guessResult = new Wordle(worstAnswer).guess(guess);
        dict.filterGuessResult(guessResult);
        return guessResult;
    }

    public static String getWorstAnswer(String guess, SmartDictionary words) {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        words.getWords().parallelStream().forEach(answer -> {
            SmartDictionary dict = words.clone();
            Wordle wordle = new Wordle(answer);
            dict.filterGuessResult(wordle.guess(guess));
            map.put(answer, dict.size());
        });
        return map.entrySet().stream().max((x,y) -> x.getValue() - y.getValue()).map(x -> x.getKey()).get();
    }

    public static GuessResult guess(String guess, String word) {
        if (guess.length() != word.length()) {
            throw new IllegalArgumentException("Guess must be same length as word.");
        }
        Map<Character, Integer> letterCounts = getLetterCounts(word);
        List<LetterResult> result = new ArrayList<>(Collections.nCopies(guess.length(), null));

        // Add correct guesses to result first.
        for (int i = 0; i < guess.length(); i++) {
            char guessLetter = guess.charAt(i);
            if (word.charAt(i) == guessLetter) {
                result.set(i, LetterResult.CORRECT);
                letterCounts.put(guessLetter, letterCounts.get(guessLetter) - 1);
            }
        }

        // Add incorrect guesses to the result.
        for (int i = 0; i < guess.length(); i++) {
            char guessLetter = guess.charAt(i);
            if (result.get(i) == null) {
                if (letterCounts.getOrDefault(guessLetter, 0) > 0) {
                    result.set(i, LetterResult.WRONG_PLACE);
                } else {
                    result.set(i, LetterResult.INCORRECT);
                }
                letterCounts.put(guessLetter, letterCounts.getOrDefault(guessLetter, 0) - 1);
            }
        }

        return new GuessResult(guess, result);
    }

    private static Map<Character, Integer> getLetterCounts(String word) {
        Map<Character, Integer> counts = new HashMap<>();
        for (char c : word.toCharArray()) {
            counts.put(c, counts.getOrDefault(c, 0) + 1);
        }
        return counts;
    }

}
