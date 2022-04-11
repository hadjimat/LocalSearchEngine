package main.Lemmatisator;

import main.model.Lemma;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.*;


public class Lemmatisator {



    LuceneMorphology luceneMorph = new RussianLuceneMorphology();

    public Lemmatisator() throws IOException {
    }

    public HashMap<String, Float> getLemmasOnField(String text) throws IOException {
        String[] splitText = text.toLowerCase().replaceAll("[^а-яА-Я_\\s]", "").replaceAll("[0-9]","").split(" ");
        HashMap<String, Float> wordCountMap = new HashMap<String, Float>();
        for (String word : splitText) {
            if (isCorrectWordType(word) && !word.isEmpty()) {
                List<String> wordBaseForms = luceneMorph.getNormalForms(word);
                for (String lemma : wordBaseForms) {
                    wordCountMap.put(lemma, wordCountMap.getOrDefault(lemma, 0f) + 1);
                }
            }
        }
        return wordCountMap;
    }

    public Set<String> getLemmaSet(String text) {
        String[] textArray = text.toLowerCase().replaceAll("[^а-яА-Я_\\s]", "").replaceAll("[0-9]","").split(" ");
        Set<String> lemmaSet = new HashSet<>();
        for (String word : textArray) {
            if (isCorrectWordType(word) && !word.isEmpty()) {
                List<String> wordBaseForms = luceneMorph.getNormalForms(word);
                lemmaSet.addAll(wordBaseForms);
            }
        }
        return lemmaSet;
    }

    public HashMap<String, Float> calculateLemmasRank(Map<String, Lemma> lemmas,
                                                      HashMap<String, Float> titleFieldLemmas,
                                                      HashMap<String, Float> bodyFieldLemmas) {
        HashMap<String, Float> lemmasAndRankMap = new HashMap<>();
        for (Map.Entry<String, Lemma> lemma : lemmas.entrySet()) {
            float rank = titleFieldLemmas.getOrDefault(lemma.getKey(), 0f) * 1.0f
                    + bodyFieldLemmas.getOrDefault(lemma.getKey(), 0f) * 0.8f;
            lemmasAndRankMap.put(lemma.getKey(), rank);
        }

        return lemmasAndRankMap;
    }

    private boolean isCorrectWordType(String word) {
        try {
            List<String> wordInfo = luceneMorph.getMorphInfo(word);
            for (String morphInfo : wordInfo) {
                String wordTypeRegex = ".*(СОЮЗ|МЕЖД|ПРЕДЛ|ЧАСТ)$";
                if (morphInfo.matches(wordTypeRegex)) {
                    return false;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println(word);
        }
        return true;
    }
}
