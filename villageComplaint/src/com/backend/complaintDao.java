package com.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class complaintDao {
	private Connection getConnection() throws SQLException {

	    String host = getEnv("MYSQLHOST","localhost");
	    String port = getEnv("MYSQLPORT","3306");
	    String database = getEnv("MYSQLDATABASE","village_db");
	    String username = getEnv("MYSQLUSER","root");
	    String password = getEnv("MYSQLPASSWORD"," ");

	    System.out.println("========== DATABASE CONNECTION ==========");
	    System.out.println("MYSQLHOST: " + host);
	    System.out.println("MYSQLPORT: " + port);
	    System.out.println("MYSQLDATABASE: " + database);
	    System.out.println("MYSQLUSER: " + username);
	    System.out.println("MYSQLPASSWORD: " + (password != null && !password.isEmpty() ? "***SET***" : "EMPTY"));

	    String url = "jdbc:mysql://" + host + ":" + port + "/" + database
	            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

	    System.out.println("JDBC URL: jdbc:mysql://" + host + ":" + port + "/" + database);

	    return DriverManager.getConnection(url, username, password);
	}
	
	 private String getEnv(
	            String variableName,
	            String defaultValue) {

	        String value =
	                System.getenv(variableName);

	        if (value == null || value.isBlank()) {
	            return defaultValue;
	        }

	        return value;
	    }
	 
	 public void initTable() {
		    String sql = "CREATE TABLE IF NOT EXISTS complaints ("
		        + "id INT AUTO_INCREMENT PRIMARY KEY,"
		        + "name VARCHAR(100), phone VARCHAR(20),"
		        + "village_name VARCHAR(100), pincode VARCHAR(10),"
		        + "category VARCHAR(100), description TEXT,"
		        + "location VARCHAR(255),"
		        + "status VARCHAR(20) DEFAULT 'Pending')";
		    try (Connection con = getConnection();
		         PreparedStatement ps = con.prepareStatement(sql)) {
		        ps.executeUpdate();
		        System.out.println("Table ready");
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		}

	public void addComplaint(String name , String phone,
			String village_name,String pincode,
			String category, String description, 
			String location) throws SQLException {
		
		String sql="insert into complaints(name,phone,village_name,pincode,category,description,location) values(?,?,?,?,?,?,?)";

		try(
				Connection con = getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				){
		
		ps.setString(1,name);
		ps.setString(2,phone );
		ps.setString(3,village_name);
		ps.setString(4,pincode);
		ps.setString(5,category);
		ps.setString(6, description);
		ps.setString(7, location);
		
		ps.executeUpdate();
		System.out.println("Complaints  Added Successfull");
		
	}
	catch(SQLException e) {
		e.printStackTrace();
	}
	}

	
	public String getComplaint() throws SQLException {
		StringBuilder json = new StringBuilder();

		    json.append("[");
		String sql="select id,name,phone,village_name,pincode,category,description,location,status from complaints";
		
		try(Connection con = getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs= ps.executeQuery();
				){
			
			
			boolean first= true;
			
			while(rs.next()) {
				if(!first) {
					json.append(",");
				}
				
				  json.append("{")
	                .append("\"id\":").append(rs.getInt("id"))
	                .append(",\"name\":\"").append(escape(rs.getString("name"))).append("\"")
	                .append(",\"phone\":\"").append(escape(rs.getString("phone"))).append("\"")
	                .append(",\"village_name\":\"").append(escape(rs.getString("village_name"))).append("\"")
	                .append(",\"pincode\":\"").append(escape(rs.getString("pincode"))).append("\"")
	                .append(",\"category\":\"").append(escape(rs.getString("category"))).append("\"")
	                .append(",\"description\":\"").append(escape(rs.getString("description"))).append("\"")
	                .append(",\"location\":\"").append(escape(rs.getString("location"))).append("\"")
	                .append(",\"status\":\"").append(escape(rs.getString("status"))).append("\"")
	                .append("}");
				  
				  first=false;
			}
			
		}
		return json.append("]").toString();
				
	}


	
	public void updateComplaint(int id, String status) throws SQLException {

	    String sql = "UPDATE complaints SET status = ? WHERE id = ?";

	    try (
	        Connection con =
	            getConnection();

	        PreparedStatement ps =
	            con.prepareStatement(sql)
	    ) {

	        ps.setString(1, status);
	        ps.setInt(2, id);

	        ps.executeUpdate();

	        System.out.println("Complaint status updated");
	    }
	}
	
	private String escape(String s) {
		

	    return s == null
	            ? ""
	            : s.replace("\\", "\\\\")
	               .replace("\"", "\\\"");
	}
	
	
}

