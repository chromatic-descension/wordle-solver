package wordle;

public class Util {
    
    public static int countChar(String word, char c) {
        return (int) word.chars().filter(x -> x == c).count();
    }
}
