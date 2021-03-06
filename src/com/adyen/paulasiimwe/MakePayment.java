package com.adyen.paulasiimwe;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.adyen.model.*;
import org.json.JSONException;
import org.json.JSONObject;
import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.Name.GenderEnum;
import com.adyen.model.checkout.DefaultPaymentMethodDetails;
import com.adyen.model.checkout.LineItem;
import com.adyen.model.checkout.LineItem.TaxCategoryEnum;
import com.adyen.model.checkout.PaymentsRequest;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.service.Checkout;
import com.google.gson.Gson;

import static com.adyen.paulasiimwe.GetPaymentMethods.API;
import static com.adyen.paulasiimwe.GetPaymentMethods.liveAPI;

/**
 * Servlet implementation class MakePayment
 */
@WebServlet("/MakePayment")
public class MakePayment extends HttpServlet {
	

	Client client;
	Checkout checkout;
	Boolean marketPay = false;
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MakePayment() {
        super();
        // TODO Auto-generated constructor stub

		if(GetPaymentMethods.live){
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
		
		
		String reference = generateString();
        paymentsRequest.setReference("M"+reference);
		
		if(marketPay) {
			SplitAmount subMerchantSplitAmount = new SplitAmount();
			subMerchantSplitAmount.setCurrency(amount.getCurrency());
			subMerchantSplitAmount.setValue((long) ((amount.getValue())*0.9));
			
			Split subMerchantSplit = new Split();
			subMerchantSplit.setAmount(subMerchantSplitAmount);
			subMerchantSplit.setAccount("8815756263128205");//Account Code for TestAccountHolder01
			subMerchantSplit.setDescription("Porcelain Doll: Eliza (20cm)");
			subMerchantSplit.setType(Split.TypeEnum.MARKETPLACE);
			subMerchantSplit.setReference(reference+"_sm");
			
			
			SplitAmount marketPlaceSplitAmount = new SplitAmount();
			marketPlaceSplitAmount.setCurrency(amount.getCurrency());
			marketPlaceSplitAmount.setValue((
					amount.getValue()-((long) ((amount.getValue())*0.9))
					));//Remainder after deducting 90% of amount
			
			Split marketPlaceSplit = new Split();
			marketPlaceSplit.setAmount(marketPlaceSplitAmount);
			marketPlaceSplit.setType(Split.TypeEnum.COMMISSION);
			marketPlaceSplit.setReference(reference+"_mp");
			
			
			
			List<Split> splits = new ArrayList<Split>();
			splits.add(subMerchantSplit);
			splits.add(marketPlaceSplit);
			paymentsRequest.setSplits(splits);
		}
		
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
			
			
			Name shopperName = new Name();
          shopperName.setFirstName("Paul");
          shopperName.setLastName("Asiimwe");
          shopperName.setGender(GenderEnum.MALE);
          paymentsRequest.setShopperName(shopperName);
          
//          Address billingAddress = new Address();
//          billingAddress.setCity("Stockholm");
//          billingAddress.setCountry("SE");
//          billingAddress.setPostalCode("111 22");
//          billingAddress.setStreet("Kungsbron");
//          billingAddress.setHouseNumberOrName("16 12 trp");
//          paymentsRequest.setBillingAddress(billingAddress);
			
//          Address deliveryAddress = new Address();
//          deliveryAddress.setCity("UGINE");
//          deliveryAddress.setCountry("FR");
//          deliveryAddress.setPostalCode("73400");
//          deliveryAddress.setStreet("Rue du Centenaire");
//          deliveryAddress.setHouseNumberOrName("461");
//          paymentsRequest.setDeliveryAddress(deliveryAddress);

			Date birthDate = new Date();
            birthDate.setDate(30);
            birthDate.setMonth(7);
            birthDate.setYear(85);
            paymentsRequest.setDateOfBirth(birthDate);
            
            paymentsRequest.setShopperLocale("en_US");
            
            paymentsRequest.setCountryCode(request.getParameter("countryCode"));
            
            paymentsRequest.setShopperEmail("simonhopper@test.adyen.com");


            
            paymentsRequest.setTelephoneNumber("0775888502");
            

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


                    paymentsRequest.addEncryptedCardData(encryptedCardNumber,encryptedExpiryMonth, encryptedExpiryYear, encryptedSecurityCode
                    		, "JOHN SMITH"
                    		);

                    paymentsRequest.setShopperIP("192.0.2.1");
                    
                    HashMap<String, String> additionalData = new HashMap<>();
                    additionalData.put("allow3DS2", "true");

                    //paymentsRequest.setThreeDSAuthenticationOnly(true);
                    
                    paymentsRequest.setAdditionalData(additionalData);

                    //paymentsRequest.setRecurringProcessingModel(PaymentsRequest.RecurringProcessingModelEnum.CARD_ON_FILE);
                    
                    if(request.getParameter("channel").equalsIgnoreCase("web")){
                    	paymentsRequest.setRedirectFromIssuerMethod("GET");
                    	paymentsRequest.setOrigin("http://localhost:8080/AdyenServlet/dropin.html");
                    	paymentsRequest.setBrowserInfo(
                                new BrowserInfo()
                                        .acceptHeader("\"Mozilla\\/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit\\/537.36 (KHTML, like Gecko) Chrome\\/70.0.3538.110 Safari\\/537.36\"")
                                        .userAgent("\"text\\/html,application\\/xhtml+xml,application\\/xml;q=0.9,image\\/webp,image\\/apng,*\\/*;q=0.8\"")
                                        .language("en")
                                        .javaEnabled(false)
                                        .colorDepth(24)
                                        .screenHeight(723)
                                        .screenWidth(1536)
                                        .timeZoneOffset(0)
                        );
                    }else {
                    	paymentsRequest.setBrowserInfo(
                                new BrowserInfo()
                                        .acceptHeader("\"Mozilla\\/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit\\/537.36 (KHTML, like Gecko) Chrome\\/70.0.3538.110 Safari\\/537.36\"")
                                        .userAgent("\"text\\/html,application\\/xhtml+xml,application\\/xml;q=0.9,image\\/webp,image\\/apng,*\\/*;q=0.8\"")
                                     );
                    }

                    
                    
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
                case "trustly":
                case "bcmc_mobile_app":
                case "bcmc_mobile_QR":
                case "bcmc_mobile":
				case "cellulant":
                
                	dm.setType(paymentMethodType);

                    paymentsRequest.setPaymentMethod(dm);
                	break;
                	
                	
                case "facilypay_3x":
                case "facilypay_4x":
                	
                	dm.setType(paymentMethodType);
                    
                    paymentsRequest.setShopperReference("simonhopper@test.adyen.com");
                    
                    
                    LineItem lineItem1 = new LineItem();
                    lineItem1.setAmountIncludingTax(Long.valueOf(
    						request.getParameter("value")
    						)
    				);
                    lineItem1.setAmountIncludingTax(Long.valueOf(
    						request.getParameter("value")
    						));
                    lineItem1.setAmountExcludingTax(Long.valueOf(
    						request.getParameter("value")
    						));
                    lineItem1.setQuantity(1L);
                    lineItem1.setDescription("Stuff");
                    lineItem1.setId("Item1");
                    lineItem1.setTaxAmount(0L);
                    lineItem1.setTaxCategory(TaxCategoryEnum.NONE);
                    paymentsRequest.addLineItemsItem(lineItem1);
                    
                    
                    HashMap<String, String> oneyAdditionalData = new HashMap<>();
                    oneyAdditionalData.put("merchant_pays", "true");
                    paymentsRequest.setAdditionalData(oneyAdditionalData);
                	break;
                	
                
                case "paywithgoogle":
                	dm.setType(paymentMethodType);
                	try {
                		dm.setGooglepayToken(paymentComponentData.getString("paywithgoogle.token"));
                	}catch (JSONException e) {
                		dm.setGooglepayToken(paymentComponentData.getString("token"));
                	}
                	
                	
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
            		if(responseAction.has("method")) {
            			if(responseAction.getString("method").equalsIgnoreCase("POST")){
                			responseAction.put("data", responseJson.getJSONObject("redirect").getJSONObject("data"));
                		}
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
