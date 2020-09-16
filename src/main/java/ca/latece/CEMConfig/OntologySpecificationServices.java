package ca.latece.CEMConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import OWLSpecification.*;
import ca.latece.CEMConfig.Entities.Property;

@Path("/config")
public class OntologySpecificationServices {
	String file = "file:"+System.getProperty("user.dir")+"/Cem/GenericCEMOntology.owl";
	String prefix = "http://www.semanticweb.org/imenbenzarti/ontologies/2017/5/CEMOntology#";
	String targetfile = "file:"+System.getProperty("user.dir")+"/Cem/SpecificCEMOntology.owl";
	SpecificOntologyGenerator generator = new SpecificOntologyGenerator(prefix, file, targetfile);
	
	
	@POST
    @Path("/addProduct/{product}")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response addProduct( @PathParam("product") String productName){
        generator.addProductCategory(productName);
        return Response.ok().build();
    }
	
	@POST
    @Path("/addProductCategories")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public Response addSubCategory( String categories ){
		Gson gson = new Gson ();
		JsonObject json = gson.fromJson(categories, JsonObject.class);
		JsonArray cat = json.get("categories").getAsJsonArray() ;
		addCtaegories("ProductCategory", cat);
		System.out.println("rest fonctionne5 : "+ cat + "eeeeeee"+ categories);
		return Response.ok().build();
    }
	
	public void addCtaegories (String superCategory, JsonArray array){
		Iterator<JsonElement> iterator  = array.iterator();
		while(iterator.hasNext()){
			JsonElement element = iterator.next();
			generator.createSubClass(superCategory, element.getAsJsonObject().get("id").getAsString());
			System.out.println(element.getAsJsonObject().get("children"));
			if (element.getAsJsonObject().get("children")!=null)
				addCtaegories( element.getAsJsonObject().get("id").getAsString(), element.getAsJsonObject().get("children").getAsJsonArray());
			
		}
	}
	
	@POST
    @Path("/addDataProperty")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addDataPropertyNumeric(String json /*String className, String dataPropertyName*/) {
		Gson gson = new Gson ();
		CEMDataTypes.NUMERIC.getId();
		JsonArray jsonTab = gson.fromJson(json, JsonArray.class);
		Iterator<JsonElement> tableau = jsonTab.iterator();
		
		while (tableau.hasNext()) {
			JsonObject jsonText = tableau.next().getAsJsonObject();
			
			String className = jsonText.get("className").getAsString() ;
			String dataPropertyName = jsonText.get("dataPropertyName").getAsString() ;
			System.out.println("************"+dataPropertyName+"*********************");
			String dataPropertyType = jsonText.get("dataPropertyType").getAsString() ;
			if (dataPropertyType.equals(CEMDataTypes.NUMERIC.getId())) {
				generator.addDataPropertyNumeric(className, dataPropertyName);
			}
			else if (dataPropertyType.equals(CEMDataTypes.NUMERIC_RANGE.getId())&&(!(jsonText.get("minRange").getAsString().isEmpty()) && !(jsonText.get("maxRange").getAsString().isEmpty()))){
				double min = jsonText.get("minRange").getAsDouble() ;
				double max = jsonText.get("maxRange").getAsDouble() ;
				if(jsonText.get("subDataProp").isJsonNull()){
					generator.addDataPropertyNumericRange(className, dataPropertyName, min, max);
				}
				else {
					generator.addDataPropertyNumericRange(className, dataPropertyName, jsonText.get("subDataProp").getAsString(), min, max);
				}					
				}
			else if (dataPropertyType.equals(CEMDataTypes.LITERAL_VALUES.getId())){
				JsonArray enumValues = jsonText.get("enums").getAsJsonArray();
				List<String> values = new ArrayList<String>();
				for(JsonElement val:enumValues){
					String literal = val.getAsJsonObject().get("enumValue").getAsString();
					if (!literal.isEmpty())
						values.add(literal);
				}
				if( values.size()!=0){
					if(jsonText.get("subDataProp").isJsonNull()){
						generator.addDataPropertyEnumeration(className, dataPropertyName,values);
					}
					else {
						generator.addDataPropertyEnumeration(className, dataPropertyName, jsonText.get("subDataProp").getAsString(), values);
					}
				}
			}
			else if (dataPropertyType.equals(CEMDataTypes.LITERAL.getId()))
				generator.addDataPropertyString(className, dataPropertyName);	
			
		}
		
		
		generator.saveOntology();
		return Response.ok().build();
	}
	
	@GET
    @Path("/getSuperCategory/{category}")
    //@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
    public String getSuperCategory( @PathParam("category") String category){
        String superClass = generator.getSuperClass(category);
        System.out.println(category	 + " superClass "+ superClass);
        return superClass;
    }
	
	@GET
    @Path("/getDataProperties/{category}")
	@Produces(MediaType.APPLICATION_JSON)
    public Response getSuperDataProperties( @PathParam("category") String category){
		List<Property> properties = new ArrayList<Property>();
		String superCategory = getSuperCategory(category);
		if(!superCategory.equals("ProductCategory")){
	        List<OWLDataProperty> dataProperties = generator.getDataPropertiesOfAClass(getSuperCategory(category));
	        for(OWLDataProperty oDataProperty: dataProperties){
	        	Property property = new Property();
	        	property.setName(oDataProperty.getIRI().getShortForm());
	        	OWLDataRange range = generator.getRangeAxiomOfDataProperty(oDataProperty);
	        	if(range!=null){
	        		property.setType(generator.getRangeType(range).getId());
	        		property.setValues(generator.getRangeFromRamgeAxiom(range));
	        		properties.add(property);
	        	}
	        }
        }
        GenericEntity<List<Property>> entity = new GenericEntity<List<Property>>(properties) {};
        return Response.ok(entity).build();
    }
	
	
	@GET
    @Path("/getValues/{dataProperty}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRangesValuesFromDataProperty(@PathParam("dataProperty") String dataProperty){
		OWLDataRange range = generator.getRangeAxiomOfDataProperty(dataProperty);
		if(range!=null){
			List<String> values = generator.getRangeFromRamgeAxiom(range);
			System.out.println("values : "+values);
			GenericEntity<List<String>> entity = new GenericEntity<List<String>>(values) {};
			return Response.ok(entity).build();
			}
		else return Response.status(0).build();
		
	}
	

}
