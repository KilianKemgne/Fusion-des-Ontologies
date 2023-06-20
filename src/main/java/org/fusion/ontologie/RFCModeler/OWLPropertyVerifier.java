package org.fusion.ontologie.RFCModeler;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.util.iterator.ExtendedIterator;

public class OWLPropertyVerifier {
    public static boolean isClassInDomain(OntModel ontModel, String className, String propertyName) {
        // Déclaration des préfixes
        String prefixes = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n";

        // Requête SPARQL pour vérifier si une classe fait partie du domaine d'une dataTypeProperty spécifique
        String sparqlQuery = prefixes +
                "ASK\n" +
                "WHERE {\n" +
                "  ?property rdf:type owl:DatatypeProperty ;\n" +
                "            rdfs:domain ?class ;\n" +
                "            rdfs:label ?propertyName .\n" +
                "  ?class owl:equivalentClass/owl:intersectionOf/rdf:rest*/rdf:first ?domainClass.\n" +
                "  FILTER (STR(?class) = \"" + className + "\") .\n" +
                "  FILTER (STR(?propertyName) = \"" + propertyName + "\") .\n" +
                "}";

        // Exécuter la requête SPARQL
        Query query = QueryFactory.create(sparqlQuery);
        try (QueryExecution qe = QueryExecutionFactory.create(query, ontModel)) {
            boolean result = qe.execAsk();
            return result;
        }
    }

    public static boolean isDataPropertyOfClass(OntModel model, String className, String dataPropertyName) {
        OntClass ontClass = model.getOntClass(className);
        if (ontClass == null) {
            System.out.println("Classe introuvable : " + className);
            return false;
        }

        DatatypeProperty dataProperty = model.getDatatypeProperty(dataPropertyName);

        if (dataProperty == null) {
            System.out.println("DataProperty introuvable : " + dataPropertyName);
            return false;
        }

        ExtendedIterator<? extends OntClass> subclasses = ontClass.listSubClasses(true);
        while (subclasses.hasNext()) {
            OntClass subclass = subclasses.next();
            if (subclass.hasDeclaredProperty(dataProperty, true)) {
                return true;
            }
        }

        return false;
    }
}