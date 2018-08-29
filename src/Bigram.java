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
    public static String DebutS = "<START>";
    public static String FinS = "<END>";
    private static HashMap<String, Node> myHashMap;

    public static String wordpred = "";


    public static String startProbalities(String wordF) {

        myHashMap = new HashMap<String, Node>(); // Initialisation de Hashmap.
        wordpred = "";
        final Node startSymbol = new Node(DebutS);
        myHashMap.put(DebutS, startSymbol);

        final String str = readFile(filedico, false); // Lire de fichier dans un String

        buildGraph(str); // Construire un graphique en utilisant chaque mot dans la chaîne.
        writeProbabilities(wordF); // calcul la probabilite

        return wordpred;
    }


    private static String readFile(final String theFileName, final boolean isFormated) {
        String str = "";
        str = getString(theFileName, isFormated, str);
        return str;
    }

    static String getString(String theFileName, boolean isFormated, String str) {
        try (Scanner sc = new Scanner(new File(theFileName));) {
            str = sc.useDelimiter("\\Z").next().toLowerCase().trim();
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
        processNextString(currentNode, FinS); // Ajouter le symbole de fin
    }

    private static void processNextString(final Node theCurrentNode, final String theNextString) {
        /*
         * Si le hashmap contient le nœud, on donne au nœud actuel un pointeur
         * à elle. sinon, on cré un nouveau nœud, l’ajoutons à hashmap et donne son
         * pointeur vers le nœud actuel.
         */
        if (myHashMap.containsKey(theNextString)) {
            theCurrentNode.processNextNode(myHashMap.get(theNextString));
        } else {
            final Node node = new Node(theNextString);
            myHashMap.put(theNextString, node);
            theCurrentNode.processNextNode(node);
        }
    }

    private static void writeProbabilities(String seW) {
        final List<Node> nodeList = new ArrayList<Node>(myHashMap.values());
        List<String> probList = new ArrayList<String>();

        // Gets probability for all nodes.
        for (final Node node : nodeList) {
            probList.addAll(node.calculateAllProbability(seW));
        }
        // shuffles results.
        Collections.shuffle(probList);
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
         * Le mot associé au noeud.
         */
        private final String myStringName;

        /**
         * Ce sont des tableaux parallèles pour suivre les nœuds des enfants et leur
         * nombre d'occurrences.
         */
        private final List<Node> myNodeList;
        private final List<Integer> myIntList;

        public Node(final String myStringName) {
            this.myStringName = myStringName;

            // Tableaux parallèles pour suivre tous les nœuds enfants.
            myNodeList = new ArrayList<Node>();
            myIntList = new ArrayList<Integer>();
        }

        public Double getProbability(final String word) {
            return (double) myIntList.get(getNodeIndex(word)) / getTotalOccurrences();
        }

        public void processNextNode(final Node noeudSuivant) {
            // if it does not contain the node, add it.
            if (!myNodeList.contains(noeudSuivant)) {
                addNode(noeudSuivant);
            }
            // if the node exists, increment count.
            else {
                incrementCount(noeudSuivant);
            }
        }

        /**
         * Ajouter le noeud a myNodeArray
         *
         * @param noeudSuinant
         */
        public void addNode(final Node noeudSuinant) {
            myNodeList.add(noeudSuinant);
            myIntList.add(1);
        }

        /**
         * Incrémente le nombre de nœuds dans myIndexArray.
         * @param noeudSuivant
         */
        public void incrementCount(final Node noeudSuivant) {
            final int index = myNodeList.indexOf(noeudSuivant);
            myIntList.set(index, myIntList.get(index) + 1);
        }

        /**
         * Obtient le nombre total de pointeurs en itération via myIntArray.
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
         * Calcule la probabilité en divisant chaque nombre d'occurrences pour le
         * nœud enfant, divisé par le nombre total de fois que tous les enfants se produisent.
         * Trouve aussi tous les mots et mot suivant rechercher.
         *
         * @return String contenant toutes les probabilites calculer.
         */
        public List<String> calculateAllProbability(String seaw) {
            final List<String> probList = new ArrayList<String>();
            final double total = getTotalOccurrences();
            int index = 0;
            for (final Node node : myNodeList) {
                final double prob = (double) myIntList.get(index) / total;
                if(myStringName.toString().contains(seaw)){
                    wordpred += myStringName + " " + node.myStringName + "\n";
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
         * Obtient l'index d'un noeud.
         *
         * @param nomNoeud - nom du noeud que nous recherchons.
         * @return Index du noeud dans myNodeArray.
         */
        public int getNodeIndex(final String nomNoeud){
            int i = -1;
            for (i = 0; i < myNodeList.size(); i++) {
                if (nomNoeud.equals(myNodeList.get(i).getMyStringName())) {
                    break;
                }
            }
            return i;
        }

    }
}
