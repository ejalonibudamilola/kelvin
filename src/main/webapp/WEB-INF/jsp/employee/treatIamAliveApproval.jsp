<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
        <link rel="stylesheet" href="styles/omg.css" TYPE="text/css" />
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
        <link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
        <link rel="stylesheet" href="css/screen.css" type="text/css"/>
        <link rel="stylesheet" href="css/jquery-ui.css">
        <link rel="stylesheet" href="dataTables/css/jquery.dataTables.min.css" type="text/css">

        <title>I am Alive</title>
    </head>
    <style>
        .btn-success{
            background-color:#326032 !important;
        }
        .btn-sm{
            font-size:10px !important;
            line-height:1 !important;
        }
        .toggle.btn-sm {
            min-height: 22px !important;
        }
        .register1 {background: #9BA9B3; width:1200px; margin: 0 10px 10px 10px}
        .register1 thead th  {background: #EBF6FD; font-size:8pt; padding:10px; font-weight:bold}
        .register1 tr:nth-child(even) {background: #EBF6FD; font-size:8pt}
        .register1 tr:nth-child(odd) {background: white; font-size:8pt}
        .tableDiv{
            margin-top:2%;
            overflow-y: auto;
            overflow-x: hidden;
            height: 500px;
        }
        .tableDiv thead th{
            position: sticky;
            top: 0;
            z-index:1;
        }

        .tableDiv tbody td{
            z-index:0;
        }

    </style>
    <body class="main">
        <form:form modelAttribute="miniBean">
        <c:set value="${empList}" var="dispBean" scope="request"/>
        <table class="main" width="75%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
               <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
               <tr>
                  <td>
                     <div style="color:red; padding: 4px 10px" class="title">Treat am Alive <c:out value="${roleBean.staffTypeName}"/></div>
                  </td>
               </tr>

               <tr>
                  <td>
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
                  <div class="tableDiv">

                    <table width="100%"  class="register1" id="">

                        <thead>
                            <tr>
                               <th><c:out value="${roleBean.staffTitle}"/></th>
                               <th><c:out value="${roleBean.staffTypeName}"/> Name</th>
                               <th><c:out value="${roleBean.mdaTitle}"/> Name</th>
                               <th>I am Alive Date<br/> (YYYY-MM-DD)</th>
                               <th>Monthly Pension</th>
                               <th>Extend Date <br/>(YYYY-MM-DD)</th>
                               <th>Suspend<br/></th>
                            </tr>
                        </thead>
                        <tbody>
                               <c:forEach items="${miniBean.employeeList}" var="myList" varStatus="gridRow">
                                <tr>
                                    <td>${myList.hiringInfo.pensioner.employeeId}</td>
                                    <td>${myList.hiringInfo.pensioner.displayName}</td>
                                    <td>${myList.hiringInfo.pensioner.mdaName}</td>
                                    <td>${myList.hiringInfo.amAliveDate}</td>
                                    <td>${myList.hiringInfo.monthlyPensionAmount}</td>
                                    <td>${myList.amAliveDate}</td>
                                    <td>${myList.suspensionStatus}</td>
                                </tr>
                                </c:forEach>
                        </tbody>
                        </table>
                        <table>
                           <c:if test="${miniBean.addWarningIssued}">
                                    <tr align = "left">
                                    	<td align=right width="25%"><b>Approval/Rejection Memo*</b></td>

									       <td width="75%" align = "left">
						                     <form:textarea path="approvalMemo" rows="5" cols="35" disabled="${saved}"/>
									       </td>
                                    </tr>
                           </c:if>
                        </table>
                                           <input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/close.png">
	                                       <c:if test="${roleBean.superAdmin}">
                                           <c:choose>
                                     	   <c:when test="${not miniBean.addWarningIssued}">
	                                          <input type="image" name="_approve" value="approve" title="Approve Selected ${roleBean.staffTypeName}" class="" src="images/approve.png">
                                              <input type="image" name="_reject" value="reject" title="Reject Selected ${roleBean.staffTypeName}" class="" src="images/reject_h.png">
	                                       </c:when>
	                                       <c:otherwise>
                                            <input type="image" name="_confirm" value="confirm" title="Confirm to 'Approve All Selected ${roleBean.staffTypeName}'" class="" src="images/confirm_h.png">
	                                       </c:otherwise>
	                                       </c:choose>
	                                       </c:if>
                  </div>
                  </td>
               </tr>
               <tr>
                    <%@ include file="/WEB-INF/jsp/footerFile.jsp"%>
               </tr>
        </table>
        </form:form>

    </body>