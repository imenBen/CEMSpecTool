package OWLSpecification;

public enum CEMGenericOntologyElements {
	//classes
	CUSTOMER("Customer"), CUSTOMER_CATEGORY("CustomerCategory"), PRODUCT("Product"), PRODUCT_CATEGORY("ProductCategory"), PRODUCT_ITEM("ProductItem"),
	//dataProperties
	CUSTOMER_PROPERTY("CustomerProperty"), CUSTOMER_CATEGORY_PROPERTY("CustomerCategoryProperty"), PRODUCT_PROPERTY("ProductProperty"), PRODUCT_CATEGORY_PROPERTY("ProductCategoryProperty"), PRODUCT_ITEM_PROPERTY("ProductItemProperty");
	private String name;
	CEMGenericOntologyElements(String name){
		this.name= name;
	}	
	public String getName(){
		return name;
	}
}
