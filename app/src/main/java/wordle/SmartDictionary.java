package wordle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import wordle.GuessResult.LetterResult;

public class SmartDictionary implements Iterable<String> {

    private Set<String> allWords;

    public SmartDictionary(Set<String> allWords) {
        this.allWords = new HashSet<>(allWords);
    }

    public Set<String> getWords() {
        return allWords;
    }

    public String getRandomWord() {
        return allWords.stream().skip((int) (Math.random() * allWords.size())).findFirst().get();
    }

    public int size() {
        return allWords.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public SmartDictionary clone() {
        return new SmartDictionary(allWords);
    }

    public SmartDictionary filterLength(int length) {
        allWords = allWords.stream().filter(word -> word.length() == length).collect(Collectors.toSet());
        return this;
    }

    public SmartDictionary filterLetterAtPosition(char letter, int position) {
        allWords.retainAll(positionCache.get(cacheString(position, letter)));
        return this;
    }

    public SmartDictionary filterLetterNotAtPosition(char letter, int position) {
        allWords.retainAll(notPositionCache.get(cacheString(position, letter)));
        return this;
    }

    public SmartDictionary filterLetterMinimum(char letter, int minimum) {
        allWords.retainAll(minCache.get(cacheString(minimum, letter)));
        return this;
    }

    public SmartDictionary filterLetterMaximum(char letter, int maximum) {
        allWords.retainAll(maxCache.get(cacheString(maximum, letter)));
        return this;
    }

    public SmartDictionary filterGuessResult(GuessResult guessResult) {
        Map<Character, Integer> minCount = new HashMap<>();
        Map<Character, Integer> maxCount = new HashMap<>();

        for (int i = 0; i < guessResult.size(); i++) {
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
    
    private static String cacheString(int i, char c) {
        return i + "_" + c;
    }

    public static Map<String, Set<String>> positionCache;
    public static Map<String, Set<String>> notPositionCache;
    public static Map<String, Set<String>> minCache;
    public static Map<String, Set<String>> maxCache;

    static {
        positionCache = new HashMap<>();
        notPositionCache = new HashMap<>();
        minCache = new HashMap<>();
        maxCache = new HashMap<>();
        for (int i = 0; i < App.LENGTH; i++) {
            for (char c = 'a'; c <= 'z'; c++) {
                final int ii = i;
                final char cc = c;
                final String key = cacheString(i, c);
                positionCache.put(key, App.ALL_WORDS.stream()
                        .filter(word -> word.length() > ii && word.charAt(ii) == cc).collect(Collectors.toSet()));
                notPositionCache.put(key, App.ALL_WORDS.stream()
                        .filter(word -> word.length() <= ii || word.charAt(ii) != cc).collect(Collectors.toSet()));
                minCache.put(key,
                        App.ALL_WORDS.stream().filter(word -> Util.countChar(word, cc) >= ii)
                                .collect(Collectors.toSet()));
                maxCache.put(key,
                        App.ALL_WORDS.stream().filter(word -> Util.countChar(word, cc) <= ii)
                                .collect(Collectors.toSet()));
            }
        }
        System.out.println("Finished creating cache.");
    }

    @Override
    public Iterator<String> iterator() {
        return getWords().iterator();
    }

}
