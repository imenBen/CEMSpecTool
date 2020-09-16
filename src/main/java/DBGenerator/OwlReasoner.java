package DBGenerator;
import java.io.File;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.coode.owlapi.turtle.TurtleOntologyFormat;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentTarget;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.io.StreamDocumentTarget;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.io.SystemOutDocumentTarget;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLVariable;
import org.semanticweb.owlapi.model.SetOntologyID;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;

import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.semanticweb.owlapi.util.OWLOntologyMerger;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import uk.ac.manchester.cs.owlapi.modularity.ModuleType;

public class OwlReasoner {

	String DOCUMENT_IRI = "file:/Users/imenbenzarti/Documents/WorkspaceOntologyDB/Cem/ProductOntology.owl";
	String prefix = "http://www.semanticweb.org/imenbenzarti/ontologies/2017/4/productOntology#";

	public void shouldUseReasoner() throws OWLOntologyCreationException {
		DBConnection dbConnection = DBConnection.getInstance();
		// Create our ontology manager in the usual way.
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		// Load a copy of the people+pets ontology. We'll load the ontology from
		// the web (it's acutally located in the TONES ontology repository).
		IRI docIRI = IRI.create(DOCUMENT_IRI);
		// We load the ontology from a document - our IRI points to it directly
		OWLOntology ont = manager.loadOntologyFromOntologyDocument(docIRI);
		System.out.println("Loaded " + ont.getOntologyID());
		// We need to create an instance of OWLReasoner. An OWLReasoner provides
		// the basic query functionality that we need, for example the ability
		// obtain the subclasses of a class etc. To do this we use a reasoner
		// factory. Create a reasoner factory. In this case, we will use HermiT,
		// but we could also use FaCT++ (http://code.google.com/p/factplusplus/)
		// or Pellet(http://clarkparsia.com/pellet) Note that (as of 03 Feb
		// 2010) FaCT++ and Pellet OWL API 3.0.0 compatible libraries are
		// expected to be available in the near future). For now, we'll use
		// HermiT HermiT can be downloaded from http://hermit-reasoner.com Make
		// sure you get the HermiT library and add it to your class path. You
		// can then instantiate the HermiT reasoner factory: Comment out the
		// first line below and uncomment the second line below to instantiate
		// the HermiT reasoner factory. You'll also need to import the
		// org.semanticweb.HermiT.Reasoner package.
		OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		// OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
		// We'll now create an instance of an OWLReasoner (the implementation
		// being provided by HermiT as we're using the HermiT reasoner factory).
		// The are two categories of reasoner, Buffering and NonBuffering. In
		// our case, we'll create the buffering reasoner, which is the default
		// kind of reasoner. We'll also attach a progress monitor to the
		// reasoner. To do this we set up a configuration that knows about a
		// progress monitor. Create a console progress monitor. This will print
		// the reasoner progress out to the console.
		ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
		// Specify the progress monitor via a configuration. We could also
		// specify other setup parameters in the configuration, and different
		// reasoners may accept their own defined parameters this way.
		OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
		// Create a reasoner that will reason over our ontology and its imports
		// closure. Pass in the configuration.
		OWLReasoner reasoner = reasonerFactory.createReasoner(ont, config);
		// Ask the reasoner to do all the necessary work now
		reasoner.precomputeInferences();
		// We can determine if the ontology is actually consistent (in this
		// case, it should be).
		boolean consistent = reasoner.isConsistent();
		System.out.println("Consistent: " + consistent);
		System.out.println("\n");
		// We can easily get a list of unsatisfiable classes. (A class is
		// unsatisfiable if it can't possibly have any instances). Note that the
		// getUnsatisfiableClasses method is really just a convenience method
		// for obtaining the classes that are equivalent to owl:Nothing. In our
		// case there should be just one unsatisfiable class - "mad_cow" We ask
		// the reasoner for the unsatisfiable classes, which returns the bottom
		// node in the class hierarchy (an unsatisfiable class is a subclass of
		// every class).
		Node<OWLClass> bottomNode = reasoner.getUnsatisfiableClasses();
		// This node contains owl:Nothing and all the classes that are
		// equivalent to owl:Nothing - i.e. the unsatisfiable classes. We just
		// want to print out the unsatisfiable classes excluding owl:Nothing,
		// and we can used a convenience method on the node to get these
		Set<OWLClass> unsatisfiable = bottomNode.getEntitiesMinusBottom();
		if (!unsatisfiable.isEmpty()) {
			System.out.println("The following classes are unsatisfiable: ");
			for (OWLClass cls : unsatisfiable) {
				System.out.println("    " + cls);
			}
		} else {
			System.out.println("There are no unsatisfiable classes");
		}
		System.out.println("\n");
		// Now we want to query the reasoner for all descendants of vegetarian.
		// Vegetarians are defined in the ontology to be animals that don't eat
		// animals or parts of animals.
		OWLDataFactory fac = manager.getOWLDataFactory();
		// Get a reference to the vegetarian class so that we can as the
		// reasoner about it. The full IRI of this class happens to be:
		// <http://owl.man.ac.uk/2005/07/sssw/people#vegetarian>
		OWLClass productCategory = fac.getOWLClass(IRI.create(prefix + "ProductCategory"));
		// Now use the reasoner to obtain the subclasses of vegetarian. We can
		// ask for the direct subclasses of vegetarian or all of the (proper)
		// subclasses of vegetarian. In this case we just want the direct ones
		// (which we specify by the "true" flag).
		NodeSet<OWLClass> subClses = reasoner.getSubClasses(productCategory, true);
		// The reasoner returns a NodeSet, which represents a set of Nodes. Each
		// node in the set represents a subclass of vegetarian pizza. A node of
		// classes contains classes, where each class in the node is equivalent.
		// For example, if we asked for the subclasses of some class A and got
		// back a NodeSet containing two nodes {B, C} and {D}, then A would have
		// two proper subclasses. One of these subclasses would be equivalent to
		// the class D, and the other would be the class that is equivalent to
		// class B and class C. In this case, we don't particularly care about
		// the equivalences, so we will flatten this set of sets and print the
		// result
		Set<OWLClass> clses = subClses.getFlattened();
		System.out.println("Subclasses of productCategory: ");
		for (OWLClass cls : clses) {
			System.out.println("    " + cls);
		}
		System.out.println("\n");
		int id = 0;

		 dbConnection.executeUpdateQuery("CREATE TABLE `CEMOnto`.`productCategory` ( `id` INT NOT NULL, `name` VARCHAR(45) NULL,PRIMARY KEY (`id`));");
		NodeSet<OWLNamedIndividual> individualsNodeSetProductCategory = reasoner.getInstances(productCategory, true);
		Stream<OWLNamedIndividual> individualsProductCategory = individualsNodeSetProductCategory.entities();
		System.out.println("Instances of productCategory: ");
		Iterator<OWLNamedIndividual> categoriesIterator = individualsProductCategory.iterator();

		OWLClass commonProperty = fac.getOWLClass(IRI.create(prefix + "CommonProperty"));
		OWLObjectProperty theProperty = fac.getOWLObjectProperty(prefix + "theProperty");
		OWLObjectProperty hasCommonProperty = fac.getOWLObjectProperty(prefix + "hasCommonProperty");
		OWLDataProperty valueP = fac.getOWLDataProperty(prefix + "valueP");
		NodeSet<OWLNamedIndividual> individualsNodeSetcommonProperty = reasoner.getInstances(commonProperty, true);
		Stream<OWLNamedIndividual> individualscommonProperty = individualsNodeSetcommonProperty.entities();
		System.out.println("Instances of commonProperty: ");

		// properties declared in the data base as colomn of table product
		// category
		List<OWLNamedIndividual> addedPropertyToDB = new ArrayList<OWLNamedIndividual>();
		while (categoriesIterator.hasNext()) {
			OWLNamedIndividual categoriesinds = categoriesIterator.next();
			id++;
			System.out.println("    " + categoriesinds.getIRI().getShortForm());dbConnection.executeUpdateQuery("INSERT INTO`CEMOnto`.`productCategory` (`id`, `name`) VALUES ('"+id+"','"+categoriesinds.getIRI().getShortForm()+"');");
			NodeSet<OWLNamedIndividual> commonProperties = reasoner.getObjectPropertyValues(categoriesinds,
					hasCommonProperty);
			Stream<OWLNamedIndividual> commonPropertiesNames = commonProperties.entities();
			Iterator<OWLNamedIndividual> commonPropertiesNamesIterator = commonPropertiesNames.iterator();
			//System.out.println("common properties ");
			while (commonPropertiesNamesIterator.hasNext()) {
				OWLNamedIndividual ind = commonPropertiesNamesIterator.next();
				System.out.println("    	" + ind.getIRI().getShortForm());

			
				
				NodeSet<OWLNamedIndividual> properties = reasoner.getObjectPropertyValues(ind, theProperty);
				Stream<OWLNamedIndividual> propertiesNames = properties.entities();
				Iterator<OWLNamedIndividual> propertiesNamesIterator = propertiesNames.iterator();
				//System.out.println("property ");
				OWLNamedIndividual theCurrentProperty=null; 
				while (propertiesNamesIterator.hasNext()) {
					theCurrentProperty = propertiesNamesIterator.next();
					//System.out.println("    		" + ind2.getIRI().getShortForm());
					if(!addedPropertyToDB.contains(theCurrentProperty)){
						dbConnection.executeUpdateQuery("ALTER TABLE `CEMOnto`.`productCategory` ADD COLUMN `"+theCurrentProperty.getIRI().getShortForm()+"` VARCHAR(45) NULL ;");
						addedPropertyToDB.add(theCurrentProperty);
					}
				}
				Set<OWLLiteral> propertiesValues = reasoner.getDataPropertyValues(ind, valueP);

				Iterator<OWLLiteral> values = propertiesValues.iterator();
				//System.out.println("value ");
				while (values.hasNext()) {
					//System.out.println("    		" + values.next());
					dbConnection.executeUpdateQuery("UPDATE `CEMOnto`.`productCategory` SET `"+theCurrentProperty.getIRI().getShortForm()+"`='"+values.next()+"' WHERE `name`='"+categoriesinds.getIRI().getShortForm()+"';");
				}
				// dbConnection.executeUpdateQuery("INSERT INTO
				// `CEMOnto`.`productCategory` (`id`, `name`) VALUES ('"+id+"',
				// '"+ind.getIRI().getShortForm()+"');");
			}

		}

		// In this case, we should find that the classes, cow, sheep and giraffe
		// are vegetarian. Note that in this ontology only the class cow had
		// been stated to be a subclass of vegetarian. The fact that sheep and
		// giraffe are subclasses of vegetarian was implicit in the ontology
		// (through other things we had said) and this illustrates why it is
		// important to use a reasoner for querying an ontology. We can easily
		// retrieve the instances of a class. In this example we'll obtain the
		// instances of the class pet. This class has a full IRI of
		// <http://owl.man.ac.uk/2005/07/sssw/people#pet> We need to obtain a
		// reference to this class so that we can ask the reasoner about it.
		OWLClass property_ = fac.getOWLClass(IRI.create(prefix + "Property"));
		// Ask the reasoner for the instances of pet
		NodeSet<OWLNamedIndividual> individualsNodeSet = reasoner.getInstances(property_, true);
		// The reasoner returns a NodeSet again. This time the NodeSet contains
		// individuals. Again, we just want the individuals, so get a flattened
		// set.
		Set<OWLNamedIndividual> individuals = individualsNodeSet.getFlattened();
		System.out.println("Instances of pet: ");
		for (OWLNamedIndividual ind : individuals) {
			System.out.println("    " + ind);
		}
		System.out.println("\n");
		// Again, it's worth noting that not all of the individuals that are
		// returned were explicitly stated to be pets. Finally, we can ask for
		// the property values (property assertions in OWL speak) for a given
		// individual and property. Let's get the property values for the
		// individual Mick, the full IRI of which is
		// <http://owl.man.ac.uk/2005/07/sssw/people#Mick> Get a reference to
		// the individual Mick
		OWLNamedIndividual bike = fac.getOWLNamedIndividual(IRI.create(prefix + "#bicycle"));
		// Let's get the pets of Mick Get hold of the has_pet property which has
		// a full IRI of <http://owl.man.ac.uk/2005/07/sssw/people#has_pet>
		OWLObjectProperty subCategory = fac.getOWLObjectProperty(IRI.create(prefix + "subCategory"));
		// Now ask the reasoner for the has_pet property values for Mick
		NodeSet<OWLNamedIndividual> petValuesNodeSet = reasoner.getObjectPropertyValues(bike, subCategory);
		Set<OWLNamedIndividual> values = petValuesNodeSet.getFlattened();
		System.out.println("The sub category property values for bike are: ");
		for (OWLNamedIndividual ind2 : values) {
			System.out.println("    " + ind2);
		}
		// Notice that Mick has a pet Rex, which wasn't asserted in the
		// ontology. Finally, let's print out the class hierarchy. Get hold of
		// the top node in the class hierarchy (containing owl:Thing) Now print
		// the hierarchy out
		Node<OWLClass> topNode = reasoner.getTopClassNode();

		print(topNode, reasoner, 0);
	}

	private static void print(Node<OWLClass> parent, OWLReasoner reasoner, int depth) {
		// We don't want to print out the bottom node (containing owl:Nothing
		// and unsatisfiable classes) because this would appear as a leaf node
		// everywhere
		if (parent.isBottomNode()) {
			return;
		}
		// Print an indent to denote parent-child relationships
		printIndent(depth);
		// Now print the node (containing the child classes)
		printNode(parent);
		for (Node<OWLClass> child : reasoner.getSubClasses(parent.getRepresentativeElement(), true)) {
			// Recurse to do the children. Note that we don't have to worry
			// about cycles as there are non in the inferred class hierarchy
			// graph - a cycle gets collapsed into a single node since each
			// class in the cycle is equivalent.
			print(child, reasoner, depth + 1);
		}
	}

	private static void printIndent(int depth) {
		for (int i = 0; i < depth; i++) {
			System.out.print("    ");
		}
	}

	private static void printNode(Node<OWLClass> node) {
		DefaultPrefixManager pm = new DefaultPrefixManager(
				"file:/Users/imenbenzarti/Documents/WorkspaceOntologyDB/Cem/ProductOntology.owl#");
		// Print out a node as a list of class names in curly brackets
		System.out.print("{");
		for (Iterator<OWLClass> it = node.getEntities().iterator(); it.hasNext();) {
			OWLClass cls = it.next();
			// User a prefix manager to provide a slightly nicer shorter name
			System.out.print(pm.getShortForm(cls));
			if (it.hasNext()) {
				System.out.print(" ");
			}
		}
		System.out.println("}");
	}

	private static void printProperties(OWLOntologyManager man, OWLOntology ont, OWLReasoner reasoner, OWLClass cls) {
		if (!ont.containsClassInSignature(cls.getIRI())) {
			throw new RuntimeException("Class not in signature of the ontology");
		}
		// Note that the following code could be optimised... if we find that
		// instances of the specified class do not have a property, then we
		// don't need to check the sub properties of this property
		System.out.println("Properties of " + cls);
		for (OWLObjectPropertyExpression prop : ont.getObjectPropertiesInSignature()) {
			boolean sat = hasProperty(man, reasoner, cls, prop);
			if (sat) {
				System.out.println("Instances of " + cls + " necessarily have the property " + prop);
			}
		}
	}

	private static boolean hasProperty(OWLOntologyManager man, OWLReasoner reasoner, OWLClass cls,
			OWLObjectPropertyExpression prop) {
		// To test whether the instances of a class must have a property we
		// create a some values from restriction and then ask for the
		// satisfiability of the class interesected with the complement of this
		// some values from restriction. If the intersection is satisfiable then
		// the instances of the class don't have to have the property,
		// otherwise, they do.
		OWLDataFactory dataFactory = man.getOWLDataFactory();
		OWLClassExpression restriction = dataFactory.getOWLObjectSomeValuesFrom(prop, dataFactory.getOWLThing());
		// Now we see if the intersection of the class and the complement of
		// this restriction is satisfiable
		OWLClassExpression complement = dataFactory.getOWLObjectComplementOf(restriction);
		OWLClassExpression intersection = dataFactory.getOWLObjectIntersectionOf(cls, complement);
		return !reasoner.isSatisfiable(intersection);
	}

}
