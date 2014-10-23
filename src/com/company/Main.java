package com.company;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();
        HashMap<String, Integer> myDictionary = new HashMap<String, Integer>();
        HashMap<String, Integer> myStemmedDictionay = new HashMap<String, Integer>();
        String cranfield_path = "";
        Scanner in = new Scanner(System.in);

        // Get the path of Cranfield folder from the user
        System.out.println("Enter the complete path of the Cranfield documents directory");
        cranfield_path = in.next();

        // Part - 1 Tokenizing
        Tokenizer myTokenizer = new Tokenizer(cranfield_path);
        myDictionary = myTokenizer.tokenize();
        myTokenizer.getStats();

        // Part - 2 Stemming
        Stemmer s = new Stemmer(myTokenizer.getFileCount());

        for(Map.Entry<String, Integer> e: myDictionary.entrySet()) {
            String currentString = e.getKey();
            int currentCount = e.getValue();
            int stringLength = currentString.length();
            char[] w =  currentString.toCharArray();
            for(int i = 0; i < stringLength; i++) {
                s.add(w[i]);
            }
            s.stem();
            String u;
            u = s.toString();
            s.addStemToDictionary(u, currentCount);
        }
        s.getStats();
        final long endTime = System.currentTimeMillis();
        System.out.println("Total time taken : " + (endTime - startTime));
    }
}