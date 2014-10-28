package com.company;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class IndexBuilder {
    int sizeUncompOne = 0;
    int sizeUncompTwo = 0;
    int sizeCompOne = 0;
    int sizeCompTwo = 0;

    TreeMap<Integer, TreeMap<String, Integer>> lemmatizedDictionary = new TreeMap<Integer, TreeMap<String, Integer>>();
    TreeMap<String, TreeSet<Integer>> lemmatizedTermFrequency = new TreeMap<String, TreeSet<Integer>>();
    TreeMap<String, String> lemmatized = new TreeMap<String, String>();
    ArrayList<Integer> invertedListCount = new ArrayList<Integer>();

    public int buildUncompressedIndexVersionOne(TreeMap<Integer, TreeMap<String, Integer>> myDictionary, TreeMap<String, TreeSet<Integer>> tokenizedTermFrequency) {
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
                sizeUncompOne = sizeUncompOne + tokenToWrite.length();
                int documentFrequency = e.getValue().size();
                outputStream.write(tokenToWrite.getBytes());
                outputStream.write("|".getBytes());
                outputStream.write(Integer.toString(documentFrequency).getBytes());
                sizeUncompOne = sizeUncompOne + (Integer.SIZE/Byte.SIZE);
                outputStream.write("|".getBytes());
                int count = 0;
                for(int i : e.getValue()) {
                    count = count + 1;
                    String docId = Integer.toString(i);
                    int occurrence = lemmatizedDictionary.get(i).get(tokenToWrite);
                    outputStream.write(docId.getBytes());
                    outputStream.write("|".getBytes());
                    outputStream.write((Integer.toString(occurrence)).getBytes());
                    if(count == e.getValue().size() - 1){
                        outputStream.write(">".getBytes());
                    } else {
                        outputStream.write("|".getBytes());
                    }
                }
                sizeUncompOne = sizeUncompOne + 2 * e.getValue().size() * (Integer.SIZE/Byte.SIZE);
            }
            outputStream.close();
        } catch (IOException e) {
            System.out.println(e);
        }
        invertedListCount.add(lemmatizedTermFrequency.size());
        return sizeUncompOne;
    }

    public int buildUncompressedIndexVersionTwo(TreeMap<Integer, TreeMap<String, Integer>> stemmedDictionary, TreeMap<String, TreeSet<Integer>> stemmedTermFrequency) {
        // write the uncompressed postings to a file
        ObjectOutputStream outputStream = null;
        try {
            outputStream =  new ObjectOutputStream(new FileOutputStream("Uncompressed_Ver_2"));
            for(Map.Entry<String, TreeSet<Integer>> e : stemmedTermFrequency.entrySet()) {
                String tokenToWrite = e.getKey();
                sizeUncompTwo = sizeUncompTwo + tokenToWrite.length();
                int documentFrequency = e.getValue().size();
                outputStream.write(tokenToWrite.getBytes());
                outputStream.write("|".getBytes());
                outputStream.write(Integer.toString(documentFrequency).getBytes());
                sizeUncompTwo = sizeUncompTwo + (Integer.SIZE/Byte.SIZE);
                outputStream.write("|".getBytes());
                int count = 0;
                for(int i : e.getValue()) {
                    count = count + 1;
                    String docId = Integer.toString(i);
                    int occurrence = stemmedDictionary.get(i).get(tokenToWrite);
                    outputStream.write(docId.getBytes());
                    outputStream.write("|".getBytes());
                    outputStream.write(Integer.toString(occurrence).getBytes());
                    if(count == e.getValue().size() - 1){
                        outputStream.write(">".getBytes());
                    } else {
                        outputStream.write("|".getBytes());
                    }
                }
                sizeUncompTwo = sizeUncompTwo + 2 * e.getValue().size() * (Integer.SIZE/Byte.SIZE);
            }
            outputStream.close();
        } catch(IOException e) {
            System.out.println(e);
        }
        invertedListCount.add(stemmedTermFrequency.size());
        return sizeUncompTwo;
    }

    public int builCompressedIndexVersionOne(String filename) {
        Encoder myEncoder = new Encoder();
        // write the uncompressed postings to a file
        ObjectOutputStream outputStream = null;
        sizeCompOne = 0;
        try {
            outputStream =  new ObjectOutputStream(new FileOutputStream(filename));
            for(Map.Entry<String, TreeSet<Integer>> e : lemmatizedTermFrequency.entrySet()) {
                String tokenToWrite = e.getKey();
                String docFrequency = myEncoder.gammaCode(e.getValue().size());
                byte[] docFrequencyEncoded = myEncoder.convertToByteArray(docFrequency);
                ArrayList<Integer> docList = new ArrayList<Integer>();
                docList.addAll(e.getValue());
                ArrayList<Integer> docGapList = new ArrayList<Integer>();
                docGapList.addAll(buildDocGap(e.getValue()));

                outputStream.write(tokenToWrite.getBytes());
                sizeCompOne = sizeCompOne + tokenToWrite.length();
                outputStream.write(("|").getBytes());
                outputStream.write(docFrequencyEncoded);
                sizeCompOne = sizeCompOne + docFrequencyEncoded.length;
                outputStream.write(("|").getBytes());

                for (int i = 0; i < e.getValue().size(); i++) {
                    String docGap = myEncoder.deltaCode(docGapList.get(i));
                    byte[] docGapEncoded = myEncoder.convertToByteArray(docGap);
                    String occurrence = myEncoder.gammaCode(lemmatizedDictionary.get(docList.get(i)).get(tokenToWrite));
                    byte[] occurrenceEncoded = myEncoder.convertToByteArray(occurrence);
                    outputStream.write(docGapEncoded);
                    sizeCompOne = sizeCompOne + docGapEncoded.length;
                    outputStream.write(("|").getBytes());
                    outputStream.write(occurrenceEncoded);
                    sizeCompOne = sizeCompOne + occurrenceEncoded.length;
                    if( i == e.getValue().size() - 1) {
                        outputStream.write((">").getBytes());
                    } else {
                        outputStream.write(("|").getBytes());
                    }
                }
            }
            outputStream.close();
        } catch(IOException e) {
            System.out.println(e);
        }
        return sizeCompOne;
    }

    public int buildCompressedIndexVersionTwo(TreeMap<Integer, TreeMap<String, Integer>> stemmedDictionary, TreeMap<String, TreeSet<Integer>> stemmedTermFrequency, String filename ) {
        Encoder myEncoder = new Encoder();
        // write the uncompressed postings to a file
        ObjectOutputStream outputStream = null;
        sizeCompTwo = 0;
        try {
            outputStream =  new ObjectOutputStream(new FileOutputStream(filename));
            for(Map.Entry<String, TreeSet<Integer>> e : stemmedTermFrequency.entrySet()) {
                String tokenToWrite = e.getKey();
                String docFrequency = myEncoder.gammaCode(e.getValue().size());
                byte[] docFrequencyEncoded = myEncoder.convertToByteArray(docFrequency);
                ArrayList<Integer> docList = new ArrayList<Integer>();
                docList.addAll(e.getValue());
                ArrayList<Integer> docGapList = new ArrayList<Integer>();
                docGapList.addAll(buildDocGap(e.getValue()));

                outputStream.write(tokenToWrite.getBytes());
                sizeCompTwo = sizeCompTwo + tokenToWrite.length();
                outputStream.write(("|").getBytes());
                outputStream.write(docFrequencyEncoded);
                sizeCompTwo = sizeCompTwo + docFrequencyEncoded.length;
                outputStream.write(("|").getBytes());

                for (int i = 0; i < e.getValue().size(); i++) {
                    String docGap = myEncoder.gammaCode(docGapList.get(i));
                    byte[] docGapEncoded = myEncoder.convertToByteArray(docGap);
                    String occurrence = myEncoder.gammaCode(stemmedDictionary.get(docList.get(i)).get(tokenToWrite));
                    byte[] occurrenceEncoded = myEncoder.convertToByteArray(occurrence);
                    outputStream.write(docGapEncoded);
                    sizeCompTwo = sizeCompTwo + docGapEncoded.length;
                    outputStream.write(("|").getBytes());
                    outputStream.write(occurrenceEncoded);
                    sizeCompTwo = sizeCompTwo + occurrenceEncoded.length;
                    if( i == e.getValue().size() - 1) {
                        outputStream.write((">").getBytes());
                    } else {
                        outputStream.write(("|").getBytes());
                    }
                }
            }
            outputStream.close();
        } catch(IOException e) {
            System.out.println(e);
        }
        return sizeCompTwo;
    }

    private ArrayList<Integer> buildDocGap(TreeSet<Integer> docList) {
        ArrayList<Integer> tempList = new ArrayList<Integer>();
        tempList.addAll(docList);
        ArrayList<Integer> docGap = new ArrayList<Integer>();
        int previous = 0;
        for(int i = 0; i < tempList.size(); i++) {
            int entry = tempList.get(i) - previous;
            docGap.add(entry);
            previous = tempList.get(i);
        }
        return docGap;
    }

    public ArrayList<Integer> getInvertedListCount(){
        return invertedListCount;
    }
}
