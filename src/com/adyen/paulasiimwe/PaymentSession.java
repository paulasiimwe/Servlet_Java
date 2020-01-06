package com.adyen.paulasiimwe;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.service.Checkout;
import com.adyen.service.exception.ApiException;
import com.google.gson.Gson;
import com.adyen.model.Amount;
import com.adyen.model.checkout.PaymentSessionRequest;
import com.adyen.model.checkout.PaymentSessionResponse;

/**
 * Servlet implementation class PaymentSession
 */
@WebServlet("/PaymentSession")
public class PaymentSession extends HttpServlet {
	
	static String API = "AQEyhmfxJ4LIbhBDw0m/n3Q5qf3VaY9UCJ1+XWZe9W27jmlZihMPlwWFQSNNxzoSFKkbaUEQwV1bDb7kfNy1WIxIIkxgBw==-FgMmXEbV5KRHFSIE9AMC8R8r/ryQ08qAVsnnJqjR7e8=-ZH4mT8daH7IIAPI9";
	
	Client client;
	Checkout checkout;
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PaymentSession() {
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
		
		PaymentSessionRequest paymentSessionRequest = new PaymentSessionRequest();
		
		paymentSessionRequest.setMerchantAccount("PaulAsiimwe");
		
		paymentSessionRequest.setCountryCode(request.getParameter("countryCode"));
		Amount amount = new Amount();
		amount.setCurrency(request.getParameter("currency"));
		
		
		
		try {
			paymentSessionRequest.setChannel(PaymentSessionRequest.ChannelEnum.fromValue(request.getParameter("channel")));
			amount.setValue(
					Long.valueOf(
							request.getParameter("value")
							)
					);
		}catch (Exception e) {
			amount.setValue(1000L);
			paymentSessionRequest.setChannel(PaymentSessionRequest.ChannelEnum.WEB);
		}
		
		if(paymentSessionRequest.getChannel()==PaymentSessionRequest.ChannelEnum.WEB) {
			paymentSessionRequest.setOrigin("http://localhost:8080");
			paymentSessionRequest.setSdkVersion(request.getParameter("sdkVersion"));
		}
		
		paymentSessionRequest.setAmount(amount);
		
		try {
			if(!request.getParameter("returnurl").isEmpty()) {
				paymentSessionRequest.setReturnUrl(request.getParameter("returnurl"));
			}
		}catch(Exception e) {
			
		}
		
		
		
		paymentSessionRequest.setToken(request.getParameter("token"));
		
		paymentSessionRequest.setReference(generateString());
		
		try {
			
			System.out.println("\n\nPaymentSession Request:\n"+
            		new JSONObject(
            				new Gson().toJson(
            						paymentSessionRequest
            						)
            				).toString(4)
            		);
			
			PaymentSessionResponse Response = checkout.paymentSession(paymentSessionRequest);
			
			Gson gson = new Gson();
            String json = gson.toJson(Response);
            
            JSONObject jb = new JSONObject(json);
            
            System.out.println("\n\nPaymentSession Response:\n"+
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
	
	public static String generateString() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
