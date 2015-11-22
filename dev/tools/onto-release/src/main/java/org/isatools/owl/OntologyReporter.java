package org.isatools.owl;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import owltools.io.CatalogXmlIRIMapper;

import java.io.File;
import java.util.*;

/**
 * Created by agbeltran on 27/02/2015.
 */
public class OntologyReporter {

    private static String CATALOG_FILE =  "catalog-v001.xml";

    private static String LABEL = "http://www.w3.org/2000/01/rdf-schema#label";
    private static String ALTERNATIVE_TERM = "http://purl.obolibrary.org/obo/IAO_0000118";
    private static String STATO_ALTERNATIVE_TERM = "http://purl.obolibrary.org/obo/STATO_0000032";
    private static String DEFINITION = "http://purl.obolibrary.org/obo/IAO_0000115";
    private static String EXAMPLE_OF_USAGE = "http://purl.obolibrary.org/obo/IAO_0000117";
    private static String HAS_CURATION_STATUS = "http://purl.obolibrary.org/obo/IAO_0000114";
    private static String CURATION_STATUS_METADATA_INCOMPLETE = "http://purl.obolibrary.org/obo/IAO_0000123";


    private OWLOntologyManager manager = null;
    private OWLDataFactory dataFactory = null;
    private OWLOntology devOntology, devImportsOntology, classifiedOntology = null;
    private OntologyReport ontologyReport = null;
    private Set<OWLEntity> entities = null;

    public OntologyReporter(){
        manager = OWLManager.createOWLOntologyManager();
        dataFactory = manager.getOWLDataFactory();
        ontologyReport = new OntologyReport();
        entities = new HashSet<OWLEntity>();
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


    public void buildReport(String devPath, boolean catalogFileExists, String iriPrefix) {

        //load development ontology from local file
        loadOntology(devPath, catalogFileExists);
        if (devOntology==null){
            System.err.println("Ontology "+devPath+" couldn't be loaded!");
            return;
        }

        entities.addAll(devOntology.getClassesInSignature());
        entities.addAll(devOntology.getDataPropertiesInSignature());
        entities.addAll(devOntology.getObjectPropertiesInSignature());
        entities.addAll(devOntology.getAnnotationPropertiesInSignature());
        entities.addAll(devOntology.getIndividualsInSignature());

        //System.out.println(devOntology.getIndividualsInSignature());

        entities.addAll(devOntology.getDatatypesInSignature());

        System.out.println("There are " + entities.size() + " entities in the ontology signature");

        int count = 0;
        for(OWLEntity entity : entities){

            if (entity.getIRI().toString().startsWith(iriPrefix)) {

                String label = null;
                List<String> labels = getClassAnnotation(entity, LABEL);

                if (labels.size() == 0) {
                    System.err.println("No label for term " + entity.getIRI().toString());
                    label = "";

                } else if (labels.size() > 1) {
                    System.err.println("There are more than one label assigned " + labels);
                    label = labels.get(0);
                } else {
                    label = labels.get(0);
                }

                List<String> definitions = getClassAnnotation(entity, DEFINITION);

                boolean noDefinition = false;
                String definition = null;
                if (definitions.size() == 0) {
                    System.out.println("No DEFINITION for term " + entity.getIRI().toString() + " " + label);
                    noDefinition = true;
                    count++;
                } else if (definitions.size() > 1) {
                    System.out.println("There are more than one DEFINITION assigned " + definitions);
                    definition = definitions.get(0);
                } else if (definitions.size() == 1){
                    definition = definitions.get(0);
                }

//                if (definition ==null || (definition != null && definition.isEmpty()) ){
//                        System.out.println("No DEFINITION for term " + entity.getIRI().toString() + " " + label);
//                        count++;
//                        definition = "";
//                }

                //synonyms
                List<String> synonyms = new ArrayList<String>();
                List<String> toAdd = getClassAnnotation(entity, ALTERNATIVE_TERM);
                if (toAdd!=null)
                    synonyms.addAll(toAdd);

                toAdd = getClassAnnotation(entity, STATO_ALTERNATIVE_TERM);
                if (toAdd!=null)
                    synonyms.addAll(toAdd);


                ontologyReport.addEntity(label,
                        entity.getIRI().toString(),
                        definition,
                        synonyms
                );

                if (noDefinition)
                    ontologyReport.addNoDefinitionEntity(label,
                            entity.getIRI().toString(),
                            synonyms);

                List<String> curationStatusList = getClassAnnotation(entity, HAS_CURATION_STATUS);
                for(String value: curationStatusList){
                    if (value.equals(CURATION_STATUS_METADATA_INCOMPLETE))
                        ontologyReport.addIncompleteMetadataEntity(label,
                                entity.getIRI().toString(),
                                definition,
                                synonyms);
                }


            }
        }

        System.out.println("There are "+entities.size()+" entities in the ontology signature with the IRI prefix "+iriPrefix);
        System.out.println("There are " + count + " entities with no DEFINITION");

    }

    public int getEntitiesSize(){
        return entities.size();

    }

    public Map<String, List<EntityReport>> getDuplicates(){
        return ontologyReport.getDuplicates();
    }

    public void saveReport(String outDir, String outFile) throws Exception{
        ontologyReport.saveReport(outDir, outFile);
    }


    /*
    public static void main( String[] args ) throws Exception {


        OntologyReporter ontologyReporter = new OntologyReporter();
        String devPath = "/Users/agbeltran/work-dev/stato/src/ontology/stato.owl";
        //String outDir = "/Users/agbeltran/work-dev/stato/buildReport/";
        String outDir = "/Users/agbeltran/Desktop/";
        String outFile = "stato-buildReport.txt";
        String releaseIRI = "http://purl.obolibrary.org/obo/stato.owl";
        String iriPrefix = "http://purl.obolibrary.org/obo/STATO_";

        ontologyReporter.buildReport(devPath, true, iriPrefix);
        ontologyReporter.saveReport(outDir, outFile);


        /**
         OntologyReporter ontologyReporter = new OntologyReporter();
         String devPath = "/Users/agbeltran/workspace/obi-code/src/ontology/branches/obi.owl";
         //String outDir = "/Users/agbeltran/work-dev/stato/buildReport/";
         String outDir = "/Users/agbeltran/Desktop/";
         String outFile = "obi-buildReport.txt";
         String iriPrefix = "http://purl.obolibrary.org/obo/OBI_";

         ontologyReporter.buildReport(devPath, true, iriPrefix, outDir, outFile);


        /*
        OntologyReporter ontologyReporter = new OntologyReporter();
        String devPath = "/Users/agbeltran/workspace/nmrML/ontologies/nmrCV.owl";
        //String outDir = "/Users/agbeltran/work-dev/stato/buildReport/";
        String outDir = "/Users/agbeltran/Desktop/";
        String outFile = "nmrcv-buildReport.xls";
        String iriPrefix = "http://nmrML.org/nmrCV#";

        ontologyReporter.buildReport(devPath, true, iriPrefix, outDir, outFile);


    }
    */
}
