package org.isatools.owl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

/**
 * Created by agbeltran on 27/02/2015.
 */
public class OntologyReport {

    //iri, entityreport map
    private Map<String, EntityReport> iriClassReportMap = null;
    private Map<String, List<EntityReport>> iriDuplicates = null;


    public OntologyReport() {
        iriClassReportMap = new TreeMap<String, EntityReport>();
        iriDuplicates = new HashMap<String, List<EntityReport>>();
    }

    /**
     * Adds an entity to the maps for entities and for duplicates
     *
     * @param label
     * @param iri
     * @param definition
     * @param synonyms
     */
    public void addEntity(String label, String iri, String definition, List<String> synonyms){
        EntityReport entityReport = new EntityReport(label, iri, definition, synonyms);
        iriClassReportMap.put(iri, entityReport);

        List<EntityReport> duplicateList = iriDuplicates.get(iri);
        if (duplicateList==null)
            duplicateList = new ArrayList<EntityReport>();
        duplicateList.add(entityReport);

    }

    public Map<String, List<EntityReport>> getDuplicates(){
        return iriDuplicates;
    }

    public String toString(){
        StringBuffer buffer = new StringBuffer();

        for(String iri : iriClassReportMap.keySet()){
            buffer.append(iriClassReportMap.get(iri).toString()+"\n");
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
