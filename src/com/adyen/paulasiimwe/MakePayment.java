package com.adyen.paulasiimwe;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;
import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.Address;
import com.adyen.model.Amount;
import com.adyen.model.BrowserInfo;
import com.adyen.model.Name;
import com.adyen.model.Name.GenderEnum;
import com.adyen.model.checkout.DefaultPaymentMethodDetails;
import com.adyen.model.checkout.LineItem;
import com.adyen.model.checkout.LineItem.TaxCategoryEnum;
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
			if(request.getParameter("channel").equalsIgnoreCase("Android")){
				paymentComponentData = new JSONObject(EncodingUtil.decodeURIComponent(request.getParameter("data")));
			}else {
				paymentComponentData = new JSONObject(request.getParameter("data"));
			}
			
		}catch(JSONException e) {
			paymentComponentData = new JSONObject(EncodingUtil.decodeURIComponent(request.getParameter("data")));
		}catch(Exception e) {
			paymentComponentData = new JSONObject(request.getParameter("data"));
		}
		
		
		System.out.println("\n\nPayment Component Data:\n"+paymentComponentData.toString(4));
		
		try{

            paymentsRequest.setReference(generateString());
            
            

            DefaultPaymentMethodDetails dm  = new DefaultPaymentMethodDetails();
            
            String paymentMethodType;

            
            paymentMethodType = paymentComponentData.getString("type");

    		//paymentsRequest.setChannel(PaymentsRequest.ChannelEnum.WEB);
    		//paymentsRequest.setOrigin("http://localhost:8080/AdyenServlet/landing.html");


            switch (paymentMethodType){


                case "scheme":
                    String encryptedCardNumber = paymentComponentData.getString("encryptedCardNumber");
                    String encryptedExpiryMonth = paymentComponentData.getString("encryptedExpiryMonth");
                    String encryptedExpiryYear = paymentComponentData.getString("encryptedExpiryYear");
                    String encryptedSecurityCode = paymentComponentData.getString("encryptedSecurityCode");


                    paymentsRequest.addEncryptedCardData(encryptedCardNumber,encryptedExpiryMonth, encryptedExpiryYear, encryptedSecurityCode, "John Smith");

                    paymentsRequest.setShopperIP("192.0.2.1");
                    
                    HashMap<String, String> additionalData = new HashMap<>();
                    additionalData.put("allow3DS2", "true");
                    
                    paymentsRequest.setAdditionalData(additionalData);

                    //paymentsRequest.setRecurringProcessingModel(PaymentsRequest.RecurringProcessingModelEnum.CARD_ON_FILE);

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
                    
//                    paymentsRequest.shopperReference("VickVickersonJr1985");
//                    paymentsRequest.setShopperInteraction(PaymentsRequest.ShopperInteractionEnum.ECOMMERCE);
//                    paymentsRequest.storePaymentMethod(true);

                    break;

                case "ideal":

                    dm.setType("ideal");
                    dm.setIdealIssuer(paymentComponentData.getString("issuer"));

                    paymentsRequest.setPaymentMethod(dm);

                    break;

                case "paypal":

                    dm.setType("paypal");

                    paymentsRequest.setPaymentMethod(dm);

                    break;

                case "sepadirectdebit":

                    dm.setType("sepadirectdebit");
                    try {
                    	dm.setSepaIbanNumber(paymentComponentData.getString("sepa.ibanNumber"));
                        dm.setSepaOwnerName(paymentComponentData.getString("sepa.ownerName"));
                    }catch(Exception e) {
                    	dm.setSepaIbanNumber(paymentComponentData.getString("ibanNumber"));
                        dm.setSepaOwnerName(paymentComponentData.getString("ownerName"));
                    }
                    


                    paymentsRequest.setPaymentMethod(dm);
                    
                    break;

                case "wechatpayQR":

                    dm.setType("wechatpayQR");


                    paymentsRequest.setPaymentMethod(dm);

                    break;

                case "wechatpayWeb":

                    dm.setType("wechatpayWeb");

                    paymentsRequest.setPaymentMethod(dm);

                    break;
                    
                case "klarna_account":
                case "klarna_paynow":
                case "klarna":

                    dm.setType(paymentMethodType);

                    paymentsRequest.setPaymentMethod(dm);
                    
                    paymentsRequest.setShopperLocale("en_US");
                    
                    paymentsRequest.setCountryCode(request.getParameter("countryCode"));
                    
                    paymentsRequest.setShopperEmail("simonhopper@test.adyen.com");
                    
//                    Name shopperName = new Name();
//                    shopperName.setFirstName("Simon");
//                    shopperName.setLastName("Hopper");
//                    shopperName.setGender(GenderEnum.MALE);
//                    paymentsRequest.setShopperName(shopperName);
                    
//                    Address billingAddress = new Address();
//                    billingAddress.setCity("Berlin");
//                    billingAddress.setCountry("DE");
//                    billingAddress.setPostalCode("10117");
//                    billingAddress.setStreet("Friedrichstraße");
//                    billingAddress.setHouseNumberOrName("63");
//                    paymentsRequest.setBillingAddress(billingAddress);
                    
                    
                    LineItem lineItem = new LineItem();
                    lineItem.setAmountIncludingTax(Long.valueOf(
    						request.getParameter("value")
    						)
    				);
                    lineItem.setAmountExcludingTax(Long.valueOf(
    						request.getParameter("value")
    						)-100L);
                    lineItem.setQuantity(1L);
                    lineItem.setDescription("Stuff");
                    lineItem.setId("Item1");
                    lineItem.setTaxAmount(100L);
                    lineItem.setTaxCategory(TaxCategoryEnum.HIGH);
                    paymentsRequest.addLineItemsItem(lineItem);
                    
                    
//                    HashMap<String, String> klarnaAdditionalData = new HashMap<>();
//                    klarnaAdditionalData.put("openinvoicedata.merchantData", "eyJjdXN0b21lcl9hY");
//                    paymentsRequest.setAdditionalData(klarnaAdditionalData);
                    

                    break;
                    
                case "directEbanking":
                	dm.setType(paymentMethodType);

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
            
            //NOT RECOMMENDED FOR PRODUCTION LEVEL
            paymentsRequest.setRedirectFromIssuerMethod("GET");
            
            paymentsRequest.setChannel(
            		PaymentsRequest.ChannelEnum.fromValue(
            				request.getParameter("channel")
            				)
            		);
            
            System.out.println("\n\nPayment Request:\n"+
            		new JSONObject(
            				new Gson().toJson(
            						paymentsRequest
            						)
            				).toString(4)
            		);
            
            PaymentsResponse paymentsResponse = checkout.payments(paymentsRequest);
            
            


            Gson gson = new Gson();
            String json = gson.toJson(paymentsResponse);
            
            System.out.println("\n\nRaw Payment Response:\n"+json);
            

            JSONObject responseJson = new JSONObject(json);
            
            	if(paymentsResponse.getAction() != null) {
            		JSONObject responseAction = responseJson.getJSONObject("action");
            		
            		//If method is POST, add the data JSON object from the root level response to the action JSON Object
            		if(responseAction.getString("method").equalsIgnoreCase("POST")){
            			responseAction.put("data", responseJson.getJSONObject("redirect").getJSONObject("data"));
            		}
            		
            		//Add payment data from the root level response to the action JSON object
            		responseAction.put("paymentData", paymentsResponse.getPaymentData());
            		
            		responseJson.remove("action");
            		
            		responseJson.put("action", responseAction);
            	}
            
            System.out.println("\n\nPayment Response:\n"+responseJson.toString(4));
            
            
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
        	System.out.println("\n\n"+e.toString());
        	out.println(e.toString());
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
