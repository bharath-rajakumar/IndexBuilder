package com.company;

import java.util.*;

public class Main {

    public static Stemmer s;
    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();
        TreeMap<Integer, TreeMap<String, Integer>> myDictionary;
        TreeMap<Integer, TreeMap<String, Integer>> myStemmedDictionary = new TreeMap<Integer, TreeMap<String, Integer>>();
        TreeMap<String, String> stemmed = new TreeMap<String, String>(); //keeps track of stemmed token for a non-stemmed token
        TreeMap<String, TreeSet<Integer>> tokenizedTermFrequency;
        TreeMap<String, TreeSet<Integer>> stemmedTermFrequency = new TreeMap<String, TreeSet<Integer>>();
        String cranfield_path = "";
        Scanner in = new Scanner(System.in);

        // Get the path of Cranfield folder from the user
        System.out.println("Enter the complete path of the Cranfield documents directory");
        cranfield_path = in.next();

        // Part - 1 Tokenizing
        Tokenizer myTokenizer = new Tokenizer(cranfield_path);
        myTokenizer.tokenize();
        myDictionary = myTokenizer.getComplexDictionary();
        tokenizedTermFrequency = myTokenizer.getTermFrequency();

        // Part - 2 Stemming
        s = new Stemmer(myTokenizer.getFileCount());
        for(Map.Entry<Integer, TreeMap<String, Integer>> e : myDictionary.entrySet()) {
            int docId = e.getKey();
            TreeMap<String, Integer> currentDict = e.getValue();
            for(Map.Entry<String, Integer> f: currentDict.entrySet()){
                // Check if the word has been stemmed previously
                String stemmedToken = "";
                String token = f.getKey();
                int tokenOccurence = f.getValue();
                if(stemmed.containsKey(token)){
                    stemmedToken = stemmed.get(token);
                } else {
                    // do the stemming and add it to this dictionary
                    stemmedToken = stemThisToken(token);
                    stemmed.put(token, stemmedToken);
                }

                // add the entry to stemmedDictionary
                if(myStemmedDictionary.containsKey(docId)) {
                    TreeMap<String, Integer> tempEntry = myStemmedDictionary.get(docId);
                    if(tempEntry.containsKey(stemmedToken)) {
                        int occurence = tokenOccurence + tempEntry.get(stemmedToken);
                        tempEntry.remove(token);
                        tempEntry.put(stemmedToken, occurence);
                    } else {
                        int occurence = tokenOccurence;
                        tempEntry.put(stemmedToken, occurence);
                    }
                    myStemmedDictionary.put(docId, tempEntry);
                } else {
                    TreeMap<String, Integer> tempEntry = new TreeMap<String, Integer>();
                    int occurence = currentDict.get(token);
                    tempEntry.put(stemmedToken, occurence);
                    myStemmedDictionary.put(docId, tempEntry);
                }

                // create stemmed Term Frequency
                if(stemmedTermFrequency.containsKey(stemmedToken)){
                    TreeSet<Integer> termFrequency = stemmedTermFrequency.get(stemmedToken);
                    // merge this set of TF's with another token's TF that has the same stemmed token
                    TreeSet<Integer> anotherTermFrequency = new TreeSet<Integer>();
                    anotherTermFrequency.addAll(tokenizedTermFrequency.get(token));
                    termFrequency.addAll(anotherTermFrequency);
                    stemmedTermFrequency.put(stemmedToken, termFrequency);
                } else {
                    TreeSet<Integer> anotherTermFrequency = new TreeSet<Integer>();
                    anotherTermFrequency.addAll(tokenizedTermFrequency.get(token));
                    stemmedTermFrequency.put(stemmedToken, anotherTermFrequency);
                }
            }
        }

        IndexBuilder myIndexBuilder = new IndexBuilder();

        // Build Uncompressed Index Version One
        myIndexBuilder.buildUncompressedIndexVersionOne(myDictionary, tokenizedTermFrequency);

        // Build Uncompressed Index Version Two
        myIndexBuilder.buildUncompressedIndexVersionTwo(myStemmedDictionary, stemmedTermFrequency);

        // Build Compressed Index Version One

        // Build Compressed Index Version Two

    }

    private static String stemThisToken(String token) {
        String currentString = token;
        int stringLength = currentString.length();
        char[] v = currentString.toCharArray();
        for(int i = 0; i < stringLength; i++) {
            s.add(v[i]);
        }
        s.stem();
        return s.toString();
    }
}
