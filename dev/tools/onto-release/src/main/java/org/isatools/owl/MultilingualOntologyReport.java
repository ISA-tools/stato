package org.isatools.owl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

/**
 * Created by agbeltran on 27/02/2015.
 */
public class MultilingualOntologyReport {

    private Map<String, MultilingualEntityReport> iriClassReportMap = null;
    private Map<String, List<MultilingualEntityReport>> iriDuplicates = null;
    private Map<String, MultilingualEntityReport> iriNoDefinitions = null;
    private Map<String, MultilingualEntityReport> iriMetadataIncomplete = null;


    public MultilingualOntologyReport() {
        iriClassReportMap = new TreeMap<String, MultilingualEntityReport>();
        iriDuplicates = new HashMap<String, List<MultilingualEntityReport>>();
        iriNoDefinitions = new TreeMap<String, MultilingualEntityReport>();
        iriMetadataIncomplete = new TreeMap<String, MultilingualEntityReport>();
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
        MultilingualEntityReport entityReport = new MultilingualEntityReport(labels, iri, definitions, synonyms, curationStatusIRI);

        //if it already exists, add to duplicates
        MultilingualEntityReport existingEntityReport = iriClassReportMap.get(iri);

        if (existingEntityReport!=null) {
            List<MultilingualEntityReport> duplicateList = iriDuplicates.get(iri);
            if (duplicateList == null)
                duplicateList = new ArrayList<MultilingualEntityReport>();
            duplicateList.add(entityReport);
            iriDuplicates.put(iri, duplicateList);
        }

        iriClassReportMap.put(iri, entityReport);
    }

    public void addNoDefinitionEntity(Map<String, String> labels,
                                      String iri,
                                      Map<String, String> synonyms,
                                      String curationStatusIRI){
        MultilingualEntityReport entityReport = new MultilingualEntityReport(labels, iri, null, synonyms, curationStatusIRI);
        iriNoDefinitions.put(iri, entityReport);
    }

    public void addIncompleteMetadataEntity(Map<String, String> labels,
                                            String iri,
                                            Map<String, String> definitions,
                                            Map<String, String> synonyms,
                                            String curationStatusIRI){
        MultilingualEntityReport entityReport = new MultilingualEntityReport(labels, iri, definitions, synonyms, curationStatusIRI);
        iriMetadataIncomplete.put(iri, entityReport);
    }

    public Map<String, List<MultilingualEntityReport>> getDuplicates(){
        return iriDuplicates;
    }

    public Map<String, MultilingualEntityReport> getNoDefinitions(){
        return iriNoDefinitions;
    }

    public Map<String, MultilingualEntityReport> getMetadataIncomplete(){
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
            for(MultilingualEntityReport entityReport: iriDuplicates.get(iri)){
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
