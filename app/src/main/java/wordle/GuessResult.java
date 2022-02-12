package wordle;

import java.util.List;

public class GuessResult {

    private String guess;
    private List<LetterResult> letterResults;

    public GuessResult(String guess, List<LetterResult> letterResults) {
        this.guess = guess;
        this.letterResults = letterResults;
    }

    public String getGuess() {
        return guess;
    }

    public List<LetterResult> getLetterResults() {
        return letterResults;
    }

    public int size() {
        return guess.length();
    }

    public boolean isCorrect() {
        return letterResults.stream().allMatch(x -> x == LetterResult.CORRECT);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < letterResults.size(); i++) {
            switch (letterResults.get(i)) {
                case CORRECT:
                    sb.append(Colors.GREEN);
                    break;
                case WRONG_PLACE:
                    sb.append(Colors.YELLOW);
                    break;
                case INCORRECT:
                    sb.append(Colors.RED);
                    break;
            }
            sb.append(guess.charAt(i)).append(Colors.RESET);
        }
        return sb.toString();
    }

    public enum LetterResult {
        CORRECT,
        WRONG_PLACE,
        INCORRECT
    }
}
