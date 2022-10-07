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
                    <c:if test="${not saved}">
                        <div style="padding-top:1%;padding-left:1%">
                            <b> Total Estimated Savings: <font color="green"><c:out value="${miniBean.totalMonthlyBasicStr}"/></font></b>
                            <p>( <c:out value="${miniBean.amountInWords}"/> )</p>
                            <p><b><font color="green"> Note** Extend Date - Auto extension of 'I Am Alive Date' by the configured value of <c:out value="${miniBean.empDiff}"/> Months</p></font></b>
                        </div>
                    </c:if>
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
                               <th>Suspend<br/> <input class="toggle-all" data-toggle="toggle" type="checkbox" onchange="handleToggleAll()"/></th>
                            </tr>
                        </thead>
                        <tbody>
                           <c:choose>
                            <c:when test="${saved}">
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
                            </c:when>
                            <c:otherwise>
                            <c:forEach items="${miniBean.employeeList}" var="myList" varStatus="gridRow">
                                <tr>
                                    <td>${myList.employee.employeeId}</td>
                                    <td>${myList.employee.displayName}</td>
                                    <td>${myList.employee.mdaName}</td>
                                    <td>${myList.expectedDateOfRetirement}</td>
                                    <td>${myList.monthlyPensionAmount}</td>
                                    <td><input style="width:90px" class="newDate" type="" value="${myList.resetIAmAliveDate}" readonly></td>
                                    <td>
                                        <spring:bind path="miniBean.employeeList[${gridRow.index}].suspendBind">
                                        	<input type="hidden" name="_<c:out value="${status.expression}"/>">
                                        	<input class="toggle-demo" data-toggle="toggle" type="checkbox" name="<c:out value="${status.expression}"/>" value="true" <c:if test="${status.value}">checked</c:if>  />
                                        </spring:bind>
                                    </td>
                                </tr>
                            </c:forEach>
                            </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                    <c:if test="${not saved}">
                    <input type="image" name="_update" value="update" title="Update" class="updateReportSubmit" src="images/update.png">
                    </c:if>
                    <input type="image" name="_close" value="close" title="Close" class="updateReportSubmit" src="images/close.png">
                  </div>
                  </td>
               </tr>
               <tr>
                    <%@ include file="/WEB-INF/jsp/footerFile.jsp"%>
               </tr>
        </table>
        </form:form>
        <script src="scripts/jquery-ui.js"></script>
        <script language="javaScript">
                    $( ".newDate" ).datepicker({
                        dateFormat : 'yy-mm-dd',
                         changeMonth: true,
                         changeYear: true,
                         yearRange: '1800:+0'
                     });
                    $(".newDate").click(function(){});
                    $('.toggle-all').bootstrapToggle('off');
                    $('.toggle-demo').bootstrapToggle('off');

                    function handleToggleAll(){
                        $(".toggle-all").change(function(){
                            if($(this).prop("checked") == true){
                                $('.toggle-demo').bootstrapToggle('on');
                            }
                            else{
                                $('.toggle-demo').bootstrapToggle('off');
                            }
                        });
                    }
        </script>
    </body>