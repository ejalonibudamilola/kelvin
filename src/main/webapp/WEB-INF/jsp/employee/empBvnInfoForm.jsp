<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<html>
<head>
<title><c:out value="${roleBean.staffTypeName}"/> BVN Information</title>
 <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
        <link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
        <link rel="stylesheet" href="styles/spinner.css" TYPE="text/css" />
</head>

<style>
   #empBvnTbl thead tr th{
      font-size:8pt !important;
   }
</style>

<body class="main">
<div class="loader"></div>
 <script type="text/JavaScript" src="scripts/jacs.js"></script>
	<form:form modelAttribute="miniBean">
	<c:set value="${miniBean}" var="dispBean" scope="request"/>
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					
			<tr>
				<td colspan="2">
					<div class="title"><c:out value="${roleBean.staffTypeName}"/> BVN Information for<br>
					<c:out value="${miniBean.name}"/>
					</div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainBody" id="mainbody">
                                  <div id="topOfPageBoxedErrorMessage" style="display:${miniBean.displayErrors}">
										<spring:hasBindErrors name="miniBean">
										<ul>
											<c:forEach var="errMsgObj" items="${errors.allErrors}">
											<li>
											<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
											</li>
											</c:forEach>
										</ul>
										</spring:hasBindErrors>
									</div>
									<table>
										<tr align="left">
                                       	 <td class="activeTH" colspan="2">Filter By</td>
				                       </tr>
				                        <tr>
                                      
                                        <td class="activeTD">
                                            Pay Period :&nbsp;                                   
                                        </td>
										<td class="activeTD">	 Month&nbsp;<form:select path="runMonth">
																<form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
																<c:forEach items="${monthList}" var="mList">
																<form:option value="${mList.id}">${mList.name}</form:option>
																</c:forEach>
																</form:select>
											&nbsp;&nbsp;
												 Year&nbsp;<form:select path="runYear">
																<form:option value="-1">&lt;&nbsp;Select&nbsp;&gt;</form:option>
																<c:forEach items="${yearList}" var="yList">
																<form:option value="${yList.id}">${yList.id}</form:option>
																</c:forEach>
																</form:select>
												                        
                                        </td>
                                       
                                    </tr>
                                     <tr>
                                      
                                        <td class="activeTD">
                                            Service Entry Date:&nbsp;                                   
                                        </td>
                                        <td class="activeTD">
                                           Between :  &nbsp;<form:input path="allowanceStartDate"/>&nbsp;<img src="images/calendar.png" id="fromDateId" width="16" height="16" border="0" title="Pick from date" onclick="JACS.show(document.getElementById('allowanceStartDate'),event);">
                                        	&nbsp;And : &nbsp;<form:input path="allowanceEndDate"/>
													<img src="images/calendar.png" id="toDateId" width="16" height="16" border="0" title="Pick to date" onclick="JACS.show(document.getElementById('allowanceEndDate'),event);">
                                        
                                        </td>
                                       
                                        
                                    </tr>
                                    <tr>
                                       <td class="activeTD">
                                            Bank Type :&nbsp;                                   
                                        </td>
                                        <td class="activeTD">
                                            <form:select path="bankTypeInd">
														<form:option value="0">&lt;All Types&gt;</form:option>
														<form:option value="1" title="Deposit Money Banks i.e.,First Bank, GT Bank et al">D.M.B</form:option>
														<form:option value="2" title="Micro-Finance Banks">M.F.B</form:option>
												</form:select>       
                                        </td>
                                    </tr>
									<tr>
                                       <td class="activeTD">
                                            <c:out value="${roleBean.mdaTitle}"/> :&nbsp;
                                        </td>
                                        <td class="activeTD">
                                            <form:select path="mdaInstId">
														<form:option value="0">&lt;Ignore&gt;</form:option>
														<c:forEach items="${mdaList}" var="mList">
														<form:option value="${mList.id}">${mList.name}</form:option>
														</c:forEach>
												</form:select>       
                                        </td>
                                    </tr>
                                    <tr>
                                       <td class="activeTD">
                                            B.V.N Status :&nbsp;                                   
                                        </td>
                                        <td class="activeTD">
                                            <form:select path="bvnStatusInd">
														<form:option value="0">&lt;All Types&gt;</form:option>
														<form:option value="1" title="${roleBean.staffTypeName} with B.V.N Data">Valid BVN Data</form:option>
														<form:option value="2" title="${roleBean.staffTypeName} with NO B.V.N Data">No BVN Data</form:option>
												</form:select>       
                                        </td>
                                    </tr> 
                                    <tr>
                                       <td class="activeTD">
                                            Employment Status :&nbsp;                                   
                                        </td>
                                        <td class="activeTD">
                                            <form:select path="statusInd">
														<form:option value="2" title="All ${roleBean.staffTypeName}s" >&lt;Ignore Status&gt;</form:option>
														<form:option value="0" title="Active ${roleBean.staffTypeName}s ONLY">Active</form:option>
														<form:option value="1" title="Inactive ${roleBean.staffTypeName}s ONLY">Terminated</form:option>
												</form:select>       
                                        </td>
                                    </tr> 
									<tr>
                                    	<td class="buttonRow" align="right">
 											<input type="image" name="_updateReport" id="updateReport" value="updateReport" title="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
                                    	</td>
                                    </tr>
			          			</table>   
                                <table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
                                    <tr>
                                        <td valign="top" class="mainBody" id="mainbody">
                               <table>
								 
					<tr>
					<td>
					<display:table name="dispBean" id="empBvnTbl" class="display table" export="false" sort="page" defaultsort="1" requestURI="${appContext}/viewEmployeeBVNInfo.do">
						<display:setProperty name="paging.banner.placement" value="bottom" />
						<display:column property="employeeId" title="${roleBean.staffTitle}"></display:column>
						<display:column property="displayName" title="${roleBean.staffTypeName} Name"></display:column>
						<display:column property="salaryInfo.levelStepStr" title="Level/Step"></display:column>
						<display:column property="mdaName" title="${roleBean.mdaTitle}"></display:column>
						<display:column property="businessName" title="Bank Name"></display:column>
						<display:column property="accountNumber" title="Account Number"></display:column>
						<display:column property="bvnNumber" title="BVN"></display:column>
					</display:table>
					
					
				</td>
			</tr>
			
			
		</table>
			<div class="reportBottomPrintLink">
				<a href="${appContext}/empBvnReport.do?rm=${miniBean.runMonth}&ry=${miniBean.runYear}">
				View in Excel </a><br />
				</div>
			</td>
		</tr>
		<tr><td>&nbsp;</td></tr>
		<tr>
              <td align="left">
              &nbsp;&nbsp;&nbsp;<input type="image" name="_cancel" value="cancel" alt="Cancel" src="images/close.png">
              </td>
        </tr>
		 </table>
		 </td>
		 </tr>
		 </table>
		 </td>
		 </tr>
		 
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
	</table>
	<div class="spin"></div>
	</form:form>
	<script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
        <script>
                    $(function() {
                       $("#empBvnTbl").DataTable({
                          "order" : [ [ 1, "asc" ] ],
                          //can also use "columns" instead of "columnDefs". "columns" take precedence over "columnDefs"
                          //also properties higher up, take precedence over those below
                          "columnDefs":[
                             {"targets": [0], "orderable" : false}
                          ]
                       });
                    });

                     $("#updateReport").click(function(e){
                         $(".spin").show();
                     });


                    window.onload = function exampleFunction() {
                        $(".loader").hide();
                    }
        </script>
</body>
</html>
