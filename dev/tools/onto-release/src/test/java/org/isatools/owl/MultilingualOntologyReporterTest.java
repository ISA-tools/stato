package org.isatools.owl;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by agbeltran on 07/11/15.
 */
public class MultilingualOntologyReporterTest {

    static MultilingualOntologyReporter ontologyReporter = null;
    static String devPath=null, outDir, outFile, releaseIRI, iriPrefix;

    @BeforeClass
    public static void setUp() throws Exception {
        String baseDir = System.getProperty("basedir");

        if (baseDir == null) {
            try {
                baseDir = new File(".").getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println(" baseDir --> " + baseDir);
        if (baseDir.endsWith("onto-release")) {
            devPath = baseDir + "/../../ontology/stato.owl";
            outDir = baseDir + "/src/test/resources/";
        } else {
            devPath = baseDir + "/dev/ontology/stato.owl";
            outDir = baseDir + "/dev/tools/onto-release/src/test/resources/";
        }

        ontologyReporter = new MultilingualOntologyReporter();

        outFile = "stato-buildReport.txt";
        releaseIRI = "http://purl.obolibrary.org/obo/stato.owl";
        iriPrefix = "http://purl.obolibrary.org/obo/STATO_";

        ontologyReporter.buildReport(devPath, true, iriPrefix);
        ontologyReporter.saveReport(outDir, outFile);
    }


    @Test
    public void checkEntities(){
        assert(ontologyReporter.getEntitiesSize()>0);
    }

    @Test
    public void noDuplicates(){
        Map<String, List<MultilingualEntityReport>> duplicates = ontologyReporter.getDuplicates();
        assert(duplicates.isEmpty());
    }

}
