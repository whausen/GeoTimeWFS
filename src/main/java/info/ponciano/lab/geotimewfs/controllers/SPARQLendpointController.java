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

import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping(value = "/sparqlend")
public class SPARQLendpointController {
    
    private PiSparql ont = new PiSparql();
    private final StorageService storageService;
    
    @Autowired
    public SPARQLendpointController(StorageService storageService) {
        this.storageService = storageService;
    }
    
    @GetMapping("/")
    public String enrichmentHome(Model model) {
        return "sparql";
    }
    
    @ModelAttribute(name = "squery")
    public SparqlQuery sparqlquery() {
        return new SparqlQuery();
    }
    
    @PostMapping("/results")
    public String results(@ModelAttribute("squery") SparqlQuery sq, Model model) throws Exception {

      String r = null;
    	List<String> columnNames = new ArrayList<String>();
		List<String[]> resultList = new ArrayList<String[]>();
        
      //prefixes for SPARQL query
  		String prefixes = "PREFIX schema: <http://schema.org/>"+
  				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"+
  				"PREFIX owl: <http://www.w3.org/2002/07/owl#>"+
  				"PREFIX hist: <http://wikiba.se/history/ontology#>"+
  				"PREFIX wd: <http://www.wikidata.org/entity/>"+
  				"PREFIX wdt: <http://www.wikidata.org/prop/direct/>"+
  				"PREFIX wikibase: <http://wikiba.se/ontology#>"+
  				"PREFIX dct: <http://purl.org/dc/terms/>"+
  				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
  				"PREFIX bd: <http://www.bigdata.com/rdf#>"+
  				"PREFIX wds: <http://www.wikidata.org/entity/statement/>\r\n" + 
  				"PREFIX wdv: <http://www.wikidata.org/value/>"+
  				"PREFIX p: <http://www.wikidata.org/prop/>\r\n" + 
  				"PREFIX ps: <http://www.wikidata.org/prop/statement/>\r\n" +
  				"PREFIX psv: <http://www.wikidata.org/prop/statement/value/>" + 
  				"PREFIX pq: <http://www.wikidata.org/prop/qualifier/>";
    
        try {
        	
        	String queryString = prefixes + sq.getResults();
        	Query query = QueryFactory.create(queryString);
    		System.out.println(queryString);		
    		QueryExecution qexec = QueryExecutionFactory.sparqlService("https://query.wikidata.org/sparql", query);
    		//store results in ResultSet format
    		ResultSet results = qexec.execSelect();
    		//gives the column names of the query
    		columnNames = results.getResultVars();
    		System.out.println("Column Names : "+ columnNames);	
    		
    		List<Integer> numberOfColumns = new ArrayList<Integer>();
    		for (int i=0; i<columnNames.size(); i++) {
    			numberOfColumns.add(i);
    		}
    		System.out.println(numberOfColumns);
    		
    		
    		//for all the QuerySolution in the ResultSet file
    		while (results.hasNext()) {
    			QuerySolution solu = results.next();
    			String[]ls=new String[columnNames.size()];
    			for (int i=0; i<columnNames.size(); i++) {
    				String columnName = columnNames.get(i);
    				RDFNode node = solu.get(columnName);
    				String a = null;

                    //test if resource
                    if(node.isResource()){
                    	a =node.asResource().getLocalName();
                    }
                	//test if literal
                    if(node.isLiteral()) {
                        a = node.asLiteral().toString();
                    }
                    if(a.contains("^^http://www.w3.org/2001/XMLSchema#double")) {
                        a = a.replace("^^http://www.w3.org/2001/XMLSchema#double", "");
                    }
                    ls[i]=a;
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