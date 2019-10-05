package com.adyen.paulasiimwe;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioFormat.Encoding;

import org.json.JSONException;
import org.json.JSONObject;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.Amount;
import com.adyen.model.BrowserInfo;
import com.adyen.model.checkout.DefaultPaymentMethodDetails;
import com.adyen.model.checkout.PaymentMethodsRequest;
import com.adyen.model.checkout.PaymentsRequest;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.service.Checkout;
import com.google.gson.Gson;

/**
 * Servlet implementation class MakePayment
 */
@WebServlet("/MakePayment")
public class MakePayment extends HttpServlet {
	
	static String API = "AQEyhmfxJ4LIbhBDw0m/n3Q5qf3VaY9UCJ1+XWZe9W27jmlZihMPlwWFQSNNxzoSFKkbaUEQwV1bDb7kfNy1WIxIIkxgBw==-FgMmXEbV5KRHFSIE9AMC8R8r/ryQ08qAVsnnJqjR7e8=-ZH4mT8daH7IIAPI9";
	
	Client client;
	Checkout checkout;
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MakePayment() {
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
		
		PaymentsRequest paymentsRequest = new PaymentsRequest();
		paymentsRequest.setMerchantAccount("PaulAsiimwe");
		Amount amount = new Amount();
		amount.setCurrency(request.getParameter("currency"));
		amount.setValue(
				Long.valueOf(
						request.getParameter("value")
						)
				);
		paymentsRequest.setAmount(amount);
		
		JSONObject paymentComponentData;
		
		try {
			paymentComponentData = new JSONObject(EncodingUtil.decodeURIComponent(request.getParameter("data")));
			
			paymentComponentData = paymentComponentData.getJSONObject("mPaymentComponentData");
		}catch(JSONException e) {
			paymentComponentData = new JSONObject(EncodingUtil.decodeURIComponent(request.getParameter("data")));
		}catch(Exception e) {
			paymentComponentData = new JSONObject(request.getParameter("data"));
		}
		
		
		System.out.println(paymentComponentData.toString(4));
		
		try{

            paymentsRequest.setReference(generateString());

            DefaultPaymentMethodDetails dm  = new DefaultPaymentMethodDetails();

            String paymentMethodType = paymentComponentData.getJSONObject("paymentMethod").getString("type");
            

    		paymentsRequest.setChannel(PaymentsRequest.ChannelEnum.WEB);
    		paymentsRequest.setOrigin("http://localhost:8080/AdyenServlet/landing.html");


            switch (paymentMethodType){


                case "scheme":
                    String encryptedCardNumber = paymentComponentData.getJSONObject("paymentMethod").getString("encryptedCardNumber");
                    String encryptedExpiryMonth = paymentComponentData.getJSONObject("paymentMethod").getString("encryptedExpiryMonth");
                    String encryptedExpiryYear = paymentComponentData.getJSONObject("paymentMethod").getString("encryptedExpiryYear");
                    String encryptedSecurityCode = paymentComponentData.getJSONObject("paymentMethod").getString("encryptedSecurityCode");


                    paymentsRequest.addEncryptedCardData(encryptedCardNumber,encryptedExpiryMonth, encryptedExpiryYear, encryptedSecurityCode, "John Smith");

                    paymentsRequest.setShopperIP("192.0.2.1");

                    paymentsRequest.setBrowserInfo(
                            new BrowserInfo()
                                    .acceptHeader("\"Mozilla\\/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit\\/537.36 (KHTML, like Gecko) Chrome\\/70.0.3538.110 Safari\\/537.36\"")
                                    .userAgent("\"text\\/html,application\\/xhtml+xml,application\\/xml;q=0.9,image\\/webp,image\\/apng,*\\/*;q=0.8\"")
                                    /*.javaEnabled(false)
                                    .language("en")
                                    .colorDepth(24)
                                    .screenHeight(723)
                                    .screenWidth(1536)
                                    .timeZoneOffset(0)*/
                    );

                    break;

                case "ideal":

                    dm.setType("ideal");
                    dm.setIdealIssuer(paymentComponentData.getJSONObject("paymentMethod").getString("issuer"));

                    paymentsRequest.setPaymentMethod(dm);

                    break;

                case "klarna":

                    dm.setType("klarna");

                    paymentsRequest.setPaymentMethod(dm);

                    break;


                case "paypal":

                    dm.setType("paypal");

                    paymentsRequest.setPaymentMethod(dm);

                    break;

                case "sepadirectdebit":

                    dm.setType("sepadirectdebit");
                    dm.setSepaIbanNumber(paymentComponentData.getJSONObject("paymentMethod").getString("sepa.ibanNumber"));
                    dm.setSepaOwnerName(paymentComponentData.getJSONObject("paymentMethod").getString("sepa.ownerName"));


                    paymentsRequest.setPaymentMethod(dm);

                case "wechatpayQR":

                    dm.setType("wechatpayQR");


                    paymentsRequest.setPaymentMethod(dm);

                    break;

                case "wechatpayWeb":

                    dm.setType("wechatpayWeb");

                    paymentsRequest.setPaymentMethod(dm);

                    break;

            }

            try {
            	paymentsRequest.setReturnUrl(
            			EncodingUtil.decodeURIComponent(
            					request.getParameter("returnurl")
            					)
            			);
            }catch (Exception e) {
            	paymentsRequest.setReturnUrl("http://localhost:8080/AdyenServlet/landing.html");
            }
            
            PaymentsResponse paymentsResponse = checkout.payments(paymentsRequest);


            Gson gson = new Gson();
            String json = gson.toJson(paymentsResponse);

            JSONObject responseJson = new JSONObject(json);
            
            System.out.println(responseJson.toString(4));
            
            out.println(responseJson);

//            Log.v("DropIn_Response",responseJson.toString(4));


//            if(paymentsResponse.getAction()!=null){
//                paymentData = responseJson.getString("paymentData");
//                return new CallResult(CallResult.ResultType.ACTION, gson.toJson(paymentsResponse.getAction()));
//            }else{
//                return new CallResult(CallResult.ResultType.FINISHED, json);
//            }




        }catch (Exception e){
//            return new CallResult(CallResult.ResultType.FINISHED, "FAIL");
        	System.out.println(e.toString());
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
