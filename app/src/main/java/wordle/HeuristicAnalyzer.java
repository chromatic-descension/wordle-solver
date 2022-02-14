package wordle;

public class HeuristicAnalyzer {

     // Random guess from remaining answer pool (10k trials)
     // Average guess count: 3.4465

    // length=5, maxGuesses=50, validSplit=0.50, maxAnswers=50
    // Average guess count: 2.96

    // length=5, maxGuesses=50, validSplit=0.75, maxAnswers=50
    // Average guess count: 3.03

    // length=5, maxGuesses=100, validSplit=0.75, maxAnswers=100
    // Average guess count: 2.92

    // length=5, maxGuesses=500, validSplit=0.75, maxAnswers=200
    // Average guess count: 2.84

    public static void analyze(int length) {
        int games = 10000;
        System.out.println("Playing " + games + " and finding average # of guesses.");
        int guesses = 0;
        for (int i=0; i<games; i++) {
            guesses += play(length, 1, 1, 1);
        }
        double avg = (double)(guesses) / games;
        System.out.println("Average guess count: " + avg);
    }

    public static int play(int length, int maxGuesses, double validSplit, int maxAnswers) {
        Wordle wordle = new Wordle(length);
        Agent agent = new Agent(length);
        for (int i=0; i<100; i++) {
            GuessResult guessResult = wordle.guess(agent.guessSmart(maxGuesses, validSplit, maxAnswers));
            System.out.println(guessResult);
            if (guessResult.isCorrect()) {
                return i;
            }
            agent.update(guessResult);
        }
        throw new IllegalStateException("100 guesses and stil didn't find the word: " + wordle.getWord());
    }
}
