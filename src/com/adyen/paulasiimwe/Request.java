package com.adyen.paulasiimwe;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Request {

public static String post(String requestBody, String endPoint, String apiKey, String contentType) {
		
		try {
			URL url = new URL (endPoint);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", contentType);
			connection.setRequestProperty("x-api-key", apiKey);
			//connection.setRequestProperty("Accept", "application/json");
			connection.setDoOutput(true);
			
			System.out.println("Endpoint: "+endPoint);
			System.out.println("Request: \n"+requestBody);
			
			OutputStream os = connection.getOutputStream();
			    byte[] input = requestBody.getBytes("utf-8");
			    os.write(input, 0, input.length);           
			
			
			BufferedReader br = new BufferedReader(
					  new InputStreamReader(connection.getInputStream(), "utf-8"));
					    StringBuilder Response = new StringBuilder();
					    String responseLine = null;
					    while ((responseLine = br.readLine()) != null) {
					        Response.append(responseLine.trim());
					    }
					    
					    return Response.toString();
					

		}catch (Exception e) {
			return e.toString();
		}
		
	}

private Request(){
    	super();
	}
}
