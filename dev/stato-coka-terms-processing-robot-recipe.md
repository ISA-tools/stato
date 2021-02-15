# STATO / Covid Knowledge Accelerator (COKA) FHIR collaboration

author: [philippe rocca-serra](philippe.rocca-serra@oerc.ox.ac.uk)   [orcid](https://orcid.org/0000-0001-9853-5668)
license: CC-BY-4.0
date: 2021-02-15

## Creating a `standalone` 'Risk of Bias Ontology Module' for the FHIR COKA group

Using OBOFoundry Robot ontology building tool [1]

```bash
java -jar robot.jar template  --template ./robot-templates/stato-coka-robot-template-all.csv  --prefix "stato_ext:http://stato-ontology.org/"  --ontology-iri "http://purl.org/obofoundry/stato-robi-extension"   --output robot-outputs/standalone/coka-robi.owl
```

or using the latest template 
	
```bash
java -jar ../robot.jar template  --template ./robot-templates/test-stato-coka-robis-latest.csv  --prefix "stato_ext:http://stato-ontology.org/"  --ontology-iri "http://purl.org/obofoundry/stato-robi-extension"   --output robot-outputs/standalone/test-stato-coka-robis-latest.owl
```



## Adding terms directly to STATO  and creating an `integrated version` of STATO + FHIR COKA group terminology needs

Using OBOFoundry Robot ontology building tool

```bash
java -jar ../robot.jar template  --merge-before  --input ./stato/stato.owl --template ./robot-templates/stato-coka-robot-template-all.csv  --prefix "stato_ext:http://stato-ontology.org/"  --ontology-iri "http://purl.org/obofoundry/stato-robi-extension"   --output robot-outputs/stato-integrated/stato-as-input-coka-robi.owl
```

or using the latest template 

```bash
java -jar ../robot.jar template  --merge-before  --input ./stato/stato.owl  --template ./robot-templates/test-stato-coka-robis-latest.csv  --prefix "stato_ext:http://stato-ontology.org/"  --ontology-iri "http://purl.org/obofoundry/stato-robi-extension"   --output robot-outputs/stato-integrated/test-stato-coka-robis-latest.owl
```



The stato github repository currently host several robot templates allowing handling of COKA FHIR group terminology needs. 
There is one Robot Template per work area, ie  Statistical Types, Statistical Models, Study Desing and Risk of Bias.


- stato-coka-robot-template-robi-only.csv
- stato-coka-robot-template-stat-types-only.csv
- stato-coka-robot-template-stat-models-only.csv
- stato-coka-robot-template-study-design-only.csv

There is also an additional template which corresponds to the concatenation of all four individual, area-specific templates.

-stato-coka-robot-template-all.csv

each of these modules


### Converting the resuling OWL to FHIR using the `OWL to FHIR Transformer`

`[OWL to FHIR Transformer](https://github.com/aehrc/fhir-owl)` 

```bash
git clone https://github.com/aehrc/fhir-owl.git
```


```bash
cd ./git/fhir-owl/
```

build from source:

```bash
mvn package
```


1. navigate to the target directory to get the built jar file

2. run the following command to convert from OWL to the FHIR JSON format

java -jar fhir-owl-1.0.0.jar -i [input OWL file] -o [output FHIR JSON file]


```bash
java -jar fhir-owl-1.0.0.jar -i results/stato-as-input-coka-robi.owl -o results/stato-as-input-coka-robi-fhir.json
```


```bash
java -jar fhir-owl-1.0.0.jar -i /Users/philippe/Documents/git/robot-1.8/stato-coka/results/stato-as-input-coka-robi.owl -o /Users/philippe/Documents/git/robot-1.8/stato-coka/results/stato-as-input-coka-robi-fhir.json
```


Fancier command with more advanced options:
```bash
java -jar fhir-owl-1.0.0.jar 
-i so-simple.owl
-o so-simple.json
-id so -name "Sequence Ontology"
-status active -codeReplace "_,:" 
-s "http://www.geneontology.org/formats/oboInOwl#hasExactSynonym" 
-mainNs "http://purl.obolibrary.org/obo/SO_" 
-labelsToExclude "wiki,WIKI"
```


## Reference:

1. [Robot Ontology Building Tool](http://robot.obolibrary.org/)

2. [OWL to FHIR Transformer](https://github.com/aehrc/fhir-owl)


