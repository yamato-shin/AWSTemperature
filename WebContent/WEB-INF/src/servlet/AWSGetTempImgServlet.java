package servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import data.DynamoDbAccess;

/**
 * Servlet implementation class showTempServlet
 * @param
 */
public class AWSGetTempImgServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public AWSGetTempImgServlet() {
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String place = request.getParameter("place");
		String timeFrom = request.getParameter("timeStart");
		int timePeriod = Integer.parseInt(request.getParameter("timePeriod"));
		
		// Search Condition
		TimeZone tz = TimeZone.getTimeZone("Asia/Tokyo");
		SimpleDateFormat fmtParam = new SimpleDateFormat("yyyyMMddHHmm");
		fmtParam.setTimeZone(tz);
		String startRow	= String.valueOf(timeFrom);
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(tz);
		cal.set(Calendar.YEAR,			Integer.parseInt(timeFrom.substring(0,"yyyy".length())));
		cal.set(Calendar.MONTH,			Integer.parseInt(timeFrom.substring("yyyy".length(),"yyyyMM".length())) -1);
		cal.set(Calendar.DATE,			Integer.parseInt(timeFrom.substring("yyyyMM".length(),"yyyyMMdd".length())));
		cal.set(Calendar.HOUR_OF_DAY,	Integer.parseInt(timeFrom.substring("yyyyMMdd".length(),"yyyyMMddHH".length())));
		cal.set(Calendar.MINUTE,		Integer.parseInt(timeFrom.substring("yyyyMMddHH".length(),"yyyyMMddHHmm".length())));
		cal.add(Calendar.MINUTE, 		timePeriod);
		String stopRow	= fmtParam.format(cal.getTime());
		
		// Get Amazon DynamoDB client
		AmazonDynamoDBClient client = DynamoDbAccess.getClient();

		// Amazon DynamoDB search
		Map<String, Condition> keyConditions = new HashMap<String, Condition>();
		
		Condition hashKeyCondition = new Condition()
			.withComparisonOperator(ComparisonOperator.EQ)
			.withAttributeValueList(new AttributeValue().withS(place));
		keyConditions.put("placeKey", hashKeyCondition);
		
		Condition rangeKeyCondition = new Condition()
			.withComparisonOperator(ComparisonOperator.BETWEEN)
			.withAttributeValueList(new AttributeValue().withS(startRow), new AttributeValue().withS(stopRow));
		keyConditions.put("timeKey", rangeKeyCondition);
		
		QueryRequest qReq = new QueryRequest()
			.withTableName("temperature")
			.withKeyConditions(keyConditions)
			.withAttributesToGet(Arrays.asList("temperature", "time"))
			.withConsistentRead(true);
		
		QueryResult qRslt = client.query(qReq);
		
		// jfreeChart initialization
		// data set of graph
		TimeSeriesCollection tsColl = new TimeSeriesCollection();
		
		// set Amazon DynamoDB data to data set
		TimeSeries ts = new TimeSeries("Temperature at Wind Sports", "Date/Time", "Temperature");
		for(Map<String, AttributeValue> record : qRslt.getItems()) {
			Double tmp = Double.parseDouble(record.get("temperature").getS());
			String time		= record.get("time").getS();
			int intYear		= Integer.parseInt(time.substring(0,"yyyy".length()));
			int intMonth	= Integer.parseInt(time.substring("yyyy/".length(), "yyyy/MM".length()));
			int intDate		= Integer.parseInt(time.substring("yyyy/MM/".length(), "yyyy/MM/dd".length()));
			int intHour		= Integer.parseInt(time.substring("yyyy/MM/dd ".length(), "yyyy/MM/dd HH".length()));
			int intMinute	= Integer.parseInt(time.substring("yyyy/MM/dd HH:".length(), "yyyy/MM/dd HH:mm".length()));
			ts.add(new Second(0, intMinute, intHour, intDate, intMonth, intYear), tmp);
		}
		tsColl.addSeries(ts);
		
		// output graph with JFreeChart
		int width = 400;
		int height = 300;
		JFreeChart tsChart = ChartFactory.createTimeSeriesChart("Temperature at Wind Sports", "Date/Time", "Temperature", tsColl);
		XYPlot plot = tsChart.getXYPlot();
		NumberAxis numAxis = (NumberAxis)plot.getRangeAxis();
		numAxis.setAutoRange(true);
		ValueAxis domAxis = plot.getDomainAxis();
		domAxis.setAutoRange(true);
		XYAreaRenderer renderer = new XYAreaRenderer(XYAreaRenderer.AREA);
		plot.setRenderer(renderer);
		
		OutputStream out = response.getOutputStream();
		response.setContentType("image/png");
		ChartUtilities.writeChartAsPNG(out, tsChart, width, height);
		
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
