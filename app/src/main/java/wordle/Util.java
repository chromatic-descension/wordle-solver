package wordle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Util {
    
    public static int countChar(String word, char c) {
        return (int) word.chars().filter(x -> x == c).count();
    }

    public static Set<String> readDictionaryFile(String fileName, int length) {
        return readDictionaryFile(fileName).stream().filter(x -> x.length() == length).collect(Collectors.toSet());
    }
    
    public static Set<String> readDictionaryFile(String fileName) {
        Set<String> words = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String word;
            while ((word = br.readLine()) != null) {
                words.add(word);
            }
        } catch (Exception e) {
            throw new RuntimeException("Couldn't read dictionary.");
        }
        return words;
    }
}
