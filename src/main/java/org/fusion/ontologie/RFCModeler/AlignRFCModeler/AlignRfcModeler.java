package org.fusion.ontologie.RFCModeler.AlignRFCModeler;

import lattice.util.concept.DefaultFormalAttribute;
import lattice.util.concept.DefaultFormalObject;
import lattice.util.concept.FormalAttribute;
import lattice.util.concept.FormalObject;
import lattice.util.exception.BadInputDataException;
import lattice.util.relation.BinaryRelation;
import lattice.util.relation.RelationalContextFamily;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.fusion.ontologie.RFCModeler.SynonymsEvaluator;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Objects;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AlignRfcModeler {

    private RelationalContextFamily relationalContextFamily;

    public  Model readOnto(String path_name) throws MalformedURLException {

        File file = new File(path_name);
        OntModel onto = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        onto.getDocumentManager().addAltEntry(file.toURI().toString(), file.toURI().toString());
        Model mod = onto.read(file.toURI().toString());
        return mod;
    }

    public  RelationalContextFamily modelRFC(OntModel model1, OntModel model2) throws BadInputDataException, IOException {

        RelationalContextFamily relationalContextFamily = new RelationalContextFamily();
        relationalContextFamily.setName("OntologyRFC");

        BinaryRelation conceptContext1 = computeConceptContext(model1);
        BinaryRelation domainContext1 = computeDomainContext(model1);
        BinaryRelation rangeContext1 = computeRangeContext(model1);

        BinaryRelation conceptContext2 = computeConceptContext(model2);
        BinaryRelation domainContext2 = computeDomainContext(model2);
        BinaryRelation rangeContext2 = computeRangeContext(model2);

        BinaryRelation conceptContext = mergeContexts(conceptContext1,conceptContext2, conceptContext1.getRelationName());
        BinaryRelation domainContext = mergeContexts(domainContext1,domainContext2, domainContext1.getRelationName());
        BinaryRelation rangeContext = mergeContexts(rangeContext1,rangeContext2, rangeContext1.getRelationName());

        relationalContextFamily.addRelation(conceptContext);
        relationalContextFamily.addRelation(domainContext);
        relationalContextFamily.addRelation(rangeContext);
        

        return relationalContextFamily;
    }


    /**Compute Interrelation contexts*/
    public   BinaryRelation computeDomainContext(OntModel model) {

        BinaryRelation domainContext=new BinaryRelation("domainContext");


        ExtendedIterator<ObjectProperty> propertyIterator = model.listObjectProperties();

        //filling domain names : names of relationships
        for (ExtendedIterator<ObjectProperty> it = propertyIterator; it.hasNext(); ) {
            OntProperty o = it.next();
            domainContext.addObject(new DefaultFormalObject(o.getLocalName()));
        }

        ExtendedIterator<OntClass> listOntClass = model.listClasses();

        // Filling Classes names
        while (listOntClass.hasNext()) {
            OntClass ontClass = listOntClass.next();

            //System.out.println("***** ObjectClass de " + ontClass.getURI());
            domainContext.addAttribute(new DefaultFormalAttribute(ontClass.getLocalName()));

            ExtendedIterator<ObjectProperty> propertyIterator1 = model.listObjectProperties();

            //Filling relations
            for (ExtendedIterator<ObjectProperty> it = propertyIterator1; it.hasNext(); ) {
                OntProperty o1 = it.next();

                if(o1.getDomain().getLocalName().equalsIgnoreCase(ontClass.getLocalName())){
                    domainContext.setRelation(domainContext.indexOfFormalObject(new DefaultFormalObject(o1.getLocalName())), domainContext.indexOfFormalAttribute(new DefaultFormalAttribute(ontClass.getLocalName())), true);
                }
            }


        }
        System.out.println("\n");
        Vector<FormalAttribute> attributes= domainContext.getAttributes();
        for (FormalAttribute fa: attributes){
            System.out.print("|"+fa.getName()+" |");
        }
        System.out.println("///");
        Vector<FormalObject> objects= domainContext.getObjects();
        for (FormalObject fo: objects){
            System.out.print("|"+fo.getName()+" |");
        }


        return domainContext;
    }
    public  BinaryRelation computeRangeContext(OntModel model) {

        BinaryRelation rangeContext=new BinaryRelation("rangeContext");


        ExtendedIterator<ObjectProperty> propertyIterator = model.listObjectProperties();

        //filling domain names : names of relationships
        for (ExtendedIterator<ObjectProperty> it = propertyIterator; it.hasNext(); ) {
            OntProperty o = it.next();
            rangeContext.addObject(new DefaultFormalObject(o.getLocalName()));
        }

        ExtendedIterator<OntClass> listOntClass = model.listClasses();

        // Filling Classes names
        while (listOntClass.hasNext()) {
            OntClass ontClass = listOntClass.next();

            //System.out.println("***** ObjectClass de " + ontClass.getURI());
            rangeContext.addAttribute(new DefaultFormalAttribute(ontClass.getLocalName()));

            ExtendedIterator<ObjectProperty> propertyIterator1 = model.listObjectProperties();

            //Filling relations
            for (ExtendedIterator<ObjectProperty> it = propertyIterator1; it.hasNext(); ) {
                OntProperty o1 = it.next();

                if(o1.getRange().getLocalName().equalsIgnoreCase(ontClass.getLocalName())){
                    rangeContext.setRelation(rangeContext.indexOfFormalObject(new DefaultFormalObject(o1.getLocalName())), rangeContext.indexOfFormalAttribute(new DefaultFormalAttribute(ontClass.getLocalName())), true);
                }
            }
            Vector<FormalAttribute> attributes= rangeContext.getAttributes();
            for (FormalAttribute fa: attributes){
                System.out.print("|"+fa.getName()+" |");
            }
            System.out.println("///");
            Vector<FormalObject> objects= rangeContext.getObjects();
            for (FormalObject fo: objects){
                System.out.print("|"+fo.getName()+" |");
            }



        }


        return rangeContext;
    }
    /**----Compute Interrelation contexts*/
    /**Compute Roles context*/
    public  BinaryRelation computeRoleContext(OntModel model){
        BinaryRelation roleContext=new BinaryRelation("role context");

        return roleContext;
    }
    /**----Compute Roles context*/
    /**Compute Merged Concept Context for the two Ontologies*/
    public BinaryRelation computeConceptContext(OntModel model){

        System.out.println("\n");

        BinaryRelation conceptContext=new BinaryRelation("conceptContext");
        // Class iterator
        ExtendedIterator<OntClass> classIterator = model.listClasses();
        ExtendedIterator<DatatypeProperty> propertyIterator1 = model.listDatatypeProperties();

        for (ExtendedIterator<DatatypeProperty> it = propertyIterator1; it.hasNext(); ) {
            DatatypeProperty l = it.next();
            conceptContext.addAttribute(new DefaultFormalAttribute(l.getLocalName()));
        }

        // Adding Attributes to Context
        while (classIterator.hasNext()) {
            OntClass ontClass = classIterator.next();
            if(!conceptContext.containsFormalObject(new DefaultFormalObject(ontClass.getLocalName()))) {

                // System.out.println(ontClass.getLocalName()+"  Concept : " + ontClass.getURI());
                conceptContext.addObject(new DefaultFormalObject(ontClass.getLocalName()));
                conceptContext.addAttribute(new DefaultFormalAttribute(ontClass.getLocalName()));
                //Adding reflexive relations
                conceptContext.setRelation(conceptContext.indexOfFormalObject(new DefaultFormalObject(ontClass.getLocalName())), conceptContext.indexOfFormalAttribute(new DefaultFormalAttribute(ontClass.getLocalName())), true);
                //Handling  heritage in context

                String superClassURI="";
                if(!Objects.equals(ontClass.getSuperClass(),null)){
                     superClassURI = ontClass.getSuperClass().getURI();

                }else{
                     superClassURI = "p";

                }
                Pattern pat = Pattern.compile("http://www\\.w3\\.org/.*/rdf-schema#Resource");
                Matcher m = pat.matcher(superClassURI);



                if (ontClass.getSuperClass()!=null && !m.matches()) {
                    if(!conceptContext.containsFormalObject(new DefaultFormalObject(ontClass.getSuperClass().getLocalName()))) {

                        conceptContext.addAttribute(new DefaultFormalAttribute(ontClass.getSuperClass().getLocalName()));
                        conceptContext.setRelation(conceptContext.indexOfFormalObject(new DefaultFormalObject(ontClass.getLocalName())), conceptContext.indexOfFormalAttribute(new DefaultFormalAttribute(ontClass.getSuperClass().getLocalName())), true);
                    }else {
                        conceptContext.setRelation(conceptContext.indexOfFormalObject(new DefaultFormalObject(ontClass.getLocalName())), conceptContext.indexOfFormalAttribute(new DefaultFormalAttribute(ontClass.getSuperClass().getLocalName())), true);

                    }

                }
                //-----End of handling heritage


                ExtendedIterator<DatatypeProperty> propertyIterator = model.listDatatypeProperties();
                model.listDatatypeProperties();


                //  System.out.println(ontClass.getLocalName()+"  Class name : " );
                for (ExtendedIterator<DatatypeProperty> it = propertyIterator; it.hasNext(); ) {
                    DatatypeProperty dt = it.next();


                    if (dt.getDomain().getLocalName().equals(ontClass.getLocalName())) {
                        conceptContext.setRelation(conceptContext.indexOfFormalObject(new DefaultFormalObject(ontClass.getLocalName())), conceptContext.indexOfFormalAttribute(new DefaultFormalAttribute(dt.getLocalName())), true);
                    }

                }
            }
        }

        //Filling heritage relations
        ExtendedIterator<OntClass> finalClassIterator = model.listClasses();
        int i=0;
        while (finalClassIterator.hasNext()) {
            OntClass ontClass = finalClassIterator.next();
           // System.out.println("class "+i+" "+ontClass.getLocalName());
            getSubClass(ontClass,conceptContext);
            i++;
        }

        Vector<FormalAttribute> attributes= conceptContext.getAttributes();
        for (FormalAttribute fa: attributes){
            System.out.print("|"+fa.getName()+" |");
        }
        System.out.println("///");
        Vector<FormalObject> objects= conceptContext.getObjects();
        for (FormalObject fo: objects){
            System.out.print("|"+fo.getName()+" |");
        }

        return conceptContext;
    }
    public  BinaryRelation mergeContexts(BinaryRelation context1,BinaryRelation context2, String name) throws BadInputDataException, IOException {

        int j1= context1.getAttributesNumber();
        int j2= context2.getAttributesNumber();

        int i1= context1.getObjectsNumber();
        int i2= context2.getObjectsNumber();

        BinaryRelation mergedContext =new BinaryRelation(name);

        /**Adding Context1*/
        //Adding Context1 elements
        for (int i = 0; i < i1; i++) {
            mergedContext.addObject(context1.getFormalObject(i));
        }
        for (int j = 0; j < j1; j++) {
            mergedContext.addAttribute(context1.getFormalAttribute(j));
        }
        //Adding Context 1 relations
        for (int i = 0; i < i1; i++) {
            for (int j = 0; j < j1; j++) {
                if(!context1.getRelation(i,j).isEmpty()){
                    mergedContext.setRelation(i,j,true);
                }
            }
        }
        /**----Adding Context1*/

        /**Adding Context2*/
        //Adding Context2 elements
        for (int i = 0; i < i2; i++) {

            
            SynonymsEvaluator synEv= new SynonymsEvaluator("src/main/java/org/fusion/ontologie/wordnetDictPrinceton/dict",context2.getFormalObject(i).getName());
            Vector<FormalObject> objVector=mergedContext.getObjects();
            boolean hasSynonyms=false;
            for(FormalObject o:objVector){
                if(synEv.isSynonym(o.getName())){
                    hasSynonyms=true;
                }
            }
            if(!hasSynonyms){
                mergedContext.addObject(context2.getFormalObject(i));
            }
        }

        for (int j = 0; j < j2; j++) {

            SynonymsEvaluator synEv= new SynonymsEvaluator("src/main/java/org/fusion/ontologie/wordnetDictPrinceton/dict",context2.getFormalAttribute(j).getName());
            Vector<FormalAttribute> objVector=mergedContext.getAttributes();
            boolean hasSynonyms=false;
            for(FormalAttribute a:objVector){
                if(synEv.isSynonym(a.getName())){
                    hasSynonyms=true;
                }
            }
            if(!hasSynonyms){
                mergedContext.addAttribute(context2.getFormalAttribute(j));
            }
        }
        // Adding Context2 Relations


        //for each object of merged context
        for (int i = 0; i < mergedContext.getObjectsNumber(); i++) {

            //List merged context Object synonyms
            SynonymsEvaluator synEv= new SynonymsEvaluator("src/main/java/org/fusion/ontologie/wordnetDictPrinceton/dict",mergedContext.getFormalObject(i).getName());
            Vector<FormalObject> objVector=context2.getObjects();

            //for each object of context2
            for(FormalObject o:objVector){
                //check if syn of an obj in merged context
                if(synEv.isSynonym(o.getName()) || Objects.equals(o.getName(), mergedContext.getFormalObject(i).getName())){
                    System.out.println("AAAAAAAAAAAAAAA");
                    //if yes for each attribute linked to it in context2
                    for (int j = 0; j < context2.getAttributesNumber(); j++) {

                        //if the relation exists

                        if(!context2.getRelation(context2.indexOfFormalObject(o),j).isEmpty()){
                            //check for context2 attribute synonyms in merged context
                            SynonymsEvaluator synEv1= new SynonymsEvaluator("src/main/java/org/fusion/ontologie/wordnetDictPrinceton/dict",context2.getFormalAttribute(j).getName());
                            Vector<FormalAttribute> merjedAttrib=mergedContext.getAttributes();
                            for(FormalAttribute fa:merjedAttrib){
                                //then if synonym add the relation
                                
                                if(synEv1.isSynonym(fa.getName()) || Objects.equals(fa.getName(), context2.getFormalAttribute(j).getName())){
                                    mergedContext.setRelation(i,mergedContext.indexOfFormalAttribute(fa),true);
                                }
                            }


                        }
                    }
                }
            }

        }
        /**----Adding Context2*/


        return mergedContext;
    }
//    public  BinaryRelation computeMergedConceptContext(OntModel model1,OntModel model2) throws BadInputDataException, IOException {
//
//        System.out.println("\n");
//        BinaryRelation context1 = computeConceptContext(model1);
//        BinaryRelation context2 = computeConceptContext(model2);
//        BinaryRelation binaryRelation = mergeContexts(context1, context2);
//
//
//
//        return binaryRelation;
//    }
    /**----Compute Merged Concept Context for the two Ontologies*/
    public  void getSubClass(OntClass ontClass1, BinaryRelation context) {

        ExtendedIterator<OntClass> classIterator = ontClass1.listSubClasses();
        while (classIterator.hasNext()) {
            OntClass ontClass = classIterator.next();
            System.out.println("+++++ SubClass : " + ontClass.getURI());
            context.addAttribute(new DefaultFormalAttribute(ontClass.getLocalName()));
            context.setRelation(context.indexOfFormalObject(new DefaultFormalObject(ontClass1.getLocalName())),context.indexOfFormalAttribute(new DefaultFormalAttribute(ontClass.getLocalName())), true);
        }

    }
    private  void printSubClasses(OntClass ontClass, int depth) {
        String indent = "";
        for (int i = 0; i < depth; i++) {
            indent += "\t";
        }
        System.out.println(indent + ontClass.getLocalName());

        ExtendedIterator<OntClass> subClassIterator = ontClass.listSubClasses();
        while (subClassIterator.hasNext()) {
            OntClass subClass = subClassIterator.next();
            printSubClasses(subClass, depth + 1);
        }
    }


}