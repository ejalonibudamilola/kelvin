<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp"%>

<html>
<head>
<title>Salary Structure Approval Page</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">


</head>

<style>
    .tableDiv{
        max-width: 1200px;
        overflow-x: auto;
    }
    .fixedColumn{
        left: 0;
        position: sticky;
    }
    thead th{
        background-color: #f1f1f1;
    }
</style>

<body class="main">

	<form:form modelAttribute="sBean">
		<table class="main" width="70%" border="1" bordercolor="#33c0c8"
			cellspacing="0" cellpadding="0" align="center">

			<%@ include file="/WEB-INF/jsp/headerFile.jsp"%>

			<tr>
				<td>
					<table width="100%" align="center" border="0" cellpadding="0"
						cellspacing="0">

						<tr>
							<td colspan="2">
								<div class="title">
									Salary Structure Approval
								</div>
							</td>
						</tr>
						<tr>
							<td valign="top"
								class="mainBody hundredPercentWidth verticalTopAlign"
								id="mainbody">


								<div class="panel-body">
									<table id="userTable" class="hundredPercentWidth table display" cellspacing="0" cellpadding="0">

											<thead>
												<tr><th class="twentyPercentWidth">Salary Type</th>
													<th class="twentyPercentWidth">Date Created</th>
													<th class="twentyPercentWidth">Created By</th>
													<th class="twentyPercentWidth">Approval Status</th>
												</tr>
											</thead>
											<tbody>
												<c:forEach items="${miniBean}" var="innerBean" varStatus="gridRow">
													<tr>
														<td><a href="${appContext}/approveSalaryStructure.do?mId=${innerBean.id}"><c:out value="${innerBean.salaryType.description}" /></a></td>
														<td><c:out value="${innerBean.lastModTs}" /></td>
														<td><c:out value="${innerBean.initiator.actualUserName}" /></td>
														<td><c:out value="${innerBean.approvalStatusStr}" /></td>
													</tr>
												</c:forEach>
											</tbody>

										 </table>
										 </td>
										 </tr>


										<tr>
										  <td class="buttonRow" align="right">
                                           <input type="image" name="_cancel" value="cancel" title="Close Report" src="images/close.png">
                                          </td>
										</tr>
										 <tr>
                                       <%@ include file="/WEB-INF/jsp/footerFile.jsp"%>
                                         </tr>
									</table>

							</td>

						</tr>

					</table>



	</form:form>
	<script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
	<script type="text/javascript">
		$(function() {

			$("#userTable").DataTable({
				"pageLength": 20,
				//can also use "columns" instead of "columnDefs". "columns" take precedence over "columnDefs"
				//also properties higher up, take precedence over those below
				"columnDefs":[
					{"targets": 0, "orderable" : true},
					{"targets": 2, "searchable" : true },
					{"targets": 2, "orderable" : true }
					//{"targets": [0, 1], "orderable" : false }
				]
			});
		});
	</script>
</body>

</html>
