public class BruteForce {

    public String BForce(String alltext, String findtext) {

        String [] words = alltext.split(" ");

        //ArrayList<String> results = new ArrayList<String>();
        String results = "";

        for(int i = 0; i < words.length; i++){
            if (words[i].contains(findtext)) {
                results += words[i] +  "\n";
            }
        }
        return results;
    }

    /*public String brtforce (String P, String T) {
        // P mot a rechercher et T liste de tous les mots
        String ltpos = "";	//  liste des position
        String ltmot = ""; // liste des mots trouve
        int m = P.length(); // taille mot a recherche
        int n = T.length(); // taille de tous les mots
        long start = System.currentTimeMillis();
        for(int i = 0; i< n-m; i++) {
            int j = 0;
            while((j < m) && (T.charAt(i+j)== P.charAt(j))) {
                j++;
            }
            if(j == m) {
                ltpos += String.valueOf(i) + "\n";
                ltmot += T.substring(i, i+5)+ "\n";
            }
        }
        long end = System.currentTimeMillis();

        System.out.println();
        System.out.println("Taille mot : " + m + " - " + "Taille Dico : "
                + n + " - Temps d'execution = " + (end-start) + " ms"
                + " - Taille memoire : " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
        return ltmot;
    }*/
}
