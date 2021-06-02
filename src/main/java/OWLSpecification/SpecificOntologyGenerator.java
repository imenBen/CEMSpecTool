package OWLSpecification;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.swing.JSpinner.ListEditor;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.DataRangeType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataRangeVisitor;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.SetOntologyID;

public class SpecificOntologyGenerator {

	private OWLOntology ontology;
	private String prefix;
	private String file;
	private String targetFile;

	public SpecificOntologyGenerator(String prefix, String file, String targetFile) {
		this.prefix = prefix;
		this.file = file;
		this.targetFile = targetFile;
		try {
			loadSpecificOntology() ;
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* classes generation */

	/* à modifier créer une nouvelle copie specifique selon le scenario */
	public void loadGenericOntology() throws OWLOntologyCreationException {

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		// Load a copy of the people+pets ontology. We'll load the ontology from
		// the web (it's acutally located in the TONES ontology repository).
		IRI docIRI = IRI.create(file);
		// We load the ontology from a document - our IRI points to it directly
		ontology = manager.loadOntologyFromOntologyDocument(docIRI);

		try {
			manager.saveOntology(ontology, IRI.create(targetFile));
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/* à modifier créer une nouvelle copie specifique selon le scenario */
	public void loadSpecificOntology() throws OWLOntologyCreationException {

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		// Load a copy of the people+pets ontology. We'll load the ontology from
		// the web (it's acutally located in the TONES ontology repository).
		IRI docIRI = IRI.create(targetFile);
		// We load the ontology from a document - our IRI points to it directly
		ontology = manager.loadOntologyFromOntologyDocument(docIRI);

		/*try {
			manager.saveOntology(ontology, IRI.create(targetFile));
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

	}
	
	public void saveOntology(){
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		try {
			manager.saveOntology(ontology, new RDFXMLOntologyFormat(), IRI.create(targetFile));
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	//get the super Class of a class
	public String getSuperClass(String className){
		String superClass="";
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		// We can get a reference to a data factory from an OWLOntologyManager.
		OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();
		// Now we create the class
		OWLClass owlClass = factory.getOWLClass(prefix + className);
		Iterator<OWLSubClassOfAxiom> supers = ontology.subClassAxiomsForSubClass(owlClass).iterator();
		while(supers.hasNext()){
			superClass=supers.next().getSuperClass().asOWLClass().getIRI().getShortForm();
		}
		return superClass;
	}

	//get the sub Classes of a class
		public List<String> getSubClasses(String className){
			List<String> subclasses = new ArrayList<String>();
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			// We can get a reference to a data factory from an OWLOntologyManager.
			OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();
			OWLClass owlClass = factory.getOWLClass(prefix + className);
			Iterator<OWLSubClassOfAxiom> subs = ontology.subClassAxiomsForSuperClass(owlClass).iterator();
			
			while(subs.hasNext()){
				String subclass = subs.next().getSubClass().asOWLClass().getIRI().getShortForm();
				subclasses.add(subclass);
				System.out.println(subclass+" sous de "+ className);
				subclasses.addAll(getSubClasses(subclass));
				
			}
			return subclasses;
					
		}
		
		
		public List<String> getSubDataProperties(String propertyName){
			List<String> subProperties = new ArrayList<String>();
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			// We can get a reference to a data factory from an OWLOntologyManager.
			OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();
			OWLDataProperty owlDataProperty = factory.getOWLDataProperty(prefix + propertyName);
			Iterator<OWLSubDataPropertyOfAxiom> subs = ontology.dataSubPropertyAxiomsForSuperProperty(owlDataProperty).iterator();
			
			while(subs.hasNext()){
				String subProperty = subs.next().getSubProperty().asOWLDataProperty().getIRI().getShortForm();
				subProperties.add(subProperty);
				System.out.println(subProperty+" sous prop de "+ propertyName);
				subProperties.addAll(getSubDataProperties(subProperty));
				
			}
			return subProperties;
					
		}
		
		
		
	public void createSubClass(String className, String subClassName) {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		// We can get a reference to a data factory from an OWLOntologyManager.
		OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();
		// Now we create the class
		OWLClass owlClass = factory.getOWLClass(prefix + className);
		OWLClass owlSubClass = factory.getOWLClass(prefix + subClassName);
		OWLDeclarationAxiom declarationAxiom = factory.getOWLDeclarationAxiom(owlSubClass);
		manager.addAxiom(ontology, declarationAxiom);

		OWLSubClassOfAxiom ax = factory.getOWLSubClassOfAxiom(owlSubClass, owlClass);
		// Add our axiom to the ontology
		manager.applyChange(new AddAxiom(ontology, ax));
		try {
			manager.saveOntology(ontology, new RDFXMLOntologyFormat(), IRI.create(targetFile));
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void addProductCategory(String productCategory) {
		createSubClass("ProductCategory", productCategory);
	}

	public void addProductModel(String product) {
		createSubClass("Product", product);
	}

	public void addProductItem(String productItem) {
		createSubClass("ProductItem", productItem);
	}

	/* remove a data property axiom from a class
	 */
	public boolean removeDataProperty(String className, String dataPropertyName) {
		List<OWLDataProperty> properties = getDataPropertiesOfAClass(className);
		/* supprimer la data property si elle existe déja*/
		for(OWLDataProperty prop : properties) {
			if (prop.getIRI().getShortForm().equals(dataPropertyName)){
				ontology.removeAxioms(ontology.axioms(prop));
				return true;
			}
		}return false;
	}
	/* data properties generation */

	
	
	public void addDataPropertyNumeric(String className, String dataPropertyName, String SuperDataPropertyName) {
		boolean delete = removeDataProperty(className, dataPropertyName)	;	
		if (delete)
			System.out.println("********************************deleted");
		/*remove data property of it exists*/
		OWLOntologyManager manager = ontology.getOWLOntologyManager();
		// We can get a reference to a data factory from an OWLOntologyManager.
		OWLDataFactory factory = manager.getOWLDataFactory();

		OWLDataProperty newDataProperty = factory.getOWLDataProperty(IRI.create(prefix + dataPropertyName));
		OWLDataPropertyDomainAxiom domainAxiom = factory.getOWLDataPropertyDomainAxiom(newDataProperty,
				factory.getOWLClass(prefix + className));
		OWLDataPropertyRangeAxiom rangeAxiom = factory.getOWLDataPropertyRangeAxiom(newDataProperty,
				factory.getDoubleOWLDatatype());
		OWLSubDataPropertyOfAxiom subDataPropertyAxion = factory.getOWLSubDataPropertyOfAxiom(newDataProperty,
				factory.getOWLDataProperty(IRI.create(prefix + SuperDataPropertyName)));
		// Add the range axiom to our ontology
		manager.addAxiom(ontology, domainAxiom);
		manager.addAxiom(ontology, rangeAxiom);
		manager.addAxiom(ontology, subDataPropertyAxion);
		
		
	}
	
	public void addDataPropertyNumeric(String className, String dataPropertyName) {
		addDataPropertyNumeric(className, dataPropertyName, "ProductCategoryProperty");		
	}

	public void addDataPropertyString(String className, String dataPropertyName, String SuperDataPropertyName) {
		boolean delete = removeDataProperty(className, dataPropertyName)	;	
		if (delete)
			System.out.println("********************************deleted");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		// We can get a reference to a data factory from an OWLOntologyManager.
		OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();

		OWLDataProperty newDataProperty = factory.getOWLDataProperty(IRI.create(prefix + dataPropertyName));
		OWLDataPropertyDomainAxiom domainAxiom = factory.getOWLDataPropertyDomainAxiom(newDataProperty,
				factory.getOWLClass(prefix + className));
		OWLDataPropertyRangeAxiom rangeAxiom = factory.getOWLDataPropertyRangeAxiom(newDataProperty,
				factory.getStringOWLDatatype());
		OWLSubDataPropertyOfAxiom subDataPropertyAxion = factory.getOWLSubDataPropertyOfAxiom(newDataProperty,
				factory.getOWLDataProperty(IRI.create(prefix + SuperDataPropertyName)));
		// Add the range axiom to our ontology
		manager.addAxiom(ontology, domainAxiom);
		manager.addAxiom(ontology, rangeAxiom);
		manager.addAxiom(ontology, subDataPropertyAxion);
	}

	public void addDataPropertyString(String className, String dataPropertyName) {
		addDataPropertyString(className, dataPropertyName, "ProductCategoryProperty" );
		}

	public void addDataPropertyNumericRange(String className, String dataPropertyName, String SuperDataPropertyName, double minValue,
			double maxValue) {
		boolean delete = removeDataProperty(className, dataPropertyName)	;	
		if (delete)
			System.out.println("********************************deleted");
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		// We can get a reference to a data factory from an OWLOntologyManager.
		OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();

		OWLDataProperty newDataProperty = factory.getOWLDataProperty(IRI.create(prefix + dataPropertyName));
		OWLDataPropertyDomainAxiom domainAxiom = factory.getOWLDataPropertyDomainAxiom(newDataProperty,
				factory.getOWLClass(prefix + className));

		OWLDatatypeRestriction minValueRestriction = factory.getOWLDatatypeMinInclusiveRestriction(minValue);
		OWLDatatypeRestriction maxValueRestriction = factory.getOWLDatatypeMaxInclusiveRestriction(maxValue);

		OWLDataPropertyRangeAxiom rangeAxiom = factory.getOWLDataPropertyRangeAxiom(newDataProperty,
				factory.getOWLDataUnionOf(minValueRestriction, maxValueRestriction));
		OWLSubDataPropertyOfAxiom subDataPropertyAxion = factory.getOWLSubDataPropertyOfAxiom(newDataProperty,
				factory.getOWLDataProperty(IRI.create(prefix + SuperDataPropertyName)));
		// Add the range axiom to our ontology
		manager.addAxiom(ontology, domainAxiom);
		manager.addAxiom(ontology, rangeAxiom);
		manager.addAxiom(ontology, subDataPropertyAxion);

	}
	
	public void addDataPropertyNumericRange(String className, String dataPropertyName, double minValue,
			double maxValue) {
		addDataPropertyNumericRange(className, dataPropertyName, "ProductCategoryProperty", minValue, maxValue);
		

	}
	/*
	 * add sub data property of a property in the hierarchy of data properties
	 */
	public void addDataPropertyEnumeration(String className, String dataPropertyName,String SuperDataPropertyName, List<String> literalValues) {
		boolean delete = removeDataProperty(className, dataPropertyName)	;
		
		if (delete)
			System.out.println("********************************deleted");
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		// We can get a reference to a data factory from an OWLOntologyManager.
		OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();
		List<OWLLiteral> literals = new ArrayList<OWLLiteral>();
		if (literalValues!=null){
			for (String value : literalValues) {
				System.out.println(value);
				literals.add(factory.getOWLLiteral(value));
			}
			OWLDataProperty newDataProperty = factory.getOWLDataProperty(IRI.create(prefix + dataPropertyName));
			OWLDataPropertyDomainAxiom domainAxiom = factory.getOWLDataPropertyDomainAxiom(newDataProperty,
					factory.getOWLClass(prefix + className));
			OWLDataPropertyRangeAxiom rangeAxiom = factory.getOWLDataPropertyRangeAxiom(newDataProperty,
					factory.getOWLDataOneOf(literals));
			OWLSubDataPropertyOfAxiom subDataPropertyAxiom = factory.getOWLSubDataPropertyOfAxiom(newDataProperty,
					factory.getOWLDataProperty(IRI.create(prefix + SuperDataPropertyName)));
			// Add the range axiom to our ontology
			manager.addAxiom(ontology, domainAxiom);
			manager.addAxiom(ontology, rangeAxiom);
			manager.addAxiom(ontology, subDataPropertyAxiom);
			}
		}
	/*
	 * add sub data property of product category Property
	 */
	public void addDataPropertyEnumeration(String className, String dataPropertyName , List<String> literalValues) {
		addDataPropertyEnumeration(className, dataPropertyName,"ProductCategoryProperty" , literalValues);		
	}

	@SuppressWarnings("deprecation")
	public List<OWLDataProperty> getDataPropertiesOfAClass(OWLClass owlClass) {
		List<OWLDataProperty> datatypes = new ArrayList<OWLDataProperty>();
		Set<OWLDataPropertyDomainAxiom> allDataPropertyDomainAxioms = ontology
				.getAxioms(AxiomType.DATA_PROPERTY_DOMAIN);
		for (OWLDataPropertyDomainAxiom axiom : allDataPropertyDomainAxioms) {
			if (axiom.getDomain().isOWLClass()) {
				if (axiom.getDomain().asOWLClass().equals(owlClass)) {
					datatypes.add(axiom.getProperty().asOWLDataProperty());
				}
			}
		}
		return datatypes;
	}
	

	public List<OWLDataProperty> getDataPropertiesOfAClass(String className) {
		OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();
		return getDataPropertiesOfAClass(factory.getOWLClass(prefix + className));
	}

	public OWLDataRange getRangeAxiomOfDataProperty(OWLDataProperty owlDataProperty) {
		Iterator<OWLDataPropertyRangeAxiom> allDataPropertyRangeAxioms = ontology.dataPropertyRangeAxioms(owlDataProperty).iterator();
		if (allDataPropertyRangeAxioms.hasNext()) {
				return allDataPropertyRangeAxioms.next().getRange();
		}
		return null;
	}
	public OWLDataRange getRangeAxiomOfDataProperty(String dataPropertyName) {
		
		// We can get a reference to a data factory from an OWLOntologyManager.
		OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLDataProperty owlDataProperty = factory.getOWLDataProperty(prefix + dataPropertyName);
		
		return getRangeAxiomOfDataProperty(owlDataProperty);
		}

	public List<String> getRangeFromRamgeAxiom(OWLDataRange oDataRange) {
		final List<String> literals = new ArrayList<String>();
		if (oDataRange.getDataRangeType().equals(DataRangeType.DATA_ONE_OF)) {
			oDataRange.accept(new OWLDataRangeVisitor() {

				public void visit(OWLDataOneOf node) {
					Iterator<OWLLiteral> values = node.values().iterator();
					while (values.hasNext()) {
						literals.add(values.next().getLiteral());

					}
				}
			});
			return literals;
		}
		/*
		 * the first value returned is the min and the second value is the max
		 */
		else if (oDataRange.getDataRangeType().equals(DataRangeType.DATA_UNION_OF)) {
			oDataRange.accept(new OWLDataRangeVisitor() {
				public void visit(OWLDataUnionOf node) {
					Iterator<OWLDataRange> operands = node.getOperands().iterator();
					while (operands.hasNext()) {
						OWLDataRange operand = operands.next();
						if (operand.getDataRangeType().equals(DataRangeType.DATATYPE_RESTRICTION)) {
							operand.accept(new OWLDataRangeVisitor() {
								public void visit(OWLDatatypeRestriction node) {
									Iterator<OWLFacetRestriction> facetRestrictions = node.facetRestrictions()
											.iterator();
									while (facetRestrictions.hasNext()) {
										OWLFacetRestriction owlFacetRestriction = facetRestrictions.next();
										if (owlFacetRestriction.getFacet().name().equals("MIN_INCLUSIVE")
												|| owlFacetRestriction.getFacet().name().equals("MIN_EXCLUSIVE")) {
											literals.add(owlFacetRestriction.getFacetValue().getLiteral());
										} else if (owlFacetRestriction.getFacet().name().equals("MAX_INCLUSIVE")
												|| owlFacetRestriction.getFacet().name().equals("MAX_EXCLUSIVE")) {
											literals.add(owlFacetRestriction.getFacetValue().getLiteral());
										}
									}
								}
							});
						}
					}

				}
			});
		}

		else if (oDataRange.getDataRangeType().equals(DataRangeType.DATATYPE)) {
			if (oDataRange.asOWLDatatype().isDouble()) {
				literals.add("numeric Type");
			} else if (oDataRange.asOWLDatatype().isString()) {
				literals.add("Literal Type");
			}
		}
		return literals;
	}
	
	
	public CEMDataTypes getRangeType(OWLDataRange oDataRange){
		if(oDataRange.getDataRangeType().equals(DataRangeType.DATA_UNION_OF))
			return CEMDataTypes.NUMERIC_RANGE;
		else if (oDataRange.getDataRangeType().equals(DataRangeType.DATA_ONE_OF))
				return CEMDataTypes.LITERAL_VALUES;
		else if (oDataRange.getDataRangeType().equals(DataRangeType.DATATYPE)){
			if (oDataRange.asOWLDatatype().isDouble()) {
				return CEMDataTypes.NUMERIC;
			} else if (oDataRange.asOWLDatatype().isString()) {
				return CEMDataTypes.LITERAL;
			}
		}return CEMDataTypes.EMPTY_TYPE;
	}
	/*find direct super property of a dataproperty*/
	/*aclass : c class , datapropertyname = a data property of the class*/
	public String FindDirectSuperDataProperty(String aclass, String dataPropertyName){
		String superClass = getSuperClass(aclass);
		List<OWLDataProperty> dataproperties = getDataPropertiesOfAClass(superClass) ;
		for (OWLDataProperty dp : dataproperties) {
			String dpString = dp.asOWLDataProperty().getIRI().getShortForm();
			if (dpString.split("_")[0].equals(dataPropertyName.split("_")[0]) && !dpString.equals(dataPropertyName) )
					return dpString;
		}
			return null;
	}

	
	public List <String> FindDirectSuperDataProperty( String dataPropertyName){
		List <String> classProp = new ArrayList<String>();
		OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();
		OWLDataProperty owlDataProperty = factory.getOWLDataProperty(prefix + dataPropertyName);
		Iterator<OWLDataPropertyDomainAxiom> domainAxioms = ontology.dataPropertyDomainAxioms(owlDataProperty).iterator();
		
	    classProp.add(domainAxioms.next().getDomain().asOWLClass().getIRI().getShortForm());
		
		Iterator<OWLSubDataPropertyOfAxiom> dataproperties = ontology.dataSubPropertyAxiomsForSubProperty(owlDataProperty).iterator();
		classProp.add(dataproperties.next().getSuperProperty().asOWLDataProperty().getIRI().getShortForm());
		
			return classProp;
	}
}
