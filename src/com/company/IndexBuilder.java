package com.company;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class IndexBuilder {

    TreeMap<Integer, TreeMap<String, Integer>> lemmatizedDictionary = new TreeMap<Integer, TreeMap<String, Integer>>();
    TreeMap<String, TreeSet<Integer>> lemmatizedTermFrequency = new TreeMap<String, TreeSet<Integer>>();
    TreeMap<String, String> lemmatized = new TreeMap<String, String>();

    public void buildUncompressedIndexVersionOne(TreeMap<Integer, TreeMap<String, Integer>> myDictionary, TreeMap<String, TreeSet<Integer>> tokenizedTermFrequency) {
        Lemma lemma = new Lemma();
        for(Map.Entry<Integer, TreeMap<String, Integer>> e : myDictionary.entrySet()) {
            int docId = e.getKey();
            TreeMap<String, Integer> currentDict = e.getValue();
            for(Map.Entry<String, Integer> f: currentDict.entrySet()){
                // Check if the word has been lemmatized previously
                String lemmatizedToken=null;
                String token = f.getKey();
                int tokenOccurence = f.getValue();

                if(lemmatized.containsKey(token)){
                    lemmatizedToken = lemmatized.get(token);
                } else {
                    // find the lemma and add it to this dictionary
                    lemmatizedToken = lemma.lemmatize(token);
                    lemmatized.put(token, lemmatizedToken);
                }

                // add the entry to lemmatizeded dictionary
                if(lemmatizedDictionary.containsKey(docId)) {
                    TreeMap<String, Integer> tempEntry = lemmatizedDictionary.get(docId);
                    if(tempEntry.containsKey(lemmatizedToken)) {
                        int occurence = tokenOccurence + tempEntry.get(lemmatizedToken);
                        tempEntry.remove(token);
                        tempEntry.put(lemmatizedToken, occurence);
                    } else {
                        int occurence = tokenOccurence;
                        tempEntry.put(lemmatizedToken, occurence);
                    }
                    lemmatizedDictionary.put(docId, tempEntry);
                } else {
                    TreeMap<String, Integer> tempEntry = new TreeMap<String, Integer>();
                    int occurence = currentDict.get(token);
                    tempEntry.put(lemmatizedToken, occurence);
                    lemmatizedDictionary.put(docId, tempEntry);
                }

                // create lemmatized Term Frequency
                if(lemmatizedTermFrequency.containsKey(lemmatizedToken)){
                    TreeSet<Integer> termFrequency = lemmatizedTermFrequency.get(lemmatizedToken);
                    // merge this set of TF's with another token's TF that has the same lemma token
                    TreeSet<Integer> anotherTermFrequency = tokenizedTermFrequency.get(token);
                    termFrequency.addAll(anotherTermFrequency);
                    lemmatizedTermFrequency.put(lemmatizedToken, termFrequency);
                } else {
                    lemmatizedTermFrequency.put(lemmatizedToken, tokenizedTermFrequency.get(token));
                }
            }
        }

        // write the uncompressed postings to a file
        ObjectOutputStream outputStream = null;
        try {
            outputStream =  new ObjectOutputStream(new FileOutputStream("Uncompressed_Ver_1"));
            //  Iterate document wise for a given token
            for(Map.Entry<String, TreeSet<Integer>> e : lemmatizedTermFrequency.entrySet()) {
                String tokenToWrite = e.getKey();
                int documentFrequency = e.getValue().size();
                ArrayList<String> docListTermFrequency = new ArrayList<String>();
                for(int i : e.getValue()) {
                    String docId = Integer.toString(i);
                    int occurrence = lemmatizedDictionary.get(i).get(tokenToWrite);
                    String pair = docId + ":" + Integer.toString(occurrence);
                    docListTermFrequency.add(pair);
                }
                outputStream.write(tokenToWrite.getBytes());
                outputStream.write((", "+documentFrequency).getBytes());
                outputStream.write((", "+docListTermFrequency.toString()+"\n").getBytes());
            }
            outputStream.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void buildUncompressedIndexVersionTwo(TreeMap<Integer, TreeMap<String, Integer>> stemmedDictionary, TreeMap<String, TreeSet<Integer>> stemmedTermFrequency) {
        // write the uncompressed postings to a file
        ObjectOutputStream outputStream = null;
        try {
            outputStream =  new ObjectOutputStream(new FileOutputStream("Uncompressed_Ver_2"));
            //  Iterate document wise for a given token
            for(Map.Entry<String, TreeSet<Integer>> e : stemmedTermFrequency.entrySet()) {
                String tokenToWrite = e.getKey();
                int documentFrequency = e.getValue().size();
                ArrayList<String> docListTermFrequency = new ArrayList<String>();
                for(int i : e.getValue()) {
                    String docId = Integer.toString(i);
                    int occurrence = stemmedDictionary.get(i).get(tokenToWrite);
                    String pair = docId + ":" + Integer.toString(occurrence);
                    docListTermFrequency.add(pair);
                }
                outputStream.write(tokenToWrite.getBytes());
                outputStream.write((", "+documentFrequency).getBytes());
                outputStream.write((", "+docListTermFrequency.toString()).getBytes());
            }
            outputStream.close();
        } catch(IOException e) {
            System.out.println(e);
        }
    }

    public void builCompressedIndexVersionOne() {

    }

    public void buildCompressedIndexVersionTwo() {

    }
}
