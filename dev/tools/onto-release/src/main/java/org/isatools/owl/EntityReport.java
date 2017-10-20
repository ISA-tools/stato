package org.isatools.owl;

import java.util.Map;

/**
 * Created by agbeltran on 04/11/15.
 */
public class EntityReport {

    protected Map<String, String> labels;
    protected String iri;
    protected Map<String, String> definitions;
    protected Map<String, String> synonyms;
    protected String curationStatusIRI;

    public EntityReport(Map<String,String> l, String i, Map<String, String> d, Map<String, String> s, String cs){
        labels = l;
        iri = i;
        definitions = d;
        synonyms = s;
        curationStatusIRI = cs;
    }

    public Map<String, String> getLabels(){
        return labels;
    }

    public String getLabel(String languageTag) { return labels.get(languageTag); }

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
        for (String key: labels.keySet())
            addToLine(buffer, labels.get(key));
        if (definitions!=null && !definitions.isEmpty())
            for (String dlang: definitions.keySet())
                addToLine(buffer, definitions.get(dlang).replaceAll("\n", ""));
//            for(String synonym : synonyms){
//                addToLine(buffer, synonym);
//            }
        addToLine(buffer, synonyms.toString());
        addToLine(buffer, curationStatusIRI);
        return buffer.toString();
    }
}
