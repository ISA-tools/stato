package org.isatools.owlbuild;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by agbeltran on 03/11/15.
 */
public class OWLReleaserTest {

    static String devPath, version, outDir, outFile, releaseIRI;
    static OWLReleaser releaseBuilder;

    @BeforeClass
    public static void setUp() {
        String baseDir = System.getProperty("basedir");

        if (baseDir == null) {
            try {
                baseDir = new File(".").getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(" baseDir --> " + baseDir);

        releaseBuilder = new OWLReleaser();
        if (baseDir.endsWith("onto-release"))
            devPath = baseDir + "/../../ontology/stato.owl";
        else
            devPath = baseDir + "/dev/ontology/stato.owl";

        version = "1.4";
        outDir = baseDir + "/releases/";
        outFile = "stato.owl";
        releaseIRI = "http://purl.obolibrary.org/obo/stato.owl";
    }

    @After
    public void tearDown() {
    }

    @Test
    public void releaseTest() {
        releaseBuilder.createReleaseOntology(devPath, true, outDir, version, outFile, releaseIRI);
    }


}
