package org.isatools.owlbuild;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by agbeltran on 15/04/2014.
 */
public class OWLClassifier {

    private OWLOntologyManager manager = null;
    private OWLReasonerFactory reasonerFactory = null;
    private OWLReasoner reasoner = null;
    private OWLOntology ontology = null, classifiedOntology = null;

    public OWLClassifier(){

    }

    public void classify(OWLOntology onto, IRI iri) {
        ontology = onto;
        //manager = onto.getOWLOntologyManager();
        //creating new manager to be able to re-use original IRI
        manager = OWLManager.createOWLOntologyManager();

        System.out.println("Ontology has " + ontology.getAxioms().size() + " axioms.");
        System.out.println("Starting reasoning...");
        int seconds;
        long elapsedTime;
        long startTime = System.currentTimeMillis();


        // Create the reasoner and classify the ontology
        //reasoner = reasonerFactory.createNonBufferingReasoner(ontology);
        reasoner=new Reasoner.ReasonerFactory().createReasoner(ontology);


        System.out.println("Checking consistency...");
        if(!reasoner.isConsistent()) {
            System.out.println("Ontology is not consistent!");
            return;
        }
        System.out.println("End of checking consistency...");

        reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);


//        Node<OWLClass> unsatisfiableClasses = reasoner.getUnsatisfiableClasses();
//        if (unsatisfiableClasses.getSize() > 1) {
//            System.out.println("There are " + unsatisfiableClasses.getSize() +
//                    " unsatisfiable classes in the ontology: ");
//            for(OWLClass cls : unsatisfiableClasses) {
//                if (!cls.isOWLNothing()) {
//                    System.out.println("    unsatisfiable: " + cls.getIRI());
//                }
//            }
//        }

        // To generate an inferred ontology we use implementations of inferred
        // axiom generators to generate the parts of the ontology we want (e.g.
        // subclass axioms, equivalent classes axioms, class assertion axiom
        // etc. - see the org.semanticweb.owlapi.util package for more
        // implementations). Set up our list of inferred axiom generators
        List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
        gens.add(new InferredSubClassAxiomGenerator());
        gens.add(new InferredClassAssertionAxiomGenerator());

//        gens.add(new InferredDataPropertyCharacteristicAxiomGenerator());
//        gens.add(new InferredDisjointClassesAxiomGenerator());
//        gens.add(new InferredEquivalentClassAxiomGenerator());
//        gens.add(new InferredEquivalentDataPropertiesAxiomGenerator());
//        gens.add(new InferredEquivalentObjectPropertyAxiomGenerator());
//        gens.add(new InferredInverseObjectPropertiesAxiomGenerator());
//        gens.add(new InferredObjectPropertyCharacteristicAxiomGenerator());
//        gens.add(new InferredPropertyAssertionGenerator());
//        gens.add(new InferredSubClassAxiomGenerator());
//        gens.add(new InferredSubDataPropertyAxiomGenerator());
//        gens.add(new InferredSubObjectPropertyAxiomGenerator());


        // Now get the inferred ontology generator to generate some inferred
        // axioms for us (into our fresh ontology). We specify the reasoner that
        // we want to use and the inferred axiom generators that we want to use.
        InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);


        System.out.println("Using these axiom generators:");
        for(InferredAxiomGenerator inf: iog.getAxiomGenerators()) {
            System.out.println("    "+ inf);
        }


        startTime = System.currentTimeMillis();


        iog.fillOntology(manager, ontology);

        elapsedTime = System.currentTimeMillis() - startTime;
        seconds = (int) Math.ceil(elapsedTime / 1000);
        System.out.println("Reasoning took " + seconds + " seconds.");


        //change IRI
        OWLOntologyURIChanger uriChanger = new OWLOntologyURIChanger(manager);
        List<OWLOntologyChange> list = uriChanger.getChanges(ontology, iri);
        manager.applyChanges(list);
        classifiedOntology = ontology;

    }

    public OWLOntology getClassifiedOntology(){
        return classifiedOntology;
    }


}
