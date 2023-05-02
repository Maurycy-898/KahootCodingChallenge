package solution;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


/**
 * Tree like structure that stores words in a way that enables fast search for hints
 * - that is words starting with provided query.
 */
public class HintTree {
    /**  Represents an empty character in this tree.  */
    public  final static Character emptyChar = '\0';
    /**  The root of this tree.  */
    private final HashMap<Character, HintNode> hintRoot;


    /**
     * Create new, empty Hint-Tree
     */
    public HintTree() {
        this.hintRoot = new HashMap<>();
    }


    /**
     * Adds a word to this tree.
     * @param word the word to add
     */
    public void addWord(String word) {
        if (word == null) return;  // skip null words
        if (word.equals("")) return;  // skip empty words
        if (word.contains(" ")) return;  // skip word groups/sentences (optional)

        Character key = word.charAt(0);
        if (this.hintRoot.containsKey(key)) {
            hintRoot.get(key).updateNode(word);
        } else {
            hintRoot.put(key, new HintNode(word));
        }
    }

    /**
     * Adds a group of words to this tree.
     * @param words words to add
     */
    public void addWords(List<String> words) {
        for (String word : words) {
            this.addWord(word);
        }
    }

    /**
     * Finds all words starting with the query
     * @param query the query for which we are searching results
     * @return list of words starting with provided query
     */
    public LinkedList<String> findHints(String query) {
        LinkedList<String> hintList = new LinkedList<>();

        if (query == null) return hintList;  // skip null words
        if (query.equals("")) return hintList;  // skip empty words
        if (query.contains(" ")) return hintList;  // skip word groups/sentences (optional)

        Character key = query.charAt(0);
        if (this.hintRoot.containsKey(key)) {
            this.hintRoot.get(key).findHints(query, hintList);
        }

        return hintList;
    }


    /**
     * The node of this tree.
     * It always stores the longest common prefix of the subtree it creates (at least one letter).
     * The node and its children store suffixes of the parent nodes that start with the storedText.
     * If it has no children - it stores last part of word.
     */
    private static class HintNode {
        /**  Map of this node children (suffixes of this node's text)  */
        private final HashMap<Character, HintNode> childrenMap;
        /**  The text stored in this node (the longest common prefix of this subtree)  */
        private String storedText;

        /**
         * Creates new hint node that stores passed text
         * @param text the text this node will store
         */
        public HintNode(String text) {
            this.childrenMap = new HashMap<>();
            this.storedText = text;
        }

        /**
         * Updates this node and its child nodes recursively if needed.
         * The node stores the longest common prefix of the subtree it creates
         * When adding new word/text - we update the prefix (if needed) and
         * update the children nodes with the suffix of the texts (stored and updating)
         *
         * NOTE: text and this node storedText have at least the first letter in common
         * (Because of how is this tree constructed)
         *
         * @param text the text we are updating the node with
         */
        public void updateNode(String text) {
            if (text.equals(storedText)) return;

            String commonPrefix = longestCommonPrefix(text, this.storedText);
            String childText1 = storedText.substring(commonPrefix.length());
            String childText2 = text.substring(commonPrefix.length());

            Character key1, key2;
            // When storedText is the common prefix, and it is last part of stored word
            // then we add empty element to mark that it creates new word by itself
            if (childText1.equals("")) {
                if (childrenMap.isEmpty()) {
                    key1 = HintTree.emptyChar;
                    childrenMap.put(key1, new HintNode(childText1));
                }
            } else {
                key1 = childText1.charAt(0);
                if (this.childrenMap.containsKey(key1)) {
                    childrenMap.get(key1).updateNode(childText1);
                } else {
                    childrenMap.put(key1, new HintNode(childText1));
                }
            }

            // When updating text is the common prefix
            // then we add empty element to mark that it creates new word by itself
            if (childText2.equals("")) {
                key2 = HintTree.emptyChar;
                childrenMap.put(key2, new HintNode(childText2));
            } else {
                key2 = childText2.charAt(0);
                if (this.childrenMap.containsKey(key2)) {
                    childrenMap.get(key2).updateNode(childText2);
                } else {
                    childrenMap.put(key2, new HintNode(childText2));
                }
            }

            this.storedText = commonPrefix;
        }

        /**
         * Called recursively when parent node didn't contain full query
         * Since we know parent node contained query prefix, if this node contains the suffix
         * then this node and its children starts with the query, and we may collect words.
         * Otherwise, find the common prefix and continue search in appropriate child node.
         *
         * @param queryPrefix  the query prefix that this node - parent nodes contain
         * @param querySuffix  the query suffix that we are searching for
         * @param hintList  list of hints that this function updates
         */
        private void findHints(String queryPrefix, String querySuffix,LinkedList<String> hintList) {
            if (this.storedText.startsWith(querySuffix)) {
                this.collectHints(hintList, queryPrefix);
            }
            else if (this.storedText.length() < querySuffix.length()) {
                String commonPrefix = longestCommonPrefix(querySuffix, this.storedText);
                String newSuffix = querySuffix.substring(commonPrefix.length());
                String newPrefix = queryPrefix + commonPrefix;

                Character key = newSuffix.charAt(0);
                this.childrenMap.get(key).findHints(newPrefix, newSuffix, hintList);
            }
        }

        /**
         * First to be called from the root node.
         * If stored text starts with query - collect stored words from children nodes.
         * Otherwise, find the common prefix and continue search in appropriate child node.
         * For further search we must provide query prefix and suffix
         *
         * @param query  the query for which we are searching results
         * @param hintList  list of hints that this function updates
         */
        public void findHints(String query, LinkedList<String> hintList) {
            if (this.storedText.startsWith(query)) {
                this.collectHints(hintList, "");
            }
            else if (this.storedText.length() < query.length()) {
                String queryPrefix = longestCommonPrefix(query, this.storedText);
                String querySuffix = query.substring(queryPrefix.length());

                Character key = querySuffix.charAt(0);
                if (this.childrenMap.containsKey(key)) {
                    this.childrenMap.get(key).findHints(queryPrefix, querySuffix, hintList);
                }
            }
        }

        /**
         * When query search is complete - node starting with the query is found.
         * Collects all words saved in this node children (they start with the query).
         *
         * @param hintList list of hints that this function updates
         * @param prefix the prefix to add, from previous nodes in search process
         */
        private void collectHints(LinkedList<String> hintList, String prefix) {
            String hint = prefix + this.storedText;
            if (this.childrenMap.isEmpty()) {
                hintList.add(hint);
            }
            this.childrenMap.forEach((key, child) -> child.collectHints(hintList, hint));
        }

        /**
         * To find the longest common prefix
         * @param s1 first string
         * @param s2 second string
         * @return the longest common prefix of two strings
         */
        private static String longestCommonPrefix(String s1, String s2) {
            int minLength = Math.min(s1.length(), s2.length());
            for (int i = 0; i < minLength; i++) {
                if (s1.charAt(i) != s2.charAt(i)) {
                    return s1.substring(0, i);
                }
            }
            return s1.substring(0, minLength);
        }
    }
}
