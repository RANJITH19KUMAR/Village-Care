package com.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class complaintDao {
	
	private String host =
	        System.getenv().getOrDefault("MYSQLHOST", "localhost");

	private String port =
	        System.getenv().getOrDefault("MYSQLPORT", "3306");

	private String database =
	        System.getenv().getOrDefault("MYSQLDATABASE", "village_db");

	private String username =
	        System.getenv().getOrDefault("MYSQLUSER", "root");

	private String password =
	        System.getenv().getOrDefault("MYSQLPASSWORD","");
	
	
	private String url =
	        "jdbc:mysql://" + host + ":" + port + "/" + database;
	
	public void addComplaint(String name , String phone,String village_name,String pincode,String category, String description, String location) {
		
		String sql="insert into complaint(name,phone,village_name,pincode,category,description,location) values(?,?,?,?,?,?,?)";
		
	try (Connection con =DriverManager.getConnection(url,username,password);
		PreparedStatement ps = con.prepareStatement(sql)){
		
		ps.setString(1,name);
		ps.setString(2,phone );
		ps.setString(3,village_name);
		ps.setString(4,pincode);
		ps.setString(5,category);
		ps.setString(6, description);
		ps.setString(7, location);
		
		ps.executeUpdate();
		System.out.println("Complaint  Added Successfull");
		
	}
	catch(SQLException e) {
		e.printStackTrace();
	}
	}
	
	public String getComplaint() throws SQLException {
		StringBuilder json = new StringBuilder();

		    json.append("[");
		String sql="select id,name,phone,village_name,pincode,category,description,location,status from complaint";
		
		try(Connection con = DriverManager.getConnection(url,username,password);
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

	private Object escape(String s) {
		

	    return s == null
	            ? ""
	            : s.replace("\\", "\\\\")
	               .replace("\"", "\\\"");
	}
	
	
	public void updateComplaint(int id, String status) throws SQLException {

	    String sql = "UPDATE complaint SET status = ? WHERE id = ?";

	    try (
	        Connection con =
	            DriverManager.getConnection(url, username, password);

	        PreparedStatement ps =
	            con.prepareStatement(sql)
	    ) {

	        ps.setString(1, status);
	        ps.setInt(2, id);

	        ps.executeUpdate();

	        System.out.println("Complaint status updated");
	    }
	}
}
