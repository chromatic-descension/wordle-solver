package wordle;

import java.util.Set;

public class Agent {

    private SmartDictionary dict;

    public Agent(int length) {
        dict = new SmartDictionary(App.ALL_WORDS).filterLength(length);
    }

    public String guess() {
        return dict.getRandomWord();
    }

    public void update(GuessResult guessResult) {
        dict.filterGuessResult(guessResult);

        Set<String> words = dict.getWords();
        System.out.print("...{ ");
        int i = 0;
        for (String word : words) {
            if (i < 3 || i >= words.size() - 3) {
                System.out.print(word + ", ");
            }
            i ++;
        }
        System.out.println(" } " + words.size());
    }

}
