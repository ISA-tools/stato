package org.isatools.owl;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import owltools.io.CatalogXmlIRIMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
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

    private List<String> getClassAnnotation(OWLEntity clazz, String annotationString){
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

        Set<OWLEntity> entities = new HashSet<OWLEntity>();
        entities.addAll(devOntology.getClassesInSignature());
        entities.addAll(devOntology.getDataPropertiesInSignature());
        entities.addAll(devOntology.getObjectPropertiesInSignature());
        entities.addAll(devOntology.getAnnotationPropertiesInSignature());
        entities.addAll(devOntology.getIndividualsInSignature());

        System.out.println(devOntology.getIndividualsInSignature());

        entities.addAll(devOntology.getDatatypesInSignature());

        System.out.println("There are " + entities.size() + " entities in the ontology signature");

        int count = 0;
        for(OWLEntity entity : entities){

            if (entity.getIRI().toString().startsWith(iriPrefix)){

                String label = null;
                List<String> labels = getClassAnnotation(entity, LABEL);

                if (labels.size()==0) {
                    System.err.println("No label for term " + entity.getIRI().toString());
                    label = "";

                }else if (labels.size() > 1){
                    System.err.println("There are more than one label assigned "+labels);
                    label = labels.get(0);
                }else {
                    label = labels.get(0);
                }


                List<String> definitions = getClassAnnotation(entity, definition);


                String definition = null;
                if (definitions.size()==0) {

                    System.err.println("No definition for term "+entity.getIRI().toString()+" "+label);
                    definition = "";
                    count++;

                }else if (definitions.size() > 1){
                    System.err.println("There are more than one definition assigned "+definitions);
                    definition = definitions.get(0);
                }else {
                    definition = definitions.get(0);
                }

                if (definition.isEmpty()) {
                    System.err.println("No definition for term "+entity.getIRI().toString()+" "+label);
                    count++;
                }


                //synonyms
                List<String> synonyms = new ArrayList<String>();
                List<String> toAdd = getClassAnnotation(entity, alternative_term);
                if (toAdd!=null)
                    synonyms.addAll(toAdd);

                toAdd = getClassAnnotation(entity, STATO_alternative_term);
                if (toAdd!=null)
                    synonyms.addAll(toAdd);


                ontologyReport.addClass(label,
                        entity.getIRI().toString(),
                        definition,
                        synonyms
                );

            }
        }

        System.out.println("There are "+entities.size()+" entities in the ontology signature with the IRI prefix "+iriPrefix);
        System.out.println("There are "+count+" entities with no definition");
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
