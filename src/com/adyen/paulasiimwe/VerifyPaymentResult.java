package com.adyen.paulasiimwe;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.checkout.PaymentResultRequest;
import com.adyen.model.checkout.PaymentResultResponse;
import com.adyen.service.Checkout;
import com.adyen.service.exception.ApiException;
import com.google.gson.Gson;

/**
 * Servlet implementation class VerifyPaymentResult
 */
@WebServlet("/VerifyPaymentResult")
public class VerifyPaymentResult extends HttpServlet {
	
static String API = "AQEyhmfxJ4LIbhBDw0m/n3Q5qf3VaY9UCJ1+XWZe9W27jmlZihMPlwWFQSNNxzoSFKkbaUEQwV1bDb7kfNy1WIxIIkxgBw==-FgMmXEbV5KRHFSIE9AMC8R8r/ryQ08qAVsnnJqjR7e8=-ZH4mT8daH7IIAPI9";
	
	Client client;
	Checkout checkout;
	
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VerifyPaymentResult() {
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
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		
		PaymentResultRequest paymentResultRequest = new PaymentResultRequest();
		
		paymentResultRequest.setPayload(request.getParameter("payload"));
		
try {
			
			System.out.println("\n\nPaymentResult Request:\n"+
            		new JSONObject(
            				new Gson().toJson(
            						paymentResultRequest
            						)
            				).toString(4)
            		);
			
			PaymentResultResponse Response = checkout.paymentResult(paymentResultRequest);
			
			Gson gson = new Gson();
            String json = gson.toJson(Response);
            
            JSONObject jb = new JSONObject(json);
            
            System.out.println("\n\nPaymentResult Response:\n"+
            		jb.toString(4)
            		);
            
            out.print(jb);
			//return json;
		} catch (ApiException e) {
			System.out.println(e.toString());
			out.print(e.toString());
			//return "Failed to get Methods, error is: "+e.toString();
		} catch (IOException e) {
			System.out.println(e.toString());
			out.print(e.toString());
			//return "Failed to get Methods, error is: "+e.toString();
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
