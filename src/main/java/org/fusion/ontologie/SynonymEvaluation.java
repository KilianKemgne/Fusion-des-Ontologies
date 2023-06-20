package org.fusion.ontologie;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;
import org.apache.jena.rdf.model.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class SynonymEvaluation {

    private Model model;

    public SynonymEvaluation(Model model) {
        this.model = model;
    }

    public void luceneIndex(Analyzer analyzer, String indexPath) throws IOException {
        // Créer un IndexWriter à partir de l'analyseur et du chemin d'accès à l'index
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(FSDirectory.open(Paths.get(indexPath)), config);

        // Parcourir les ressources de notre modèle Jena et extraire leurs libellés
        StmtIterator iter = model.listStatements();
        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();
            Resource subj = stmt.getSubject();
            Property pred = stmt.getPredicate();
            RDFNode obj = stmt.getObject();

            // Ajouter le label de la ressource au IndexWriter
            if (obj.isLiteral()) {
                String label = obj.asLiteral().getLexicalForm();
                Document doc = new Document();
                doc.add(new TextField("label", label, Field.Store.YES));
                writer.addDocument(doc);
            }
        }

        // Fermer le IndexWriter
        writer.close();

    }

    public void evaluate() throws IOException {

        // Création de l'objet WordNet
        String wordNetPath = "src/main/java/org/fusion/ontologie/wordnetDictPrinceton/dict";
        IDictionary dict = new Dictionary(new java.io.File(wordNetPath));
        dict.open();

        // Configuration de l'analyseur Lucene
        Analyzer analyzer = new StandardAnalyzer();

        // création de l'index lucene
        String indexPath = "src/main/java/org/fusion/ontologie/lucene/index";
        luceneIndex(analyzer, indexPath);

        // Ouverture de l'index Lucene
        //String indexDirectoryPath = indexPath;
        FSDirectory indexDirectory = FSDirectory.open(Paths.get(indexPath));
        IndexReader reader = DirectoryReader.open(indexDirectory);
        IndexSearcher searcher = new IndexSearcher(reader);

        // Récupération des mots à partir du modèle RDF
        StmtIterator iter = model.listStatements();
        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();
            RDFNode object = stmt.getObject();
            if (object.isLiteral()) {
                String word = object.asLiteral().getLexicalForm();
                System.out.println("Word: " + word);

                // Recherche des synsets (ensembles de synonymes) pour le mot
                IIndexWord idxWord = dict.getIndexWord(word, POS.NOUN);
                if (idxWord != null) {
                    IWordID wordID = idxWord.getWordIDs().get(0); // On récupère le premier synset
                    IWord iword = dict.getWord(wordID);
                    ISynset synset = iword.getSynset();

                    // Récupération des mots synonymes
                    for (IWord w : synset.getWords()) {
                        String synonym = w.getLemma();
                        System.out.println("\t\tSynonym: " + synonym);

                        // Recherche de termes similaires à partir des mots composés
                        TermQuery query = new TermQuery(new Term("content", synonym));
                        TopDocs topDocs = searcher.search(query, 10);
                        ScoreDoc[] hits = topDocs.scoreDocs;
                        for (ScoreDoc hit : hits) {
                            int docId = hit.doc;
                            Document doc = searcher.doc(docId);
                            String similarTerm = doc.get("content");
                            System.out.println("Similar term: " + similarTerm);
                        }
                    }
                } else {
                    System.out.println("No synsets found for " + word);
                }
            }
        }

        // Fermeture du dictionnaire WordNet
        dict.close();
        // Fermeture de l'index Lucene
        reader.close();
    }
}

