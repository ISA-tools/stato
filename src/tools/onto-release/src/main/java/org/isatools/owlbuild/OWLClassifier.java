package org.isatools.owlbuild;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;

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

    public void classify(OWLOntology onto) {
        ontology = onto;
        manager = onto.getOWLOntologyManager();


        System.out.println("Ontology has " + ontology.getAxioms().size() + " axioms.");
        System.out.println("Starting reasoning...");
        int seconds;
        long elapsedTime;
        long startTime = System.currentTimeMillis();


        // Create the reasoner and classify the ontology
        //reasoner = reasonerFactory.createNonBufferingReasoner(ontology);
        reasoner=new Reasoner.ReasonerFactory().createReasoner(ontology);


//        System.out.println("Checking consistency...");
//        if(!reasoner.isConsistent()) {
//            System.out.println("Ontology is not consistent!");
//            return;
//        }
//        System.out.println("End of checking consistency...");

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



        // Put the inferred axioms into a fresh empty ontology - note that there
        // is nothing stopping us stuffing them back into the original asserted
        // ontology if we wanted to do this.
        classifiedOntology = ontology;

    }

    public OWLOntology getClassifiedOntology(){
        return classifiedOntology;
    }


}
