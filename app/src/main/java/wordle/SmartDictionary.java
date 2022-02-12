package wordle;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import wordle.GuessResult.LetterResult;

public class SmartDictionary {
    
    private Stream<String> allWords;

    public SmartDictionary(Set<String> allWords) {
        this.allWords = allWords.parallelStream();
    }

    public Set<String> getWords() {
        Set<String> words = allWords.collect(Collectors.toSet());
        allWords = words.parallelStream();
        return words;
    }

    public String getRandomWord() {
        Set<String> words = getWords();
        return words.stream().skip((int) (Math.random() * words.size())).findFirst().get();
    }

    public int size() {
        return getWords().size();
    }

    public SmartDictionary clone() {
        return new SmartDictionary(getWords());
    }

    public SmartDictionary filterLength(int length) {
        allWords = allWords.filter(word -> word.length() == length);
        return this;
    }

    public SmartDictionary filterLetterAtPosition(char letter, int position) {
        System.out.println("filtering " + letter + " at " + position);
        allWords = allWords.filter(word -> word.length() <= position || word.charAt(position) != letter);
        return this;
    }

    public SmartDictionary filterLetterNotAtPosition(char letter, int position) {
        System.out.println("filtering " + letter + " not at " + position);
        allWords = allWords.filter(word -> word.length() > position && word.charAt(position) == letter);
        return this;
    }

    public SmartDictionary filterLetterMinimum(char letter, int minimum) {
        System.out.println("filtering min " + minimum + " of " + letter);
        allWords = allWords.filter(word -> Util.countChar(word, letter) >= minimum);
        return this;
    }

    public SmartDictionary filterLetterMaximum(char letter, int maximum) {
        System.out.println("filtering max " + maximum + " of " + letter);
        allWords = allWords.filter(word -> Util.countChar(word, letter) <= maximum);
        return this;
    }

    
    public SmartDictionary filterGuessResult(GuessResult guessResult) {
        Map<Character, Integer> minCount = new HashMap<>();
        Map<Character, Integer> maxCount = new HashMap<>();
        
        for (int i=0; i<guessResult.size(); i++) {
            char letter = guessResult.getGuess().charAt(i);
            LetterResult result = guessResult.getLetterResults().get(i);

            if (result == LetterResult.CORRECT) {
                filterLetterAtPosition(letter, i);
            } else if (result == LetterResult.WRONG_PLACE) {
                filterLetterNotAtPosition(letter, i);
            }
            if (result != LetterResult.INCORRECT) {
                minCount.put(letter, minCount.getOrDefault(letter, 0) + 1);
                if (maxCount.containsKey(letter)) {
                    maxCount.put(letter, minCount.get(letter));
                }
            } else {
                maxCount.put(letter, minCount.getOrDefault(letter, 0));
            }
        }
        for (Entry<Character, Integer> entry : minCount.entrySet()) {
            filterLetterMinimum(entry.getKey(), entry.getValue());
        }
        for (Entry<Character, Integer> entry : maxCount.entrySet()) {
            filterLetterMaximum(entry.getKey(), entry.getValue());
        }

        return this;
    }

}
