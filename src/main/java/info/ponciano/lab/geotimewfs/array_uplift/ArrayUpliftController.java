package info.ponciano.lab.geotimewfs.array_uplift;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import info.ponciano.lab.geotimewfs.controllers.storage.StorageService;
import info.ponciano.lab.geotimewfs.models.Metadata;
import info.ponciano.lab.geotimewfs.models.SHPdata;
import info.ponciano.lab.pitools.files.PiFile;

@Controller
public class ArrayUpliftController {
	
	private ArrayUpliftModel am;
	private final StorageService storageService;

    @Autowired
    public ArrayUpliftController(StorageService storageService) {
        this.storageService = storageService;
    }

	
    /**
     * Get function to load CSV file
     * @param model of the view
     * @return the view to load a CSV file
     */
	@GetMapping("/csv_loading")
    public String getCsvLoadingView(Model model) {
		model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(ArrayUpliftController.class,
                        "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));
		String m="You successfully uplift the shapefile: vg250gem.shp, if you want to uplift shapefile attributes, "
				+"please convert the DBF file into CSV file and load it.";
		model.addAttribute("message", m);
        return "csvloading";
    }

    @GetMapping("/csv_loading/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
	
	//view to load DBF file
	//@GetMapping("/dbf_loading")
    //public abstract String getDbfLoadingView(Model model);
	
	//View with name of uploaded file and view of the array to define the properties of the ontology  
	@PostMapping("/array_uplift")
    public String arrayUpliftView(@RequestParam("file") MultipartFile file, Model model) {//RedirectAttributes redirectAttributes) {
		// store file
		storageService.store(file);

		//redirectAttributes.addFlashAttribute("message",
						//"You successfully upload the file(s): " + file.getOriginalFilename() 
						//+ ". Please indicate now the mapping between attributes of your file and the appropriate ontological properties");
		
		// store the csv file in a specific folder for CSV files
		/**String pathCSV = "csv-dir/" + file.getOriginalFilename();
		try {
			moveFile(file.getOriginalFilename(), pathCSV);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}**/
		String message="You successfully upload the file(s): " + file.getOriginalFilename() ;
		message += ". Please indicate now the mapping between attributes of your file and the appropriate ontological properties";
		model.addAttribute("message", message);
		try {
			//File reading
			String filename=file.getOriginalFilename();
			if(filename.substring(filename.length()-4).equals("csv"));
			PiFile pf= new PiFile("upload-dir/"+filename);
			String [][] attribute=pf.readCSV(";");
			//init the array uplift model with attribute
			this.am=new ArrayUpliftModelImp(attribute, "rdf-data/"+filename.substring(0, filename.length()-4)+".owl");
			//init list to display a sample of the csv file 
			List<String[]> lf=new ArrayList<String []>();
			if(attribute!=null) {
				lf= this.am.geFirstRows(5);
				//init the number of property to map
				int nbcol=lf.get(0).length;
				model.addAttribute("nbc", nbcol);
			}
			//provide the sample to the model used by the view
			model.addAttribute("fc", lf);
			//init and provide the property list
			List<String> prop=this.am.getProperties();
			model.addAttribute("prop", prop);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return "arrayMappingView";
		
	}
	
	/**
	 * move a file from a directory to the CSV storage directory
	 * 
	 * @param filename: name of the file to move
	 * @param pathSHP:  initial path of the file to move
	 * @throws IOException
	 */
	private void moveFile(String filename, String pathSHP) throws IOException {
		PiFile dirSHPdata = new PiFile(SHPdata.DIR_SHP_DATA);
		if (!dirSHPdata.exists())
			dirSHPdata.mkdir();
		new PiFile(pathSHP).mv(dirSHPdata.getPath() + filename);
	}
	
	//adding of a new Property from its local name, range and type
		//require to update hashmap and list 
		@PostMapping("/property_adding")
	    public String addNewProperty() {
			String localname="";
			String range="";
			String message="";
			boolean adding=false;
			if(am!=null) {
				adding=this.am.addProperty(localname, range);
				if(adding) message="The property "+localname+" has been successfully added.";
				else message="The adding of the property "+localname+" has failed.";
			}
			else
				message="The model has not been initialized";

			return message;
		}
	
	// initialize the model attribute "metadata"
		@ModelAttribute(name = "propmap")
		public PropertyMapping propmap() {
			return new PropertyMapping();
		}

	@PostMapping("/uplift_validation")
    public String ontologyPopulation(@ModelAttribute("propmap") PropertyMapping propmap, Model model) {
		String m="";
		try {
			this.am.createOntology(propmap.getClassname(), propmap.getProperties());
			m="Ontology successfully created.";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			m="Error: the ontology has not been created.";
		}
		model.addAttribute("message", m);
		return "view";
	}
	
	
	
}
