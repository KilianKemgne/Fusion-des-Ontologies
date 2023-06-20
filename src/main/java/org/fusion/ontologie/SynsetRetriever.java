package org.fusion.ontologie;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;
import edu.mit.jwi.morph.WordnetStemmer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class SynsetRetriever {

    private IDictionary dictionary;
    private WordnetStemmer stemmer;

    public SynsetRetriever(String dictPath) throws IOException {
        URL url = new File(dictPath).toURI().toURL();
        dictionary = new Dictionary(url);
        dictionary.open();
        stemmer = new WordnetStemmer(dictionary);
    }

    public List<ISynsetID> getSynsets(String word, POS pos) {
        List<String> stems = stemmer.findStems(word, pos);
        if (stems.isEmpty()) {
            return null; // pas de stem pour ce mot et POS
        }
        IIndexWord indexWord = dictionary.getIndexWord(stems.get(0), pos);
        if (indexWord == null) {
            return null; // mot introuvable dans le WordNet
        }
        List<IWordID> wordIDs = indexWord.getWordIDs();
        if (wordIDs.isEmpty()) {
            return null; // mot introuvable dans le WordNet
        }
        IWord wordObj = dictionary.getWord(wordIDs.get(0));
        return wordObj.getSynset().getRelatedSynsets();
    }

}
