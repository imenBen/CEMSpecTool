package OWLSpecification;

public enum CEMDataTypes {
	
	NUMERIC ("1"), NUMERIC_RANGE("2"), LITERAL_VALUES("3"), LITERAL("4"), EMPTY_TYPE("0") ;

	private String id;
	
	
	CEMDataTypes(String id){
		this.id= id;
	}
	
		
	public String getId(){
		return id;
	}
	
	
	
}
