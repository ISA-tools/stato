package org.isatools.owlbuild;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLOntologyMerger;
import owltools.io.CatalogXmlIRIMapper;

import java.io.File;
import java.util.Set;

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
    private org.semanticweb.owlapi.model.IRI temporaryIRI = IRI.create("http://temporary.org");
    private Set<OWLAnnotation> annotationSet = null;

    private static String CATALOG_FILE =  "catalog-v001.xml";
    private static IRI VERSION_INFO_IRI = org.semanticweb.owlapi.model.IRI.create("http://www.w3.org/2002/07/owl#versionInfo");

    public OWLReleaser(){
        manager = OWLManager.createOWLOntologyManager();
        dataFactory = manager.getOWLDataFactory();
        classifier = new OWLClassifier();
    }

    public void createReleaseOntology(String devPath,
                                      boolean catalogFileExists,
                                      String outDir,
                                      String version,
                                      String outFile,
                                      String releaseIRI){

        //load development ontology from local file
        loadOntology(devPath, catalogFileExists);
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
        classifyDevImportsOntology(org.semanticweb.owlapi.model.IRI.create(releaseIRI),version);
        if (classifiedOntology==null){
            System.err.println("The ontology "+devPath+" couldn't be classified!");
            return;
        }

        //save release ontology
        saveReleaseOnto(outDir+"/"+version+"/"+outFile);
        saveReleaseOnto(outDir+"/latest_release/"+outFile);
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
        annotationSet = devOntology.getAnnotations();
        } catch (OWLOntologyCreationException e) {
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

    private void classifyDevImportsOntology(org.semanticweb.owlapi.model.IRI iri, String version){
        classifier.classify(devImportsOntology, iri);
        classifiedOntology = classifier.getClassifiedOntology();

        //add annotations and set version info
        if (annotationSet!=null){
            for(OWLAnnotation annotation : annotationSet) {

                if (annotation.getProperty().getIRI().equals(VERSION_INFO_IRI)){

                    annotation = dataFactory.getOWLAnnotation(dataFactory.getOWLAnnotationProperty(VERSION_INFO_IRI), dataFactory.getOWLLiteral(version));
                }

                manager.applyChange(new AddOntologyAnnotation(classifiedOntology, annotation));
            }
        }
    }

    /**
     * Saved the ontology for release
     *
     * @param outPath
     */
    private void saveReleaseOnto(String outPath){
        File outFile = new File(outPath);
        try {
            manager.saveOntology(classifiedOntology, IRI.create(outFile.toURI()));
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        }
    }

    public static void main( String[] args )
    {
        OWLReleaser releaseBuilder = new OWLReleaser();
        String devPath = "/Users/agbeltran/workspace/stato-agb/src/ontology/stato.owl";
        String version = "1.0";
        String outDir = "/Users/agbeltran/workspace/stato-agb/releases/";
        String outFile = "stato.owl";
        String releaseIRI = "http://purl.obolibrary.org/obo/stato.owl";


        releaseBuilder.createReleaseOntology(devPath, true, outDir, version, outFile, releaseIRI);
    }
}
