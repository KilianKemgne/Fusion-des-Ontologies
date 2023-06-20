package org.fusion.ontologie.RFCModeler.SimpleRFCModeler;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Vector;


public class RfcModeler {

    public  Model readOnto(String path_name) throws MalformedURLException {

        File file = new File(path_name);
        OntModel onto = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
        onto.getDocumentManager().addAltEntry(file.toURI().toString(), file.toURI().toString());
        Model mod = onto.read(file.toURI().toString());
        return mod;
    }

    public  OntModel createFCR(Model mod1, Model mod2) throws FileNotFoundException {

        OntModel fcrOnt = ModelFactory.createOntologyModel();

        fcrOnt.addSubModel(mod1);
        fcrOnt.addSubModel(mod2);

        FileOutputStream fos = new FileOutputStream("fcr.owl");
        fcrOnt.write(fos, "RDF/XML-ABBREV");


        return fcrOnt;
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



    public   BinaryRelation computeDomainContext(OntModel model) {

        BinaryRelation domainContext=new BinaryRelation("domainContext");


        ExtendedIterator<ObjectProperty> propertyIterator = model.listObjectProperties();

        //filling domain names : names of relationships
        for (ExtendedIterator<ObjectProperty> it = propertyIterator; it.hasNext(); ) {
            OntProperty o = it.next();
            System.out.println("*****"+o.getLocalName()+"******");
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
                System.out.println("/////");
                System.out.println("relation name :"+o1.getLocalName());
                System.out.println("OntClass name :"+ontClass.getLocalName());
                System.out.println("relation domain :"+o1.getDomain().getLocalName());
                System.out.println(o1.getDomain().getLocalName().equalsIgnoreCase(ontClass.getLocalName()));
                System.out.println("super class :"+ ontClass.getSuperClass());
                System.out.println("////");
                if(o1.getDomain().getLocalName().equalsIgnoreCase(ontClass.getLocalName())){
                    System.out.println(ontClass.getLocalName()+" participe à la relation "+o1.getLocalName());
                    domainContext.setRelation(domainContext.indexOfFormalObject(new DefaultFormalObject(o1.getLocalName())), domainContext.indexOfFormalAttribute(new DefaultFormalAttribute(ontClass.getLocalName())), true);
                }
            }
            Vector<FormalAttribute> attributes= domainContext.getAttributes();
            for (FormalAttribute fa: attributes){
                System.out.print("|"+fa.getName()+" |");
            }
            System.out.println("///");
            Vector<FormalObject> objects= domainContext.getObjects();
            for (FormalObject fo: objects){
                System.out.print("|"+fo.getName()+" |");
            }



        }
        return domainContext;
    }
    public  BinaryRelation computeRangeContext(OntModel model) {

        BinaryRelation rangeContext=new BinaryRelation("rangeContext");


        ExtendedIterator<ObjectProperty> propertyIterator = model.listObjectProperties();

        //filling domain names : names of relationships
        for (ExtendedIterator<ObjectProperty> it = propertyIterator; it.hasNext(); ) {
            OntProperty o = it.next();
            System.out.println("*****"+o.getLocalName()+"******");
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
                System.out.println("/////");
                System.out.println("relation name :"+o1.getLocalName());
                System.out.println("OntClass name :"+ontClass.getLocalName());
                System.out.println("relation domain :"+o1.getRange().getLocalName());
                System.out.println(o1.getRange().getLocalName().equalsIgnoreCase(ontClass.getLocalName()));
                System.out.println("super class :"+ ontClass.getSuperClass());
                System.out.println("////");
                if(o1.getRange().getLocalName().equalsIgnoreCase(ontClass.getLocalName())){
                    System.out.println(ontClass.getLocalName()+" participe à la relation "+o1.getLocalName());
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
    public   BinaryRelation computeConceptContext(OntModel model){

        BinaryRelation conceptContext=new BinaryRelation("conceptContext");
        // Class iterator
        ExtendedIterator<OntClass> classIterator = model.listClasses();
        System.out.println("nb property : "+model.listClasses().toList().size());

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
                if (ontClass.getSuperClass()!=null) {
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
        System.out.print("concepts length : "+ conceptContext.getObjectsNumber());
        System.out.print("\nattributes length : "+ conceptContext.getAttributesNumber()+"\n");
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
    public  BinaryRelation mergeContexts(BinaryRelation context1, BinaryRelation context2,String contextName) throws BadInputDataException {

        int j1= context1.getAttributesNumber();
        int j2= context2.getAttributesNumber();

        int i1= context1.getObjectsNumber();
        int i2= context2.getObjectsNumber();

        BinaryRelation mergedContext =new BinaryRelation(contextName);

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
            mergedContext.addObject(context2.getFormalObject(i));
        }
        for (int j = 0; j < j2; j++) {
            mergedContext.addAttribute(context2.getFormalAttribute(j));
        }
        // Adding Context2 Relations
        for (int i = 0; i < i2; i++) {
            for (int j = 0; j < j2; j++) {
                if(!context2.getRelation(i,j).isEmpty()){
                    mergedContext.setRelation(i+i1,j1+j,true);
                }
            }
        }
        /**----Adding Context2*/


        return mergedContext;
    }
    public  BinaryRelation computeMergedConceptContext(OntModel model1,OntModel model2,String contextName) throws BadInputDataException {
        BinaryRelation context1 = computeConceptContext(model1);
        BinaryRelation context2 = computeConceptContext(model2);
        return mergeContexts(context1, context2,contextName);
    }
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
