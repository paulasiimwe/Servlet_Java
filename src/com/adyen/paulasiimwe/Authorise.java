package com.adyen.paulasiimwe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import com.google.gson.Gson;

/**
 * Servlet implementation class Authorise
 */
@WebServlet("/Authorise")
public class Authorise extends HttpServlet {
	
	static String API = "AQEyhmfxJ4LIbhBDw0m/n3Q5qf3VaY9UCJ1+XWZe9W27jmlZihMPlwWFQSNNxzoSFKkbaUEQwV1bDb7kfNy1WIxIIkxgBw==-FgMmXEbV5KRHFSIE9AMC8R8r/ryQ08qAVsnnJqjR7e8=-ZH4mT8daH7IIAPI9";
	
	
	
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Authorise() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		
		
		System.out.println(request.getParameter("country"));
		
		
		String jsonRequestBody = "{\n" + 
				"   \"additionalData\":{\n" + 
				"      \"card.encrypted.json\":\""+request.getParameter("cardData")+"\"\n" + 
				"   },\n" + 
				"   \"amount\":{\n" + 
				"      \"value\":"+request.getParameter("value")+",\n" + 
				"      \"currency\":\""+request.getParameter("currency")+"\"\n" + 
				"   },\n" + 
				"   \"reference\":\""+request.getParameter("merchantReference")+"\",\n" + 
				"   \"merchantAccount\":\"PaulAsiimwe\"\n" + 
				"}";
		
		String url = "https://pal-test.adyen.com/pal/servlet/Payment/v51/authorise";
		
		String contentType = "application/json";
		
		String postResponseString  = Request.post(jsonRequestBody,
				url,
				API,
				contentType);
		
		
		
		try {
			JSONObject postResponse = new JSONObject(postResponseString);
			out.print(postResponse.toString(4));
			System.out.println("Response: \n"+postResponse.toString(4));
		}catch (Exception e) {
			System.out.println("Raw Response: \n"+postResponseString);
			out.print(postResponseString);
		}
		

		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
