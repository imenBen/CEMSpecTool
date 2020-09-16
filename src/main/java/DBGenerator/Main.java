package DBGenerator;
import java.sql.SQLException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class Main {
	private static String DOCUMENT_IRI = "file:/Users/imenbenzarti/Documents/WorkspaceOntologyDB/Cem/ProductOntology.owl";
	private static String prefix = "http://www.semanticweb.org/imenbenzarti/ontologies/2017/5/CEMOntology#";
	
	public static void main(String[] args) throws OWLOntologyCreationException, SQLException {
		
		// TODO Auto-generated method stub
		DataBaseGenerator.init(DOCUMENT_IRI);
		DataBaseGenerator.ProductCategoryTableCreation();
		DataBaseGenerator.ProductTableCreation();
		DataBaseGenerator.ProductItemsTableCreation();
		DataBaseGenerator.ProductTableInstances();
		DataBaseGenerator.ProductItemsTableInstances();
		DataBaseGenerator.CustomerCategoryTableCreation();
		DataBaseGenerator.CustomerTableCreation();
		DataBaseGenerator.CustomerTableInstances();
		DataBaseGenerator.recommend();
		DataBaseGenerator.recommendInstantiate( (float) 0.5);
	}

}
