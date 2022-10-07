<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<title>Approve Mass Reassignment</title>
<link rel="stylesheet" href="styles/omg.css" type="text/css" />
<link rel="stylesheet" href="styles/skye.css" type="text/css" media ="screen">
<link rel="stylesheet" href="css/screen.css" type="text/css"/>
<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
</head>

<style>
    thead tr th{
        font-size:8pt !important;
    }
</style>
<body class="main">
    <form:form modelAttribute="miniBean">
	<table class="main" width="80%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
		<%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
		<tr>
			<td>
				<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

                    <tr>
                        <td colspan="2">
                            <div class="title">Mass Reassignment Approvals</div>
                        </td>
                    </tr>

                    <tr>
                        <td valign="top" class="mainBody" id="mainbody">
                            <table border="0" cellspacing="0" cellpadding="3" width="80%" align="left" >
                                <tr align="left">
                                    <td class="activeTH">View Criteria</td>
                                </tr>
                                <tr>
                                    <td class="activeTD">
                                        <table border="0" cellspacing="0" cellpadding="2" width="100%" align="left">
                                            <tr>
                                               <td align=right width="10%">
                                                 View by
                                               </td>
                                               <td>
                                                  <select id="viewOpt" onchange="myFunction()">
                                                     <option value="0">Pending Mass Reassignment</option>
                                                     <option value="1">Approved Mass Reassignment</option>
                                                     <option value="2">Rejected Mass Reassignment</option>
                                                     <option value="3">All Mass Reassignment</option>
                                                  </select>
                                               </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr>
                       <td valign="top">
                          <div id="pDiv" style="margin:1% 1%" class="panel panel-danger">
                             <div class="panel-heading">
                                <p style="font-size:10pt" class="panel-title lead">
                                  List of Pending Mass Reassignment Awaiting Approval
                                </p>
                             </div>
                             <div class="panel-body">
                                <table id="PMR" class="hundredPercentWidth table display" cellspacing="0" cellpadding="0">
                                   <thead>
                                       <tr>
                                          <th>Name</th>
                                          <th>Created By</th>
                                          <th>Creation Date</th>
                                          <th>From Pay Group</th>
                                          <th>To Pay Group</th>
                                          <th>Monthly Salary Implication</th>
                                          <th>Employee Affected</th>
                                       </tr>
                                   </thead>
                                   <tbody>
                                       <c:forEach items="${pList}" var="pList" varStatus="currIndex">
                                           <tr>
                                               <td>
                                                   <a href="${appContext}/approveMassReassign.do?aid=${pList.id}">
                                                      <c:out value="${pList.name}"/>
                                                   </a>
                                               </td>
                                               <td>
                                                  <c:out value="${pList.createdBy.actualUserName}"/>
                                               </td>
                                               <td><c:out value="${pList.creationDate}"/></td>
                                               <td><c:out value="${pList.fromSalaryType.description}"/></td>
                                               <td><c:out value="${pList.toSalaryType.description}"/></td>
                                               <td><c:out value="${pList.monthlyImplication}"/></td>
                                               <td><c:out value="${pList.totalEmp}"/></td>
                                           </tr>
                                       </c:forEach>
                                   </tbody>
                                </table>
                             </div>
                             &nbsp; &nbsp; <input type="image" name="_close" value="close" title="Close" class="" src="images/close.png">
                          </div>
                       </td>
                    </tr>

                    <tr>
                       <td valign="top">
                          <div id="aDiv" style="margin:1% 1%;" class="panel panel-danger">
                             <div class="panel-heading">
                                <p style="font-size:10pt" class="panel-title lead">
                                   List of Approved Mass Reassignment
                                </p>
                             </div>
                             <div class="panel-body">
                                <table id="AMR" class="hundredPercentWidth table display" cellspacing="0" cellpadding="0">
                                   <thead>
                                       <tr>
                                          <th>Name</th>
                                          <th>Created By</th>
                                          <th>Creation Date</th>
                                          <th>From Pay Group</th>
                                          <th>To Pay Group</th>
                                          <th>Monthly Salary Implication</th>
                                          <th>Employee Affected</th>
                                       </tr>
                                   </thead>
                                   <tbody>
                                       <c:forEach items="${aList}" var="aList" varStatus="currIndex">
                                           <tr>
                                               <td>
                                                   <a href="${appContext}/approveMassReassign.do?aid=${aList.id}">
                                                      <c:out value="${aList.name}"/>
                                                   </a>
                                               </td>
                                               <td>
                                                  <c:out value="${aList.createdBy.actualUserName}"/>
                                               </td>
                                               <td><c:out value="${aList.creationDate}"/></td>
                                               <td><c:out value="${aList.fromSalaryType.description}"/></td>
                                               <td><c:out value="${aList.toSalaryType.description}"/></td>
                                               <td><c:out value="${aList.monthlyImplication}"/></td>
                                               <td><c:out value="${aList.totalEmp}"/></td>
                                           </tr>
                                       </c:forEach>
                                   </tbody>
                                </table>
                             </div>
                             &nbsp; &nbsp; <input type="image" name="_close" value="close" title="Close" class="" src="images/close.png">
                          </div>
                       </td>
                    </tr>

                    <tr>
                       <td valign="top">
                          <div id="rDiv" style="margin:1% 1%;" class="panel panel-danger">
                             <div class="panel-heading">
                                <p style="font-size:10pt" class="panel-title lead">
                                   List of Mass Reassignment Rejected
                                </p>
                             </div>
                             <div class="panel-body">
                                <table id="RMR" class="hundredPercentWidth table display" cellspacing="0" cellpadding="0">
                                   <thead>
                                       <tr>
                                          <th>Name</th>
                                          <th>Created By</th>
                                          <th>Creation Date</th>
                                          <th>From Pay Group</th>
                                          <th>To Pay Group</th>
                                          <th>Monthly Salary Implication</th>
                                          <th>Employee Affected</th>
                                       </tr>
                                   </thead>
                                   <tbody>
                                       <c:forEach items="${rList}" var="rList" varStatus="currIndex">
                                           <tr>
                                               <td>
                                                   <a href="${appContext}/approveMassReassign.do?aid=${mList.id}">
                                                      <c:out value="${rList.name}"/>
                                                   </a>
                                               </td>
                                               <td>
                                                  <c:out value="${rList.createdBy.actualUserName}"/>
                                               </td>
                                               <td><c:out value="${rList.creationDate}"/></td>
                                               <td><c:out value="${rList.fromSalaryType.description}"/></td>
                                               <td><c:out value="${rList.toSalaryType.description}"/></td>
                                               <td><c:out value="${rList.monthlyImplication}"/></td>
                                               <td><c:out value="${rList.totalEmp}"/></td>
                                           </tr>
                                       </c:forEach>
                                   </tbody>
                                </table>
                             </div>
                             &nbsp; &nbsp; <input type="image" name="_close" value="close" title="Close" class="" src="images/close.png">
                          </div>
                       </td>
                    </tr>

                    <tr>
                       <td valign="top">
                          <div id="allDiv" style="margin:1% 1%;" class="panel panel-danger">
                             <div class="panel-heading">
                                <p style="font-size:10pt" class="panel-title lead">
                                   List of All Mass Reassignment
                                </p>
                             </div>
                             <div class="panel-body">
                                <table id="allMR" class="hundredPercentWidth table display" cellspacing="0" cellpadding="0">
                                   <thead>
                                       <tr>
                                          <th>Name</th>
                                          <th>Created By</th>
                                          <th>Creation Date</th>
                                          <th>From Pay Group</th>
                                          <th>To Pay Group</th>
                                          <th>Monthly Salary Implication</th>
                                          <th>Employee Affected</th>
                                       </tr>
                                   </thead>
                                   <tbody>
                                       <c:forEach items="${allList}" var="allList" varStatus="currIndex">
                                           <tr>
                                               <td>
                                                   <a href="${appContext}/approveMassReassign.do?aid=${mList.id}">
                                                      <c:out value="${allList.name}"/>
                                                   </a>
                                               </td>
                                               <td>
                                                  <c:out value="${allList.createdBy.actualUserName}"/>
                                               </td>
                                               <td><c:out value="${allList.creationDate}"/></td>
                                               <td><c:out value="${allList.fromSalaryType.description}"/></td>
                                               <td><c:out value="${allList.toSalaryType.description}"/></td>
                                               <td><c:out value="${allList.monthlyImplication}"/></td>
                                               <td><c:out value="${allList.totalEmp}"/></td>
                                           </tr>
                                       </c:forEach>
                                   </tbody>
                                </table>
                             </div>
                             &nbsp; &nbsp; <input type="image" name="_close" value="close" title="Close" class="" src="images/close.png">
                          </div>
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

    	    $('#aDiv').css("display", "none");
            $('#rDiv').css("display", "none");
            $('#allDiv').css("display", "none");

    		$(function() {
    			$("#PMR").DataTable({
    				"order" : [ [ 1, "asc" ] ],
    				//can also use "columns" instead of "columnDefs". "columns" take precedence over "columnDefs"
    				//also properties higher up, take precedence over those below
    				"columnDefs":[
    					{"targets": 0, "orderable" : false},
    					{"targets": 4, "searchable" : false }
    					//{"targets": [0, 1], "orderable" : false }
    				]
    			});
    		});

            $(function() {
    			$("#AMR").DataTable({
    				"order" : [ [ 1, "asc" ] ],
    				//can also use "columns" instead of "columnDefs". "columns" take precedence over "columnDefs"
    				//also properties higher up, take precedence over those below
    				"columnDefs":[
    					{"targets": 0, "orderable" : false},
    					{"targets": 4, "searchable" : false }
    					//{"targets": [0, 1], "orderable" : false }
    				]
    			});
    		});

            $(function() {
    			$("#RMR").DataTable({
    				"order" : [ [ 1, "asc" ] ],
    				//can also use "columns" instead of "columnDefs". "columns" take precedence over "columnDefs"
    				//also properties higher up, take precedence over those below
    				"columnDefs":[
    					{"targets": 0, "orderable" : false},
    					{"targets": 4, "searchable" : false }
    					//{"targets": [0, 1], "orderable" : false }
    				]
    			});
    		});

            $(function() {
    			$("#allMR").DataTable({
    				"order" : [ [ 1, "asc" ] ],
    				//can also use "columns" instead of "columnDefs". "columns" take precedence over "columnDefs"
    				//also properties higher up, take precedence over those below
    				"columnDefs":[
    					{"targets": 0, "orderable" : false},
    					{"targets": 4, "searchable" : false }
    					//{"targets": [0, 1], "orderable" : false }
    				]
    			});
    		});


    		function myFunction() {
               var vt = $('#viewOpt').val();
               console.log("vt is "+vt);
               if(vt=='0'){
                  $('#pDiv').css("display", "block");
                  $('#aDiv').css("display", "none");
                  $('#rDiv').css("display", "none");
                  $('#allDiv').css("display", "none");
               }
               else if(vt=='1'){
                  $('#pDiv').css("display", "none");
                  $('#aDiv').css("display", "block");
                  $('#rDiv').css("display", "none");
                  $('#allDiv').css("display", "none");
               }
               if(vt=='2'){
                  $('#pDiv').css("display", "none");
                  $('#aDiv').css("display", "none");
                  $('#rDiv').css("display", "block");
                  $('#allDiv').css("display", "none");
               }
               else if(vt=='3'){
                  $('#pDiv').css("display", "none");
                  $('#aDiv').css("display", "none");
                  $('#rDiv').css("display", "none");
                  $('#allDiv').css("display", "block");
               }
            }
    	</script>
</body>
</html>
