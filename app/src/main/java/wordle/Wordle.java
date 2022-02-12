package wordle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wordle.GuessResult.LetterResult;

public class Wordle {

    private String word;

    public Wordle(int length) {
        word = new SmartDictionary(App.ALL_WORDS).filterLength(length).getRandomWord();
    }

    public Wordle(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public GuessResult guess(String guess) {
        return guess(guess, word);
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
