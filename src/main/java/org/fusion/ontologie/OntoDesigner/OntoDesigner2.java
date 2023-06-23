package org.fusion.ontologie.OntoDesigner;

import lattice.util.concept.Concept;
import lattice.util.relation.RelationalContextFamily;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;

public class OntoDesigner2 {

    private RelationalContextFamily rcf;

    private OntModel ontModel = ModelFactory.createOntologyModel();


    public OntoDesigner2(RelationalContextFamily rcf) {
        this.rcf = rcf;
    }

    public File generateOntModel() throws FileNotFoundException {

        fillConcept();
        fillDomain();
        fillRange();

        FileOutputStream output = new FileOutputStream("fusion.owl");
        File file = new File("fusion.owl");
        System.out.println(file.getAbsolutePath());
        ontModel.write(output, "RDF/XML-ABBREV");

        return file;
    }

    // Cette méthode a pour rôle l'ajout des concepts comme classe dans notre ontologie fusionnée
    public void fillConcept() {

        var list = rcf.getRelation(0).getLattice().findNode(0).getConcept().getExtent().toArray();

        for (int i = 0; i < list.length; i++) {
            list[i] = list[i].toString();
        }

        for (int j = 0; j < rcf.getRelation(0).getLattice().getSize(); j++) {

            Concept concept = rcf.getRelation(0).getLattice().findNode(j).getConcept();

            for (int k = 0; k < concept.getExtent().size(); k++) {
                OntClass ontClass = ontModel.getOntClass(concept.getExtent().stream().toList().get(k).toString());

                if(ontModel.getOntClass(concept.getExtent().stream().toList().get(k).toString()) == null){

                    // Ajout des concepts du treilli de concept comme classe dans le fichier owl final
                    String namespace = "http://example.com/ontology#";
                    String resourceName = concept.getExtent().stream().toList().get(k).toString();
                    String uri = namespace + resourceName;
                    String validUri = ResourceFactory.createResource(uri).getURI();
                    Literal lab = ontModel.createLiteral(resourceName);
                    ontClass = ontModel.createClass(validUri);
                    ontClass.addLabel(lab);

                }

                if (concept.getIntent().size() == 1) {
                    for (int l = 0; l < concept.getIntent().size(); l++) {
                        boolean verifier = false;
                        System.out.println(Arrays.stream(list).toList());
                        for (Object o : list) {
                            System.out.println("***"+o);
                            System.out.println("+++"+concept.getIntent().toArray()[l]);

                            if (o.toString().equals(concept.getIntent().toArray()[l].toString())) {
                                verifier = true;
                                System.out.println("***"+verifier);
                                break;
                            }
                        }
                        if (verifier) {
                            if (ontModel.getOntClass(concept.getIntent().stream().toList().get(l).toString()) == null){
                                String namespace = "http://example.com/ontology#";
                                String resourceName = concept.getIntent().stream().toList().get(l).toString();
                                String uri = namespace + resourceName;
                                String validUri = ResourceFactory.createResource(uri).getURI();
                                Literal lab = ontModel.createLiteral(resourceName);
                                OntClass ontClass1 = ontModel.createClass(validUri);
                                ontClass1.addLabel(lab);
                                if (!ontClass.equals(ontClass1))
                                    ontClass.addSuperClass(ontClass1);
                            } else {
                                if (!ontClass.equals(ontModel.getOntClass(concept.getIntent().stream().toList().get(l).toString())))
                                    ontClass.addSuperClass(ontModel.getOntClass(concept.getIntent().stream().toList().get(l).toString()));
                            }

                        } else {
                            String namespace = "http://example.com/ontology#";
                            String resourceName = concept.getIntent().stream().toList().get(l).toString();
                            String uri = namespace + resourceName;
                            String validUri = ResourceFactory.createResource(uri).getURI();
                            Literal lab = ontModel.createLiteral(resourceName);
                            DatatypeProperty datatypeProperty = ontModel.createDatatypeProperty(validUri);
                            datatypeProperty.addLabel(lab);
                            datatypeProperty.addDomain(ontClass);
                            ontClass.addProperty(datatypeProperty, resourceName);
                        }
                    }
                } else if (concept.getIntent().size() > 1) {
                    for (int l = 0; l < concept.getIntent().size(); l++) {
                        boolean verifier = false;
                        System.out.println(Arrays.stream(list).toList());
                        for (Object o : list) {
                            System.out.println("***"+o);
                            System.out.println("+++"+concept.getIntent().toArray()[l]);

                            if (o.toString().equals(concept.getIntent().toArray()[l].toString())) {
                                verifier = true;
                                System.out.println("***"+verifier);
                                break;
                            }
                        }
                        if (!verifier) {
                            String namespace = "http://example.com/ontology#";
                            String resourceName = concept.getIntent().stream().toList().get(l).toString();
                            String uri = namespace + resourceName;
                            String validUri = ResourceFactory.createResource(uri).getURI();
                            Literal lab = ontModel.createLiteral(resourceName);
                            DatatypeProperty datatypeProperty = ontModel.createDatatypeProperty(validUri);
                            datatypeProperty.addLabel(lab);
                            datatypeProperty.addDomain(ontClass);
                            ontClass.addProperty(datatypeProperty, resourceName);

                        }
                    }
                }



            }
        }
    }

    // Cette méthode est dédiées au remplissage des domain dans les interralations entre les classes
    public void fillDomain() {

        for (int j = 0; j < rcf.getRelation(1).getLattice().getSize(); j++) {

            Concept concept = rcf.getRelation(1).getLattice().findNode(j).getConcept();

            for (int k = 0; k < concept.getExtent().size(); k++) {
                ObjectProperty objectProperty = ontModel.getObjectProperty(concept.getExtent().stream().toList().get(k).toString());

                if(ontModel.getObjectProperty(concept.getExtent().stream().toList().get(k).toString()) == null){

                    // Ajout des concepts du treilli de concept comme classe dans le fichier owl final
                    String namespace = "http://example.com/ontology#";
                    String resourceName = concept.getExtent().stream().toList().get(k).toString();
                    String uri = namespace + resourceName;
                    String validUri = ResourceFactory.createResource(uri).getURI();
                    Literal lab = ontModel.createLiteral(resourceName);
                    objectProperty = ontModel.createObjectProperty(validUri);
                    objectProperty.addLabel(lab);

                }

                for (int l = 0; l < concept.getIntent().size(); l++) {
                    if (ontModel.getOntClass(concept.getIntent().stream().toList().get(l).toString()) == null){
                        String namespace = "http://example.com/ontology#";
                        String resourceName = concept.getIntent().stream().toList().get(l).toString();
                        String uri = namespace + resourceName;
                        String validUri = ResourceFactory.createResource(uri).getURI();
                        Literal lab = ontModel.createLiteral(resourceName);
                        OntClass ontClass1 = ontModel.createClass(validUri);
                        ontClass1.addLabel(lab);
                        objectProperty.addDomain(ontClass1);
                    } else {
                        objectProperty.addDomain(ontModel.getOntClass(concept.getIntent().stream().toList().get(l).toString()));
                    }
                }
            }
        }
    }

    // Cette méthode est dédiée au remplissage des range dans les interralations entre les classes
    public void fillRange() {

        for (int j = 0; j < rcf.getRelation(2).getLattice().getSize(); j++) {

            Concept concept = rcf.getRelation(2).getLattice().findNode(j).getConcept();

            for (int k = 0; k < concept.getExtent().size(); k++) {
                ObjectProperty objectProperty = ontModel.getObjectProperty(concept.getExtent().stream().toList().get(k).toString());

                if(ontModel.getObjectProperty(concept.getExtent().stream().toList().get(k).toString()) == null){

                    // Ajout des concepts du treilli de concept comme classe dans le fichier owl final
                    String namespace = "http://example.com/ontology#";
                    String resourceName = concept.getExtent().stream().toList().get(k).toString();
                    String uri = namespace + resourceName;
                    String validUri = ResourceFactory.createResource(uri).getURI();
                    Literal lab = ontModel.createLiteral(resourceName);
                    objectProperty = ontModel.createObjectProperty(validUri);
                    objectProperty.addLabel(lab);

                }

                for (int l = 0; l < concept.getIntent().size(); l++) {
                    if (ontModel.getOntClass(concept.getIntent().stream().toList().get(l).toString()) == null){
                        String namespace = "http://example.com/ontology#";
                        String resourceName = concept.getIntent().stream().toList().get(l).toString();
                        String uri = namespace + resourceName;
                        String validUri = ResourceFactory.createResource(uri).getURI();
                        Literal lab = ontModel.createLiteral(resourceName);
                        OntClass ontClass1 = ontModel.createClass(validUri);
                        ontClass1.addLabel(lab);
                        objectProperty.addRange(ontClass1);
                    } else {
                        objectProperty.addRange(ontModel.getOntClass(concept.getIntent().stream().toList().get(l).toString()));
                    }
                }
            }
        }
    }

}
