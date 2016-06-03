package org.isatools.owl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by agbeltran on 04/11/15.
 */
public class EntityReport {

    protected String label;
    protected String iri;
    protected String definition;
    protected List<String> synonyms;
    protected String curationStatusIRI;

    public EntityReport(String l, String i, String d, List<String> s, String cs){
        label = l;
        iri = i;
        definition = d;
        synonyms = new ArrayList<String>(s);
        curationStatusIRI = cs;
    }

    public String getLabel(){
        return label;
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
        if (definition!=null)
            addToLine(buffer, definition.replaceAll("\n", ""));
//            for(String synonym : synonyms){
//                addToLine(buffer, synonym);
//            }
        addToLine(buffer, synonyms.toString());
        addToLine(buffer, curationStatusIRI);
        return buffer.toString();
    }
}
