package com.adyen.paulasiimwe;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.Amount;
import com.adyen.model.checkout.PaymentMethodsRequest;
import com.adyen.model.checkout.PaymentMethodsResponse;
import com.adyen.service.Checkout;
import com.adyen.service.exception.ApiException;
import com.google.gson.Gson;

/**
 * Servlet implementation class AdyenTestServlet
 */
@WebServlet("/GetPaymentMethods")
public class GetPaymentMethods extends HttpServlet {
	
	static String API = "AQEyhmfxJ4LIbhBDw0m/n3Q5qf3VaY9UCJ1+XWZe9W27jmlZihMPlwWFQSNNxzoSFKkbaUEQwV1bDb7kfNy1WIxIIkxgBw==-FgMmXEbV5KRHFSIE9AMC8R8r/ryQ08qAVsnnJqjR7e8=-ZH4mT8daH7IIAPI9";

	static String liveAPI = "AQEyhmfxKY3PbxBEw0m/n3Q5qf3VaY9UCJ1+XWZe9W27jmlZirXJmGmIlBs97G+e9glHKXUQwV1bDb7kfNy1WIxIIkxgBw==-HikZpEZVs+l1vsARBJmgpNtNqfsDkElT6t82mZwaOQ0=-$A5{Fys9pJyZ8Y2>";

	Client client;
	Checkout checkout;
	static Boolean live = false;
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetPaymentMethods() {
        super();
        // TODO Auto-generated constructor stub

		if(live){
			client = new Client(liveAPI,
					Environment.LIVE, "14bc048714e340cf-AdyenTechSupport");
		}else{
			client = new Client(API,
					Environment.TEST);
		}

        
        checkout = new Checkout(client);
        
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		response.setContentType("application/json");
		
		//;
		
		PrintWriter out = response.getWriter();
		
		PaymentMethodsRequest paymentMethodsRequest = new PaymentMethodsRequest();
		paymentMethodsRequest.setMerchantAccount("PaulAsiimwe");
        
		paymentMethodsRequest.setCountryCode(request.getParameter("countryCode"));
		Amount amount = new Amount();
		amount.setCurrency(request.getParameter("currency"));
		
		try {
			paymentMethodsRequest.setChannel(PaymentMethodsRequest.ChannelEnum.fromValue(request.getParameter("channel")));
			amount.setValue(
					Long.valueOf(
							request.getParameter("value")
							)
					);
		}catch (Exception e) {
			amount.setValue(1000L);
			paymentMethodsRequest.setChannel(PaymentMethodsRequest.ChannelEnum.WEB);
		}
		
		
		
		paymentMethodsRequest.setAmount(amount);
		
		List<String> allowedPaymentMethods = new ArrayList<String>();
		allowedPaymentMethods.add("mc");
		
		//paymentMethodsRequest.setAllowedPaymentMethods(allowedPaymentMethods);
		//paymentMethodsRequest.setChannel(PaymentMethodsRequest.ChannelEnum.fromValue(request.getParameter("channel").toUpperCase()));
		try {
			
			System.out.println("\n\nPaymentMethods Request:\n"+
            		new JSONObject(
            				new Gson().toJson(
            						paymentMethodsRequest
            						)
            				).toString(4)
            		);
			
			PaymentMethodsResponse Response = checkout.paymentMethods(paymentMethodsRequest);
			
			Gson gson = new Gson();
            String json = gson.toJson(Response);

            //json = json.replace("Credit Card", "Kaaddi");
            
            JSONObject jb = new JSONObject(json);
            
            System.out.println("\n\nPaymentMethods Response:\n"+
            		jb.toString()
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
