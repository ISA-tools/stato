package org.isatools.owl;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Created by agbeltran on 07/11/15.
 */
public class OntologyReporterTest {

    OntologyReporter ontologyReporter = null;
    String devPath=null, outDir, outFile, releaseIRI, iriPrefix;

    @Before
    public void setUp() {
        ontologyReporter = new OntologyReporter();
        devPath = "/Users/agbeltran/work-dev/stato/src/ontology/stato.owl";
        //String outDir = "/Users/agbeltran/work-dev/stato/buildReport/";
        outDir = "/Users/agbeltran/Desktop/";
        outFile = "stato-buildReport.txt";
        releaseIRI = "http://purl.obolibrary.org/obo/stato.owl";
        iriPrefix = "http://purl.obolibrary.org/obo/STATO_";


    }

    @Test
    public void buildReport(){
        ontologyReporter.buildReport(devPath, true, iriPrefix);
    }

    @Test
    public void checkEntities(){
        assert(ontologyReporter.getEntitiesSize()>0);
    }

    @Test
    public void noDuplicates(){
        Map<String, List<EntityReport>> duplicates = ontologyReporter.getDuplicates();
        assert(duplicates.isEmpty());
    }

    @Test
    public void saveReport() throws Exception {
        ontologyReporter.saveReport(outDir, outFile);
    }
}
