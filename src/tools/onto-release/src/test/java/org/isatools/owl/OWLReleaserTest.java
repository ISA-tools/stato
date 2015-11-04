package org.isatools.owl;

import org.isatools.owlbuild.OWLReleaser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by agbeltran on 03/11/15.
 */
public class OWLReleaserTest {

    String devPath, version, outDir, outFile, releaseIRI;
    OWLReleaser releaseBuilder;

    @Before
    public void setUp() {
        releaseBuilder = new OWLReleaser();
        devPath = "/Users/agbeltran/workspace/stato-agb/src/ontology/stato.owl";
        version = "1.3";
        outDir = "/Users/agbeltran/workspace/stato-agb/releases/";
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
