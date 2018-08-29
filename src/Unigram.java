import java.io.*;
import java.util.*;

public class Unigram {

    /**
     * Static strings of filename.
     */
    public static String FILE_NAME = "dicofile.txt";

    private static List<String> myOutputList = new ArrayList<String>();

    /**
     * Hashmap that stores a string word and their occurrences.
     */
    private static HashMap<String, Double> myHashMap;
    public static Trie t = new Trie();

    private static String resultNG = "";
    private static String resultNGF = "";


    /**
     * Method that runs all other methods to find and write all probabilities
     * of words found in the file.
     */
    public static List<String> startProbalities(String wordF) {
        myHashMap = new HashMap<String, Double>();
        final String str = readFile(FILE_NAME, false);
        countOccurrences(str, wordF);
        List<String> a= t.autocomplete(wordF);
        //writeProbabilities();
        return a;
    }

    private static String readFile(final String theFileName, final boolean isFormated) {
        String str = "";
        str = getString(theFileName, isFormated, str);
        return str;
    }

    static String getString(String theFileName, boolean isFormated, String str) {
        str = Bigram.getString(theFileName, isFormated, str);
        return str;
    }

    /**
     * Iterates though the given string and counts occurrence of each word.
     * Puts the data in a hashmap<TheWord, theNumberOfOccurences>.
     *
     * @param theString - the string being evaluated.
     */
    /*private static void countOccurrences(final String theString, final String wordfind) {
        for (final String word : theString.split(" ")) {
            // Checks for empty string
            if (!"".equals(word.trim())) {
                if (myHashMap.containsKey(word)) {
                    final double newValue = myHashMap.get(word).intValue() + 1;
                    myHashMap.put(word, newValue);
                } else {
                    myHashMap.put(word, 1.0);
                }
            }
        }
    }*/

    private static void countOccurrences(final String theString, final String wordfind) {
        for (final String word : theString.split(" ")) {
            // Checks for empty string
            if (!"".equals(word.trim())) {
                if (myHashMap.containsKey(word)) {
                    final double newValue = myHashMap.get(word).intValue() + 1;
                    myHashMap.put(word, newValue);
                    t.insert(word);
                } else {
                    myHashMap.put(word, 1.0);
                    t.insert(word);
                }
            }
        }
    }

    /**
     * Iterates through each string in list, calculates the probability of each
     * and writes it to a file.
     *
     */
    private static void writeProbabilities() {

        resultNG = "";
        final Iterator<Map.Entry<String, Double>> it = myHashMap.entrySet().iterator();
        while (it.hasNext()) {
            @SuppressWarnings("rawtypes")
            final
            Map.Entry pair = it.next();
            final double prob = (double) pair.getValue() / myHashMap.size();
            //biOutputList.add("P(" + pair.getKey() + ") = " + prob);
            resultNG += pair.getKey() + "\t   P = " + prob + "\n";
            /*if ( pair.getKey().toString().contains(wordfind)){
                resultNGF += pair.getKey() + "\t   P = " + prob + "\n";
            }*/
        }
    }
    static class TrieNode {
        char data;
        LinkedList<TrieNode> children;
        TrieNode parent;
        boolean isEnd;

        public TrieNode(char c) {
            data = c;
            children = new LinkedList<TrieNode>();
            isEnd = false;
        }

        public TrieNode getChild(char c) {
            if (children != null)
                for (TrieNode eachChild : children)
                    if (eachChild.data == c)
                        return eachChild;
            return null;
        }

        protected List<String> getWords() {
            List<String> list = new ArrayList<String>();
            if (isEnd) {
                list.add(toString());
            }

            if (children != null) {
                for (int i = 0; i < children.size(); i++) {
                    if (children.get(i) != null) {
                        list.addAll(children.get(i).getWords());
                    }
                }
            }
            return list;
        }
        public String toString() {
            if (parent == null) {
                return "";
            } else {
                return parent.toString() + new String(new char[] {data});
            }
        }
    }

    static class Trie {
        private TrieNode root;

        public Trie() {
            root = new TrieNode(' ');
        }

        public void insert(String word) {
            if (search(word) == true)
                return;

            TrieNode current = root;
            TrieNode pre ;
            for (char ch : word.toCharArray()) {
                pre = current;
                TrieNode child = current.getChild(ch);
                if (child != null) {
                    current = child;
                    child.parent = pre;
                } else {
                    current.children.add(new TrieNode(ch));
                    current = current.getChild(ch);
                    current.parent = pre;
                }
            }
            current.isEnd = true;
        }

        public boolean search(String word) {
            TrieNode current = root;
            for (char ch : word.toCharArray()) {
                if (current.getChild(ch) == null)
                    return false;
                else {
                    current = current.getChild(ch);
                }
            }
            if (current.isEnd == true) {
                return true;
            }
            return false;
        }

        public List<String> autocomplete(String prefix) {
            TrieNode lastNode = root;
            for (int i = 0; i< prefix.length(); i++) {
                lastNode = lastNode.getChild(prefix.charAt(i));
                if (lastNode == null)
                    return new ArrayList<String>();
            }
            return lastNode.getWords();
        }
    }
}
