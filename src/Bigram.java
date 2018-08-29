import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;


public class Bigram {

    public static String filedico = "dicofile.txt";
    public static String fileoutbiprobs = "biprobs.txt";
    public static String DebutS = "<START>";
    public static String FinS = "<END>";
    private static HashMap<String, Node> myHashMap;

    public static String wordpred = "";


    public static String startProbalities(String wordF) {
        // Initialize Hashmap.
        myHashMap = new HashMap<String, Node>();
        wordpred = "";
        final Node startSymbol = new Node(DebutS);
        myHashMap.put(DebutS, startSymbol);
        // Read in File to String
        final String str = readFile(filedico, false);
        // Build graph using each word in string.
        buildGraph(str);
        // Calculates probability and write to file.
        writeProbabilities(fileoutbiprobs, wordF);
        return wordpred;
    }


    private static String readFile(final String theFileName, final boolean isFormated) {
        String str = "";
        str = getString(theFileName, isFormated, str);
        return str;
    }

    static String getString(String theFileName, boolean isFormated, String str) {
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

    private static void buildGraph(final String theString) {
        Node currentNode = myHashMap.get(DebutS);
        for (final String word : theString.split(" ")) {
            // Checks for empty string
            if (!"".equals(word.trim())) {
                processNextString(currentNode, word);
                currentNode = myHashMap.get(word);
            }
        }
        // adds end symbol
        processNextString(currentNode, FinS);
    }

    private static void processNextString(final Node theCurrentNode, final String theNextString) {
        /*
         * if the hashmap contains the node, we give the current node a pointer
         * to it. else we create a new node, add it to hashmap, and gives its
         * pointer to the current node.
         */
        if (myHashMap.containsKey(theNextString)) {
            theCurrentNode.processNextNode(myHashMap.get(theNextString));
        } else {
            final Node node = new Node(theNextString);
            myHashMap.put(theNextString, node);
            theCurrentNode.processNextNode(node);
        }
    }

    private static void writeProbabilities(final String theOutfileName, String seW) {
        final List<Node> nodeList = new ArrayList<Node>(myHashMap.values());
        List<String> probList = new ArrayList<String>();

        // Gets probability for all nodes.
        for (final Node node : nodeList) {
            probList.addAll(node.calculateAllProbability(seW));
        }
        // shuffles results.
        Collections.shuffle(probList);
        // writes all probabilities to file.
        writeFile(probList, fileoutbiprobs);
    }

    private static void writeFile(final List<?> theStringToWrite, final String theOutFileName){
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(theOutFileName), "utf-8"))) {
            for (final Object string : theStringToWrite) {
                writer.write(string.toString());
                writer.newLine();
            }
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    static class Node {

        /**
         * The word associated with the node.
         */
        private final String myStringName;

        /**
         * These are parallel Arrays to keep track of children nodes and their
         * number of occurrences.
         */
        private final List<Node> myNodeList;
        private final List<Integer> myIntList;

        public Node(final String myStringName) {
            this.myStringName = myStringName;

            // Parallel Arrays to keep track of all children nodes.
            myNodeList = new ArrayList<Node>();
            myIntList = new ArrayList<Integer>();
        }

        public Double getProbability(final String word) {
            return (double) myIntList.get(getNodeIndex(word)) / getTotalOccurrences();
        }

        public void processNextNode(final Node theNextNode) {
            // if it does not contain the node, add it.
            if (!myNodeList.contains(theNextNode)) {
                addNode(theNextNode);
            }
            // if the node exists, increment count.
            else {
                incrementCount(theNextNode);
            }
        }

        /**
         * Adds node to myNodeArray
         *
         * @param theNextNode
         *            - the node following our current node (this).
         */
        public void addNode(final Node theNextNode) {
            myNodeList.add(theNextNode);
            myIntList.add(1);
        }

        /**
         * Increments count for node in myIndexArray.
         *
         * @param theNextNode
         *            - the node following our current node (this).
         */
        public void incrementCount(final Node theNextNode) {
            final int index = myNodeList.indexOf(theNextNode);
            myIntList.set(index, myIntList.get(index) + 1);
        }

        /**
         * Gets the total number of out pointers by iterating through
         * myIntArray.
         *
         * @return the total number of occurrences in myIntList.
         */
        public int getTotalOccurrences() {
            int total = 0;
            for (final Integer integer : myIntList) {
                total += integer;
            }
            return total;
        }

        /**
         * Calculates probability by dividing each number of occurrences for the
         * child node, divided by the total number of times all children occur.
         *
         * @return String containing all probabilities calculated fot this node.
         */
        public List<String> calculateAllProbability(String seaw) {
            final List<String> probList = new ArrayList<String>();
            final double total = getTotalOccurrences();
            int index = 0;
            for (final Node node : myNodeList) {
                final double prob = (double) myIntList.get(index) / total;
                probList.add("P(" + node.myStringName + "|" + myStringName + ") = " + prob);
                if(node.myStringName.toString().contains(seaw)){
                    wordpred += node.myStringName + " " + myStringName + "\n";
                }
                index += 1;
            }
            return probList;
        }

        /**
         * @return myStringName
         */
        public String getMyStringName() {
            return myStringName;
        }

        /**
         * Checks if the name of the node matches any of the children nodes
         * stored in myNodeArray.
         *
         * @param theChildName - name of the node we're trying to find.
         * @return true or false if the node is in myNodeArray.
         */
        public boolean isChild(final String theChildName){
            for (final Node node : myNodeList) {
                if (theChildName.equals(node.getMyStringName())){
                    return true;
                }
            }
            return false;
        }

        /**
         * Gets the index of a node given its name.
         *
         * @param theNodeName - name of the node we're searching for.
         * @return the index of the node in myNodeArray.
         */
        public int getNodeIndex(final String theNodeName){
            int i = -1;
            for (i = 0; i < myNodeList.size(); i++) {
                if (theNodeName.equals(myNodeList.get(i).getMyStringName())) {
                    break;
                }
            }
            return i;
        }

    } // End node class
}
