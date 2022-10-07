<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<html>
<head>
<title> <c:out value="${roleBean.staffTypeName}"/>&apos;s By <c:out value="${roleBean.mdaTitle}"/></title>
<link rel="stylesheet" href="styles/omg.css" TYPE="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="icon" 
      type="image/png" 
      href="<c:url value="/images/coatOfArms.png"/>">
<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
<link rel="stylesheet" href="css/screen.css" type="text/css"/>
<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
<link rel="stylesheet" href="styles/spinner.css" TYPE="text/css" />
</head>
<style>
   #empTable thead tr th{
      font-size:8pt !important;
   }
</style>

<body class="main">
<div class="loader"></div>
    <form:form modelAttribute="miniBean">	
	<c:set value="${miniBean}" var="dispBean" scope="request"/>
	<table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td>
							<div class="navTopBannerAndSignOffLink">
								<span class="navTopSignOff">
									<a href="javascript: self.close ()" title='Close Window' id="topNavSearchButton1">Close Window</a>
								</span>
							</div>
						</td>
					</tr>
					
			<tr>
				<td colspan="2">	
					<div class="title"> <c:out value="${roleBean.staffTypeName}"/> list for <c:out value="${miniBean.ministryName}" /> </div>
				</td>
			</tr>
			<tr>
				<td valign="top" class="mainbody" id="mainbody">
				
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
				<br/>
					<br/>
					     List of <c:out value="${roleBean.staffTypeName}"/>&apos;s currently in <c:out value="${miniBean.mbapName}"/>.
					<br/>
					<br/>
     				
     				<table border="0" cellspacing="0" cellpadding="0" width="90%"><tr><td>
					<table class="formtable" border="0" cellspacing="1" cellpadding="0" width="70%" align="left" >
						<tr align="left">
							<td class="activeTH" colspan="2"><c:out value="${miniBean.mbapName}" /></td>
						</tr>
						 
									<tr>
										<td width="15%" align="left" class="activeTD"><c:out value="${miniBean.mbapName}" /> Name :</td>
										<td width="25%" align="left" class="activeTD"><c:out value="${miniBean.ministryName}"/></td>
									</tr>
									<tr>
										<td width="15%" align="left" class="activeTD">Current Staff Strength</td>
										 <td width="25%" align="left" class="activeTD"><c:out value="${miniBean.noOfEmployees}"/>										
										</td>
									</tr>
									<tr>
										<td width="15%" align="left" class="activeTD">Number of Women (&#37;) :</td>
										<td width="25%" align="left" class="activeTD"><c:out value="${miniBean.noOfFemales}"/>&nbsp;(&nbsp;<c:out value="${miniBean.femalePercentageStr}"/>&#37;&nbsp;)</td>
									</tr>
									<tr>
										<td width="15%" align="left" class="activeTD">Number of Men (&#37;) :</td>
										<td width="25%" align="left" class="activeTD"><c:out value="${miniBean.noOfMales}"/>&nbsp;(&nbsp;<c:out value="${miniBean.malePercentageStr}"/>&#37;&nbsp;)</td>
									</tr>
									
									<tr>
										<td width="15%" align="left" class="activeTD">
											 Last Total Pay	:									
										</td>
										<td width="25%" align="left" class="activeTD"><c:out value="${stats.totalPayStr}"/>&nbsp;(&nbsp;<c:out value="${stats.totalPayPercentileStr}"/>&#37;&nbsp;)		
									</tr>
									<tr>
										<td width="15%" align="left" class="activeTD">
											 Last Net Pay	:									
										</td>
										<td width="25%" align="left" class="activeTD"><c:out value="${stats.netPayStr}"/>&nbsp;(&nbsp;<c:out value="${stats.netPayPercentileStr}"/>&#37;&nbsp;)		
									</tr>
									<tr>
										<td width="15%" align="left" class="activeTD">
											 Last Total Allowances	:									
										</td>
										<td width="25%" align="left" class="activeTD"><c:out value="${stats.totalAllowanceStr}"/>&nbsp;(&nbsp;<c:out value="${stats.totalAllowPercentileStr}"/>&#37;&nbsp;)		
									</tr>
									<tr>
										<td width="15%" align="left" class="activeTD">
											 Last Special Allowances :									
										</td>
										<td width="25%" align="left" class="activeTD"><c:out value="${stats.totalSpecialAllowanceStr}"/>&nbsp;(&nbsp;<c:out value="${stats.totalSpecAllowPercentileStr}"/>&#37;&nbsp;)		
									</tr>
									<tr>
										<td width="5%" align="left" class="activeTD">
											 Last Loan Obligation :									
										</td>
										<td width="25%" align="left" class="activeTD"><c:out value="${stats.totalGarnishmentsStr}"/>&nbsp;(&nbsp;<c:out value="${stats.totalGarnPercentileStr}"/>&#37;&nbsp;)		
									</tr>
									<tr>
										<td width="15%" align="left" class="activeTD">
											 Last Taxes Paid :									
										</td>
										<td width="25%" align="left" class="activeTD"><c:out value="${stats.totalPayStr}"/>&nbsp;(&nbsp;<c:out value="${stats.totalPayPercentileStr}"/>&#37;&nbsp;)		
									</tr>
									<tr>
										<td width="15%" align="left" class="activeTD">
											 Last Staff Strength :									
										</td>
										<td width="25%" align="left" class="activeTD"><c:out value="${stats.totalStaff}"/>&nbsp;(&nbsp;<c:out value="${stats.totalStaffPercentileStr}"/>&#37;&nbsp;)		
									</tr>
								 
						
					</table>
				</td>
			</tr>
			
		</table>
		
						
	
	<table border="0" cellspacing="0" cellpadding="0" width="100%"><tr><td>
			<table border="0" cellspacing="0" cellpadding="0" width="100%" align="left" >
						<tr align="left">
							<td ><c:out value="${roleBean.staffTypeName}"/>s</td>
							
						</tr>
						<tr>
							<td >
								<table width="100%" border="0" cellspacing="0" cellpadding="0">
									<display:table name="dispBean" id="empTable" class="display table" export="false" sort="page" defaultsort="1" requestURI="${appContext}/empByMDAPDetails.do">
									<display:column property="abstractEmployeeEntity.employeeId" title="${roleBean.staffTitle}" ></display:column>
									<display:column property="abstractEmployeeEntity.displayName" title="Name" ></display:column>
									<c:choose>
									<c:when test="${roleBean.pensioner}">
                                       <display:column property="monthlyPensionStr" title="Monthly Pension"></display:column>
									    <display:column property="birthDateStr" title="Birth Date"></display:column>
									    <display:column property="hireDateStr" title="Hire Date"></display:column>
									    <display:column property="pensionStartDateStr" title="Pension Start Date"></display:column>
                                        <display:column property="yearsOnPension" title="Years As Pensioner"></display:column>
									</c:when>
									<c:otherwise>
                                        <display:column property="abstractEmployeeEntity.salaryScaleName" title="Pay Group"></display:column>
									    <display:column property="abstractEmployeeEntity.levelStepStr" title="Level & Step"></display:column>
									    <display:column property="birthDateStr" title="Birth Date"></display:column>
									    <display:column property="hireDateStr" title="Hire Date"></display:column>
									    <display:column property="expDateOfRetireStr" title="Exp. Retire Date"></display:column>
									    <display:column property="yearsOfService" title="Years In Service"></display:column>
									</c:otherwise>
                                    </c:choose>
									<display:setProperty name="paging.banner.placement" value="" />
									</display:table>
								</table>
								</td>
						 </tr>
								
							
			
			</table>
			</td>
			</tr>
			</table>
			
            <br><br>
            <div class="reportBottomPrintLink">
				<a href="${appContext}/singleMdapSummaryDetailsExcel.do?oid=${miniBean.id}&tind=${miniBean.objectInd}" title='View/Export All Records to Microsoft Excel'>
				View All Records in Excel </a><br />
				</div>
				<br>
				</td>
				</tr>
				<tr>
				  <%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
				  </tr>
				</table>
				
			</td>
			</tr>



			
		</table>
	
</form:form>
<script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
<script>
    $(function() {
        $("#empTable").DataTable({
            "order" : [ [ 1, "asc" ] ],
            "columnDefs":[
                {"targets": [0], "orderable" : false}
            ]
        });
    });

    window.onload = function exampleFunction() {
         $(".loader").hide();
    }
</script>

</body>
</html>
