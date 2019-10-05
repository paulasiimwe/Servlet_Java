package com.adyen.paulasiimwe;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		response.setContentType("application/json");
		
		PrintWriter out = response.getWriter();
		
		String paymentMethodType = request.getParameter("paymentMethodType");
		
		PaymentsDetailsRequest paymentsDetailsRequest = new PaymentsDetailsRequest();
        HashMap<String, String> details = new HashMap<>();

        try{
            //Log.v("actionData",actionComponentData.toString(4));

            if(paymentMethodType.equalsIgnoreCase("scheme")){
                paymentsDetailsRequest.set3DRequestData(
                		request.getParameter("MD"),
                		request.getParameter("PaRes"),
                		request.getParameter("paymentData")
                );
            }else{
                details.put("payload", request.getParameter("payload"));

                paymentsDetailsRequest.setDetails(details);
            }




            PaymentsResponse paymentsResponse = checkout.paymentsDetails(paymentsDetailsRequest);

            Gson gson = new Gson();
            String json = gson.toJson(paymentsResponse);
            
            out.println(json);

            //Log.v("DetailsCallResponse",json);

            //return new CallResult(CallResult.ResultType.FINISHED, json);
        }catch (Exception e){
//            Log.e("DetailsCallResponse",e.toString());
//            return new CallResult(CallResult.ResultType.FINISHED, "FAIL");
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
