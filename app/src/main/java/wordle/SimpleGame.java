package wordle;

public class SimpleGame {
    public static void play(int length) {
        Wordle wordle = new Wordle(length);
        Agent agent = new Agent(length);
        
        System.out.println(wordle.getWord());
        for (int i=0; i<100; i++) {
            GuessResult guessResult = wordle.guess(agent.guess());
            System.out.println(guessResult);
            if (guessResult.isCorrect()) {
                break;
            }
            agent.update(guessResult);
        }
    }
}
