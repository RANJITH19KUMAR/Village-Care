package com.backend;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class complaintHandler implements HttpHandler {

	private complaintDao dao =new  complaintDao();
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
	
		String path= exchange.getRequestURI().getPath();
		System.out.println("Request " +path);
		
		
		if(path.equals("/") || path.equals("/index.html") || path.equals("/index")) {
			serveFile(exchange,"src/web/index.html","text/html");
		}
		else if(path.equals("/complaint.html") || path.equals("/complaint")) {
			serveFile(exchange,"src/web/complaint.html","text/html");
		}
		else if(path.equals("/admin.html") || path.equals("/admin")) {
			serveFile(exchange,"src/web/admin.html","text/html");
		}
		
		else if(path.equals("/style.css")) {
			serveFile(exchange,"src/web/style.css","text/css");
		}
		else if(path.equals("/script.js")) {
			serveFile(exchange,"src/web/script.js","application/javascript");
		}
		
		else if(path.equals("/addComplaint") && exchange.getRequestMethod().equalsIgnoreCase("POST")) {
			
			String body = readBody(exchange);
			
			Map<String, String> data = parseForm(body);
			
			String name=data.get("name");
			String phone=data.get("mobile");
			String village_name = data.get("village");
			String pincode=data.get("pincode");
			String category = data.get("category");
			String description = data.get("description");
			String location = data.get("location");
			
			System.out.println("Complaint new  Data Received");
			System.out.println(name);
			System.out.println(phone);
			System.out.println(village_name);
			System.out.println(pincode);
			System.out.println(category);
			System.out.println(description);
			System.out.println(location);
			
			try {
				
				dao.addComplaint(name, phone, village_name, pincode, category, description, location);
				sendResponse(exchange,200," Complaint Submitted");
			}
			catch(Exception e) {
				
				e.printStackTrace();
				sendResponse(exchange,500,"Database error" +e.getMessage());
			}
			
		}
		
		else if(path.equals("/complaints")
		        && exchange.getRequestMethod().equalsIgnoreCase("GET")) {

		    try {

		        String json = dao.getComplaint();

		        System.out.println("Database JSON: " + json);

		        exchange.getResponseHeaders().set(
		                "Content-Type",
		                "application/json; charset=UTF-8"
		        );

		        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

		        exchange.sendResponseHeaders(
		                200,
		                bytes.length
		        );

		        try (OutputStream output = exchange.getResponseBody()) {

		            output.write(bytes);

		        }

		    } catch (Exception e) {

		        e.printStackTrace();

		        sendResponse(
		                exchange,
		                500,
		                "Database Error: " + e.getMessage()
		        );
		    }
		}
		
		else if (path.equals("/updateComplaint")
		        && exchange.getRequestMethod().equalsIgnoreCase("POST")) {

		    try {

		        String body = readBody(exchange);

		        Map<String, String> data = parseForm(body);

		        int id = Integer.parseInt(data.get("id"));

		        String status = data.get("status");

		        dao.updateComplaint(id, status);

		        sendResponse(
		            exchange,
		            200,
		            "Complaint status updated successfully"
		        );

		    } catch (Exception e) {

		        e.printStackTrace();

		        sendResponse(
		            exchange,
		            500,
		            "Update failed"
		        );
		    }
		}
		else {
			sendResponse(exchange,404,"File not found");
		}
		
	}
	
	

	private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
		byte[] bytes =
                response.getBytes(
                        StandardCharsets.UTF_8
                );

        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "text/plain; charset=UTF-8"
                );

        exchange.sendResponseHeaders(
                statusCode,
                bytes.length
        );

        OutputStream output =
                exchange.getResponseBody();

        output.write(bytes);

        output.close();
		
	}

	private Map<String, String> parseForm(String body) throws UnsupportedEncodingException {
		
		  Map<String, String> map = new HashMap<>();

		   for(String pair : body.split("&")) {
			   String[] p=pair.split("=",2);
			   
			   if(p.length==2) 
				   map.put(URLDecoder.decode(p[0] , "UTF-8") , URLDecoder.decode(p[1],"UTF-8"));
		   }
		   return map;
	}

	private String readBody(HttpExchange exchange) throws IOException {
		
		InputStream input=exchange.getRequestBody();
		return new String (input.readAllBytes(),StandardCharsets.UTF_8);
	
	}

	private void serveFile(HttpExchange exchange, String filePath, String contentType) throws IOException {
		
		 java.nio.file.Path path =
		            java.nio.file.Paths.get(filePath);

		    if (!java.nio.file.Files.exists(path)) {

		        sendResponse(
		                exchange,
		                404,
		                "File Not Found"
		        );

		        return;
		    }
		

		    byte[] bytes =
		            java.nio.file.Files.readAllBytes(path);

		    exchange.getResponseHeaders()
		            .set("Content-Type", contentType);

		    exchange.sendResponseHeaders(
		            200,
		            bytes.length
		    );

		    OutputStream output =
		            exchange.getResponseBody();

		    output.write(bytes);
		    output.close();
		
	}

}
