<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	// get parameters from servlet
	// current Date/Time
	String period		= (String)request.getAttribute("period");
	String curYear		= (String)request.getAttribute("year");
	String curMonth		= (String)request.getAttribute("month");
	String curDate		= (String)request.getAttribute("date");
	String curHour		= (String)request.getAttribute("hour");
	String curMinute	= (String)request.getAttribute("minute");
	// next Date/Time
	String nextYear		= (String)request.getAttribute("nextYear");
	String nextMonth	= (String)request.getAttribute("nextMonth");
	String nextDate		= (String)request.getAttribute("nextDate");
	String nextHour		= (String)request.getAttribute("nextHour");
	String nextMinute	= (String)request.getAttribute("nextMinute");
	// previous Date/Time
	String prevYear		= (String)request.getAttribute("prevYear");
	String prevMonth	= (String)request.getAttribute("prevMonth");
	String prevDate		= (String)request.getAttribute("prevDate");
	String prevHour		= (String)request.getAttribute("prevHour");
	String prevMinute	= (String)request.getAttribute("prevMinute");
	// Date/Time in input area
	String inputYear	= (String)request.getAttribute("inputYear");
	String inputMonth	= (String)request.getAttribute("inputMonth");
	String inputDate	= (String)request.getAttribute("inputDate");
	String inputHour	= (String)request.getAttribute("inputHour");
	String inputMinute	= (String)request.getAttribute("inputMinute");
	
	// URL parameters of Image
	String imageURL		= (String)request.getAttribute("imageURL");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Temperature</title>
</head>
<body>
<div style="color:red;"><%=(String)request.getAttribute("errorMsg") %></div>
<div>
<form name="showLatestForm">
<input type="hidden" name="year" value="" />
<input type="hidden" name="month" value="" />
<input type="hidden" name="date" value="" />
<input type="hidden" name="hour" value="" />
<input type="hidden" name="minute" value="" />
<input type="hidden" name="ACTION" value="showLatest" />
<input type="hidden" name="period" value="<%=period %>" />
<input type="hidden" name="place" value="windsports" />
<input type="submit" name="submitLatestBtn" value="最新表示" />
</form>
</div>
<form name="chgPeiodForm" action="/AWSTemperature/show" method="post">
<div>
表示範囲
<input type="radio" name="period" value="30" id="radio:period:30" <%="30".equals(period) ? "checked" : "" %>/><label for="radio:period:30">30分</label>
<input type="radio" name="period" value="60" id="radio:period:60" <%="60".equals(period) ? "checked" : "" %>/><label for="radio:period:60">1時間</label>
<input type="radio" name="period" value="180" id="radio:period:180" <%="180".equals(period) ? "checked" : "" %>/><label for="radio:period:180">3時間</label>
<input type="radio" name="period" value="360" id="radio:period:360" <%="360".equals(period) ? "checked" : "" %>/><label for="radio:period:360">6時間</label>
<input type="radio" name="period" value="720" id="radio:period:720" <%="720".equals(period) ? "checked" : "" %>/><label for="radio:period:720">12時間</label>
<input type="hidden" name="year"	value="<%=curYear %>" />
<input type="hidden" name="month"	value="<%=curMonth %>" />
<input type="hidden" name="date"	value="<%=curDate %>" />
<input type="hidden" name="hour"	value="<%=curHour %>" />
<input type="hidden" name="minute"	value="<%=curMinute %>" />
<input type="hidden" name="place"	value="windsports" />
<input type="hidden" name="ACTION"	value="changeDispRange" />
</div>
<div>
<input type="submit" name="submitBtn" value="期間変更" />
</div>
</form>
<div>
<form name="prevPageForm" action="/AWSTemperature/show" method="post">
<input type="submit" name="submitPrevBtn" value="&lt;" />
<input type="hidden" name="year" value="<%=prevYear %>" />
<input type="hidden" name="month" value="<%=prevMonth %>" />
<input type="hidden" name="date" value="<%=prevDate %>" />
<input type="hidden" name="hour" value="<%=prevHour %>" />
<input type="hidden" name="minute" value="<%=prevMinute %>" />
<input type="hidden" name="period" value="<%=period %>" />
<input type="hidden" name="place" value="windsports" />
<input type="hidden" name="ACTION" value="showPrev" />
</form>
<form name="nextPageForm" action="/AWSTemperature/show" method="post">
<input type="submit" name="submitNextBtn" value="&gt;" />
<input type="hidden" name="year" value="<%=nextYear %>" />
<input type="hidden" name="month" value="<%=nextMonth %>" />
<input type="hidden" name="date" value="<%=nextDate %>" />
<input type="hidden" name="hour" value="<%=nextHour %>" />
<input type="hidden" name="minute" value="<%=nextMinute %>" />
<input type="hidden" name="period" value="<%=period %>" />
<input type="hidden" name="place" value="windsports" />
<input type="hidden" name="ACTION" value="showNext" />
</form>
</div>

<div>
<img src="/AWSTemperature/getimage<%=imageURL %>" />
</div>

<form name="searchForm" action="/AWSTemperature/show" method="post">
<div>
<input type="text" name="year" value="<%=inputYear %>" />年
<input type="text" name="month" value="<%=inputMonth %>" />月
<input type="text" name="date" value="<%=inputDate %>" />日
<input type="text" name="hour" value="<%=inputHour %>" />時
<input type="text" name="minute" value="<%=inputMinute %>" />分
<input type="hidden" name="period" value="<%=period %>" />
<input type="hidden" name="backupYear" value="<%=curYear %>" />
<input type="hidden" name="backupMonth" value="<%=curMonth %>" />
<input type="hidden" name="backupDate" value="<%=curDate %>" />
<input type="hidden" name="backupHour" value="<%=curHour %>" />
<input type="hidden" name="backupMinute" value="<%=curMinute %>" />
<input type="hidden" name="nextYear" value="<%=nextYear %>" />
<input type="hidden" name="nextMonth" value="<%=nextMonth %>" />
<input type="hidden" name="nextDate" value="<%=nextDate %>" />
<input type="hidden" name="nextHour" value="<%=nextHour %>" />
<input type="hidden" name="nextMinute" value="<%=nextMinute %>" />
<input type="hidden" name="prevYear" value="<%=prevYear %>" />
<input type="hidden" name="prevMonth" value="<%=prevMonth %>" />
<input type="hidden" name="prevDate" value="<%=prevDate %>" />
<input type="hidden" name="prevHour" value="<%=prevHour %>" />
<input type="hidden" name="prevMinute" value="<%=prevMinute %>" />
<input type="hidden" name="imageURL" value="<%=imageURL %>" />
<input type="hidden" name="place" value="windsports" />
<input type="hidden" name="ACTION" value="showSpecifiedDate" />
</div>
<div>
<input type="submit" name="submitBtn" value="日時変更" />
</div>
</form>

</body>
</html>