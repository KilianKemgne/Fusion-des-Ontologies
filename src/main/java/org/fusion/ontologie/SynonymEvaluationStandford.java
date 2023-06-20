package org.fusion.ontologie;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;
import edu.mit.jwi.morph.WordnetStemmer;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class SynonymEvaluationStandford {
    private Model model;
    private IDictionary dict;
    private WordnetStemmer stemmer;
    private Analyzer analyzer;
    private Directory indexDirectory;

    public SynonymEvaluationStandford(Model model) throws IOException {
        this.model = model;
        dict = new Dictionary(new File("src/main/java/org/fusion/ontologie/wordnetDictPrinceton/dict"));
        dict.open();
        stemmer = new WordnetStemmer(dict);
        analyzer = new StandardAnalyzer();
        indexDirectory = FSDirectory.open(Paths.get("src/main/java/org/fusion/ontologie/lucene/index"));
    }

    public void luceneIndex() throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(indexDirectory, config);

        StmtIterator iter = model.listStatements();
        while (iter.hasNext()) {
            //System.out.println("*****"+iter);
            Statement stmt = iter.nextStatement();
            RDFNode obj = stmt.getObject();
            // System.out.println("+++++"+obj.asResource().getLocalName());
            if (obj.isLiteral()) {
                String label = obj.asLiteral().getLexicalForm();
                //System.out.println("+++++"+label);
                Document doc = new Document();
                doc.add(new TextField("label", label, Field.Store.YES));
                writer.addDocument(doc);
            }
        }

        writer.close();
    }

    public void evaluate() throws IOException, ParseException {
        luceneIndex();

        IndexReader reader = DirectoryReader.open(indexDirectory);
        IndexSearcher searcher = new IndexSearcher(reader);

        StmtIterator iter = model.listStatements();
        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();
            RDFNode object = stmt.getObject();
            if (object.isLiteral()) {
                String word = object.asLiteral().getLexicalForm();
                System.out.println("Word: " + word);

                List<String> lemmas = stemmer.findStems(word, POS.NOUN);
                if (!lemmas.isEmpty()) {
                    String lemma = lemmas.get(0);
                    System.out.println("Lemma: " + lemma);

                    IIndexWord idxWord = dict.getIndexWord(lemma, POS.NOUN);
                    if (idxWord != null) {
                        for (IWordID wordID : idxWord.getWordIDs()) {
                            IWord iword = dict.getWord(wordID);
                            if (iword != null) {
                                ISynset synset = iword.getSynset();

                                for (IWord w : synset.getWords()) {
                                    String synonym = w.getLemma();
                                    System.out.println("\tSynonym: " + synonym);

//                                    QueryParser parser = new QueryParser("label", analyzer);
//                                    Query query = parser.parse(synonym);
//                                    TopDocs topDocs = searcher.search(query, 10);
//                                    ScoreDoc[] hits = topDocs.scoreDocs;
//                                    for (ScoreDoc hit : hits) {
//                                        int docId = hit.doc;
//                                        Document doc = searcher.doc(docId);
//                                        String similarTerm = doc.get("label");
//                                        System.out.println("\t\tSimilar term: " + similarTerm);
//                                        //System.out.println("\t\tscore term: " + hit.score);
//                                    }
                                }
                            }
                        }
                    } else {
                        System.out.println("No synsets found for " + lemma);
                    }
                } else {
                    System.out.println("No lemmas found for " + word);
                }
            }
        }

        reader.close();
        dict.close();
    }
}

