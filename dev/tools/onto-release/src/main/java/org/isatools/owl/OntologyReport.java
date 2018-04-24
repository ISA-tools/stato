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
     *  @param label
     * @param iri
     * @param definition
     * @param synonyms
     * @param curationStatus
     */
    public void addEntity(String label, String iri, String definition, List<String> synonyms, String curationStatus){
        EntityReport entityReport = new EntityReport(label, iri, definition, synonyms);
        iriClassReportMap.put(iri, entityReport);

        List<EntityReport> duplicateList = iriDuplicates.get(iri);
        if (duplicateList==null)
            duplicateList = new ArrayList<EntityReport>();
        duplicateList.add(entityReport);
    }

    public void addNoDefinitionEntity(String label, String iri, List<String> synonyms, String curationStatus){
        EntityReport entityReport = new EntityReport(label, iri, "", synonyms);
        iriNoDefinitions.put(iri, entityReport);
    }

    public void addIncompleteMetadataEntity(String label, String iri, String definition, List<String> synonyms, String curationStatus){
        EntityReport entityReport = new EntityReport(label, iri, definition, synonyms);
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