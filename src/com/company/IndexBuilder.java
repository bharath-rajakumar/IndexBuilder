package com.company;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class IndexBuilder {

    public IndexBuilder() {

    }

    public void buildUncompressedIndexVersionOne() {

    }

    public void buildUncompressedIndexVersionTwo(TreeMap<Integer, TreeMap<String, Integer>> stemmedDictionary, TreeMap<String, TreeSet<Integer>> stemmedTermFrequency) {
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
            System.out.println(tokenToWrite + ", " + documentFrequency + ", " + docListTermFrequency.toString());
        }
    }

    public void builCompressedIndexVersionOne() {

    }

    public void buildCompressedIndexVersionTwo() {

    }
}
