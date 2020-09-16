package DBGenerator;
import java.sql.*;  
public class DBConnection {
	
	private static DBConnection dbConnection ;
	
	private DBConnection(){
		connection = connectToDB();
	};
	public static DBConnection getInstance(){
		if (dbConnection==null) dbConnection = new DBConnection();
		return dbConnection;
	}
	Connection connection;
	
	
	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public Connection connectToDB(){
		System.out.println("-------- MySQL JDBC Connection Testing ------------");

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
			
		}

		System.out.println("MySQL JDBC Driver Registered!");
		Connection connection = null;

		try {
			connection = DriverManager
			.getConnection("jdbc:mysql://localhost:3306/CEMOnto","root", "root");

		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			
		}

		if (connection != null) {
			System.out.println("You made it, take control your database now!");
		} else {
			System.out.println("Failed to make connection!");
		}
		return connection;
	  }

	public void executeUpdateQuery( String query){
		Statement stmt=null;
		try {
			stmt = connection.createStatement();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
		int rs=-1;
		try {
			rs = stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		System.out.println(rs);
	
	}
	public ResultSet  executeQuery( String query){
		Statement stmt=null;
		try {
			stmt = connection.createStatement();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
		ResultSet rs=null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		System.out.println(rs);
		return rs;
	}
	 
	public static void main(String args[]){  
	
	} 

}
