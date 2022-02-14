/*
 * Wordle runner.
 */
package wordle;

import java.util.Set;

public class App {

    public static final int LENGTH = 4;
    public static Set<String> ALL_WORDS = Util.readDictionaryFile("words_common.txt", LENGTH);
    // public static Set<String> ALL_WORDS = Util.readDictionaryFile("short_words.txt");
    
    public static void main(String[] args) throws Exception {
        SimpleGame.play(LENGTH);
        // HeuristicAnalyzer.analyze(LENGTH);
    }

}
