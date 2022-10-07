<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Single Loan Detail </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" type="image/png" href="<c:url value="/images/coatOfArms.png"/>">
<link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">

<script type="text/javascript">
<!--
function popup(url,windowname) 
{
 var width  = 1000;
 var height = 800;
 var left   = (screen.width  - width)/2;
 var top    = (screen.height - height)/2;
 var params = 'width='+width+', height='+height;
 params += ', top='+top+', left='+left;
 params += ', directories=no';
 params += ', location=no';
 params += ', menubar=no';
 params += ', resizable=yes';
 params += ', scrollbars=yes';
 params += ', status=no';
 params += ', toolbar=no';
 newwin=window.open(url,windowname, params);
 if (window.focus) {newwin.focus()}
 return false;
}
// -->
</script>
</head>

<body class="main">
<script type="text/javascript" src="scripts/jacs.js"></script>
<form:form modelAttribute="garnishmentDetails">
	<table class="main" width="70%" border="1"  bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>

		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
	<tr>
		<td colspan="2">
		<div class="title">
			<c:out value="${garnishmentDetails.currentDeduction}"/> Loan deduction
			For <c:out value="${garnishmentDetails.monthAndYearStr}"/></div>
		</td>
	</tr>
	<tr>
		<td valign="top" class="mainBody" id="mainBody">
		
			<!--<table>
				<tr>
					<td>
						<form:select path="subLinkSelect">
						<form:option value="paystubsReportForm">Payroll Summary</form:option>
						<form:option value="employeeDetailsReport">Employee Details</form:option>
						<form:option value="preLastPaycheckForm">Last Paycheck</form:option>
						</form:select> 
					</td>
					<td>&nbsp;</td>
					<td>
					<input type="image" name="_go" value="go" title="Go" class="" src="images/go_h.png" >
				</tr>
			</table>
		
		--> <!--<span class="reportTopPrintLink">
		<a href="${appContext}/garnishmentDetailsExcel.do?did=${garnishmentDetails.deductionId}&sDate=${garnishmentDetails.fromDateStr}&eDate=${garnishmentDetails.toDateStr}&pid=${garnishmentDetails.id}">
		View in Excel </a><br /></span>  -->

		<table>
        	<tr align="left">
                     <td class="activeTH" colspan="2">Filter By</td>
        	</tr>
        	<tr>
                  <td class="activeTD">
                       Pay Period :&nbsp;
                  </td>
                  <td class="activeTD">
                        Month&nbsp;
                       <form:select path="runMonth">
                          <form:option value="-1">Select Month</form:option>
                       	  <c:forEach items="${monthList}" var="mList">
                       		 <form:option value="${mList.id}">${mList.name}</form:option>
                       	  </c:forEach>
                       </form:select>
                        Year&nbsp
                        <form:select path="runYear">
                           <form:option value="0">Select Year</form:option>
                           <c:forEach items="${yearList}" var="yList">
                        		<form:option value="${yList.id}">${yList.name}</form:option>
                           </c:forEach>
                        </form:select>
        		  </td>
            </tr>
            <tr>
               <td class="activeTD">
                  	Loan Type :&nbsp;
               </td>
               <td class="activeTD">
                  <form:select path="deductionId">
                     <form:option value="0">All Garnishments</form:option>
                  	 <c:forEach items="${garnishmentList}" var="garnishType">
                  		<form:option value="${garnishType.id}" title="${garnishType.name}">${garnishType.description}</form:option>
                  	 </c:forEach>
                  </form:select>
               </td>
            </tr>
        </table>
		
		<table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
			<!--<tr>
				<td class="reportFormControls">
				  <span class="optional"> Month </span>
				 <form:select path="runMonth">
                               <form:option value="-1">Select Month</form:option>
						       <c:forEach items="${monthList}" var="mList">
						         <form:option value="${mList.id}">${mList.name}</form:option>
						      </c:forEach>
						      </form:select>
                              &nbsp;  
                 <form:select path="runYear">
                               <form:option value="0">Select Year</form:option>
						       <c:forEach items="${yearList}" var="yList">
						         <form:option value="${yList.id}">${yList.name}</form:option>
						      </c:forEach>
						      </form:select>
                              &nbsp;  	Loan: <form:select path="deductionId">
                               <form:option value="0">All Garnishments</form:option>
						       <c:forEach items="${garnishmentList}" var="garnishType">
						      <form:option value="${garnishType.id}" title="${garnishType.name}">${garnishType.description}</form:option>
						      </c:forEach>
						      </form:select>
                              &nbsp; 
                 </td>
			</tr>-->
			<tr>
                         <td class="buttonRow" align="right">
                             <input type="image" name="_updateReport" value="updateReport" title="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                            <br/>
                         </td>
                     </tr>
					<tr>
						<td class="reportFormControlsSpacing"><br/></td>
					</tr>
					<tr>
						<td class="reportFormControlsSpacing"><br/></td>
					</tr>
			<tr>
				<td>
                    <table id="garnReport" class="display table" cellspacing="0" cellpadding="0">
                        <thead>
                             <tr>
                                <th><c:out value="${roleBean.staffTitle}"/> ID </th>
                                <th><c:out value="${roleBean.staffTypeName}"/> Name</th>
                                <th><c:out value="${roleBean.mdaTitle}"/></th>
                                <th align="right">Amount Withheld</th>
                                <th align="right">Loan Balance</th>
                             </tr>
                        </thead>
                        <tbody>
                           <c:forEach items="${garnishmentDetails.deductionMiniBean}" var="dedMiniBean">
                                <tr class="${dedMiniBean.displayStyle}">
                                    <td><a href='${appContext}/viewGarnishmentHistory.do?eid=${dedMiniBean.id}&garnId=${dedMiniBean.deductionId}' target="_blank" onclick="popupWindow(this.href,'${dedMiniBean.name}'); return false;"><c:out value="${dedMiniBean.employeeId}"/></a></td>
                                    <!--<td><a onClick="showHistory(${dedMiniBean.id}, ${dedMiniBean.deductionId})" href="#"><c:out value="${dedMiniBean.employeeId}"/></a></td>-->
                                    <td><c:out value="${dedMiniBean.name}"/></td>
                                    <td><c:out value="${dedMiniBean.mdaName}"/></td>
                                    <td><c:out value="${dedMiniBean.amountStr}"/></td>
                                    <td><c:out value="${dedMiniBean.balanceAmountStr}"/></td>
					            </tr>
                           </c:forEach>
                        </tbody>
                    </table>
				</td>
			</tr>
			<tr>
               <td><b>No. of <c:out value="${roleBean.staffTypeName}"/> : <c:out value="${garnishmentDetails.noOfEmployees}"/></b></td>
            </tr>
			<tr>
                <td nowrap>
                   <b>Total Amount Deducted : <font color="green"><c:out value="${garnishmentDetails.totalStr}"></c:out></font></b><br>
                   <b>Total Balance : <font color="green"><c:out value="${garnishmentDetails.totalBalanceStr}"></c:out></font></b>
                </td>
            </tr>
 			<tr>
                         <td class="buttonRow" align="right">
                          
                         <input type="image" name="_cancel" value="cancel" title="Cancel" src="images/close.png">
				
                         </td>
            </tr>
		</table>
		<table>
			<tr>
				<td class="activeTD"><form:radiobutton path="reportType" value="1"  onclick="if (this.checked) { document.getElementById('excelReportLink').style.display = 'none';document.getElementById('pdfReportLink').style.display = '';}"/>PDF Report Links <form:radiobutton path="reportType" value="2" onclick="if (this.checked) { document.getElementById('excelReportLink').style.display = '';document.getElementById('pdfReportLink').style.display = 'none';}"/> Excel Report Links</td>
			</tr>
		</table>
		
		<div id="excelReportLink" class="reportBottomPrintLink" style='display:none'>
			<a href="${appContext}/garnishmentDetailsExcel.do?did=${garnishmentDetails.deductionId}&rm=${garnishmentDetails.runMonth}&ry=${garnishmentDetails.runYear}&pid=${garnishmentDetails.id}&rt=1">
			View in Excel </a><span class="tabseparator"> <!-- |</span><a href="${appContext}/singleLoanByMdaExcel.do?did=${garnishmentDetails.deductionId}&rm=${garnishmentDetails.runMonth}&ry=${garnishmentDetails.runYear}">
		    Loan By MDA </a><br />			 -->
		</div>
		
		<div id="pdfReportLink" class="reportBottomPrintLink" style='display:none'>
			<a href="${appContext}/garnishmentDetailsExcel.do?did=${garnishmentDetails.deductionId}&rm=${garnishmentDetails.runMonth}&ry=${garnishmentDetails.runYear}&pid=${garnishmentDetails.id}&rt=2">
		    View this Loan in PDF&copy; </a><span class="tabseparator"></span><br />
		</div>
		
		<br>
		<br />
		</td>
	</tr>
	<div id="result1"></div>
	</table>
	</td>
	</tr>
	<tr><%@ include file="/WEB-INF/jsp/footerFile.jsp" %></tr>
	</table>
	
	</form:form>
	<script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
    <script>
               $(function() {
                  $("#garnReport").DataTable({
                     "order" : [ [ 0, "desc" ] ],
                        "columnDefs":[
                        {"targets": [0], "orderable" : false}
                     ]
                  });
               });
               $(".reportLink").click(function(e){
                  $('#reportModal').modal({
                     backdrop: 'static',
                     keyboard: false
                  })
               });

               function showHistory(eid, garnId){
                   console.log("I got here and data is  "+eid+" and "+garnId);
                   var url="${appContext}/viewGarnishmentHistory.do";
                    console.log("url is "+url);
                    $.ajax({
                         type: "GET",
                         url: url,
                         data: {
                            eid: eid,
                            garnId: garnId
                         },
                         success: function (response) {
                            $('#result1').html(response);
                            $('#garnModal').modal('show');
                         },
                         error: function (e) {
                            alert('Error: ' + e);
                         }
                    });
               };
    </script>
</body>

</html>
