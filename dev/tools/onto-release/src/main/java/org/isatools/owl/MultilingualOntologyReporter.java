package org.isatools.owl;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.EntitySearcher;
import owltools.io.CatalogXmlIRIMapper;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by agbeltran on 27/02/2015.
 */
public class MultilingualOntologyReporter {

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
    private MultilingualOntologyReport ontologyReport = null;
    private Set<OWLEntity> entities = null;

    public MultilingualOntologyReporter(){
        manager = OWLManager.createOWLOntologyManager();
        dataFactory = manager.getOWLDataFactory();
        ontologyReport = new MultilingualOntologyReport();
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
        Stream<OWLAnnotation> annotationStream = EntitySearcher.getAnnotations(clazz, devOntology, dataFactory.getOWLAnnotationProperty(IRI.create(annotationString)));
        List<String> labels = new ArrayList<String>();
        annotationStream.forEach(
                annotation -> labels.add(annotation.getValue().toString())
        );
        return labels;
    }

    private Map<String, String> getClassAnnotationAndLanguageTag(OWLEntity clazz, String annotationString){
        Stream<OWLAnnotation> annotationStream = EntitySearcher.getAnnotations(clazz, devOntology, dataFactory.getOWLAnnotationProperty(IRI.create(annotationString)));
        // <language tag, label>
        Map<String, String> labels = new TreeMap<String, String>();

        annotationStream.forEach(
                annotation -> labels.put(((OWLLiteral)annotation.getValue()).getLang(), annotation.getValue().toString())
        );
        return labels;
    }


    public void buildReport(String devPath, boolean catalogFileExists, String iriPrefix) {

        //load development ontology from local file
        loadOntology(devPath, catalogFileExists);
        if (devOntology==null){
            System.err.println("Ontology "+devPath+" couldn't be loaded!");
            return;
        }

        entities.addAll(devOntology.classesInSignature().collect(Collectors.toSet()));
        entities.addAll(devOntology.dataPropertiesInSignature().collect(Collectors.toSet()));
        entities.addAll(devOntology.objectPropertiesInSignature().collect(Collectors.toSet()));
        entities.addAll(devOntology.annotationPropertiesInSignature().collect(Collectors.toSet()));
        entities.addAll(devOntology.individualsInSignature().collect(Collectors.toSet()));
        entities.addAll(devOntology.datatypesInSignature().collect(Collectors.toSet()));

        System.out.println("There are " + entities.size() + " entities in the ontology signature");

        int count = 0;
        for(OWLEntity entity : entities){

            if (entity.getIRI().toString().startsWith(iriPrefix)) {

                Map<String, String> labels = getClassAnnotationAndLanguageTag(entity, LABEL);

                if (labels.isEmpty()) {
                    System.err.println("No label for term " + entity.getIRI().toString());

                } else {
                    for(String key: labels.keySet())
                        if (labels.get(key).length() > 1)
                            System.err.println("There are more than one label assigned for language " + key +" -> "+ labels.get(key));
                }

                Map<String, String> definitions = getClassAnnotationAndLanguageTag(entity, DEFINITION);

                boolean noDefinition = false;
                String definition = null;
                if (definitions.isEmpty()) {
                    System.out.println("No DEFINITION for term " + entity.getIRI().toString() + " " + labels.get("en"));
                    noDefinition = true;
                    count++;
                } else {
                    for(String key: definitions.keySet())
                    if (definitions.get(key).length() > 1) {
                        System.out.println("There are more than one DEFINITION assigned for language tag " +key +" -> "+ definitions.get(key));
                    }
                }

                //synonyms pero language
                Map<String, String> synonyms = new HashMap<String, String>();
                Map<String, String> toAdd = getClassAnnotationAndLanguageTag(entity, ALTERNATIVE_TERM);
                if (toAdd!=null)
                    synonyms.putAll(toAdd);

                toAdd = getClassAnnotationAndLanguageTag(entity, STATO_ALTERNATIVE_TERM);
                if (toAdd!=null)
                    synonyms.putAll(toAdd);


                List<String> curationStatusList = getClassAnnotation(entity, HAS_CURATION_STATUS);

                String curationStatus = null;
                if (!curationStatusList.isEmpty())
                    curationStatus =  curationStatusList.get(0);
                else
                    curationStatus = "";

                ontologyReport.addEntity(labels,
                        entity.getIRI().toString(),
                        definitions,
                        synonyms,
                        curationStatus
                );

                if (noDefinition)
                    ontologyReport.addNoDefinitionEntity(labels,
                            entity.getIRI().toString(),
                            synonyms,
                            curationStatus);


                for(String value: curationStatusList){
                    if (value.equals(CURATION_STATUS_METADATA_INCOMPLETE))
                        ontologyReport.addIncompleteMetadataEntity(labels,
                                entity.getIRI().toString(),
                                definitions,
                                synonyms,
                                curationStatus);
                }


            }
        }

        System.out.println("There are "+entities.size()+" entities in the ontology signature with the IRI prefix "+iriPrefix);
        System.out.println("There are " + count + " entities with no DEFINITION");

    }

    public int getEntitiesSize(){
        return entities.size();

    }

    public Map<String, List<MultilingualEntityReport>> getDuplicates(){
        return ontologyReport.getDuplicates();
    }

    public void saveReport(String outDir, String outFile) throws Exception{
        ontologyReport.saveReport(outDir, outFile);
    }



    public static void main( String[] args ) throws Exception {


        MultilingualOntologyReporter ontologyReporter = new MultilingualOntologyReporter();
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


        OntologyReporter ontologyReporter = new OntologyReporter();
        String devPath = "/Users/agbeltran/workspace/nmrML/ontologies/nmrCV.owl";
        //String outDir = "/Users/agbeltran/work-dev/stato/buildReport/";
        String outDir = "/Users/agbeltran/Desktop/";
        String outFile = "nmrcv-buildReport.xls";
        String iriPrefix = "http://nmrML.org/nmrCV#";

        ontologyReporter.buildReport(devPath, true, iriPrefix, outDir, outFile);
         **/

    }

}
