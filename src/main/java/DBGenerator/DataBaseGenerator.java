package DBGenerator;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataRangeVisitor;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;

public class DataBaseGenerator {

	private static DBConnection dbConnection;
	private static OWLOntologyManager manager;
	private static OWLOntology ont;
	private static OWLReasoner reasoner;
	private static OWLDataFactory fac;
	
	public static String prefix ="http://www.semanticweb.org/imenbenzarti/ontologies/2017/5/CEMOntology#";

	public static void init(String documentIRI) throws OWLOntologyCreationException {
		dbConnection = DBConnection.getInstance();
		manager = OWLManager.createOWLOntologyManager();
		IRI docIRI = IRI.create(documentIRI);
		ont = manager.loadOntologyFromOntologyDocument(docIRI);
		System.out.println("Loaded " + ont.getOntologyID());
		OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
		ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
		OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
		reasoner = reasonerFactory.createReasoner(ont, config);
		reasoner.precomputeInferences();
		boolean consistent = reasoner.isConsistent();
		System.out.println("Consistent: " + consistent);
		System.out.println("\n");
		Node<OWLClass> bottomNode = reasoner.getUnsatisfiableClasses();
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
		fac = manager.getOWLDataFactory();
	}

	public static void ProductCategoryTableCreation() throws OWLOntologyCreationException {
		dbConnection.executeUpdateQuery("DROP Table IF EXISTS`CEMOnto`.`productCategories` ");
		dbConnection.executeUpdateQuery(
				" CREATE TABLE `CEMOnto`.`productCategories` ( `id` INT NOT NULL AUTO_INCREMENT,`name` VARCHAR(45) NULL,PRIMARY KEY (`id`), UNIQUE INDEX `name` (`name` ASC));");

		OWLClass productCategory = fac.getOWLClass(IRI.create(prefix + "productCategory"));
		OWLDataProperty property = fac.getOWLDataProperty(IRI.create(prefix + "ProductCategoryProperty"));
		Iterator<OWLDataProperty> properties = reasoner.getSubDataProperties(property, true).entities().iterator();
		Iterator<OWLClass> allCategories = reasoner.getSubClasses(productCategory, false).entities().iterator();
		while (allCategories.hasNext()) {
			OWLClass cat = allCategories.next();
			if (!cat.isOWLNothing()) {
				dbConnection.executeUpdateQuery("INSERT INTO`CEMOnto`.`productCategories` (`name`) VALUES ('"
						+ cat.asOWLClass().getIRI().getShortForm() + "');");
			}

		}
		while (properties.hasNext()) {
			final OWLDataProperty aproperty = properties.next();

			Iterator<OWLDataPropertyRangeAxiom> dataPropertyRangeAxioms = ont.dataPropertyRangeAxioms(aproperty)
					.iterator();
			while (dataPropertyRangeAxioms.hasNext()) {
				OWLDataPropertyRangeAxiom range = dataPropertyRangeAxioms.next();
				if (range.getRange().isOWLDatatype()) {
					if (range.getRange().asOWLDatatype().isDouble() || range.getRange().asOWLDatatype().isInteger()
							|| range.getRange().asOWLDatatype().isFloat()) {
						dbConnection.executeUpdateQuery("ALTER TABLE `CEMOnto`.`productCategories` ADD COLUMN `"
								+ aproperty.getIRI().getShortForm() + "_min" + "` VARCHAR(45) NULL ;");
						dbConnection.executeUpdateQuery("ALTER TABLE `CEMOnto`.`productCategories` ADD COLUMN `"
								+ aproperty.getIRI().getShortForm() + "_max" + "` VARCHAR(45) NULL ;");

					}
				} else {
					dbConnection.executeUpdateQuery("ALTER TABLE `CEMOnto`.`productCategories` ADD COLUMN `"
							+ aproperty.getIRI().getShortForm() + "` VARCHAR(45) NULL ;");

				}

			}
			Iterator<OWLDataProperty> subProperties = reasoner.subDataProperties(aproperty).iterator();
			while (subProperties.hasNext()) {
				final OWLDataProperty aSubProperty = subProperties.next();

				Iterator<OWLDataPropertyDomainAxiom> subDataPropertyDomainAxioms = ont
						.dataPropertyDomainAxioms(aSubProperty).iterator();
				final List<OWLClass> domains = new ArrayList<OWLClass>();
				while (subDataPropertyDomainAxioms.hasNext()) {
					OWLDataPropertyDomainAxiom aSubDataPropertyDomainAxiom = subDataPropertyDomainAxioms.next();
					OWLClassExpression theSubDomain = aSubDataPropertyDomainAxiom.getDomain();
					Iterator<OWLClass> subDomains = reasoner.getSubClasses(theSubDomain, false).entities().iterator();

					if (theSubDomain.isAnonymousExpression()) {
						OWLClass newCategory = null;
						while (subDomains.hasNext()) {
							newCategory = subDomains.next();

							domains.add(newCategory.asOWLClass());
						}
					} else {

						domains.add(theSubDomain.asOWLClass());
						while (subDomains.hasNext()) {
							domains.add(subDomains.next());
						}
					}
				}
				Iterator<OWLDataPropertyRangeAxiom> subDataPropertyRangeAxioms = ont
						.dataPropertyRangeAxioms(aSubProperty).iterator();
				while (subDataPropertyRangeAxioms.hasNext()) {
					OWLDataPropertyRangeAxiom aSubDataPropertyRangeAxiom = subDataPropertyRangeAxioms.next();
					OWLDataRange theSubRange = aSubDataPropertyRangeAxiom.getRange();
					theSubRange.accept(new OWLDataRangeVisitor() {

						public void visit(OWLDataOneOf node) {
							node.values();
							for (OWLClass domain : domains) {
								dbConnection.executeUpdateQuery(
										"UPDATE `CEMOnto`.`productCategories` SET `" + aproperty.getIRI().getShortForm()
												+ "`='" + node.values().iterator().next().getLiteral()
												+ "' WHERE `name`='" + domain.getIRI().getShortForm() + "';");
							}
						}
					});
					theSubRange.accept(new OWLDataRangeVisitor() {

						public void visit(OWLDatatypeRestriction node) {

							Iterator<OWLFacetRestriction> facetRestrictions = node.facetRestrictions().iterator();
							while (facetRestrictions.hasNext()) {
								OWLFacetRestriction owlFacetRestriction = facetRestrictions.next();
								String min;
								String max;
								for (OWLClass domain : domains) {
									if (owlFacetRestriction.getFacet().name().equals("MIN_INCLUSIVE")
											|| owlFacetRestriction.getFacet().name().equals("MIN_EXCLUSIVE")) {
										min = owlFacetRestriction.getFacetValue().getLiteral();

										dbConnection.executeUpdateQuery("UPDATE `CEMOnto`.`productCategories` SET `"
												+ aproperty.getIRI().getShortForm() + "_min" + "`='" + min
												+ "' WHERE `name`='" + domain.getIRI().getShortForm() + "';");
									}

									if (owlFacetRestriction.getFacet().name().equals("MAX_INCLUSIVE")
											|| owlFacetRestriction.getFacet().name().equals("MAX_EXCLUSIVE")) {

										max = owlFacetRestriction.getFacetValue().getLiteral();

										dbConnection.executeUpdateQuery("UPDATE `CEMOnto`.`productCategories` SET `"
												+ aproperty.getIRI().getShortForm() + "_max" + "`='" + max
												+ "' WHERE `name`='" + domain.getIRI().getShortForm() + "';");
									}
								}

							}

						}
					});
				}

			}
		}

	}

	public static void ProductTableCreation() throws OWLOntologyCreationException {
		dbConnection.executeUpdateQuery("DROP Table IF EXISTS`CEMOnto`.`products` ");
		dbConnection.executeUpdateQuery(
				" CREATE TABLE `CEMOnto`.`products` ( `id` INT NOT NULL AUTO_INCREMENT,`name` VARCHAR(45) NULL,PRIMARY KEY (`id`), UNIQUE INDEX `name` (`name` ASC));");
		OWLDataProperty property = fac.getOWLDataProperty(IRI.create(prefix + "ProductCategoryProperty"));
		OWLDataProperty productProperty = fac.getOWLDataProperty(IRI.create(prefix + "ProductProperty"));
		Iterator<OWLDataProperty> allProperties = reasoner.getSubDataProperties(property, true).entities().iterator();
		Iterator<OWLDataProperty> allProductProperties = reasoner.getSubDataProperties(productProperty, true).entities()
				.iterator();

		while (allProductProperties.hasNext()) {
			OWLDataProperty aProperty = allProductProperties.next();
			if (!aProperty.isOWLBottomDataProperty()) {
				dbConnection.executeUpdateQuery("ALTER TABLE `CEMOnto`.`products` ADD COLUMN `"
						+ aProperty.getIRI().getShortForm() + "` VARCHAR(45) NULL ;");
			}
		}
		while (allProperties.hasNext()) {
			OWLDataProperty aProperty = allProperties.next();
			if (!aProperty.isOWLBottomDataProperty()) {
				dbConnection.executeUpdateQuery("ALTER TABLE `CEMOnto`.`products` ADD COLUMN `"
						+ aProperty.getIRI().getShortForm() + "` VARCHAR(45) NULL ;");
			}
		}

	}

	public static void ProductItemsTableCreation() throws OWLOntologyCreationException {
		dbConnection.executeUpdateQuery("DROP Table IF EXISTS`CEMOnto`.`productItems` ");
		dbConnection.executeUpdateQuery(
				" CREATE TABLE `CEMOnto`.`productItems` ( `id` INT NOT NULL AUTO_INCREMENT,`name` VARCHAR(45) NULL,PRIMARY KEY (`id`), UNIQUE INDEX `name` (`name` ASC));");
		OWLDataProperty ProductItemProperty = fac.getOWLDataProperty(IRI.create(prefix + "ProductItemProperty"));
		OWLDataProperty productProperty = fac.getOWLDataProperty(IRI.create(prefix + "ProductProperty"));
		Iterator<OWLDataProperty> AllProductItemProperties = reasoner.getSubDataProperties(ProductItemProperty, true)
				.entities().iterator();
		Iterator<OWLDataProperty> allProductProperties = reasoner.getSubDataProperties(productProperty, true).entities()
				.iterator();

		while (AllProductItemProperties.hasNext()) {
			OWLDataProperty aProperty = AllProductItemProperties.next();
			if (!aProperty.isOWLBottomDataProperty()) {
				dbConnection.executeUpdateQuery("ALTER TABLE `CEMOnto`.`productItems` ADD COLUMN `"
						+ aProperty.getIRI().getShortForm() + "` VARCHAR(45) NULL ;");
			}
		}
		while (allProductProperties.hasNext()) {
			OWLDataProperty aProperty = allProductProperties.next();
			if (!aProperty.isOWLBottomDataProperty()) {
				dbConnection.executeUpdateQuery("ALTER TABLE `CEMOnto`.`productItems` ADD COLUMN `"
						+ aProperty.getIRI().getShortForm() + "` VARCHAR(45) NULL ;");
			}
		}

	}

	public static void ProductTableInstances() throws OWLOntologyCreationException {
		OWLClass productItem = fac.getOWLClass(IRI.create(prefix + "ProductItem"));

		OWLClass productCategory = fac.getOWLClass(IRI.create(prefix + "productCategory"));
		OWLDataProperty property = fac.getOWLDataProperty(IRI.create(prefix + "ProductCategoryProperty"));

		OWLDataProperty productProperty = fac.getOWLDataProperty(IRI.create(prefix + "ProductProperty"));

		OWLObjectProperty describes = fac.getOWLObjectProperty(IRI.create(prefix + "describes"));

		// retreive BicycleModel
		Iterator<OWLClass> productSubcategories = reasoner.subClasses(productCategory, false).iterator();
		while (productSubcategories.hasNext()) {
			OWLClass aProductSubCategory = productSubcategories.next();
			Iterator<OWLNamedIndividual> allIndivCategory = reasoner.instances(aProductSubCategory, true).iterator();

			while (allIndivCategory.hasNext()) {
				OWLNamedIndividual categoryIndiv = allIndivCategory.next();
				// liste des products décrits pas l'individu categorie
				Iterator<OWLNamedIndividual> describedProducts = reasoner.objectPropertyValues(categoryIndiv, describes)
						.iterator();
				while (describedProducts.hasNext()) {
					OWLNamedIndividual productIndiv = describedProducts.next();
					dbConnection.executeUpdateQuery("INSERT INTO`CEMOnto`.`products` (`name`) VALUES ('"
							+ productIndiv.getIRI().getShortForm() + "');");
					Iterator<OWLDataProperty> categoryProperties = reasoner.subDataProperties(property, true)
							.iterator();
					// remplir les propriétés de la catégorie
					while (categoryProperties.hasNext()) {
						OWLDataProperty aCategoryProperty = categoryProperties.next();
						Iterator<OWLLiteral> properties = reasoner.dataPropertyValues(categoryIndiv, aCategoryProperty)
								.iterator();
						if (properties.hasNext()) {
							dbConnection.executeUpdateQuery("UPDATE `CEMOnto`.`products` SET `"
									+ aCategoryProperty.getIRI().getShortForm() + "`='" + properties.next().getLiteral()
									+ "' WHERE `name`='" + productIndiv.getIRI().getShortForm() + "';");
						}
					}
					Iterator<OWLDataProperty> productProperties = reasoner.subDataProperties(productProperty, true)
							.iterator();
					// remplir les propriétés du produit
					while (productProperties.hasNext()) {
						OWLDataProperty aProductProperty = productProperties.next();
						Iterator<OWLLiteral> properties = reasoner.dataPropertyValues(productIndiv, aProductProperty)
								.iterator();
						if (properties.hasNext()) {
							dbConnection.executeUpdateQuery("UPDATE `CEMOnto`.`products` SET `"
									+ aProductProperty.getIRI().getShortForm() + "`='" + properties.next().getLiteral()
									+ "' WHERE `name`='" + productIndiv.getIRI().getShortForm() + "';");
						}
					}

				}

			}
		}

	}

	public static void ProductItemsTableInstances() throws OWLOntologyCreationException {
		OWLClass productItem = fac.getOWLClass(IRI.create(prefix + "ProductItem"));

		OWLClass product = fac.getOWLClass(IRI.create(prefix + "Product"));
		OWLDataProperty productProperty = fac.getOWLDataProperty(IRI.create(prefix + "ProductProperty"));

		OWLDataProperty productItemProperty = fac.getOWLDataProperty(IRI.create(prefix + "ProductItemProperty"));

		OWLObjectProperty describes = fac.getOWLObjectProperty(IRI.create(prefix + "describes"));

		// retreive BicycleModel
		Iterator<OWLClass> productSubcategories = reasoner.subClasses(product, true).iterator();

		while (productSubcategories.hasNext()) {
			OWLClass aProductSubCategory = productSubcategories.next();
			Iterator<OWLNamedIndividual> allIndivCategory = reasoner.instances(aProductSubCategory, true).iterator();

			while (allIndivCategory.hasNext()) {
				OWLNamedIndividual categoryIndiv = allIndivCategory.next();
				// liste des products décrits pas l'individu categorie
				Iterator<OWLNamedIndividual> describedProducts = reasoner.objectPropertyValues(categoryIndiv, describes)
						.iterator();
				while (describedProducts.hasNext()) {
					OWLNamedIndividual productIndiv = describedProducts.next();
					dbConnection.executeUpdateQuery("INSERT INTO`CEMOnto`.`productItems` (`name`) VALUES ('"
							+ productIndiv.getIRI().getShortForm() + "');");
					Iterator<OWLDataProperty> categoryProperties = reasoner.subDataProperties(productProperty, true)
							.iterator();
					// remplir les propriétés de produit
					while (categoryProperties.hasNext()) {
						OWLDataProperty aCategoryProperty = categoryProperties.next();
						Iterator<OWLLiteral> properties = reasoner.dataPropertyValues(categoryIndiv, aCategoryProperty)
								.iterator();
						if (properties.hasNext()) {
							dbConnection.executeUpdateQuery("UPDATE `CEMOnto`.`productItems` SET `"
									+ aCategoryProperty.getIRI().getShortForm() + "`='" + properties.next().getLiteral()
									+ "' WHERE `name`='" + productIndiv.getIRI().getShortForm() + "';");
						}
					}
					Iterator<OWLDataProperty> productProperties = reasoner.subDataProperties(productItemProperty, true)
							.iterator();
					// remplir les propriétés du produit item
					while (productProperties.hasNext()) {
						OWLDataProperty aProductProperty = productProperties.next();
						Iterator<OWLLiteral> properties = reasoner.dataPropertyValues(productIndiv, aProductProperty)
								.iterator();
						if (properties.hasNext()) {
							dbConnection.executeUpdateQuery("UPDATE `CEMOnto`.`productItems` SET `"
									+ aProductProperty.getIRI().getShortForm() + "`='" + properties.next().getLiteral()
									+ "' WHERE `name`='" + productIndiv.getIRI().getShortForm() + "';");
						}
					}

				}

			}
		}

	}

	public static void CustomerCategoryTableCreation() throws OWLOntologyCreationException {
		dbConnection.executeUpdateQuery("DROP Table IF EXISTS`CEMOnto`.`customerCategories` ");
		dbConnection.executeUpdateQuery(
				" CREATE TABLE `CEMOnto`.`customerCategories` ( `id` INT NOT NULL AUTO_INCREMENT,`name` VARCHAR(45) NULL,PRIMARY KEY (`id`), UNIQUE INDEX `name` (`name` ASC));");

		OWLClass customerCategory = fac.getOWLClass(IRI.create(prefix + "CustomerCategory"));
		OWLDataProperty property = fac.getOWLDataProperty(IRI.create(prefix + "CustomerCategoryProperty"));
		Iterator<OWLDataProperty> properties = reasoner.getSubDataProperties(property, true).entities().iterator();
		Iterator<OWLClass> allCategories = reasoner.getSubClasses(customerCategory, false).entities().iterator();
		while (allCategories.hasNext()) {
			OWLClass cat = allCategories.next();
			if (!cat.isOWLNothing()) {
				dbConnection.executeUpdateQuery("INSERT INTO`CEMOnto`.`customerCategories` (`name`) VALUES ('"
						+ cat.asOWLClass().getIRI().getShortForm() + "');");
			}

		}
		while (properties.hasNext()) {
			final OWLDataProperty aproperty = properties.next();

			Iterator<OWLDataPropertyRangeAxiom> dataPropertyRangeAxioms = ont.dataPropertyRangeAxioms(aproperty)
					.iterator();
			while (dataPropertyRangeAxioms.hasNext()) {
				OWLDataPropertyRangeAxiom range = dataPropertyRangeAxioms.next();
				if (range.getRange().isOWLDatatype()) {
					if (range.getRange().asOWLDatatype().isDouble() || range.getRange().asOWLDatatype().isInteger()
							|| range.getRange().asOWLDatatype().isFloat()) {
						dbConnection.executeUpdateQuery("ALTER TABLE `CEMOnto`.`customerCategories` ADD COLUMN `"
								+ aproperty.getIRI().getShortForm() + "_min" + "` VARCHAR(45) NULL ;");
						dbConnection.executeUpdateQuery("ALTER TABLE `CEMOnto`.`customerCategories` ADD COLUMN `"
								+ aproperty.getIRI().getShortForm() + "_max" + "` VARCHAR(45) NULL ;");

					}
				} else {
					dbConnection.executeUpdateQuery("ALTER TABLE `CEMOnto`.`customerCategories` ADD COLUMN `"
							+ aproperty.getIRI().getShortForm() + "` VARCHAR(45) NULL ;");

				}

			}
			Iterator<OWLDataProperty> subProperties = reasoner.subDataProperties(aproperty).iterator();
			while (subProperties.hasNext()) {
				final OWLDataProperty aSubProperty = subProperties.next();

				Iterator<OWLDataPropertyDomainAxiom> subDataPropertyDomainAxioms = ont
						.dataPropertyDomainAxioms(aSubProperty).iterator();
				final List<OWLClass> domains = new ArrayList<OWLClass>();
				while (subDataPropertyDomainAxioms.hasNext()) {
					OWLDataPropertyDomainAxiom aSubDataPropertyDomainAxiom = subDataPropertyDomainAxioms.next();
					OWLClassExpression theSubDomain = aSubDataPropertyDomainAxiom.getDomain();
					Iterator<OWLClass> subDomains = reasoner.getSubClasses(theSubDomain, false).entities().iterator();

					if (theSubDomain.isAnonymousExpression()) {
						OWLClass newCategory = null;
						while (subDomains.hasNext()) {
							newCategory = subDomains.next();

							domains.add(newCategory.asOWLClass());
						}
					} else {

						domains.add(theSubDomain.asOWLClass());
						while (subDomains.hasNext()) {
							domains.add(subDomains.next());
						}
					}
				}
				Iterator<OWLDataPropertyRangeAxiom> subDataPropertyRangeAxioms = ont
						.dataPropertyRangeAxioms(aSubProperty).iterator();
				while (subDataPropertyRangeAxioms.hasNext()) {
					OWLDataPropertyRangeAxiom aSubDataPropertyRangeAxiom = subDataPropertyRangeAxioms.next();
					OWLDataRange theSubRange = aSubDataPropertyRangeAxiom.getRange();
					theSubRange.accept(new OWLDataRangeVisitor() {

						public void visit(OWLDataOneOf node) {
							node.values();
							for (OWLClass domain : domains) {
								dbConnection.executeUpdateQuery("UPDATE `CEMOnto`.`customerCategories` SET `"
										+ aproperty.getIRI().getShortForm() + "`='"
										+ node.values().iterator().next().getLiteral() + "' WHERE `name`='"
										+ domain.getIRI().getShortForm() + "';");
							}
						}
					});
					theSubRange.accept(new OWLDataRangeVisitor() {

						public void visit(OWLDatatypeRestriction node) {

							Iterator<OWLFacetRestriction> facetRestrictions = node.facetRestrictions().iterator();
							while (facetRestrictions.hasNext()) {
								OWLFacetRestriction owlFacetRestriction = facetRestrictions.next();
								String min;
								String max;
								for (OWLClass domain : domains) {
									if (owlFacetRestriction.getFacet().name().equals("MIN_INCLUSIVE")
											|| owlFacetRestriction.getFacet().name().equals("MIN_EXCLUSIVE")) {
										min = owlFacetRestriction.getFacetValue().getLiteral();

										dbConnection.executeUpdateQuery("UPDATE `CEMOnto`.`customerCategories` SET `"
												+ aproperty.getIRI().getShortForm() + "_min" + "`='" + min
												+ "' WHERE `name`='" + domain.getIRI().getShortForm() + "';");
									}

									if (owlFacetRestriction.getFacet().name().equals("MAX_INCLUSIVE")
											|| owlFacetRestriction.getFacet().name().equals("MAX_EXCLUSIVE")) {

										max = owlFacetRestriction.getFacetValue().getLiteral();

										dbConnection.executeUpdateQuery("UPDATE `CEMOnto`.`customerCategories` SET `"
												+ aproperty.getIRI().getShortForm() + "_max" + "`='" + max
												+ "' WHERE `name`='" + domain.getIRI().getShortForm() + "';");
									}
								}

							}

						}
					});
				}

			}
		}

	}

	public static void CustomerTableCreation() throws OWLOntologyCreationException {
		dbConnection.executeUpdateQuery("DROP Table IF EXISTS`CEMOnto`.`customers` ");
		dbConnection.executeUpdateQuery(
				" CREATE TABLE `CEMOnto`.`customers` ( `id` INT NOT NULL AUTO_INCREMENT,`name` VARCHAR(45) NULL,PRIMARY KEY (`id`), UNIQUE INDEX `name` (`name` ASC));");
		OWLDataProperty property = fac.getOWLDataProperty(IRI.create(prefix + "CustomerCategoryProperty"));
		OWLDataProperty productProperty = fac.getOWLDataProperty(IRI.create(prefix + "CustomerProperty"));
		Iterator<OWLDataProperty> allProperties = reasoner.getSubDataProperties(property, true).entities().iterator();
		Iterator<OWLDataProperty> allProductProperties = reasoner.getSubDataProperties(productProperty, true).entities()
				.iterator();

		while (allProductProperties.hasNext()) {
			OWLDataProperty aProperty = allProductProperties.next();
			if (!aProperty.isOWLBottomDataProperty()) {
				dbConnection.executeUpdateQuery("ALTER TABLE `CEMOnto`.`customers` ADD COLUMN `"
						+ aProperty.getIRI().getShortForm() + "` VARCHAR(45) NULL ;");
			}
		}
		while (allProperties.hasNext()) {
			OWLDataProperty aProperty = allProperties.next();
			if (!aProperty.isOWLBottomDataProperty()) {
				dbConnection.executeUpdateQuery("ALTER TABLE `CEMOnto`.`customers` ADD COLUMN `"
						+ aProperty.getIRI().getShortForm() + "` VARCHAR(45) NULL ;");
			}
		}

	}

	public static void CustomerTableInstances() throws OWLOntologyCreationException {
		OWLClass customerCategory = fac.getOWLClass(IRI.create(prefix + "CustomerCategory"));
		OWLDataProperty property = fac.getOWLDataProperty(IRI.create(prefix + "CustomerCategoryProperty"));

		OWLDataProperty customerProperty = fac.getOWLDataProperty(IRI.create(prefix + "CustomerProperty"));

		OWLObjectProperty describes = fac.getOWLObjectProperty(IRI.create(prefix + "describes"));

		// retreive BicycleModel
		Iterator<OWLClass> customerSubcategories = reasoner.subClasses(customerCategory, false).iterator();
		while (customerSubcategories.hasNext()) {
			OWLClass aCustomerSubCategory = customerSubcategories.next();
			Iterator<OWLNamedIndividual> allIndivCategory = reasoner.instances(aCustomerSubCategory, true).iterator();

			while (allIndivCategory.hasNext()) {
				OWLNamedIndividual categoryIndiv = allIndivCategory.next();
				// liste des products décrits pas l'individu categorie
				Iterator<OWLNamedIndividual> customerProducts = reasoner.objectPropertyValues(categoryIndiv, describes)
						.iterator();
				while (customerProducts.hasNext()) {
					OWLNamedIndividual customerIndiv = customerProducts.next();
					dbConnection.executeUpdateQuery("INSERT INTO`CEMOnto`.`customers` (`name`) VALUES ('"
							+ customerIndiv.getIRI().getShortForm() + "');");
					Iterator<OWLDataProperty> categoryProperties = reasoner.subDataProperties(property, true)
							.iterator();
					// remplir les propriétés de la catégorie
					while (categoryProperties.hasNext()) {
						OWLDataProperty aCategoryProperty = categoryProperties.next();
						Iterator<OWLLiteral> properties = reasoner.dataPropertyValues(categoryIndiv, aCategoryProperty)
								.iterator();
						if (properties.hasNext()) {
							dbConnection.executeUpdateQuery("UPDATE `CEMOnto`.`customers` SET `"
									+ aCategoryProperty.getIRI().getShortForm() + "`='" + properties.next().getLiteral()
									+ "' WHERE `name`='" + customerIndiv.getIRI().getShortForm() + "';");
						}
					}
					Iterator<OWLDataProperty> customerProperties = reasoner.subDataProperties(customerProperty, true)
							.iterator();
					// remplir les propriétés du produit
					while (customerProperties.hasNext()) {
						OWLDataProperty aCustomerProperty = customerProperties.next();
						Iterator<OWLLiteral> properties = reasoner.dataPropertyValues(customerIndiv, aCustomerProperty)
								.iterator();
						if (properties.hasNext()) {
							dbConnection.executeUpdateQuery("UPDATE `CEMOnto`.`customers` SET `"
									+ aCustomerProperty.getIRI().getShortForm() + "`='" + properties.next().getLiteral()
									+ "' WHERE `name`='" + customerIndiv.getIRI().getShortForm() + "';");
						}
					}

				}

			}
		}

	}

	public static float recommend( OWLClass customerCategory, OWLClass productCategory,
			OWLDataProperty propertyType) {
		float recommendationRate = 0;
		int nbCommonValues = 0;
		int nbValueCustomerProperties = categoryPropertiesOfApropertyType(customerCategory, propertyType).size();
		Iterator<OWLDataProperty> customerCategoryProperties = categoryPropertiesOfApropertyType(customerCategory, propertyType).iterator();
		while (customerCategoryProperties.hasNext()) {
			OWLDataProperty customerCategoryProperty = (OWLDataProperty) customerCategoryProperties.next();
			Iterator<OWLDataProperty> productCategoryProperties = categoryPropertiesOfApropertyType(productCategory, propertyType).iterator();
			while (productCategoryProperties.hasNext()) {
				OWLDataProperty productCategoryProperty = (OWLDataProperty) productCategoryProperties.next();
				Iterator<OWLDataPropertyRangeAxiom> customerRanges = ont.dataPropertyRangeAxioms(customerCategoryProperty).iterator();
				Iterator<OWLDataPropertyRangeAxiom> productRanges = ont.dataPropertyRangeAxioms(productCategoryProperty).iterator();
				while (productRanges.hasNext()) {
					OWLDataPropertyRangeAxiom productRangeAxiom = (OWLDataPropertyRangeAxiom) productRanges
							.next();
					while (customerRanges.hasNext()) {
						OWLDataPropertyRangeAxiom customerRangeAxiom = (OWLDataPropertyRangeAxiom) customerRanges
								.next();
						
						if(productRangeAxiom.getRange().equals(customerRangeAxiom.getRange())){
							nbCommonValues++;
							
						}
						
					}
				} 
				
			}
			
		}
		System.out.println("nbValueCustomerProperties"+nbValueCustomerProperties);
		System.out.println("nbCommonValues"+nbCommonValues);
		if(nbValueCustomerProperties!=0)
			recommendationRate = (float)nbCommonValues/(float)nbValueCustomerProperties;
		
		return recommendationRate;

	}

	public static float recommend() {
		
		OWLDataProperty propertyType = fac.getOWLDataProperty(IRI.create(prefix + "emotionProperty"));
		OWLClass customerCategory = fac.getOWLClass(IRI.create(prefix + "ConfortPlus"));
		OWLClass productCategory = fac.getOWLClass(IRI.create(prefix + "mountain"));
		return recommend( customerCategory, productCategory, propertyType );

	}
	public static List<OWLDataProperty> categoryPropertiesOfApropertyType (OWLClass customerCategory, OWLDataProperty propertyType){
		List<OWLDataProperty> categoryProperty = new ArrayList<OWLDataProperty>();
		Iterator<OWLDataProperty> subPropertiesTypes = reasoner.getSubDataProperties(propertyType,false).entities().iterator();
		while (subPropertiesTypes.hasNext()) {
			OWLDataProperty owlDataProperty = (OWLDataProperty) subPropertiesTypes.next();
			Iterator<OWLClass> allDomains = reasoner.dataPropertyDomains(owlDataProperty,true).iterator();
			while (allDomains.hasNext()) {
				OWLClass owlClass = (OWLClass) allDomains.next();
				if(owlClass.equals(customerCategory)){
					categoryProperty.add(owlDataProperty);
				}
				
			}
		}
		
		
		return categoryProperty;
	}
	public static int nbCategoryPropertiesOfApropertyType (OWLClass customerCategory, OWLDataProperty propertyType){
		int nbProperties =categoryPropertiesOfApropertyType(customerCategory, propertyType).size();
		return nbProperties;
	}
	public static float recommend(OWLClass customerCategory, OWLClass productCategory){
		float recommendationRate= 0;
		OWLDataProperty propertyType = fac.getOWLDataProperty(IRI.create(prefix + "PropertyType"));
		long numberOfPropertyTypes = reasoner.getSubDataProperties(propertyType, true).entities().count();
		Iterator<OWLDataProperty> allPropertyTypes = reasoner.getSubDataProperties(propertyType, true).entities().iterator();
		while (allPropertyTypes.hasNext()) {
			OWLDataProperty aPropertyType = (OWLDataProperty) allPropertyTypes.next();
			recommendationRate = /*recommendationRate+*/ recommend( customerCategory, productCategory, aPropertyType);
			
		}
		//if(numberOfPropertyTypes!=0)
			//recommendationRate = recommendationRate/(float) numberOfPropertyTypes;
		System.out.println("recommendationRate"+recommendationRate);
		
		return recommendationRate;
	} 
	
	public static boolean recommend(OWLClass customerCategory, OWLClass productCategory, float level){
		float recommendationRate= recommend( customerCategory, productCategory);
		boolean recommand=false;
		if (recommendationRate >=level){
			recommand=true;
		}
		return recommand;
	}
	public static boolean recommend( String customerCategory, String productCategory, float level){
		OWLClass owlCustomerCategory = fac.getOWLClass(prefix+customerCategory);
		OWLClass owlProductCategory = fac.getOWLClass(prefix+productCategory);
		
		boolean recommendationRate= recommend( owlCustomerCategory, owlProductCategory, level);
		
		return recommendationRate;
	}
	public static void recommendInstantiate( float level) throws SQLException{
		ResultSet customerCategories = dbConnection.executeQuery("SELECT id, name FROM CEMOnto.customerCategories;");
		boolean is_recommended = false;
		while (customerCategories.next()) {
			String aCustomerCategory = customerCategories.getString("name");
			int aCustomerCategoryID = customerCategories.getInt("id");
			ResultSet productCategories = dbConnection.executeQuery("SELECT id , name FROM CEMOnto.productCategories;");
			
			while (productCategories.next()) {
				String aProductCategory = productCategories.getString("name");
				int aProductCategoryID = productCategories.getInt("id");
				System.out.println("is_recommended "+is_recommended);
				is_recommended = recommend( aCustomerCategory, aProductCategory, level);
				dbConnection.executeUpdateQuery("INSERT INTO `CEMOnto`.`recommendatins` (`customer_category`, `product_category`, `is_recommended`) VALUES ('"+aCustomerCategoryID+"', '"+aProductCategoryID+"', '"+is_recommended+"');");

			}
			
		}
		
	}
}
