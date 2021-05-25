package ca.latece.CEMConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import OWLSpecification.*;
import ca.latece.CEMConfig.Entities.Property;

@Path("/config")
public class OntologySpecificationServices {
	// String file =
	// "file:"+System.getProperty("user.dir")+"/Cem/GenericCEMOntology.owl";
	String file = "file:/Users/imenbenzarti/git/CEMSpecTool/Cem/GenericCEMOntology.owl";
	String prefix = "http://www.semanticweb.org/imenbenzarti/ontologies/2017/5/CEMOntology#";
	// String targetfile =
	// "file:"+System.getProperty("user.dir")+"/Cem/SpecificCEMOntology.owl";
	String targetfile = "file:/Users/imenbenzarti/git/CEMSpecTool/Cem/SpecificCEMOntology.owl";

	SpecificOntologyGenerator generator = new SpecificOntologyGenerator(prefix, file, targetfile);

	@POST
	@Path("/addProduct/{product}")
	@Consumes(MediaType.TEXT_PLAIN)
	public Response addProduct(@PathParam("product") String productName) {
		generator.addProductCategory(productName);
		return Response.ok().build();
	}

	@POST
	@Path("/addProductCategories")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addSubCategory(String categories) {
		Gson gson = new Gson();
		JsonObject json = gson.fromJson(categories, JsonObject.class);
		JsonArray cat = json.get("categories").getAsJsonArray();
		addCtaegories("ProductCategory", cat);
		System.out.println("rest f000onctionne5 : " + cat + "eeeeeee" + categories);
		System.out.println("reponse : " + Response.ok().build());
		return Response.ok().build();
	}

	public void addCtaegories(String superCategory, JsonArray array) {
		Iterator<JsonElement> iterator = array.iterator();
		while (iterator.hasNext()) {
			JsonElement element = iterator.next();
			generator.createSubClass(superCategory, element.getAsJsonObject().get("name").getAsString());
			System.out.println("#######" + element.getAsJsonObject().get("name").getAsString());
			if (element.getAsJsonObject().get("children") != null)
				addCtaegories(element.getAsJsonObject().get("name").getAsString(),
						element.getAsJsonObject().get("children").getAsJsonArray());

		}
	}

	@POST
	@Path("/addDataProperty")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addDataPropertyNumeric(String json /* String className, String dataPropertyName */) {
		Gson gson = new Gson();
		CEMDataTypes.NUMERIC.getId();
		JsonArray jsonTab = gson.fromJson(json, JsonArray.class);
		Iterator<JsonElement> tableau = jsonTab.iterator();

		while (tableau.hasNext()) {
			JsonObject jsonText = tableau.next().getAsJsonObject();

			String className = jsonText.get("className").getAsString();
			int isnew = jsonText.get("isnew").getAsInt();
			String dataPropertyName = jsonText.get("dataPropertyName").getAsString();
			//System.out.println("************" + jsonText + "*********************");
			String dataPropertyType = jsonText.get("dataPropertyType").getAsString();
			// sub classes of the class
			List<String> subclasses = generator.getSubClasses(className);
			//System.out.println("subclasses of" + className + " are : " + subclasses);
			// identification of added sub dataproperties
			int i = 1;
			if (dataPropertyType.equals(CEMDataTypes.NUMERIC.getId())) {
				if (isnew == 1) {
					// add the property to the class
					generator.addDataPropertyNumeric(className, dataPropertyName);
					// add the property to each sub class
					// identify the properties
					for (String subclass : subclasses) {
						/// if (!subclass.equals(className)) {
						// the super class of the current sub class
						String superDp = generator.FindDirectSuperDataProperty(subclass, dataPropertyName);
						// System.out.println("superdp of "+ dataPropertyName+" is "+ superDp);
						if (superDp == null || superDp == "ProductCategory")
							generator.addDataPropertyNumeric(subclass, dataPropertyName + "_" + i);
						else
							generator.addDataPropertyNumeric(subclass, dataPropertyName + "_" + i, superDp);
						i++;
						// }
					}
				} else {
					String superDp = generator.FindDirectSuperDataProperty(className, dataPropertyName);
					generator.addDataPropertyNumeric(className, dataPropertyName, superDp);
				}
			} else if (dataPropertyType.equals(CEMDataTypes.NUMERIC_RANGE.getId())
					&& (!(jsonText.get("minRange").getAsString().isEmpty())
							&& !(jsonText.get("maxRange").getAsString().isEmpty()))) {
				double min = jsonText.get("minRange").getAsDouble();
				double max = jsonText.get("maxRange").getAsDouble();
				if (isnew == 1) {
					generator.addDataPropertyNumericRange(className, dataPropertyName, min, max);

					// add the property to each sub class

					for (String subclass : subclasses) {
						// the super class of the current sub class
						String superDp = generator.FindDirectSuperDataProperty(subclass, dataPropertyName);
						if (superDp == null)
							generator.addDataPropertyNumericRange(subclass, dataPropertyName + "_" + i, min, max);

						else
							generator.addDataPropertyNumericRange(subclass, dataPropertyName + "_" + i, superDp, min,
									max);
						i++;
					}
				} else {
					String superDp = generator.FindDirectSuperDataProperty(className, dataPropertyName);
					generator.addDataPropertyNumericRange(className, dataPropertyName, superDp, min, max);
				}
				
			} else if (dataPropertyType.equals(CEMDataTypes.LITERAL_VALUES.getId())) {
				JsonArray enumValues = jsonText.get("enums").getAsJsonArray();
				List<String> values = new ArrayList<String>();
				for (JsonElement val : enumValues) {
					String literal = val.getAsJsonObject().get("enumValue").getAsString();
					if (!literal.isEmpty())
						values.add(literal);
				}
				if (values.size() != 0) {
					if (isnew == 1) {
						generator.addDataPropertyEnumeration(className, dataPropertyName, values);

						for (String subclass : subclasses) {
							// the super class of the current sub class
							String superDp = generator.FindDirectSuperDataProperty(subclass, dataPropertyName);
							if (superDp == null)
								generator.addDataPropertyEnumeration(subclass, dataPropertyName + "_" + i, values);

							else
								generator.addDataPropertyEnumeration(subclass, dataPropertyName + "_" + i, superDp,
										values);
							i++;
						}
	}
				} else {
					String superDp = generator.FindDirectSuperDataProperty(className, dataPropertyName);
					generator.addDataPropertyEnumeration(className, dataPropertyName, superDp, values);
				}
			} else if (dataPropertyType.equals(CEMDataTypes.LITERAL.getId())) {
				if (isnew == 1) {

					generator.addDataPropertyString(className, dataPropertyName);

					for (String subclass : subclasses) {
						// the super class of the current sub class
						String superDp = generator.FindDirectSuperDataProperty(subclass, dataPropertyName);
						if (superDp == null)
							generator.addDataPropertyString(subclass, dataPropertyName + "_" + i);

						else
							generator.addDataPropertyString(subclass, dataPropertyName + "_" + i, superDp);
						i++;
					}
				} else {
					String superDp = generator.FindDirectSuperDataProperty(className, dataPropertyName);
					generator.addDataPropertyString(className, dataPropertyName, superDp);
				}
			}
		}

		generator.saveOntology();
		return Response.ok().build();
	}

	@GET
	@Path("/getSuperCategory/{category}")
	// @Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String getSuperCategory(@PathParam("category") String category) {
		String superClass = generator.getSuperClass(category);
		System.out.println(category + " superClass " + superClass);
		return superClass;
	}

	@GET
	@Path("/getDataProperties/{category}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSuperDataProperties(@PathParam("category") String category) {
		List<Property> properties = new ArrayList<Property>();
		String superCategory = getSuperCategory(category);
		// if(!superCategory.equals("ProductCategory")){ deleted because the properties
		// of the root do not appear in the GUI 24/05/21
		List<OWLDataProperty> dataProperties = generator
				.getDataPropertiesOfAClass(/* getSuperCategory( */category/* ) */);
		System.out.println("dataProperties :" + dataProperties);
		for (OWLDataProperty oDataProperty : dataProperties) {
			Property property = new Property();
			property.setName(oDataProperty.getIRI().getShortForm());
			OWLDataRange range = generator.getRangeAxiomOfDataProperty(oDataProperty);
			if (range != null) {
				property.setType(generator.getRangeType(range).getId());
				property.setValues(generator.getRangeFromRamgeAxiom(range));
				properties.add(property);
			}
		}
		// }
		System.out.println("properties :" + properties);
		GenericEntity<List<Property>> entity = new GenericEntity<List<Property>>(properties) {
		};
		return Response.ok(entity).build();
	}

	@GET
	@Path("/getValues/{dataProperty}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRangesValuesFromDataProperty(@PathParam("dataProperty") String dataProperty) {
		OWLDataRange range = generator.getRangeAxiomOfDataProperty(dataProperty);
		if (range != null) {
			List<String> values = generator.getRangeFromRamgeAxiom(range);
			System.out.println("values : " + values);
			GenericEntity<List<String>> entity = new GenericEntity<List<String>>(values) {
			};
			return Response.ok(entity).build();
		} else
			return Response.status(0).build();

	}

}
