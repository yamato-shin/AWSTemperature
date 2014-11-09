package servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;

import data.DynamoDbAccess;

/**
 * Servlet implementation class PutTempServlet
 */
public class AWSPutTempServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AWSPutTempServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			// Get POST parameters
			String tmp		= request.getParameter("temperature");
			String place	= request.getParameter("place");
			
			// Check if the format of temperature is valid
			try {
				Double.parseDouble(tmp);
			} catch (NumberFormatException nfe) {
				System.out.println("Requested temperature is invalid. [" + tmp + "]");
				return;
			}

			// Get AmazonDynamoDB client
			AmazonDynamoDBClient client = DynamoDbAccess.getClient();

			// Put data to AmazonDynamoDB
			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();

			TimeZone tz = TimeZone.getTimeZone("Asia/Tokyo");
			SimpleDateFormat fmtKey = new SimpleDateFormat("yyyyMMddHHmm");
			fmtKey.setTimeZone(tz);
			SimpleDateFormat fmtValue = new SimpleDateFormat("yyyy/MM/dd HH:mm");
			fmtValue.setTimeZone(tz);
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(tz);

			item.put("placeKey", new AttributeValue().withS(place));
			item.put("timeKey", new AttributeValue().withS(fmtKey.format(cal.getTime())));
			item.put("place", new AttributeValue().withS(place));
			item.put("time", new AttributeValue().withS(fmtValue.format(cal.getTime())));
			item.put("temperature", new AttributeValue().withS(tmp));

			PutItemRequest req = new PutItemRequest().withTableName("temperature").withItem(item);
			client.putItem(req);

		} catch(Exception e) {
			System.err.println(e.getStackTrace());
		}
	}
}
