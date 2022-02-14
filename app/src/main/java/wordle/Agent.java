package wordle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Agent {

    private SmartDictionary allWords;
    private SmartDictionary dict;

    public Agent(int length) {
        allWords = new SmartDictionary(App.ALL_WORDS).filterLength(length);
        dict = allWords.clone();
    }

    public String guess() {
        return dict.getRandomWord();
    }

    public String guessSmart(int maxGuesses, double validSplit, int maxAnswers) {
        if (dict.size() <= 2) {
            return guess();
        }
        Map<String, Double> entropy = computeEntropy(allWords, dict, maxGuesses, validSplit, maxAnswers);
        String guess = entropy.entrySet().stream().min((x, y) -> x.getValue().compareTo(y.getValue())).get().getKey();
        return guess;
    }

    public void update(GuessResult guessResult) {
        dict.filterGuessResult(guessResult);
    }

    public static Map<String, Double> computeEntropy(SmartDictionary allWords, SmartDictionary remainingWords, int maxGuesses, double validSplit, int maxAnswers) {
        long time = System.currentTimeMillis();
        ConcurrentHashMap<String, Double> entropy = new ConcurrentHashMap<>();
        final AtomicInteger loader = new AtomicInteger();
        int validGuesses = (int)(maxGuesses * validSplit);
        int anyGuesses = maxGuesses - validGuesses;
        Set<String> guesses = randomSubset(remainingWords.getWords(), validGuesses);
        guesses.addAll(randomSubset(allWords.getWords(), anyGuesses));
        guesses.parallelStream().forEach(word -> {
            entropy.put(word, computeSingleWordEntropy(word, remainingWords, maxAnswers));
            int val = loader.incrementAndGet();
            if (val % 100 == 0) System.out.println( val + " / " + guesses.size() );
        });
        long totalTime = System.currentTimeMillis() - time;
        // System.out.println((double)(totalTime) / 1000);
        return entropy;
    }

    public static double computeSingleWordEntropy(String guess, SmartDictionary remainingWords, int maxAnswers) {
        Set<String> answers = randomSubset(remainingWords.getWords(), maxAnswers);
        int originalSize = answers.size();
        double score = answers.parallelStream().map(answer -> {
            SmartDictionary dict = remainingWords.clone();
            Wordle wordle = new Wordle(answer);
            dict.filterGuessResult(wordle.guess(guess));
            if (dict.isEmpty()) {
                throw new IllegalStateException("Entropy computation resulted in empty dictionary.");
            }
            return (double) (dict.size()) / originalSize;
        }).reduce(0.0, Double::sum);
        return score / originalSize;
    }

    private static Set<String> randomSubset(Set<String> set, int members) {
        List<String> list = new ArrayList<>(set);
        Collections.shuffle(list);
        return new HashSet<>(list.subList(0, Math.min(members, list.size())));
    }

}
