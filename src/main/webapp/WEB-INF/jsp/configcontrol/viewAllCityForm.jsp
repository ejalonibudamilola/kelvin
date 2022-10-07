<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            View All Cities
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
		<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
    </head>
     <style>
            #cityTbl thead tr th{
                font-size:8pt !important;
            }
      </style>
    <body class="main">
    <form:form modelAttribute="cityBean">
	<c:set value="${cityList}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    Cities List<br>

                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">
									<div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
                                    								 <spring:hasBindErrors name="cityBean">
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
                                             State Name :
                                        </td>
                                        <td width="45%">
                                        			<form:select path="stateId">
                                        					<option value="-1">&lt;&nbsp;Select&nbsp;&gt;</option>
                                        						<c:forEach items="${stateList}" var="states">
                                        								<option value="${states.id}" title="${states.fullName}">${states.name}</option>
                                        						</c:forEach>

                                        			</form:select>

                                       </td>
                                    </tr>
									<tr>
                                    	<td class="buttonRow" align="right">
                                    		 <input type="image" name="_update" value="update" title="Update Report" src="images/Update_Report_h.png">

                                    	</td>

                                    </tr>
								</table>
                                <table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
                                    <tr>
                                        <c:if test="${cityBean.showOverride eq true}">
                                        <td class="buttonRow" align="right">
                                              <input type="image" name="_add" value="add" title="Add City" src="images/Add_New_Item_h.png">

                                        </td>
                                        </c:if>
                                    </tr>


								 <c:if test="${cityBean.showOverride eq true}">
                                    <tr>
                                        <td valign="top">
											<p class="label">All LGA List</p>
											<display:table name="dispBean" id="cityTbl" class="display table" export="false" sort="page" defaultsort="1" requestURI="${appContext}/viewAllCities.do">
											<display:setProperty name="export.rtf.filename" value="AllBanksList.rtf"/>
											<display:setProperty name="export.excel.filename" value="AllBanksList.xls"/>
											<display:column property="name" title="Name" media="html" href="${appContext}/editCity.do" paramId="cId" paramProperty="id"></display:column>
											<display:column property="name" title="Name" media="excel"></display:column>
										    <display:column property="lastModBy.actualUserName" title="Last Modified By"></display:column>
										    <display:column property="lastModTs" title="Last Modified Date"></display:column>
											<display:setProperty name="paging.banner.placement" value="" />
										</display:table>
										</td>
                                    </tr>

                                    </c:if>
                                    <tr>

                                    </tr>
                                    <tr>
										<td class="buttonRow" align="right" >

											<input type="image" name="_cancel" value="cancel" title="Close" class="" src="images/close.png">

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
              $("#cityTbl").DataTable({
                  "order" : [ [ 1, "asc" ] ]
              });
           });

           var id = document.getElementById('stateId');
           id.value=${cityBean.stateId};
        </script>
    </body>
</html>
