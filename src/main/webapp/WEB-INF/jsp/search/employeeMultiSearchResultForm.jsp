<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            Multiple Results Page
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
       <link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
    </head>
    <body class="main">
   
   
        <table class="main" width="70%" border="1" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table class="alignLeft hundredPercentWidth" border="0" cellpadding="0" cellspacing="0">
                    	
                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    <c:out value="${miniBean.pageSize}" />  Employees Found.<br>
                                    
                                </div>
                            </td>
                        </tr>
                        <tr>
                           <td valign="top" class="mainBody hundredPercentWidth verticalTopAlign" id="mainbody">
                                                   
                                <div>
							
							    <div class="panel panel-danger">
												  <div class="panel-heading">
												    <h3 class="panel-title lead">
												    	<a class="linkWithUnderline" href="<c:url value="/${searchUrl}"/>"><spring:message code="users.view.search"/></a>
													</h3>
												  </div>
                                   
                                     
                                               <div class="panel-body">
															<table id="multiResTable" class="hundredPercentWidth table display" cellspacing="0" cellpadding="0">
													<thead>
														<tr>
															<th class="fivePercentWidth"><spring:message code="table.serialNo" /></th>
															<th class="fivePercentWidth"><spring:message code="table.empId" /></th>
															<th class="twentyPercentWidth"><spring:message code="table.staffName" /></th>
															<th class="twentyPercentWidth"><spring:message code="table.mdaName" /></th>
															<th class="tenPercentWidth"><spring:message code="table.payGroup" /></th>
															<th class="fivePercentWidth"><spring:message code="table.levelStep" /></th>
															 
														</tr>
													</thead>
													<tbody>
														<c:forEach items="${miniBean.employeeList}" var="employees" varStatus="gridRow">
															<tr>
																<td>
																	<c:out value="${gridRow.index + 1}" />
																	 
																</td> 
																<td>
																	<a href="${appContext}/${goUrl}?eid=${employees.id}">
																		<c:out value="${employees.employeeId}" />
																	</a>
																</td>
																<td>
																	<c:out value="${employees.displayName}" />
																</td>
																<td><c:out value="${employees.parentObjectName}" /></td>
																<td>
																	<c:out value="${employees.salaryScale}" />
																</td>	
																<td><c:out value="${employees.levelAndStep}" /></td>
																
																 
															</tr>
														</c:forEach>
													</tbody>
												</table>
												  </div>
												</div>
                                    
                                    			</div>	
						</td>
					</tr>

				</table>
			</td>
		</tr>
		<tr>
			<%@ include file="/WEB-INF/jsp/footerFile.jsp"%>
		</tr>
	</table>
	<script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
	<script type="text/javascript">
		$(function() {

			$("#multiResTable").DataTable({
				"pageLength": 10,
				"order" : [ [ 2, "asc" ] ],
				//can also use "columns" instead of "columnDefs". "columns" take precedence over "columnDefs"
				//also properties higher up, take precedence over those below
				"columnDefs":[
					//{"targets": 0, "orderable" : true},
					{"targets": 5, "searchable" : false }
					//{"targets": [0, 1], "orderable" : false }
				]
			});
		});
	</script>
</body>
</html>