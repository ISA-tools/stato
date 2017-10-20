package org.isatools.owl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

/**
 * Created by agbeltran on 27/02/2015.
 */
public class OntologyReport {

    private Map<String, EntityReport> iriClassReportMap = null;
    private Map<String, List<EntityReport>> iriDuplicates = null;
    private Map<String, EntityReport> iriNoDefinitions = null;
    private Map<String, EntityReport> iriMetadataIncomplete = null;


    public OntologyReport() {
        iriClassReportMap = new TreeMap<String, EntityReport>();
        iriDuplicates = new HashMap<String, List<EntityReport>>();
        iriNoDefinitions = new TreeMap<String, EntityReport>();
        iriMetadataIncomplete = new TreeMap<String, EntityReport>();
    }

    /**
     * Adds an entity to the maps for entities and for duplicates
     *
     * @param labels a Map of labels by language tag
     * @param iri
     * @param definitions a Map of labels by language tag
     * @param synonyms
     */
    public void addEntity(Map<String,String> labels, String iri, Map<String, String> definitions, Map<String, String> synonyms, String curationStatusIRI){
        EntityReport entityReport = new EntityReport(labels, iri, definitions, synonyms, curationStatusIRI);

        //if it already exists, add to duplicates
        EntityReport existingEntityReport = iriClassReportMap.get(iri);

        if (existingEntityReport!=null) {
            List<EntityReport> duplicateList = iriDuplicates.get(iri);
            if (duplicateList == null)
                duplicateList = new ArrayList<EntityReport>();
            duplicateList.add(entityReport);
            iriDuplicates.put(iri, duplicateList);
        }

        iriClassReportMap.put(iri, entityReport);
    }

    public void addNoDefinitionEntity(Map<String, String> labels,
                                      String iri,
                                      Map<String, String> synonyms,
                                      String curationStatusIRI){
        EntityReport entityReport = new EntityReport(labels, iri, null, synonyms, curationStatusIRI);
        iriNoDefinitions.put(iri, entityReport);
    }

    public void addIncompleteMetadataEntity(Map<String, String> labels,
                                            String iri,
                                            Map<String, String> definitions,
                                            Map<String, String> synonyms,
                                            String curationStatusIRI){
        EntityReport entityReport = new EntityReport(labels, iri, definitions, synonyms, curationStatusIRI);
        iriMetadataIncomplete.put(iri, entityReport);
    }

    public Map<String, List<EntityReport>> getDuplicates(){
        return iriDuplicates;
    }

    public Map<String, EntityReport> getNoDefinitions(){
        return iriNoDefinitions;
    }

    public Map<String, EntityReport> getMetadataIncomplete(){
        return iriMetadataIncomplete;
    }

    public String toString(){
        StringBuffer buffer = new StringBuffer();

        buffer.append("ENTITIES"+"\n");
        for(String iri : iriClassReportMap.keySet()){
            buffer.append(iriClassReportMap.get(iri).toString()+"\n");
        }
        buffer.append("ENTITIES WITHOUT DEFINITION"+"\n");
        for(String iri : getNoDefinitions().keySet()){
            buffer.append(getNoDefinitions().get(iri).toString()+"\n");
        }
        buffer.append("ENTITIES WITHOUT COMPLETE METADATA"+"\n");
        for(String iri : getMetadataIncomplete().keySet()){
            buffer.append(getMetadataIncomplete().get(iri).toString()+"\n");
        }

        buffer.append("DUPLICATES"+"\n");
        for(String iri: iriDuplicates.keySet()){
            buffer.append(iri+"\t");
            for(EntityReport entityReport: iriDuplicates.get(iri)){
                buffer.append(entityReport.getLabels()+"\t");
            }
            buffer.append("\n");
        }
        return buffer.toString();
    }

    public void saveReport(String outDirectory, String outFilename) throws FileNotFoundException {
        String outputPath = outDirectory + outFilename;
        File file = new File(outputPath);
        String content = toString();
        PrintStream printStream = new PrintStream(file);
        printStream.print(content);
        printStream.close();
    }




}
