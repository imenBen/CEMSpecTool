package OWLSpecification;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class OntologyInputRead {
	
	public static Scanner lire = new Scanner(System.in);
	public static void main (String args[]) throws OWLOntologyCreationException{
		
		
		String file = "file:"+System.getProperty("user.dir")+"/Cem/GenericCEMOntology.owl";
		String prefix = "http://www.semanticweb.org/imenbenzarti/ontologies/2017/5/CEMOntology#";
		String targetfile = "file:"+System.getProperty("user.dir")+"/Cem/SpecificCEMOntology.owl";
		
		SpecificOntologyGenerator ontologyGenerator = new SpecificOntologyGenerator(prefix, file, targetfile);
		ontologyGenerator.loadGenericOntology();
		
		
		System.out.print("Enter the name of the product to configure: ");
		String product= lire.next();
		ontologyGenerator.addProductCategory(product);
		addDataProperties(ontologyGenerator, product, CEMGenericOntologyElements.PRODUCT_CATEGORY_PROPERTY.getName());
		System.out.println("-------------------Enter the different categories of products: ------------------");
		String saisie = "";
		int i = 0;
		System.out.print("Category "+i + ":" );
		saisie = lire.next();
		while (!saisie.equals("q")){		
			addSubCategory(ontologyGenerator,product, saisie);		
			i++;
			System.out.print("Category "+i + ":" );			
			saisie = lire.next();
				
		}	
		System.out.println("-------------------Specify product models : ------------------");
		saisie="";
		i=0;
		System.out.print("Product model "+i + ":" );
		saisie = lire.next();
		while (!saisie.equals("q")){
			ontologyGenerator.addProductModel(saisie);	
			addDataProperties(ontologyGenerator, saisie, CEMGenericOntologyElements.PRODUCT_PROPERTY.getName());
			i++;
			System.out.print("Product model "+i + ":" );			
			saisie = lire.next();
		}
		System.out.println("-------------------Specify product items : ------------------");
		saisie="";
		i=0;
		System.out.print("Product item "+i + ":" );
		saisie = lire.next();
		while (!saisie.equals("q")){
			ontologyGenerator.addProductItem(saisie);	
			addDataProperties(ontologyGenerator, saisie, CEMGenericOntologyElements.PRODUCT_ITEM_PROPERTY.getName());
			i++;
			System.out.print("Product item "+i + ":" );			
			saisie = lire.next();
		}
		ontologyGenerator.saveOntology();
		//lire.close();
		
		/*ajouter toutes les categories dans une liste pour ensuite configurer leurs data propierties avec leurs super categories
		 * faire des restrictions selon les axioms
		 */
	}
	public static void addSubCategory(SpecificOntologyGenerator ontologyGenerator, String category, String subCategory){
		ontologyGenerator.createSubClass(category, subCategory);		
		System.out.println("********** Enter properties of the category "+subCategory+" ************");
		addSubDataPropertiesToSubCategories(ontologyGenerator, category, subCategory);
		System.out.println("______The properties of the category to add : ");
		addDataProperties(ontologyGenerator, subCategory, CEMGenericOntologyElements.PRODUCT_CATEGORY_PROPERTY.getName());
		System.out.println("Would you like to add a sub category of  "+ subCategory + " (y/n)?" );
		String choix = lire.next();
		if(choix.equals("y")){
			System.out.print("A sub category of "+subCategory+ ":" );
			
			String subSubCategory = lire.next();
			addSubCategory(ontologyGenerator, subCategory, subSubCategory);
			addSubCategory(ontologyGenerator,category, subCategory);			
		}		
		ontologyGenerator.saveOntology();
	}
	public static void addDataProperties(SpecificOntologyGenerator ontologyGenerator, String category, String superDateProperty){
		System.out.println("********** Saisir les propriétés de "+category+" ************");
		
		String property = "";
		int j = 0;
		System.out.print("Property "+j + ":" );
		property = lire.next();
		while (!property.equals("q")){		
			addDataTypesToDataProperty(ontologyGenerator, category, property, superDateProperty);
			System.out.print("Property "+(++j) + ":" );			
			property = lire.next();				
		}
		ontologyGenerator.saveOntology();
	}
	public static void addDataTypesToDataProperty (SpecificOntologyGenerator ontologyGenerator, String category, String property, String superDateProperty){
		System.out.println("Enter the type of property:? 1.Numeric 2.Numeric range 3.Enumeration values 4.Literal");
		
		int choice = lire.nextInt();
		switch(choice){
		case 1: ontologyGenerator.addDataPropertyNumeric(category,property,superDateProperty);break;
		case 2: System.out.println("Enter min value");
				double min=lire.nextDouble();
				System.out.println("Enter max value:");
				double max=lire.nextDouble();
				ontologyGenerator.addDataPropertyNumericRange(category,property,superDateProperty, min, max);break;
		case 3: int i=0; String literal =""; List<String> literals= new ArrayList<String>();
				System.out.println("Enter the différent values");
				System.out.print("Value "+(i++) + ":" );
				literal = lire.next();
				while(!literal.equals("q")){
					literals.add(literal);
					System.out.print("Value "+(i++) + ":" );
					literal = lire.next();
				}
				ontologyGenerator.addDataPropertyEnumeration(category, property,superDateProperty, literals);
				break;
		case 4: default: ontologyGenerator.addDataPropertyString(category,property,superDateProperty);break;
		}
		ontologyGenerator.saveOntology();
	}
	public static void addSubDataPropertiesToSubCategories(SpecificOntologyGenerator ontologyGenerator, String category, String subCategory){
		System.out.println("______Super category ( "+category+" )  properties to restrict: ");
		List<OWLDataProperty> listOfDataProperties = ontologyGenerator.getDataPropertiesOfAClass(category);
		int i =0;
		String choix= "";
		for(OWLDataProperty owlDataProperty: listOfDataProperties){
			OWLDataRange owlDataRange = ontologyGenerator.getRangeAxiomOfDataProperty(owlDataProperty);
			System.out.println("From Super Category( "+category+" ) property "+ (++i) + ": " + owlDataProperty.getIRI().getShortForm());
			List<String> listValues = ontologyGenerator.getRangeFromRamgeAxiom(owlDataRange);
			for(String value: listValues){
				System.out.println("\t "+listValues.indexOf(value)+ " - "+value);
			}
			System.out.println("Do you want to restrict this property of "+ category+ "for "+subCategory+" : (y/n)");
			choix = lire.next();
			if(choix.equals("y")){
				if(ontologyGenerator.getRangeType(owlDataRange) == CEMDataTypes.NUMERIC){
					System.out.println("Enter min value");
					double min=lire.nextDouble();
					System.out.println("Enter max value:");
					double max=lire.nextDouble();
					ontologyGenerator.addDataPropertyNumericRange(subCategory,owlDataProperty.getIRI().getShortForm()+"RestFor_"+subCategory, owlDataProperty.getIRI().getShortForm(), min, max);
				}else if(ontologyGenerator.getRangeType(owlDataRange) == CEMDataTypes.NUMERIC_RANGE){
					System.out.println("Enter min value");
					double min=lire.nextDouble();
					System.out.println("Enter max value:");
					double max=lire.nextDouble();
					if(min>=Double.parseDouble(listValues.get(0)) && max<=Double.parseDouble(listValues.get(1))){
						ontologyGenerator.addDataPropertyNumericRange(subCategory,owlDataProperty.getIRI().getShortForm()+"RestFor_"+subCategory, owlDataProperty.getIRI().getShortForm(), min, max);
					}else{
						System.out.println(" Enter a sub range of ["+Integer.parseInt(listValues.get(0))+" , "+ Integer.parseInt(listValues.get(1)) + "]");
					}
				}else if(ontologyGenerator.getRangeType(owlDataRange) == CEMDataTypes.LITERAL_VALUES){	
					
					int k=0; String literalIndex =""; List<String> literals= new ArrayList<String>();
					System.out.println("Enter the number of the chosen values");
					System.out.print("Value "+(k++) + ":" );
					literalIndex = lire.next();
					while(!literalIndex.equals("q")){
						literals.add(listValues.get(Integer.parseInt(literalIndex)));
						System.out.print("Value "+(k++) + ":" );
						literalIndex = lire.next();
					}
					ontologyGenerator.addDataPropertyEnumeration(subCategory, owlDataProperty.getIRI().getShortForm()+"RestFor_"+subCategory, owlDataProperty.getIRI().getShortForm(), literals);					
				}else if(ontologyGenerator.getRangeType(owlDataRange) == CEMDataTypes.LITERAL){
					int l=0; String literal =""; List<String> literals= new ArrayList<String>();
					System.out.println("Enter the différent values");
					System.out.print("Value "+(l++) + ":" );
					literal = lire.next();
					while(!literal.equals("q")){
						literals.add(literal);
						System.out.print("Value "+(l++) + ":" );
						literal = lire.next();
					}
					ontologyGenerator.addDataPropertyEnumeration(subCategory, owlDataProperty.getIRI().getShortForm()+"RestFor_"+subCategory, owlDataProperty.getIRI().getShortForm(), literals);
				}
				
			}
		}
	}

}
