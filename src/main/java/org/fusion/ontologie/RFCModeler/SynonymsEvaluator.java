package org.fusion.ontologie.RFCModeler;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;
import edu.mit.jwi.morph.WordnetStemmer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SynonymsEvaluator {

    private IDictionary dictionary;
    //    private IDictionary dictStanford;
//    private IDictionary dictPrinceton;
    private WordnetStemmer stemmer;

    private List<String> synonyms;
    private String toEvaluateWord;

    public SynonymsEvaluator(String dictPath,String toEvaluateWord) throws IOException {
        URL url = new File(dictPath).toURI().toURL();
        this.dictionary = new Dictionary(url);
        this.dictionary.open();
        this.stemmer = new WordnetStemmer(dictionary);
        this.toEvaluateWord=toEvaluateWord;
        synonymsEvaluation();
    }

    public boolean isSynonym(String str){
        if(this.synonyms==null){
            return false;
        }else{
            if (this.synonyms.contains(str)){
                return true;
            }else{
                return false;
            }
        }

    }

    private void synonymsEvaluation() throws IOException {

        IDictionary dict = this.dictionary;
        dict.open();
        IIndexWord idxWord = dict.getIndexWord(toEvaluateWord, POS.NOUN);

        if(idxWord !=null){
            List<IWordID> wordIDs = idxWord.getWordIDs();
            List<String> synonyms = new ArrayList<>();

            for(IWordID wordID:wordIDs) {

                IWord word = dict.getWord(wordID);
                for(IWord iWord:word.getSynset().getWords()){
                    synonyms.add(iWord.getLemma());
                    //System.out.println(iWord);
                }
                int i=0;
                for(String str:synonyms){
                 //   System.out.print(" Syn | "+i+" "+str);
                    i++;
                }

            }
            this.synonyms=synonyms;
        }else{
           // System.out.println("No synonym found for the word :\""+toEvaluateWord+"\"");
        }



    }

    public IDictionary getDictionary() {
        return dictionary;
    }

    public WordnetStemmer getStemmer() {
        return stemmer;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public String getToEvaluateWord() {
        return toEvaluateWord;
    }
}

