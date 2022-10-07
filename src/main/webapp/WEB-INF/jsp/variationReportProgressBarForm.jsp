<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<meta http-equiv="Refresh" content="2">
<title> Generating Reports  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<script type="text/javascript" src="scripts/jquery-1.4.1.min.js"></script>
<script type="text/javascript" src="scripts/ePMAjax.js"></script>

<!--<script type="text/javascript">


	var timerInterval = null;
   
	timerInterval  = window.setInterval(function (){
	
	var n = getCounterParameters();
	//alert('Value from Ajax = '+n);
	var max = document.getElementById('maxRecord').innerHTML;

	if(n == -1 || n == max){
		clearTimer(timerInterval);
		alert('Timer Cleared Successfully...');
	}else{
		document.getElementById('counter').innerHTML = n;
	}
	},1500);

	
		

// 
</script>
<script type="text/javascript">

function clearTimer(myInterval) {
	clearInterval(myInterval);
}
//
</script>
-->
<script type="text/javascript">
		var payPeriod = '<c:out value="${progressBean.payPeriod}"/>';
		
		jQuery(document).ready(function(){
			var delayInMillSec = 500;
			var win;
			(function checkServerForReportGenUpdate() {
				
				setTimeout(function(){
						jQuery.ajax({
								type: "GET",
								dataType: "text",//return data type from server can be json, html, text
								url: "variationReportStatus.do?ajaxVarReportUpdate=true",
								success: function(retVal) {
									if (retVal) {
										if(retVal == 'varReportGenNonExistent'){
											//redirect to the
											location.href = '<c:url value="/payDifference.do"/>';
										}
										else if (retVal == 'varReportGenComplete') {
											//download the file in a new window
											win = window.open('<c:url value="/variationReportStatus.do"/>');
											//redirect to the page you want after downloading
											location.href = '<c:url value="/payDifference.do?sDate='+payPeriod+'"/>';
										}
										else if (retVal == 'monthlyVariation') {
											//download the file in a new window
											//win = window.open('<c:url value="/variationReportStatus.do"/>');
											//redirect to the page you want after downloading
											location.href = '<c:url value="/monthlyVariation.do?sDate='+payPeriod+'"/>';
										}
										
										else {
											jQuery("#counter").text(retVal);
										}
									}
								},
								complete: checkServerForReportGenUpdate
						});	
				}, delayInMillSec);
			})();
		});
	</script>
	</head>


<body class="main">
    <form:form modelAttribute="progressBean">
	<table class="main" width="70%" border="1" bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="80%" border="0" cellspacing="0" cellpadding="0" align="center">
					
					
						<tr>
								<td align="right">&nbsp;</td>
								<td>&nbsp;</td>
							
						</tr>
						<tr>
							<td align="right">&nbsp;</td>
								<td>&nbsp;</td>
							
						</tr>
						<tr>
							<td align="center"><b><font color="red">Generating <c:out value="${progressBean.currentReport}"/>.....please wait!</font></b></td>
						</tr>
						<tr>
							<td align="center">processed&nbsp;&nbsp;<b><font color="blue"> <b id="counter"><c:out value="${progressBean.currentCount}"/></b> of <b id="maxRecord"><c:out value="${progressBean.totalElements}"/></b></font></b>&nbsp;&nbsp;reports</td>
						</tr>
						<tr>
							<td align="center" valign="top"><img src="images/progressbar.png"></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
						</tr>
						<tr>
							<td align="center" valign="top"><input type="image" name="_cancel" value="cancel" alt="Cancel" src="images/cancel_h.png"></td>
						</tr>
						
						<tr>
							<td>&nbsp;</td>
						</tr>
						<tr>
							
							<tr>
							<td align="right">&nbsp;</td>
								<td>&nbsp;</td>
						</tr>						
						
						<tr>
							<td></td>
						</tr>
					
					
					
				</table>
			  </td>
			  </tr>
				<tr>
				
					<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
				</tr>
		
	</table>
	</form:form>
</body>
</html>