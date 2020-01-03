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
		pairs.put("sessionValidity", "2020-01-10T08:31:06Z");
		pairs.put("shipBeforeDate", "2020-01-10");
		pairs.put("paymentAmount", request.getParameter("value"));
		pairs.put("currencyCode", request.getParameter("currency"));
		pairs.put("skinCode", "LArdpUlk");
		pairs.put("merchantReference", "Internetorder12345");
		
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
