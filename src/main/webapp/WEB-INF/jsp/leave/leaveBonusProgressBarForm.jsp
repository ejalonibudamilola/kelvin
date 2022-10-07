<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<meta http-equiv="Refresh" content="3">
<title> Generating Leave Bonus  </title>
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
--></head>


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
							<td align="center"><b><font color="red">Generating Leave Bonus.....please wait!</font></b></td>
						</tr>
						<tr>
							<td align="center">processed&nbsp;&nbsp;<b><font color="blue"> <b id="counter"><c:out value="${progressBean.currentCount}"/></b> of <b id="maxRecord"><c:out value="${progressBean.totalElements}"/></b></font></b>&nbsp;&nbsp;records</td>
						</tr>
						<tr>
							<td align="center" valign="top"><img src="images/progressbar.png"></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
						</tr>
						<tr>
							<td align="center" valign="top"><input type="image" name="_cancel" value="cancel" title="Cancel" src="images/cancel_h.png"></td>
						</tr>
						<p/>
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
					<p/>
					
					
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