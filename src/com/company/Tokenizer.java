package com.company;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Tokenizer {

    public static Logger log = Logger.getLogger(Tokenizer.class.getName());
    private HashMap<String, Integer> stopWords = createStopWordDictionary();
    private TreeMap<Integer, TreeMap<String, Integer>> complexDictionary = new TreeMap<Integer, TreeMap<String, Integer>>();
    private ArrayList<Integer> docLength = new ArrayList<Integer>();
    private TreeMap<String,TreeSet<Integer>> termFrequency = new TreeMap<String, TreeSet<Integer>>();
    private ArrayList<String> mostFrequentTermInADocument = new ArrayList<String>();
    private String cranfield_path = "";
    private int fileCount = 0;

    Tokenizer(String path) {
        this.cranfield_path = path;
    }

    public void tokenize() {
        // populate the file names of all document present inside the Cranfield directory in an ArrayList
        File cranfield_folder = new File(cranfield_path);
        File[] listOfFiles = cranfield_folder.listFiles();
        this.fileCount = listOfFiles.length;

        for(int i = 0; i < listOfFiles.length; i++) {
            log.setLevel(Level.INFO);
            log.info("Reading " + listOfFiles[i].getName());
            // Read each file and perform tokenization on each line except for the tags
            String fileName = listOfFiles[i].getAbsolutePath();
            TreeMap<String, Integer> docDictionary = new TreeMap<String, Integer>(); // keeps hold of the token and no of times the token has occurred in that document
            int documentTokenCount = 0;
            try {
                BufferedReader br = new BufferedReader(new FileReader(fileName));
                String currentLine;
                while((currentLine = br.readLine()) != null) {
                    String[] tokenizedLine = currentLine.split(" ");
                    for(String token : tokenizedLine) {
                        if (!token.startsWith("<")) { // eliminate all tokens that are tags
                            documentTokenCount = documentTokenCount + 1;
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

                                // add tokens to document dictionary and termFrequency
                                if(token != "" && token != null && token.length() > 2 && !token.matches(" ")) {
                                    int token_count = 0;
                                    if(docDictionary.containsKey(token)) {
                                        token_count = 1 + docDictionary.get(token);
                                        docDictionary.put(token, token_count);
                                    } else {
                                        docDictionary.put(token, 1);
                                    }

                                    if(termFrequency.containsKey(token)) {
                                        TreeSet<Integer> docList = termFrequency.get(token);
                                        docList.add(i);
                                        termFrequency.put(token, docList);
                                    } else {
                                        TreeSet<Integer> docList = new TreeSet<Integer>();
                                        docList.add(i);
                                        termFrequency.put(token, docList);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // find the maximum occurring term in a document
            Map.Entry<String, Integer> maxEntry = null;
            for(Map.Entry<String, Integer> entry : docDictionary.entrySet()) {
                if(maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                    maxEntry = entry;
                }
            }

            mostFrequentTermInADocument.add(maxEntry.getKey());

            complexDictionary.put(i, docDictionary); // add doc dictionary to complex dictionary
            docLength.add(documentTokenCount); // keeps track of document length
        }
    }

    public int getFileCount() {
        return this.fileCount;
    }

    public TreeMap<Integer, TreeMap<String, Integer>> getComplexDictionary() {
        return this.complexDictionary;
    }

    public TreeMap<String, TreeSet<Integer>> getTermFrequency() {
        return this.termFrequency;
    }

    private HashMap<String, Integer> createStopWordDictionary() {
        HashMap<String, Integer>  stopWords = new HashMap<String, Integer>();
        ArrayList<String> wordList = new ArrayList<String>(Arrays.asList("a", "all","an", "and", "any", "are", "as", "be", "been", "but", "by",
                "few", "for", "have", "he", "her", "here", "him", "his", "how", "i", "in", "is", "it", "its", "many", "me", "my", "none", "of",
                "on", "or", "our", "she", "some", "the", "their", "them", "there", "they", "that", "this", "us", "was", "what", "when","where",
                "which", "who", "why", "will", "with", "you", "your"));
        for(String word : wordList) {
            stopWords.put(word, 1);
        }
        return stopWords;
    }

    public void printComplexDictionary() {
        for(Map.Entry<Integer, TreeMap<String, Integer>> e : complexDictionary.entrySet()) {
            System.out.println("Doc Id = " + e.getKey());
            System.out.println("========================");
            TreeMap<String, Integer> currentDict = e.getValue();
            for(Map.Entry<String, Integer> f: currentDict.entrySet()) {
                System.out.println(f.getKey() + " -> " + f.getValue());
            }
            System.out.println("========================");
        }
        System.out.println();
    }

    public void printTermFrequency() {
        for (Map.Entry<String, TreeSet<Integer>> s : termFrequency.entrySet()){
            System.out.println(s.getKey() + "->" + s.getValue().toString());
        }
    }

}
