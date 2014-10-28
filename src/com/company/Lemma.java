package com.company;

import java.io.*;
import java.util.*;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.*;

public class Lemma {
    Properties props;
    StanfordCoreNLP pipeline;

    public Lemma() {
        this.props = new Properties();
        this.props.put("annotators", "tokenize, ssplit, pos, lemma");
        this.pipeline = new StanfordCoreNLP();
    }

    public String lemmatize(String inputString) {
        String LemmatizedString = null;
        Annotation annotation = new Annotation(inputString);
        this.pipeline.annotate(annotation);
        List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            // Iterate over all tokens in a sentence
            if(sentence.get(TokensAnnotation.class).size() == 1) {
                for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                    // Retrieve and add the lemma for each word into the
                    // list of lemmas
                    LemmatizedString = token.get(LemmaAnnotation.class);
                }
            } else {
                LemmatizedString = inputString;
            }
        }
        return LemmatizedString;
    }
}
