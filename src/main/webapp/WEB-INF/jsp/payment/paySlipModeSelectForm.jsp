<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Select Pay Slip Generation Mode  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
 <link rel="stylesheet" href="css/screen.css" type="text/css"/>
<script type="text/javascript" src="<c:url value="scripts/ePMAjax.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/utility_script.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.4.4.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="scripts/jquery-1.7.1.min.js"/>"></script>
</head>


<body class="main">
<form:form modelAttribute="miniBean">
<c:set value="${displayList}" var="dispBean" scope="request"/>
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">

		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					
					<tr>
						<td colspan="2">
								<div class="title"> Select Payslip Generation Mode </div>
						</td>

					</tr>
					<tr>
						<td valign="top" class="mainBody" id="mainbody">
						* = required<br/><br/>

						<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
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
				<table class="formTable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
					<tr align="left">
						<td class="activeTH">Make a selection</td>
					</tr>
					<tr>
						<td class="activeTD">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
								 <td width="5%" valign="top">Month &amp; Year</td>
								<td>
								&nbsp;<form:select path="runMonth" disabled="${displayList.listSize gt 0}">
												<form:option value="-1">&lt;Select Month&gt;</form:option>
												<c:forEach items="${monthList}" var="mList">
												<form:option value="${mList.id}">${mList.name}</form:option>
												</c:forEach>
												</form:select>&nbsp;<form:select path="runYear" disabled="${displayList.listSize gt 0}">
												<form:option value="0">&lt;Select Year&gt;</form:option>
												<c:forEach items="${yearList}" var="yList">
												<form:option value="${yList.id}">${yList.id}</form:option>
												</c:forEach>
												</form:select>
								</td>
						 
					          </tr>
					            
					            <tr>
					               
					                <td width="15%" valign="top" nowrap>Pay Slip Generation Mode*</td>
									<td width="25%" align="left">
									  <form:radiobutton path="mapId" value="1" onclick="if (this.checked) { document.getElementById('hiderow').style.display = ''; document.getElementById('hiderow3').style.display = '';document.getElementById('hiderow1').style.display = 'none';document.getElementById('hiderow2').style.display = 'none';}"/>Pay Group<br>
									  <form:radiobutton path="mapId" value="2" onclick="if (this.checked) { document.getElementById('hiderow1').style.display = '';document.getElementById('hiderow3').style.display = 'none'; document.getElementById('hiderow').style.display = 'none';document.getElementById('hiderow2').style.display = 'none';}"/><c:out value="${roleBean.mdaTitle}"/><br>
									  <form:radiobutton path="mapId" value="3" onclick="if (this.checked) { document.getElementById('hiderow2').style.display = ''; document.getElementById('hiderow3').style.display = 'none';document.getElementById('hiderow').style.display = 'none'; document.getElementById('hiderow1').style.display = 'none';}"/>By <c:out value="${roleBean.staffTypeName}"/><br>
									  
									  </td>
								 
					            </tr>
					             <tr  id="hiderow" style="${miniBean.hideRow}">
					              <td width="5%" valign="top">Pay Group</td>
					              <td align="left">
					              	<form:select path="salaryTypeId"  onchange="loadGradeLevels(this);">
										<form:option value="0">&lt;&nbsp;Select&nbsp;&gt;</form:option>
												<c:forEach items="${miniBean.payGroupList}" var="pList">
												<form:option value="${pList.id}">${pList.name}</form:option>
												</c:forEach>
										</form:select>
					                </td>
					             
					            </tr>
					            <tr  id="hiderow3" style="${miniBean.hideRow}">
					              <td width="5%" valign="top">Level &amp; Step</td>
					              <td align="left">
										
										<form:select path="fromLevel" id="grade-level-control" cssClass="branchControls">
											 <form:option value="0">&lt;From Level&gt;</form:option>
											 <c:forEach items="${fromLevelList}" var="fList">
											 <form:option value="${fList.level}">${fList.level}</form:option>
											 </c:forEach>
										</form:select>
										 <form:select path="toLevel" id="grade-to-level-control" cssClass="branchControls">
											 <form:option value="0">&lt;To Level&gt;</form:option>
											 <c:forEach items="${toLevelList}" var="tList">
											 <form:option value="${tList.level}">${tList.level}</form:option>
											 </c:forEach>
										</form:select>
					                </td>
					                  
					            </tr>
								 <tr  id="hiderow1" style="${miniBean.hideRow1}">
					              <td width="5%" valign="top"><c:out value="${roleBean.mdaTitle}"/></td>
					              <td align="left">
					                    
					                    <form:select path="mdaId">
					                    <form:option value="0">&lt;Select <c:out value="${roleBean.mdaTitle}"/>&gt;</form:option>
                                        <c:forEach items="${miniBean.mdaInfoList}" var="miList">
												<form:option value="${miList.id}">${miList.name}</form:option>
												</c:forEach> 
                                        </form:select>
					               
					                </td>
					             
					            </tr>
					             <tr id="hiderow2" style="${miniBean.hideRow2}">
                                      
                                       <td width="5%" valign="top"><c:out value="${roleBean.staffTitle}"/> </td>
                                        <td>
                                            <form:input path="staffId" size="8" maxlength="10" id="ognum"/> 
                                            
                                        </td>
                                    </tr>
                                 <tr>
                                 
								<td colspan="2" align="center" >
								<br>
								    <input type="image" name="_add" value="ok" title="add" src="images/add.png">
								</td>
							</tr>
                                
					          
							</table>
							 
					       <br/>
					       
					 
				</table>
				 
			<c:if test="${displayList.listSize gt 0}">
					<table>
					 <tr>
                                        <td>
                                            &nbsp;
                                        </td>
                                       
                                    </tr>
                                    <tr>
                                        <td valign="top">
											<p class="label">Selection Details</p>
											<display:table name="dispBean" class="register4" export="false" sort="page" defaultsort="1" requestURI="${appContext}/selectPayslipMode.do">
											<display:column property="name" title="Name"></display:column>
											<display:column property="paySlipObjType" title="Type"></display:column>
											<display:column property="noOfActiveEmployees" title="No. of ${roleBean.staffTypeName}"></display:column>
											<display:column property="totalPayStr" title="Total Pay"></display:column>
											<display:column property="totalDeductionsStr" title="Total Deductions"></display:column>
											<display:column property="netPayStr" title="Net Pay"></display:column>
											<display:column property="remove" title="Remove" href="${appContext}/selectPayslipMode.do" paramId="oid" paramProperty="paySlipDisplayObjId"></display:column>
											 <display:setProperty name="paging.banner.placement" value="bottom" />								    
										</display:table>
										</td>
                                    </tr>
                                    
                                    </table>
                                    </c:if>
                                    <tr><td>
                                   &nbsp; <input id="reportLink" type="image" name="_go" value="go" title="Submit" src="images/go_h.png">
                                   &nbsp; <input type="image" name="_cancel" value="cancel" title="Close" src="images/cancel_h.png">
                                       <p>
                                      </td>
                                    </tr>

		</table>
		<!-- <div id="pdfReportLink" class="reportBottomPrintLink">
        										<a href="${appContext}/payslipModelFormExcelReport.do?">
                                                    Download Excel report </a>
        										<br />

        									</div> -->
        <!-- Modal -->
                            <div class="modal fade" id="reportModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
                              <div class="modal-dialog modal-dialog-centered" role="document">
                                <div class="modal-content">
                                  <div class="modal-header" style="background-color:green; color:white">
                                    <h5 class="modal-title" id="exampleModalLongTitle" style="font-size:16px">Downloading Report....</h5>
                                  </div>
                                  <div class="modal-body">
                                    <p style="color: black; font-size:14px">
                                        Please kindly wait as the report is trying to download.
                                        After Download, you can use the Close button to close this modal
                                    </p>
                                  </div>
                                  <div class="modal-footer">
                                    <button id="close" style="background-color:red; color:white" type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                                  </div>
                                </div>
                              </div>
        </div>
		</td>
		</tr>
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
		</tr>
	</table>
	</form:form>
	<script>
	    $("#reportLink").click(function(e){
           $('#reportModal').modal({
              backdrop: 'static',
              keyboard: false
           });
        });

        $("#close").click(function(e){
           window.location.href ="${pageContext.request.contextPath}/reportsOverview.do"
        });
	</script>
</body>

</html>

