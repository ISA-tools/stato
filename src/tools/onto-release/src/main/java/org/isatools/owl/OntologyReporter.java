package org.isatools.owl;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import owltools.io.CatalogXmlIRIMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
    private OntologyReport ontologyReport = null;

    public OntologyReporter(){
        manager = OWLManager.createOWLOntologyManager();
        dataFactory = manager.getOWLDataFactory();
        ontologyReport = new OntologyReport();
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

    private List<String> getClassAnnotation(OWLClass clazz, String annotationString){
        Set<OWLAnnotation> annotationSet = clazz.getAnnotations(devOntology, dataFactory.getOWLAnnotationProperty(IRI.create(annotationString)));
        List<String> labels = new ArrayList<String>();
        for(OWLAnnotation annotation : annotationSet){
            labels.add(annotation.getValue().toString());
        }
        return labels;
    }


    public void report(String devPath, boolean catalogFileExists, String iriPrefix, String outDir, String outFile) throws Exception {

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
//                System.out.println(clazz+"\t"+ getClassAnnotation(clazz, LABEL)+"\t"
//                        + getClassAnnotation(clazz, alternative_term)
//                        + "\t"+ getClassAnnotation(clazz, STATO_alternative_term) );


               String label = null;
               List<String> labels = getClassAnnotation(clazz, LABEL);

               if (labels.size()==0) {
                   System.err.println("No label for term "+clazz.getIRI().toString());
                   label = "";

               }else if (labels.size() > 1){
                   System.err.println("There are more than one label assigned "+labels);
                   label = labels.get(0);
               }else {
                   label = labels.get(0);
               }


               List<String> definitions = getClassAnnotation(clazz, definition);


               String definition = null;
               if (definitions.size()==0) {

                   System.err.println("No definition for term "+clazz.getIRI().toString()+" "+label);
                   definition = "";

               }else if (definitions.size() > 1){
                   System.err.println("There are more than one definition assigned "+definitions);
                   definition = definitions.get(0);
               }else {
                   definition = definitions.get(0);
               }


               //synonyms
               List<String> synonyms = new ArrayList<String>();
               List<String> toAdd = getClassAnnotation(clazz, alternative_term);
               if (toAdd!=null)
                synonyms.addAll(toAdd);

               toAdd = getClassAnnotation(clazz, STATO_alternative_term);
               if (toAdd!=null)
                synonyms.addAll(toAdd);


               ontologyReport.addClass(label,
                                       clazz.getIRI().toString(),
                                       definition,
                                       synonyms
                                       );
                count++;
           }
        }

        System.out.println("There are "+classes.size()+" classes in the ontology signature with the IRI prefix "+iriPrefix);
        ontologyReport.saveReport(outDir, outFile);
    }


    public static void main( String[] args ) throws Exception {
        OntologyReporter ontologyReporter = new OntologyReporter();
        String devPath = "/Users/agbeltran/work-dev/stato/src/ontology/stato.owl";
        //String outDir = "/Users/agbeltran/work-dev/stato/report/";
        String outDir = "/Users/agbeltran/Desktop/";
        String outFile = "stato-report.txt";
        String releaseIRI = "http://purl.obolibrary.org/obo/stato.owl";
        String iriPrefix = "http://purl.obolibrary.org/obo/STATO_";

        ontologyReporter.report(devPath, true, iriPrefix, outDir, outFile);
    }
}
