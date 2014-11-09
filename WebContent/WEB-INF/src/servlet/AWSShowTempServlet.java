package servlet;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.ComUtil;

/**
 * Servlet implementation class ShowTempServlet
 */
public class AWSShowTempServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AWSShowTempServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// get parameters
		String periodStr	= request.getParameter("period");
		String inputYear	= request.getParameter("year");
		String inputMonth	= request.getParameter("month");
		String inputDate	= request.getParameter("date");
		String inputHour	= request.getParameter("hour");
		String inputMinute	= request.getParameter("minute");
		String nextYear		= request.getParameter("nextYear");
		String nextMonth	= request.getParameter("nextMonth");
		String nextDate		= request.getParameter("nextDate");
		String nextHour		= request.getParameter("nextHour");
		String nextMinute	= request.getParameter("nextMinute");
		String prevYear		= request.getParameter("prevYear");
		String prevMonth	= request.getParameter("prevMonth");
		String prevDate		= request.getParameter("prevDate");
		String prevHour		= request.getParameter("prevHour");
		String prevMinute	= request.getParameter("prevMinute");
		String curYear		= inputYear;
		String curMonth		= inputMonth;
		String curDate		= inputDate;
		String curHour		= inputHour;
		String curMinute	= inputMinute;
		String imageURL		= request.getParameter("imageURL");

		TimeZone tz = TimeZone.getTimeZone("Asia/Tokyo");		
		SimpleDateFormat fmtURL = new SimpleDateFormat("yyyyMMddHHmm");
		fmtURL.setTimeZone(tz);
		int period = 0;
		if(ComUtil.isBlank(periodStr)) {
			period = 30;
		}
		else {
			period = Integer.parseInt(periodStr);
		}
		
		// error message
		String errorMsg = "";

		
		// If nothing is specified as date/time, we consider this request as a request of showing the latest data.
		if(ComUtil.isBlank(inputYear) && ComUtil.isBlank(inputMonth) && ComUtil.isBlank(inputDate) && ComUtil.isBlank(inputHour) && ComUtil.isBlank(inputMinute)) {
			// next time: set today
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(tz);
			nextYear	= String.valueOf(cal.get(Calendar.YEAR));
			nextMonth	= String.valueOf(cal.get(Calendar.MONTH) + 1);
			nextDate	= String.valueOf(cal.get(Calendar.DATE));
			nextHour	= String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
			nextMinute	= String.valueOf(cal.get(Calendar.MINUTE));
			// current time
			cal.setTimeInMillis(cal.getTimeInMillis() - (long)(period * 60 * 1000));
			curYear		= "";
			curMonth	= "";
			curDate		= "";
			curHour		= "";
			curMinute	= "";
			String curTimeURL = fmtURL.format(cal.getTime());
			// prev time
			cal.setTimeInMillis(cal.getTimeInMillis() - (long)(period * 60 * 1000));
			prevYear	= String.valueOf(cal.get(Calendar.YEAR));
			prevMonth	= String.valueOf(cal.get(Calendar.MONTH) + 1);
			prevDate	= String.valueOf(cal.get(Calendar.DATE));
			prevHour	= String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
			prevMinute	= String.valueOf(cal.get(Calendar.MINUTE));
			// input time
			inputYear	= "";
			inputMonth	= "";
			inputDate	= "";
			inputHour	= "";
			inputMinute	= "";
			
			// image URL parameters
			imageURL	= "?place=windsports&timeStart=" + curTimeURL + "&timePeriod=" + String.valueOf(period);
		}
		else {
			// check if the format of input time is correct
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy/M/d H:m");
			try {
				fmt.parse(inputYear + "/" + inputMonth + "/" + inputDate + " " + inputHour + ":" + inputMinute);
				
				// next time
				Calendar cal = Calendar.getInstance();
				cal.setTimeZone(tz);
				cal.set(Calendar.YEAR	,Integer.parseInt(inputYear));
				cal.set(Calendar.MONTH	,Integer.parseInt(inputMonth) -1);
				cal.set(Calendar.DATE	,Integer.parseInt(inputDate));
				cal.set(Calendar.HOUR_OF_DAY	,Integer.parseInt(inputHour));
				cal.set(Calendar.MINUTE	,Integer.parseInt(inputMinute));
				cal.setTimeInMillis(cal.getTimeInMillis() + (long)(period * 60 * 1000));
				nextYear	= String.valueOf(cal.get(Calendar.YEAR));
				nextMonth	= String.valueOf(cal.get(Calendar.MONTH) + 1);
				nextDate	= String.valueOf(cal.get(Calendar.DATE));
				nextHour	= String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
				nextMinute	= String.valueOf(cal.get(Calendar.MINUTE));
				// current time
				cal.setTimeInMillis(cal.getTimeInMillis() - (long)(period * 60 * 1000));
				curYear		= String.valueOf(cal.get(Calendar.YEAR));
				curMonth	= String.valueOf(cal.get(Calendar.MONTH) + 1);
				curDate		= String.valueOf(cal.get(Calendar.DATE));
				curHour		= String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
				curMinute	= String.valueOf(cal.get(Calendar.MINUTE));
				String curTimeURL = fmtURL.format(cal.getTime());
				// prev time
				cal.setTimeInMillis(cal.getTimeInMillis() - (long)(period * 60 * 1000));
				prevYear	= String.valueOf(cal.get(Calendar.YEAR));
				prevMonth	= String.valueOf(cal.get(Calendar.MONTH) + 1);
				prevDate	= String.valueOf(cal.get(Calendar.DATE));
				prevHour	= String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
				prevMinute	= String.valueOf(cal.get(Calendar.MINUTE));
				// input time
				inputYear	= curYear;
				inputMonth	= curMonth;
				inputDate	= curDate;
				inputHour	= curHour;
				inputMinute	= curMinute;
				// image URL parameters
				imageURL	= "?place=windsports&timeStart=" + curTimeURL + "&timePeriod=" + String.valueOf(period);
				
			} catch(ParseException e) {
				errorMsg += "“ú•t‚ÌŽw’è‚ªŠÔˆá‚Á‚Ä‚¢‚Ü‚·\n";
				curYear		= request.getParameter("backupYear");
				curMonth	= request.getParameter("backupMonth");
				curDate		= request.getParameter("backupDate");
				curHour		= request.getParameter("backupHour");
				curMinute	= request.getParameter("backupMinute");
			}
		}
		
		// set parameters to request for jsp
		request.setAttribute("errorMsg"		,errorMsg);
		request.setAttribute("period"		,String.valueOf(period));
		request.setAttribute("year"			,curYear);
		request.setAttribute("month"		,curMonth);
		request.setAttribute("date"			,curDate);
		request.setAttribute("hour"			,curHour);
		request.setAttribute("minute"		,curMinute);
		request.setAttribute("nextYear"		,nextYear);
		request.setAttribute("nextMonth"	,nextMonth);
		request.setAttribute("nextDate"		,nextDate);
		request.setAttribute("nextHour"		,nextHour);
		request.setAttribute("nextMinute"	,nextMinute);
		request.setAttribute("prevYear"		,prevYear);
		request.setAttribute("prevMonth"	,prevMonth);
		request.setAttribute("prevDate"		,prevDate);
		request.setAttribute("prevHour"		,prevHour);
		request.setAttribute("prevMinute"	,prevMinute);
		request.setAttribute("inputYear"	,inputYear);
		request.setAttribute("inputMonth"	,inputMonth);
		request.setAttribute("inputDate"	,inputDate);
		request.setAttribute("inputHour"	,inputHour);
		request.setAttribute("inputMinute"	,inputMinute);
		request.setAttribute("imageURL"		,imageURL);
		
		// forward to jsp
		request.getRequestDispatcher("pages/temperature.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
