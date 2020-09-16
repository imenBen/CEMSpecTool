package DBGenerator;

public class DBUtility {
	private static DBConnection dbConnection;
	
	
	public void addPropertyColumn(String name, String tablename){
		dbConnection = DBConnection.getInstance();
		
	}

}
