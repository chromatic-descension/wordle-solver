package wordle;

public class SimpleGame {
    public static void play(int length) {
        EvilWordle wordle = new EvilWordle(length);
        Agent agent = new Agent(length);
        
        System.out.println(wordle.getWord());
        for (int i=0; i<100; i++) {
            GuessResult guessResult = wordle.guess(agent.guessSmart(10, 0.75, 10));
            System.out.println(guessResult);
            if (guessResult.isCorrect()) {
                break;
            }
            agent.update(guessResult);
        }
    }
}
