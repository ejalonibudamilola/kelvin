<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="dt"%>

<html>
<head>
<title>Add Rerun Payroll to Pending Payroll</TITLE>
<link rel="stylesheet" href="styles/omg.css" TYPE="text/css" />
<link rel="Stylesheet" href="styles/skye.css" type="text/css" media="screen">
 <link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
 <script language="javascript">
     <!--
 	 function toggleIt(which,formSwitch,formStatus,fieldInd){

        var input = document.getElementById(formSwitch);



         var outputText = document.getElementById(formStatus);


             if(which.checked) {
                 outputText.innerHTML = "Ignore Re-run Warning";
                 $("#"+fieldInd).val("1");

             } else {
                 outputText.innerHTML = "";
                 $("#"+fieldInd).val("0");
             }

         }
      // -->
     </script>

</head>

<body class="main">

<table class="main" width="70%" border="1" bordercolor="#33C0C8" cellspacing="0" cellpadding="0" align="center">

	<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
	<tr>
		<td>

		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
			
			<tr>
			
				<td colspan="2">
				<div class="title" >Commit Payroll Rerun</div>
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
                                            <c:out value="${roleBean.staffTypeName}"/>:
                                        </td>
                                        <td class="activeTD">
                                            <form:input path="ogNumber" size="9" maxlength="10"/> 
                                        </td>
                                    </tr>
                                    
                                    <tr>
                                       <td class="activeTD">
                                            Last Name/Surname :
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

							    <tr> 
                                    <dt:table name="dispBean" class="register3" sort="page" defaultsort="1" requestURI="${appContext}/approveRerunPaychecks.do">
									<dt:column property="payDate" title="Pay Date"></dt:column>
									<dt:column property="employee.employeeId" title="${roleBean.staffTitle} "></dt:column>
									<dt:column property="employee.displayName" title="${roleBean.staffTypeName} Name" ></dt:column>
									<dt:column property="salaryInfo.salaryType.name" title="Pay Group" ></dt:column>
									<dt:column property="salaryInfo.levelStepStr" title="Level/Step" ></dt:column>
									<dt:column property="totalPayStr" title="Total Pay" ></dt:column>
									<dt:column property="netPayStr" title="Net Pay" ></dt:column>
									<dt:column property="taxesPaidStr" title="Taxes" ></dt:column>
									<dt:column property="details" href="${appContext}/paystubForm.do" paramId="pid" paramProperty="id"></dt:column>
									<dt:setProperty name="paging.banner.placement" value="bottom" />
									</dt:table>
								</tr>

							</table>
							</td>
						</tr>
						<!--
						<c:if test="${roleBean.superAdmin and showRow eq true}">
                            <tr>
                                 <td align="left">
                                    <form:input path="ignorePendingChecksBind" id="ignorePendingChecksBindInd"  style="display:none"/>
                                      <label class="toggle">
                                       <input id="ignorePendingChecksBindSwitch" name="ignorePendingChecksBindSwitch" type="checkbox" onClick="toggleIt(this,'ignorePendingChecksBindSwitch','ignorePendingChecksBindStatus','ignorePendingChecksBindInd')" title="Select this box to ignore Re-Run Payroll Warning.">
                                         <span class="roundbutton"></span>
                                       </label>
                                      <span id="ignorePendingChecksBindStatus"></span>
                                 </td>
                             </tr>
                         </c:if>
                         -->
						<tr>

							<td align="left">
							
							</td>
						</tr>
						
						<tr>
							
							<td class="buttonRow" align="right">
								<c:choose>
								<c:when test="${roleBean.superAdmin}">
								   <input type="image" name="_approve" value="approve" title="Commit Payroll Rerun" class='' src='images/approve.png' onclick="">
									 <input type="image" name="_cancel" value="cancel" title="Commit Payroll Rerun Later" class='' src='images/close.png' onclick="">
								</c:when>
								<c:otherwise>
								  <input type="image" name="_cancel" value="cancel" title="Commit Payroll Rerun Later" class='' src='images/close.png' onclick="">
								</c:otherwise>
							    </c:choose>
							</td>
							
						</tr>
					</table><p/>
					
					<c:if test="${roleBean.superAdmin}">
					   If you have created these rerun paychecks in error, you can <a href='${appContext}/approveRerunPaychecks.do?pid=${approveBean.parentInstId}'>undo
				       all paychecks rerun now</a><br/>
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
	<script language="javascript">
     $(document).ready(function(){
            if($("#ignorePendingChecksBindInd").val() === "true"){
               $('input[name=ignorePendingChecksBindSwitch]').attr('checked', true);
                      $("#ignorePendingChecksBindStatus").html("Ignore Re-run Warning");
           }

           });

    </script>
</body>

</html>

