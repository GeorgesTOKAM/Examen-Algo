import java.io.*;
import java.util.*;

public class Unigram {

    /**
     * Static strings of filename.
     */
    public static String FILE_NAME = "dicofile.txt";
    public static String OUT_FILE_NAME_ONE = "uniprobs.txt";

    private static List<String> myOutputList = new ArrayList<String>();

    /**
     * Number of lines written to a file.
     */
    public static int NUMBER_OF_LINES = 100;

    /**
     * Hashmap that stores a string word and their occurrences.
     */
    private static HashMap<String, Double> myHashMap;

    private static String resultNG = "";
    private static String resultNGF = "";
    private int N = 0;

    /**
     * Method that runs all other methods to find and write all probabilities
     * of words found in the file.
     */
    public static String startProbalities(String wordF) {
        myHashMap = new HashMap<String, Double>();
        final String str = readFile(FILE_NAME, false);
        countOccurrences(str, wordF);
        writeProbabilities();
        return resultNGF;
    }

    /**
     * Reads in a text file and returns it as a string.
     *
     * @param theFileName
     *            - file to become string.
     * @param isFormated
     *            - Will the returned string contain line breaks.
     * @return the string created from reading theFileName.
     */
    private static String readFile(final String theFileName, final boolean isFormated) {
        String str = "";
        try (Scanner sc = new Scanner(new File(theFileName));) {
            // "\Z" means "end of string"
            str = sc.useDelimiter("\\Z").next().toLowerCase().trim();
            // "\r" and "\n" are line breaks in linux and windows respectively.
            if (!isFormated) {
                str = str.replaceAll("\\r", " ").replaceAll("\\n", " ");
                str = str.replaceAll("\\s+", " ");
            }
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * Iterates though the given string and counts occurrence of each word.
     * Puts the data in a hashmap<TheWord, theNumberOfOccurences>.
     *
     * @param theString - the string being evaluated.
     */
    private static void countOccurrences(final String theString, String wordfind) {
        for (final String word : theString.split(" ")) {
            // Checks for empty string
            if (!"".equals(word.trim()) && word.trim().contains(wordfind)) {
                if (myHashMap.containsKey(word)) {
                    final double newValue = myHashMap.get(word).intValue() + 1;
                    myHashMap.put(word, newValue);
                } else {
                    myHashMap.put(word, 1.0);
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
            //myOutputList.add("P(" + pair.getKey() + ") = " + prob);
            resultNG += pair.getKey() + "\t   P = " + prob + "\n";
            /*if ( pair.getKey().toString().contains(wordfind)){
                resultNGF += pair.getKey() + "\t   P = " + prob + "\n";
            }*/
        }
    }
}
