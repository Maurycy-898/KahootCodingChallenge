package solution;

import java.util.List;


public class Test {
    public static void main(String[] args) {
        HintTree dictionary = new HintTree();

        dictionary.addWord("cat");
        dictionary.addWord("car");
        dictionary.addWord("carpet");
        dictionary.addWord("cactus");
        dictionary.addWord("java");
        dictionary.addWord("javascript");
        dictionary.addWord("internet");

        List<String> hints = dictionary.findHints("crr");
        hints.forEach(System.out::println);
    }
}
