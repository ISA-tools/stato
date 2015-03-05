package org.isatools.owl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by agbeltran on 27/02/2015.
 */
public class OntologyReport {


    class ClassReport {

        protected String label;
        protected String iri;
        protected String definition;
        protected List<String> synonyms;

        public ClassReport(String l, String i, String d, List<String> s){
            label = l;
            iri = i;
            definition = d;
            synonyms = new ArrayList<String>(s);
        }

        private void addToLine(StringBuffer line, String toAdd) {
            if (toAdd == null) {
                toAdd = "";
            }

            if (!toAdd.equals("")) {
                toAdd = toAdd.trim();

                line.append("\"").append(toAdd).append("\"\t");
            }
        }

        public String toString(){
            StringBuffer buffer = new StringBuffer();
            addToLine(buffer, iri);
            addToLine(buffer, label);
            addToLine(buffer, definition.replaceAll("\n", ""));
//            for(String synonym : synonyms){
//                addToLine(buffer, synonym);
//            }
            addToLine(buffer, synonyms.toString());
            return buffer.toString();
        }

    }

    private Map<String, ClassReport> iriClassReportMap = null;

    public OntologyReport() {
        iriClassReportMap = new TreeMap<String, ClassReport>();
    }

    public void addClass(String label, String iri, String definition, List<String> synonyms){
        ClassReport classReport = new ClassReport(label, iri, definition, synonyms);
        iriClassReportMap.put(iri, classReport);
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
