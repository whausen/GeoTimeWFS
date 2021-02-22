https://geotimewfs.herokuapp.com/

# GeoTimeWFS
The goal of this project is to enrich geospatial data with linked data so that metadata and temporal revisions are no longer collected by record, but by attribute/triple in the knowledge base. This requires the development of a metadata and time concept that requires a triple declaration of timeliness, quality and origin of the data and that must be dynamically pursued during editing. The second objective of this project is a data suggestion system to enrich the knowledge base in order to facilitate the work of managing geospatial data. An intelligent solution must be developed for this and enriched maps must be displayed and generated dynamically.

This development belongs to the second phase of the project “Intelligente Datenerfassung, Haltung und Bereitstellung innerhalb der Öffentlichen Verwaltung” (https://i3mainz.hs-mainz.de/de/projekte/intelligente-datenerfassung-oeffentliche-Verwaltung), led in the i3mainz institute (https://i3mainz.hs-mainz.de) and uses the previously implemented SemanticWFS (https://github.com/i3mainz/semanticwfs).

## Install
1. Cloning git repository
````
git clone https://github.com/Cprudhomme/GeoTimeWFS.git
cd GeoTimeWFS
````
2. Deploy local dep
````
mvn deploy:deploy-file -Durl=file:./repo/ -Dfile=libs/pisemantic-1.0-SNAPSHOT.jar -DgroupId=info.ponciano.lab -DartifactId=pisemantic -Dpackaging=jar -Dversion=1.0
mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile="libs/pitools-0.1-SNAPSHOT.jar" 
````
3. Generate executable
````
mvn package
````
4. Deploy on the port 8080
````
java -jar target/geotimewfs-0.0.1-SNAPSHOT.jar 
````
5. Call your [server](http://localhost:8080) deployed

### automatic deployment
The file `system.properties` is configured for automate deployment.
## Ontology mapping:
https://www.w3.org/2015/spatial/wiki/ISO_19115_-_DCAT_-_Schema.org_mapping

