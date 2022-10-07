<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            <c:out value="${roleBean.staffTypeName}" />s on Contract
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
		<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
        <link rel="stylesheet" href="styles/spinner.css" TYPE="text/css" />
    </head>
    <style>
       #contractEmp thead tr th{
         font-size:8pt !important;
       }
    </style>
    <body class="main">
   
    <form:form modelAttribute="miniBean">
	<c:set value="${miniBean}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <%@ include file="/WEB-INF/jsp/modal.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    	
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    <c:out value="${roleBean.staffTypeName}" />s on Contract<br>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
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
								<table cellpadding="1" cellspacing="2">
									<tr align="left">
											<td class="activeTH"><b>Filter Conditions </b></td>
									</tr>
									<tr>
                                        <td class="activeTD">
                                            
                                                   Month&nbsp;<form:select path="runMonth">
														<form:option value="-1">&lt;Select Month&gt;</form:option>
														<c:forEach items="${monthList}" var="mList">
														<form:option value="${mList.id}">${mList.name}</form:option>
														</c:forEach>
												</form:select> 
												 &nbsp;&nbsp;Year&nbsp;&nbsp;<form:select path="runYear">
														<form:option value="0">&lt;Select Year&gt;</form:option>
														<c:forEach items="${yearList}" var="yList">
														<form:option value="${yList.id}">${yList.name}</form:option>
														</c:forEach>
												</form:select>                                     
                                        </td>
                                       </tr> 
                                       <tr>
                                         <td class="activeTD">
                                            
                                                   <c:out value="${roleBean.staffTitle}"/>&nbsp;<form:input path="ogNumber"/>
												    &nbsp;&nbsp; Last Name&nbsp;<form:input path="lastName"/>                         
                                        </td>
                                          
                                        </tr>
                                        <tr>
                                         <td class="activeTD">
                                             <form:radiobutton path="noOfYearsInService" value="0" title="Show only Active Contracts"/>Active<br>
                                             <form:radiobutton path="noOfYearsInService" value="1" title="Show only Expired Contracts" />Expired<br>
                                         	 <form:radiobutton path="noOfYearsInService" value="2" title="Show both Active and Expired Contracts"/>All<br>
                                         </td>
                                        </tr>    
                                             
                                    </tr> 
                                    <tr>
				                     <td class="buttonRow" align="right">
				                       <input type="image" name="_updateReport" id="updateReport" value="updateReport" title="Update Report" class="updateReportSubmit" src="images/Update_Report_h.png">
				                        <input type="image" name="_close" value="close" title="Close Report" class="updateReportSubmit" src="images/close.png">
				                     </td>
                                     
				                   </tr>
                                    </table>                            
                                <table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
                                    
                                    <tr>
                                        <td valign="top">
											<p class="label"><c:out value="${roleBean.staffTypeName}" /> Contracts</p>
											<display:table name="dispBean" id="contractEmp" class="display table" export="false" sort="page" defaultsort="1" requestURI="${appContext}/viewContractEmployees.do">
											<display:column property="ogNumber" title="${roleBean.staffTitle}" href="${appContext}/editContract.do" paramId="cid" paramProperty="id"></display:column>
											<c:choose>
											<c:when test="${expired}">
                                            <display:column property="employeeName" title="${roleBean.staffTypeName} Name"></display:column>
											<display:column property="name" title="Contract Name"></display:column>
											<display:column property="contractType" title="Pay Group"></display:column>
										    <display:column property="contractLength" title="Level & Step"></display:column>
										    <display:column property="contractStartDateStr" title="Start Date"></display:column>
										    <display:column property="contractEndDateStr" title="End Date"></display:column>
										    <display:column property="referenceNumber" title="Ref. #"></display:column>
										    <display:column property="referenceDateStr" title="Ref Date"></display:column>
										    <display:column property="changedBy" title="Creator"></display:column>
											</c:when>
											<c:otherwise>
											<display:column property="employeeName" title="${roleBean.staffTypeName} Name"></display:column>
											<display:column property="name" title="Contract Name"></display:column>											
											<display:column property="contractType" title="Pay Group"></display:column>
										    <display:column property="contractLength" title="Level & Step"></display:column>
										    <display:column property="contractStartDateStr" title="Start Date"></display:column>
										    <display:column property="contractEndDateStr" title="End Date"></display:column>
										    <display:column property="referenceNumber" title="Ref. #"></display:column>
										    <display:column property="referenceDateStr" title="Ref Date"></display:column>
										    <display:column property="changedBy" title="Creator"></display:column>
										    </c:otherwise>
										    </c:choose>
											<display:setProperty name="paging.banner.placement" value="" />
										</display:table>
										</td>
                                    </tr>
                                    <tr>
                                        
                                    </tr>
                                     
                                </table>
                                <c:if test="${miniBean.objectsExist}">
                                <div>
                                    <a id="reportLink" href="#">View in Excel </a><br />
							  </div>
							  </c:if>
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
                         $("#contractEmp").DataTable({
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

                     $("#reportLink").click(function(e){
                        $('#reportModal').modal({
                           backdrop: 'static',
                           keyboard: false
                        });
                        $(".spin").show();
                        $.ajax({
                           type: "GET",
                           success: function (response) {
                              // do something ...
                              window.location.href ="${appContext}/employeesOnContractExcel.do?rm=${miniBean.runMonth}&ry=${miniBean.runYear}&eid=${miniBean.empInstId}&ln=${miniBean.lastName}&toc=${miniBean.noOfYearsInService}";
                              console.log("Response here is " + response);
                              $(".spin").hide();
                           },
                           error: function (e) {
                              alert('Error: ' + e);
                           }
                        });
                     });
        </script>
    </body>
</html>
