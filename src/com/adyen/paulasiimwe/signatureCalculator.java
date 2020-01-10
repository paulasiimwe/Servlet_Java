package com.adyen.paulasiimwe;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.common.io.BaseEncoding;

/**
 * Servlet implementation class signatureCalculator
 */
@WebServlet("/signatureCalculator")
public class signatureCalculator extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public signatureCalculator() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		//response.setContentType("application/json");
		response.setContentType("text/html;charset=UTF-8");
		
		PrintWriter out = response.getWriter();
		
		String HMAC_KEY = "903B9FA7CF8DC9A614E2E9BCF01F6229E8A72C99AE2B60DFC7F8C6EA09AF79EC";
		Map<String, String> pairs = new HashMap<>();
        //pairs.put("shopperLocale", "en_US");
        pairs.put("merchantAccount", "PaulAsiimwe");
        pairs.put("sessionValidity", "2020-01-20T08:31:06Z");
        pairs.put("shipBeforeDate", "2020-01-20");
        pairs.put("paymentAmount", request.getParameter("value"));
        pairs.put("currencyCode", request.getParameter("currency"));
        pairs.put("skinCode", "LArdpUlk");
        pairs.put("merchantReference", "Internetorder12345");
        
//        pairs.put("shopperLocale"     , "en_GB");
//        pairs.put("brandCode"         , "");
//        pairs.put("shopperEmail"      , "test@adyen.com");
//        pairs.put("shopperReference"  , "YOUR_UNIQUE_SHOPPER_ID_IOfW3k9G2PvXFu2j");
//        pairs.put("shopper.FirstName", "Testperson-nl");
//        pairs.put("shopper.LastName", "Approved");
//        pairs.put("shopper.DateOfBirthDayOfMonth", "10");
//        pairs.put("shopper.DateOfBirthMonth", "07");
//        pairs.put("shopper.DateOfBirthYear", "1970");
//        pairs.put("shopper.Gender", "MALE");
//        pairs.put("shopper.TelephoneNumber", "0104691602");
//        pairs.put("shopper.IP", "62.128.7.69");
//        pairs.put("billingAddress.street" ,"Neherkade");
//        pairs.put("billingAddress.houseNumberOrName" , "1");
//        pairs.put("billingAddress.city" , "Gravenhage");
//        pairs.put("billingAddress.postalCode" , "2521VA");
//        pairs.put("billingAddress.stateOrProvince" , "NH");
//        pairs.put("billingAddress.country" , "NL");
//        pairs.put("airline.passenger_name" , "Santoyo/Antonio");
//        pairs.put("airline.ticket_number" , "RIMQ7U");
//        pairs.put("airline.flight_date" , "2015-02-19 00:00");
//        pairs.put("airline.customer_reference_number"  , "RIMQ7U");
//		pairs.put("airline.leg1.stop_over_code" , "LV");
//		pairs.put("airline.leg1.class_of_travel" , "T");
//		pairs.put("airline.leg1.date_of_travel" , "2019-12-20 15:55");
//		pairs.put("airline.leg1.depart_airport" , "ORY");
//		pairs.put("airline.leg1.destination_code" , "FDF");
//		pairs.put("airline.leg1.fare_base_code" , "T0L1H1");
//		pairs.put("airline.leg1.flight_number" , "8005");
//		pairs.put("brandCode" , "facilypay_4x");
		
		System.out.println("\n\n"+request.getParameter("value"));
		System.out.println(request.getParameter("currency"));
		
		SortedMap<String, String> sortedPairs = new TreeMap<>(pairs);
		
		SortedMap<String, String> escapedPairs =
		        sortedPairs.entrySet().stream()
		                .collect(Collectors.toMap(
		                        e -> e.getKey(),
		                        e -> (e.getValue() == null) ? "" : e.getValue().replace("\\", "\\\\").replace(":", "\\:"),
		                        (k, v) -> k,
		                        TreeMap::new
		                ));
		
		String signingString = Stream.concat(escapedPairs.keySet().stream(), escapedPairs.values().stream())
		        .collect(Collectors.joining(":"));


		// import from com.google.common.io.BaseEncoding;
		byte[] binaryHmacKey = BaseEncoding.base16().decode(HMAC_KEY);
		
		// Create an HMAC SHA-256 key from the raw key bytes
		SecretKeySpec signingKey = new SecretKeySpec(binaryHmacKey, "HmacSHA256");
		 
		// Get an HMAC SHA-256 Mac instance and initialize with the signing key
		Mac mac;
		try {
			mac = Mac.getInstance("HmacSHA256");
			
			try {
				mac.init(signingKey);
				
				// calculate the hmac on the binary representation of the signing string
				byte[] binaryHmac = mac.doFinal(signingString.getBytes(Charset.forName("UTF8")));
				
				//This is the guy
				String signature = Base64.getEncoder().encodeToString(binaryHmac);
				
				System.out.println(signature);
				
				out.print(signature);
				
			} catch (InvalidKeyException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (NoSuchAlgorithmException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
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
