package com.company;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Tokenizer {

    private int totalCount = 0;
    public static Logger log = Logger.getLogger(Tokenizer.class.getName());
    private HashMap<String, Integer> dictionary = new HashMap<String, Integer>();
    private HashMap<String, Integer> stopWords = createStopWordDictionary();
    private String cranfield_path = "";
    private int fileCount = 0;
    private double avgTokensPerDoc = 0.0;

    Tokenizer(String path) {
        this.cranfield_path = path;
    }

    public HashMap<String, Integer> tokenize() {
        // populate the file names of all document present inside the Cranfield directory in an ArrayList
        File cranfield_folder = new File(cranfield_path);
        File[] listOfFiles = cranfield_folder.listFiles();
        this.fileCount = listOfFiles.length;

        for(int i = 0; i < listOfFiles.length; i++) {
            log.setLevel(Level.INFO);
            log.info("Reading " + listOfFiles[i].getName());
            // Read each file and perform tokenization on each line except for the tags
            String fileName = listOfFiles[i].getAbsolutePath();
            try {
                BufferedReader br = new BufferedReader(new FileReader(fileName));
                String currentLine;
                while((currentLine = br.readLine()) != null) {
                    //System.out.println(currentLine);
                    String[] tokenizedLine = currentLine.split(" ");
                    for(String token : tokenizedLine) {
                        if (!token.startsWith("<")) { // eliminate all tokens that are tags
                            if (!stopWords.containsKey(token)) { // eliminate all tokens that are present in stopword dictionary
                                token = token.toLowerCase(); // lowercase all tokens

                                // remove all dot symbols ( . )
                                token = token.replaceAll("\\.","");

                                // remove all coma symbols ( , )
                                token = token.replaceAll(",","");

                                // remove all hyphen symbols ( - )
                                token = token.replaceAll("-","");

                                // remove all close brace symbols ( ) )
                                token = token.replaceAll("\\)","");

                                // remove all open brace symbols ( ( )
                                token = token.replaceAll("\\(","");

                                // remove all forward slash symbols ( / )
                                token = token.replaceAll("/","");

                                // remove all backward slash symbols ( \ )
                                token = token.replaceAll("\\\\","");

                                // remove all possessive symbols ( ' )
                                token = token.replaceAll("'","");

                                // add tokens to dictionary
                                if(token != "" && token != null && token.length() > 2 && !token.matches(" ")) {
                                    addTokenToDictionary(token);
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return this.dictionary;
    }

    public void getStats() {
        System.out.println("Tokenization - Statistics");
        System.out.println("1. Total no of tokens = " + this.totalCount);
        System.out.println("2. Number of unique tokens = " + this.dictionary.size());
        System.out.println("3. Number of tokens that occur only once = "+  WordOccurence(1));
        System.out.println("4. 30 most frequent word tokens in Cranfield text collection are : ");
        frequentWords(30);
        this.avgTokensPerDoc = (double)this.totalCount/(double)this.fileCount;
        System.out.println("5. Average tokens per document = " + this.avgTokensPerDoc);
    }

    public int getFileCount() {
        return this.fileCount;
    }

    private int WordOccurence(int n) {
        int countOnce = 0;
        for(Map.Entry<String, Integer> e: this.dictionary.entrySet()) {
            if (e.getValue() == 1) {
                countOnce = countOnce + 1;
            }
        }
        return countOnce;
    }

    private void frequentWords(int n) {
        PriorityQueue<Pair> top = new PriorityQueue<Pair>(n, new Comparator<Pair>() {
            @Override
            public int compare(Pair p1, Pair p2) {
                if (p1.frequency > p2.frequency) {
                    return 1;
                }

                if (p1.frequency < p2.frequency) {
                    return -1;
                }

                return 0;
            }
        });

        for(Map.Entry<String, Integer> e : this.dictionary.entrySet()){
            Pair tempPair = new Pair(e.getKey(), e.getValue());
            if(top.size() == n) {
                top.add(tempPair);
                top.poll();
            } else {
                top.add(tempPair);
            }
        }

        while(top.size() > 0) {
            String content = top.poll().toString();
            System.out.println(content);
        }
    }

    private void addTokenToDictionary(String token) {
        int token_count = 0;
        this.totalCount = this.totalCount + 1;
        if(this.dictionary.containsKey(token)) {
            token_count = this.dictionary.get(token) + 1;
            this.dictionary.put(token, token_count);
        } else {
            this.dictionary.put(token, 1);
        }
    }

    private HashMap<String, Integer> createStopWordDictionary() {
        HashMap<String, Integer>  stopWords = new HashMap<String, Integer>();
        ArrayList<String> wordList = new ArrayList<String>(Arrays.asList("a", "an", "and", "are", "as", "at", "be", "by",
                "for", "from", "has", "he", "in", "is", "its", "it", "of", "on", "she", "that", "the", "to", "was", "were", "will",
                "with"));
        for(String word : wordList) {
            stopWords.put(word, 1);
        }
        return stopWords;
    }

}

class Pair{
    String theWord = "";
    int frequency = 0;

    Pair(String word, int count) {
        this.theWord = word;
        this.frequency = count;
    }

    public String getWord() {
        return this.theWord;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public String toString() {
        return this.theWord + " - " + this.getFrequency();
    }
}
