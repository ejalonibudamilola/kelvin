<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="dt"%>

<html>
<head>
<title>Approval</TITLE>
<link rel="stylesheet" href="styles/omg.css" TYPE="text/css" />
<link rel="Stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
</head>

<body class="main">

<table class="main" width="70%" border="1" bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">

	<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
	<tr>
		<td>

		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
			
			<tr>
			
				<td colspan="2">
				<c:choose>
					<c:when test="${approveBean.hasData}">
						<div class="title" >Approve Paychecks for : <c:out value="${approveBean.payPeriodName}"></c:out></div>
					
					</c:when>
					<c:otherwise>
					<div class="title" >No Pending Paychecks found.</div>
					</c:otherwise>
				</c:choose>
				
				</td>
			
			</tr>
			
			<tr >
				<form:form modelAttribute="approveBean">
				
				<c:set value="${approveBean}" var="dispBean" scope="request"/>
				<td valign="top" class="mainBody" id="mainBody">
				<c:if test="${approveBean.hasErrors}">
						<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
								 <spring:hasBindErrors name="approveBean">
         							 <ul>
            							<c:forEach var="errMsgObj" items="${errors.allErrors}">
               								<li>
                  							<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/>
               								</li>
            							</c:forEach>
         								</ul>
     							 </spring:hasBindErrors>
						</div>
			</c:if>
				To see details
				of the paycheck, click the Details link. After you click Approve,
				you can print a Paystub to give to your employee.
				<p><br>
				</p>
				<table>
									<tr align="left">
										<td class="activeTH" colspan="2">Filter By</td>
										
									</tr>
                                    
                                    <tr>
                                      
                                       <td class="activeTD">
                                             <c:out value="${roleBean.staffTitle}"/> :
                                        </td>
                                        <td class="activeTD">
                                            <form:input path="ogNumber" size="9" maxlength="10"/> 
                                        </td>
                                    </tr>
                                    
                                    <tr>
                                       <td class="activeTD">
                                            Last Name :                                          
                                        </td>
                                        <td class="activeTD">
                                            <form:input path="lastName" size="15" maxlength="20"/>
                                        </td>
                                    </tr>
									<tr>
                                    	<td class="buttonRow" align="right">
                                    		 <input type="image" name="_update" value="update" title="Update Report" src="images/Update_Report_h.png">
                                        
                                    	</td>
                                    </tr>
								</table>
					<!--- display the checks --->

					       <table border="0" width="80%" cellpadding="5">
						    <tr>
							<td align="left">
							<table width="90%" cellspacing="1" cellpadding="3">

                                <c:choose>
                                <c:when test="${approveBean.wholeEntity}">
							             <tr>

                                            <dt:table name="dispBean" id="paychecktbl" class="display table" sort="page" defaultsort="1" requestURI="${appContext}/approvePaychecksForm.do">
                                           <dt:column property="ogNumber" title="${roleBean.staffTitle}"></dt:column>
                                            <dt:column property="employee.displayName" title="${roleBean.staffTypeName} Name" ></dt:column>
                                            <dt:column property="employee.gradeLevelAndStep" title="Pay Group" ></dt:column>
                                            <dt:column property="salaryInfo.levelStepStr" title="Level/Step" ></dt:column>
                                             <dt:column property="totalPayStr" title="Total Pay" ></dt:column>
                                             <dt:column property="netPayStr" title="Net Pay" ></dt:column>
                                             <dt:column property="taxesPaidStr" title="Taxes Paid" ></dt:column>

                                            <dt:column property="details" href="${appContext}/paystubForm.do" paramId="pid" paramProperty="id"></dt:column>

                                             <c:if test="${approveBean.admin}">
                                                 <dt:column property="delete" href="${appContext}/deletePaycheck.do" paramId="pid" paramProperty="id"></dt:column>
                                            </c:if>
                                             <dt:setProperty name="paging.banner.placement" value="bottom" />
                                            </dt:table>
                                          </tr>
                                    	</table>
                                     </td>
                               </tr>
                            </c:when>
                            <c:otherwise>
                              <tr>

                                            <dt:table name="dispBean" id="paychecktbl" class="register4" sort="page" defaultsort="1" requestURI="${appContext}/approvePaychecksForm.do">
                                           <dt:column property="ogNumber" title="${roleBean.staffTitle}"></dt:column>
                                            <dt:column property="employeeName" title="${roleBean.staffTypeName} Name" ></dt:column>
                                            <dt:column property="salaryTypeName" title="Pay Group" ></dt:column>
                                            <dt:column property="levelAndStepStr" title="Level/Step" ></dt:column>
                                             <dt:column property="totalPayStr" title="Total Pay" ></dt:column>
                                             <dt:column property="netPayStr" title="Net Pay" ></dt:column>
                                             <dt:column property="taxesPaidStr" title="Taxes Paid" ></dt:column>

                                            <dt:column property="details" href="${appContext}/paystubForm.do" paramId="pid" paramProperty="id"></dt:column>

                                             <c:if test="${approveBean.admin}">
                                                 <dt:column property="delete" href="${appContext}/deletePaycheck.do" paramId="pid" paramProperty="id"></dt:column>
                                            </c:if>
                                             <dt:setProperty name="paging.banner.placement" value="bottom" />
                                            </dt:table>
                                          </tr>
                                    	</table>
                                     </td>
                               </tr>
                            </c:otherwise>
                            </c:choose>
						<!-- <tr>
							<td align="left">
							&nbsp;
							</td>
						</tr>-->

						<tr>
						    <td>
						        <c:if test="${approveBean.confirmation}">
                        						<div id="topOfPageBoxedErrorMessage" style="display:${approveBean.displayErrors}">
                        								 <spring:hasBindErrors name="approveBean">
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
                        					            <tr>
                        									<td align="left" width="20%" nowrap>
                        									<span class="required">Generated Code*</span></td>
                        									<td width="60%">
                        										<form:input path="generatedCaptcha" size="8" maxlength="8" disabled="true" />
                        									</td>
                        					            </tr>
                        					            <tr>
                        									<td align="left" width="20%" nowrap>
                        									<span class="required">Enter Code Above*</span></td>
                        									<td width="60%">
                        										<form:input path="enteredCaptcha" size="8" maxlength="8" />&nbsp;<font color="green">case insensitive.</font>
                        									</td>
                        					            </tr>
                        					    </table>
                        	    </c:if>
                        	</td>
                        </tr>
                        <tr>
							<td class="buttonRow" align="right">
								<c:choose>
								<c:when test="${roleBean.superAdmin}">
								   <input type="image" name="_approve" value="approve" title="Approve Paychecks" class='' src='images/approve.png' onclick="">
									 <input type="image" name="_cancel" value="cancel" title="Close" class='' src='images/close.png' onclick="">
								</c:when>
								<c:otherwise>
								  <input type="image" name="_cancel" value="cancel" title="Close" class='' src='images/close.png' onclick="">
								</c:otherwise>
							    </c:choose>
							</td>
						</tr>
					</table><p/>

					<c:if test="${roleBean.superAdmin}">
					If you have created these paychecks in error, you can <a href='${appContext}/deletePendingPayroll.do'>undo
				all now</a><br/>
				</c:if>
				<br/>
				
				</td>

				</form:form> 
				
				
			</tr>
			<tr>
				<%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
			</tr>
			</table>
			
			</td>
			</tr>
			
			
		</table>
	<script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
    <script type="text/javascript">

        	/*	$(function() {
        			$("#paychecktbl").DataTable({
        				"order" : [ [ 1, "asc" ] ],
        				//can also use "columns" instead of "columnDefs". "columns" take precedence over "columnDefs"
        				//also properties higher up, take precedence over those below
        				"columnDefs":[
        					{"targets": 0, "orderable" : false},
        					{"targets": 4, "searchable" : false }
        					//{"targets": [0, 1], "orderable" : false }
        				]
        			});
        		});*/
    </script>
		
</body>

</html>

