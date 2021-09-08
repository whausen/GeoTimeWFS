package info.ponciano.lab.geotimewfs.controllers;

import org.apache.jena.query.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.jena.rdf.model.RDFNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import info.ponciano.lab.pisemantic.PiSparql;
import info.ponciano.lab.geotimewfs.controllers.storage.StorageService;
import info.ponciano.lab.geotimewfs.models.SparqlQuery;
import info.ponciano.lab.geotimewfs.models.geojson.Feature;
import info.ponciano.lab.geotimewfs.models.geojson.GeoJsonRDF;
import info.ponciano.lab.geotimewfs.models.geojson.Geometry;
import info.ponciano.lab.geotimewfs.models.semantic.KB;
import info.ponciano.lab.geotimewfs.models.semantic.OntoManagementException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import org.springframework.web.bind.annotation.RequestMapping;
import org.wololo.geojson.GeoJSON;

@Controller
@RequestMapping(value = "/enrichment")
public class EnrichmentController {

    String r = null;
    List<String> columnNames = new ArrayList<String>();
    List<String[]> resultList = new ArrayList<String[]>();
    private final StorageService storageService;

    @Autowired
    public EnrichmentController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/")
    public String enrichmentHome(Model model) {
        return "sparql";

    }

    @GetMapping("/enrich")
    public String enrich(@ModelAttribute("squery") SparqlQuery sq, Model model) {
        try {
            PiSparql ont = KB.get().getOnt();
            //creation of thematic map dataset
            OntClass dataset = ont.createClass(GeoJsonRDF.DCAT_DATASET);
            OntClass mt = ont.createClass(KB.NS + "Thematic_Map");
            Individual mapThem = dataset.createIndividual(KB.NS + sq.getResults());
            mapThem.addProperty(RDF.type, mt);

            List<Feature> features = new ArrayList<>();
            //for each item
            for (String[] r : resultList) {
                //create a feature
                Geometry geo = new Geometry("Point", Double.parseDouble(r[2]), Double.parseDouble(r[3]));
                Feature f = new Feature(geo);
                f.addProperty("hasWikidataOrigin", r[0]);
                f.addProperty("hasLabel", r[1]);
                features.add(f);
            }
            GeoJsonRDF.featureUplift(features, ont, mapThem);
            KB.get().save();
             model.addAttribute("message", "The ontology is enriched!");
            return "success";
        } catch (Exception ex) {
            Logger.getLogger(EnrichmentController.class.getName()).log(Level.SEVERE, null, ex);
            model.addAttribute("message", ex.getMessage());
            return "error";
        }
    }

    @ModelAttribute(name = "squery")
    public SparqlQuery sparqlquery() {
        return new SparqlQuery();
    }

    @PostMapping("/results")
    public String results(@ModelAttribute("squery") SparqlQuery sq, Model model) throws Exception {

        //prefixes for SPARQL query
        String prefixes = "PREFIX schema: <http://schema.org/>"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
                + "PREFIX hist: <http://wikiba.se/history/ontology#>"
                + "PREFIX wd: <http://www.wikidata.org/entity/>"
                + "PREFIX wdt: <http://www.wikidata.org/prop/direct/>"
                + "PREFIX wikibase: <http://wikiba.se/ontology#>"
                + "PREFIX dct: <http://purl.org/dc/terms/>"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + "PREFIX bd: <http://www.bigdata.com/rdf#>"
                + "PREFIX wds: <http://www.wikidata.org/entity/statement/>\r\n"
                + "PREFIX wdv: <http://www.wikidata.org/value/>"
                + "PREFIX p: <http://www.wikidata.org/prop/>\r\n"
                + "PREFIX ps: <http://www.wikidata.org/prop/statement/>\r\n"
                + "PREFIX psv: <http://www.wikidata.org/prop/statement/value/>"
                + "PREFIX pq: <http://www.wikidata.org/prop/qualifier/>";

        try {

            String queryString = prefixes + sq.getResults();
            Query query = QueryFactory.create(queryString);
            System.out.println(queryString);
            QueryExecution qexec = QueryExecutionFactory.sparqlService("https://query.wikidata.org/sparql", query);
            //store results in ResultSet format
            ResultSet resultset = qexec.execSelect();
            //gives the column names of the query
            columnNames = resultset.getResultVars();
            System.out.println("Column Names : " + columnNames);

            List<Integer> numberOfColumns = new ArrayList<Integer>();
            for (int i = 0; i < columnNames.size(); i++) {
                numberOfColumns.add(i);
            }
            System.out.println(numberOfColumns);

            //for all the QuerySolution in the ResultSet file
            while (resultset.hasNext()) {
                QuerySolution solu = resultset.next();
                String[] ls = new String[columnNames.size()];
                for (int i = 0; i < columnNames.size(); i++) {
                    String columnName = columnNames.get(i);
                    RDFNode node = solu.get(columnName);
                    String a = null;

                    //test if resource
                    if (node.isResource()) {
                        a = node.asResource().getLocalName();
                    }
                    //test if literal
                    if (node.isLiteral()) {
                        a = node.asLiteral().toString();
                    }
                    if (a.contains("^^http://www.w3.org/2001/XMLSchema#double")) {
                        a = a.replace("^^http://www.w3.org/2001/XMLSchema#double", "");
                    }
                    ls[i] = a;
                }
                Arrays.deepToString(ls);
                resultList.add(ls);
            }

        } catch (Exception e) {
            r = e.getMessage();
        }
        //add attributes to model
        model.addAttribute("cl", columnNames);
        model.addAttribute("MDlist", resultList);
        model.addAttribute("errorMessage", r);
        return "sparql";
    }

}
