package com.adyen.paulasiimwe;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.checkout.PaymentsDetailsRequest;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.service.Checkout;
import com.google.gson.Gson;

/**
 * Servlet implementation class PaymentDetails
 */
@WebServlet("/PaymentDetails")
public class PaymentDetails extends HttpServlet {
	
	
	static String API = "AQEyhmfxJ4LIbhBDw0m/n3Q5qf3VaY9UCJ1+XWZe9W27jmlZihMPlwWFQSNNxzoSFKkbaUEQwV1bDb7kfNy1WIxIIkxgBw==-FgMmXEbV5KRHFSIE9AMC8R8r/ryQ08qAVsnnJqjR7e8=-ZH4mT8daH7IIAPI9";
	
	Client client;
	Checkout checkout;
	
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PaymentDetails() {
        super();
        // TODO Auto-generated constructor stub
        
        client = new Client(API,
				Environment.TEST);
        
        checkout = new Checkout(client);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//doGet(request, response);
		
		response.setContentType("application/json");
		
		PrintWriter out = response.getWriter();
		
		String type = request.getParameter("type");
		
		System.out.println("ActionType: "+type);
		
		PaymentsDetailsRequest paymentsDetailsRequest = new PaymentsDetailsRequest();
        HashMap<String, String> details = new HashMap<>();

        try{
            //Log.v("actionData",actionComponentData.toString(4));
        	
        	switch(type) {
        	case "redirectscheme":
        		paymentsDetailsRequest.set3DRequestData(
                		request.getParameter("MD"),
                		request.getParameter("PaRes"),
                		request.getParameter("paymentData")
                );
                break;
            case "redirectideal":
            	details.put("payload", request.getParameter("payload"));

                paymentsDetailsRequest.setDetails(details);
                break;
            case "threeDS2Fingerprintscheme":
            	details.put("threeds2.fingerprint", request.getParameter("fingerprint"));

                paymentsDetailsRequest.setDetails(details);
                paymentsDetailsRequest.setPaymentData(request.getParameter("paymentData"));
                break;
                
            case "threeDS2Challengescheme":
            	details.put("threeds2.challengeResult", request.getParameter("challengeResult"));

                paymentsDetailsRequest.setDetails(details);
                paymentsDetailsRequest.setPaymentData(request.getParameter("paymentData"));
                break;
                
        	}
            
            System.out.println("\n\nPaymentDetails Request:\n"+
            		new JSONObject(
            				new Gson().toJson(
            						paymentsDetailsRequest
            						)
            				).toString(4)
            		);

            PaymentsResponse paymentsResponse = checkout.paymentsDetails(paymentsDetailsRequest);

            Gson gson = new Gson();
            String json = gson.toJson(paymentsResponse);
            
            JSONObject responseJson = new JSONObject(json);
            
            if(paymentsResponse.getAction() != null) {
        		JSONObject responseAction = responseJson.getJSONObject("action");
        		
        		responseAction.put("paymentData", paymentsResponse.getPaymentData());
        		
        		responseJson.remove("action");
        		
        		responseJson.put("action", responseAction);
        	}
            
            System.out.println("\n\nPaymentDetails Response:\n"+responseJson.toString(4));
            
            out.println(responseJson);

            
        }catch (Exception e){
//            
        	System.out.println(e.toString());
			out.print(e.toString());
        }
        
        
	}
	
	

}
