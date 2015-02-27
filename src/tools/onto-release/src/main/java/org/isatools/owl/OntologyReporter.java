package org.isatools.owl;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import owltools.io.CatalogXmlIRIMapper;

import java.io.File;
import java.util.Set;

/**
 * Created by agbeltran on 27/02/2015.
 */
public class OntologyReporter {

    private static String CATALOG_FILE =  "catalog-v001.xml";

    private static String LABEL = "http://www.w3.org/2000/01/rdf-schema#label";
    private static String alternative_term = "http://purl.obolibrary.org/obo/IAO_0000118";
    private static String STATO_alternative_term = "http://purl.obolibrary.org/obo/STATO_0000032";
    private static String definition = "http://purl.obolibrary.org/obo/IAO_0000115";
    private static String example_of_usage = "http://purl.obolibrary.org/obo/IAO_0000117";


    private OWLOntologyManager manager = null;
    private OWLDataFactory dataFactory = null;
    private OWLOntology devOntology, devImportsOntology, classifiedOntology = null;

    public OntologyReporter(){
        manager = OWLManager.createOWLOntologyManager();
        dataFactory = manager.getOWLDataFactory();
    }


    /**
     *
     * Load the ontology
     *
     * @param path
     */
    private void loadOntology(String path, boolean catalogFileExists)  {
        File file = new File(path);

        if (catalogFileExists){
            String catalogPath = file.getParent() + "/"+ CATALOG_FILE;
            try {
                manager.addIRIMapper(new CatalogXmlIRIMapper(catalogPath));
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        try{
            devOntology = manager.loadOntologyFromOntologyDocument(file);

        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
    }

    private String getClassName(OWLClass clazz){
        Set<OWLAnnotation> annotationSet = clazz.getAnnotations(devOntology, dataFactory.getOWLAnnotationProperty(IRI.create(LABEL)));
        return ((OWLAnnotation)annotationSet.toArray()[0]).getValue().toString();
    }


    public void report(String devPath, boolean catalogFileExists, String iriPrefix, String outDir, String outFile){

        //load development ontology from local file
        loadOntology(devPath, catalogFileExists);
        if (devOntology==null){
            System.err.println("Ontology "+devPath+" couldn't be loaded!");
            return;
        }

        Set<OWLClass> classes = devOntology.getClassesInSignature();

        System.out.println("There are "+classes.size()+" classes in the ontology signature");

        int count = 0;
        for(OWLClass clazz : classes){

           if (clazz.getIRI().toString().startsWith(iriPrefix)){
                System.out.println(clazz+"\t"+ getClassName(clazz));
                count++;
           }
        }

        System.out.println("There are "+classes.size()+" classes in the ontology signature with the IRI prefix "+iriPrefix);
    }


    public static void main( String[] args )
    {
        OntologyReporter ontologyReporter = new OntologyReporter();
        String devPath = "/Users/agbeltran/work-dev/stato/src/ontology/stato.owl";
        String outDir = "/Users/agbeltran/work-dev/stato/report/";
        String outFile = "stato-report.txt";
        String releaseIRI = "http://purl.obolibrary.org/obo/stato.owl";
        String iriPrefix = "http://purl.obolibrary.org/obo/STATO_";

        ontologyReporter.report(devPath, true, iriPrefix, outDir, outFile);
    }
}
