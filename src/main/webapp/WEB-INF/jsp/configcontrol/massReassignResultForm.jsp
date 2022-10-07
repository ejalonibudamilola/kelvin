<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title> Mass Reassignment Result  </title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="stylesheet" href="css/datatables.min.css" type="text/css">
</head>
<style>
    #reassignResult thead tr th{
        font-size:8pt !important;
    }
    #reassignResult_filter input{
        height:23px;
        font-size:10pt;
    }
</style>

<body class="main">
    <form:form modelAttribute="miniBean">
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
    		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
    		<tr>
    			<td>
    				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

    					<tr>
    						<td>
    							<div class="title">Mass Reassignment</div>
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
                                <table border="0" cellspacing="0" cellpadding="3" width="60%" align="left" >
                                    <tr align="left">
                                        <td class="activeTH">Mass Reassignment Result</td>
                                    </tr>
                                    <tr>
                                        <td class="activeTD">
                                            <table border="0" cellspacing="1" cellpadding="2">
                                                <tr>
                                                    <td align="right" nowrap>
                                                        <span class="required">Name</span>&nbsp;
                                                    </td>
                                                    <td align="left"><input value="${miniBean.name}" readonly/></td>
                                                </tr>
                                                <tr>
                                                    <td align="right" nowrap>
                                                        <span class="required">From Pay Group</span>&nbsp;
                                                    </td>
                                                    <td align="left"><input value="${miniBean.fromSalaryType.name}" readonly/></td>
                                                </tr>
                                                <tr>
                                                    <td align="right" nowrap>
                                                        <span class="required">To Pay Group</span>&nbsp;
                                                    </td>
                                                    <td align="left"><input value="${miniBean.toSalaryType.name}" readonly/></td>
                                                </tr>
                                                <tr>
                                                   <td align="right" nowrap>
                                                      <span class="required">Initiator</span>&nbsp;
                                                   </td>
                                                   <td align="left"><input value="${miniBean.createdBy.lastName} ${miniBean.createdBy.firstName}" readonly/></td>
                                                </tr>
                                                <tr>
                                                   <td align="right" nowrap>
                                                      <span class="required">Creation Date</span>&nbsp;
                                                   </td>
                                                   <td align="left"><input value="${miniBean.creationDate}" readonly/></td>
                                                </tr>
                                                <tr>
                                                   <td align="right" nowrap>
                                                      <span class="required">Total Gross From ${miniBean.fromSalaryType.name}</span>&nbsp;
                                                   </td>
                                                   <td align="left"><input value="${miniBean.sumFrom}" readonly/></td>
                                                </tr>
                                                <tr>
                                                   <td align="right" nowrap>
                                                      <span class="required">Total Gross To ${miniBean.toSalaryType.name}</span>&nbsp;
                                                   </td>
                                                   <td align="left"><input value="${miniBean.sumTo}" readonly/></td>
                                                </tr>
                                                <tr>
                                                   <td align="right" nowrap>
                                                      <span class="required">Net Difference</span>&nbsp;
                                                   </td>
                                                   <td align="left"><input value="${miniBean.netDiff}" readonly/></td>
                                                </tr>
                                                <tr>
                                                   <td align="right" nowrap>
                                                      <span class="required"><c:out value="${roleBean.staffTypeName}"/> Affected</span>&nbsp;
                                                   </td>
                                                   <td align="left"><input value="${miniBean.totalEmp}" readonly/></td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
    				            </table>
    			            </td>
    		            </tr>
    		            <tr>
    		                <td>
    		                    <div style="margin:0 1%">
    		                        <table id="reassignResult" class="display table">
                                       <thead>
                                           <tr>
                                               <th><c:out value="${roleBean.staffTitle}"/></th>
                                               <th><c:out value="${roleBean.staffTypeName}"/> Name</th>
                                               <th><c:out value="${roleBean.mdaTitle}"/> Name</th>
                                               <th>From Level/Step</th>
                                               <th>To Level/Step</th>
                                               <th>From Gross</th>
                                               <th>To Gross</th>
                                           </tr>
                                       </thead>
                                       <tbody>
                                          <c:forEach items="${miniBean.massReassignDetailsBeans}" var="dList">
                                             <tr>
                                                <td><c:out value="${dList.staffId}"/></td>
                                                <td><c:out value="${dList.staffName}"/></td>
                                                <td><c:out value="${dList.mdaName}"/></td>
                                                <td><c:out value="${dList.fromSalaryInfo.levelStepStr}"/></td>
                                                <td><c:out value="${dList.toSalaryInfo.levelStepStr}"/></td>
                                                <td><c:out value="${dList.fromSalaryInfo.monthlyGrossSalaryStr}"/></td>
                                                <td><c:out value="${dList.toSalaryInfo.monthlyGrossSalaryStr}"/></td>
                                             </tr>
                                          </c:forEach>
                                       </tbody>
                                    </table>
                                </div>
    		                </td>
    		            </tr>
    		            <c:if test="${miniBean.confirmation}">
                            <tr>
                                <td>
                                    <table>
                                        <tr>
                                           <td align="left" width="20%" nowrap>
                                              <span class="required">Generated Code*</span>
                                           </td>
                                           <td width="60%">
                                               <form:input path="generatedCaptcha" size="8" maxlength="8" disabled="true" />
                                           </td>
                                        </tr>
                                        <tr>
                                           <td align="left" width="20%" nowrap>
                                              <span class="required">Enter Code Above*</span>
                                           </td>
                                           <td width="60%">
                                              <form:input path="enteredCaptcha" size="8" maxlength="8" />&nbsp;<font color="green">case insensitive.</font>
                                           </td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </c:if>
    		            <tr>
    		                <td>
                                <c:choose>
                                   <c:when test="${approve eq 1 }">
                                             <input style="margin-bottom:5px" type="image" name="_approve" value="approve" title="Approve Mass Reassignment" class="" src="images/approve.png">
                                             &nbsp &nbsp <input type="image" name="_reject" value="reject" title="Reject" class="" src="images/reject_h.png">
                                             &nbsp &nbsp <input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/cancel_h.png">
                                   </c:when>
                                   <c:otherwise>
                                            <input type="image" name="_close" value="close" title="Close" class="" src="images/close.png">
                                   </c:otherwise>
                                </c:choose>
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
    <script src="scripts/datatables.min.js"></script>
    <script>
         $('#reassignResult').DataTable( {
             lengthMenu: [10, 30, 50, 'All Records'],
             searching: true
         });
    </script>
</body>
</html>