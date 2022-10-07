<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            Mini Salary Information Form
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
		<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">

    </head>
    <style>
       #emptbl thead tr th{
          font-size:8pt !important;
       }
    </style>
    <body class="main">
    <script type="text/JavaScript" src="scripts/jacs.js"></script>
    <form:form modelAttribute="miniBean">
	<c:set value="${miniBean.salaryTypeNameList}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
           <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    Create Mini Salary Information<br>

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

                                <table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
                                   <tr>
                                        <td valign="top">
											<p class="label">Pay Groups</p>
											<display:table name="dispBean" id="emptbl" class="display table" export="false" sort="page" defaultsort="1" requestURI="${appContext}/createEditMiniSalary.do">
											 <display:column property="name" title="Name"></display:column>
										     <display:column property="noOfEmployees" title="No of Level & Steps"></display:column>
						                    <display:setProperty name="paging.banner.placement" value="" />
										</display:table>
										</td>
                                    </tr>
                                    <tr>

                                    </tr>
                                    <tr>
                                     	<td class="buttonRow" align="right">
                                        <input type="image" name="_go" value="go" title="Process" src="images/ok_h.png">
                                        <input type="image" name="_cancel" value="cancel" alt="Cancel" src="images/close.png">
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
        </form:form>
        <script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
        <script type="text/javascript">
           $(function() {
              $("#emptbl").DataTable({
                 "order" : [ [ 1, "asc" ] ]
              });
           });
        </script>
    </body>
</html>
