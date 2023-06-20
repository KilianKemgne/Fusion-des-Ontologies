package org.fusion.ontologie;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

public class Main {
    public static void main(String[]  args) throws MalformedURLException, FileNotFoundException {
//        Model mod1 = readOnto("src/main/java/org/fusion/ontologie/ontologyExamples/event.owl");
//
//        Model mod2 = readOnto("src/main/java/org/fusion/ontologie/ontologyExamples/musique.owl");
//
//        Model result = Rcamerge.createFCR(mod1,mod2);
//
//        for (StmtIterator it = mod2.listStatements(); it.hasNext(); ) {
//            Statement stm = it.next();
//            System.out.println(stm.getSubject().getLocalName());
//        }
        // File file = new File("src/main/java/org/fusion/ontologie/ontologyExamples/event.owl");
        OntModel onto = ModelFactory.createOntologyModel();
        onto.read("src/main/java/org/fusion/ontologie/ontologyExamples/event.owl");


        try {
            SynonymEvaluationStandford synonymEvaluation = new SynonymEvaluationStandford(onto);
            synonymEvaluation.evaluate();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

//         BinaryContext context = Rcamerge.computeConceptsContext(onto);
//        //System.out.println(context.getRelation(13,12));
//        for (int i = 0; i < context.getObjectNumber(); i++) {
//            for (int j = 0; j < context.getAttributeNumber(); j++) {
//                if (context.getRelation(i, j)) {
//                    System.out.print("1 ");
//                } else {
//                    System.out.print("0 ");
//                }
//            }
//            System.out.println();
//        }
        
//        FormalObject fo = new DefaultFormalObject("Kilian");
//        FormalObject fo1 = new DefaultFormalObject("Jean");
//
//        FormalAttribute fa = new DefaultFormalAttribute("4GI");
//        FormalAttribute fa1 = new DefaultFormalAttribute("IA");
//        FormalAttribute fa2 = new DefaultFormalAttribute("SECU");
//
//
//        FormalAttributeValue formalAttributeValue = new FormalAttributeValue();
//
//        BinaryRelation binaryRelation = new BinaryRelation(2,3);
//        binaryRelation.addObject(fo);
//        binaryRelation.addObject(fo1);
//        binaryRelation.addAttribute(fa);
//        binaryRelation.addAttribute(fa1);
//        binaryRelation.addAttribute(fa2);
//        binaryRelation.setRelation(0,0,true);
//        binaryRelation.setRelation(1,1,true);
//        binaryRelation.setRelation(0,2,true);
//
//        ReductionGalois reductionGalois = new ReductionGalois();
//        reductionGalois.execAlgorithm(new BordatIceberg(binaryRelation));


//        SynonymEvaluation synonymEvaluation = new SynonymEvaluation(result);
//        try {
//            synonymEvaluation.evaluate();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println(result.listStatements());
//
//        BinaryRelation[] relations = GaloisReductionAlgorithm.computeGaloisReduction(mod1);
//
//        for (BinaryRelation relation : relations){
//            System.out.println("Binary relation " + relation.getName() + ":");
//            for (int i = 0; i < relations.length; i++) {
//                for (int j = 0; j < relations.length; j++) {
//                    System.out.print(relation.getValue(i, j) + " ");
//                }
//                System.out.println();
//            }
//        }

    }
}