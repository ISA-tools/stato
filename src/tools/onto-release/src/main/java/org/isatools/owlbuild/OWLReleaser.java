package org.isatools.owlbuild;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLOntologyMerger;
import owltools.io.CatalogXmlIRIMapper;

import java.io.File;

/**
 * Class with functionality to build a release of a development ontology
 *
 */
public class OWLReleaser
{
    private OWLOntologyManager manager = null;
    private OWLDataFactory dataFactory = null;
    private OWLOntology devOntology, devImportsOntology, classifiedOntology = null;
    private OWLClassifier classifier = null;
    private static String CATALOG_FILE =  "catalog-v001.xml";
    private IRI temporaryIRI = IRI.create("http://temporary.org");

    public OWLReleaser(){
        manager = OWLManager.createOWLOntologyManager();
        classifier = new OWLClassifier();
    }

    public void createReleaseOntology(String devPath, String outPath, String releaseIRI){

        //load development ontology from local file
        loadOntology(devPath);
        if (devOntology==null){
            System.err.println("Ontology "+devPath+" couldn't be loaded!");
            return;
        }

        //merge ontology and imports into a single file
        mergeDevOntoAndImports();
        if (devImportsOntology==null){
            System.err.println("The ontology "+devPath+" couldn't be merged with its imported ontologies!");
            return;
        }

        //classify ontology
        classifyDevImportsOntology(IRI.create(releaseIRI));
        if (classifiedOntology==null){
            System.err.println("The ontology "+devPath+" couldn't be classified!");
            return;
        }

        //save release ontology
        saveReleaseOnto(outPath);
    }

    /**
     *
     * @param path
     */
    private void loadOntology(String path)  {
        File file = new File(path);
        String catalogPath = file.getParent() + "/"+ CATALOG_FILE;
        try {
            manager.addIRIMapper(new CatalogXmlIRIMapper(catalogPath));
            devOntology = manager.loadOntologyFromOntologyDocument(file);
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private void mergeDevOntoAndImports() {
        OWLOntologyMerger merger = new OWLOntologyMerger(manager);
        try {
            devImportsOntology = merger.createMergedOntology(manager, temporaryIRI);
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
    }

    private void classifyDevImportsOntology(IRI iri){
        classifier.classify(devImportsOntology);
        classifiedOntology = classifier.getClassifiedOntology();
    }

    private void saveReleaseOnto(String outPath){
        // Save the inferred ontology. (Replace the URI with one that is
        // appropriate for your setup)
        File outFile = new File(outPath);
        try {
            //manager.saveOntology(classifiedOntology, new StringDocumentTarget());
            manager.saveOntology(classifiedOntology, IRI.create(outFile.toURI()));
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        }
    }

    public static void main( String[] args )
    {
        OWLReleaser releaseBuilder = new OWLReleaser();
        String devPath = "/Users/agbeltran/workspace/stato-agb/src/ontology/stato.owl";
        String outPath = "/Users/agbeltran/workspace/stato-agb/releases/stato.owl";
        String releaseIRI = //"http://temporary.org";
                         "http://purl.obolibrary.org/obo/stato.owl";

        releaseBuilder.createReleaseOntology(devPath,outPath,releaseIRI);
    }
}
