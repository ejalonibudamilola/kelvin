<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            I.P.P.M.S Ticket Form
        </title>
        <link rel="stylesheet" href="styles/omg.css" type="text/css">
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="stylesheet" href="<c:url value="styles/epayroll.css"/>" type="text/css"/>
		<link rel="stylesheet" href="css/displaytagOrig.css" type="text/css"/>
		<link rel="stylesheet" href="css/screen.css" type="text/css"/>
		<link rel="stylesheet" href="${appContext}/dataTables/css/jquery.dataTables.min.css" type="text/css">
        <link rel="stylesheet" href="styles/spinner.css" TYPE="text/css" />
        <script type="text/javascript">
        <!--
        function popup(url,windowname)
        {
         var width  = 1000;
         var height = 800;
         var left   = (screen.width  - width)/2;
         var top    = (screen.height - height)/2;
         var params = 'width='+width+', height='+height;
         params += ', top='+top+', left='+left;
         params += ', directories=no';
         params += ', location=no';
         params += ', menubar=no';
         params += ', resizable=no';
         params += ', scrollbars=yes';
         params += ', status=no';
         params += ', toolbar=no';
         newwin=window.open(url,windowname, params);
         if (window.focus) {newwin.focus()}
         return false;
        }

        // -->
        </script>
    </head>
    <style>
       #contractEmp thead tr th{
         font-size:8pt !important;
       }

.wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
}
       .Open {
           display: inline-block;
           min-width: 16px; /* pixel unit */
           padding: 4px 8px; /* pixel unit */
           border-radius: 9%;
           font-size: 12px;
           text-align: center;
           background: #034B03;
           color: #fefefe;
       }

       .Closed {
                  display: inline-block;
                  min-width: 16px; /* pixel unit */
                  padding: 4px 8px; /* pixel unit */
                  border-radius: 9%;
                  font-size: 12px;
                  text-align: center;
                  background: #FF0000;
                  color: #fefefe;
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
                            <td colspan="2">
                                <div class="title">
                                    <c:out value="${pageTitle}"/><br>
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
								Filter : <form:select path="filterInd" onchange="reloadTable(this);">
                                     <form:option value="-1">Select</form:option>
                                 		 <c:forEach items="${filterList}" var="mList">
                                 			 <form:option value="${mList.currentOtherId}" class="myFilter">${mList.name}</form:option>
                                 		 </c:forEach>
                                 </form:select>


                                <table class="reportMain" cellpadding="0" cellspacing="0" width="100%">
                                    <tr>
                                        <td valign="top">
											<p>&nbsp;</p>
											<table id="approveUpdates" name="approveUpdates"  class="display table">
											<thead>
											<th>
											Approval Memo
											</th>
											<th>
                                            Subject
                                            </th>
                                            <th>
                                            Approval Status
                                            </th>
                                            <th>
                                            Approval/Reject Date
                                            </th>
                                            <th>
                                            Approver
                                            </th>
                                            <th>
                                            Ticket Status
                                            </th>
											</tr>
											</thead>
											<tbody>
											<c:forEach var="appr" items="${miniBean.objectList}">
											<tr>
											<td>
											<a href="#" onClick="showModal(${appr.id}, ${appr.ticketId}); return false" title="${appr.approvalMemo}">
											<c:out value="${appr.memoSubString}"/>
											</a>
											</td>
											<td>
                                            <c:out value="${appr.subject}"/>
                                            </td>
                                            <td>
                                            <c:out value="${appr.approvalStatusStr}"/>
                                            </td>
                                            <td>
                                            <c:out value="${appr.approvalDateStr}"/>
                                            </td>
                                            <td>
                                            <c:out value="${appr.approver.actualUserName}"/>
                                            </td>
                                            <td>
                                            <c:choose>
                                             <c:when test="${appr.ticketOpen eq 0}">
                                             <a href="${appContext}/${appr.url}" title="View">
                                            <span class="Open"><c:out value="${appr.ticketOpenStr}"/></span>
                                            </a>
                                            </c:when>
                                            <c:otherwise>
                                            <a href="#" onClick="showModal(${appr.id}, ${appr.ticketId}); return false" title="${appr.approvalMemo}">
                                             <span class="Closed"><c:out value="${appr.ticketOpenStr}"/></span>
                                             </a>
                                            </c:otherwise>
                                            </c:choose>
                                            </td>
											</tr>
											</c:forEach>
											</tbody>
											</table>
										</td>
                                    </tr>
                                    <tr>

                                    </tr>
                                </table>
                                </div>

                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
             <c:if test="${(roleBean.superAdmin or roleBean.privilegedUser) and openTicketsExists}">
            <tr>
               <td class="buttonRow">
                  <input type="image" name="_closeOpenTickets" value="closeOpenTickets" title="Close All Open Tickets" src="images/close_o.png">
                </td>
              </tr>
                </c:if>
            <tr>
                <%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
            </tr>
        </table>
        <div class="spin"></div>
        </form:form>
        <div class="spin"></div>
        <script src="<c:url value="/dataTables/js/jquery.dataTables.min.js"/>"></script>
        <script>
                      $(function() {
                         $("#approveUpdates").DataTable({
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


                    function showModal(id, tId){
                    window.location.href = "${appContext}/approvalUpdates.do?tid="+tId;
                    };
                    function showModal2(url){
                         window.location.href = "${appContext}/"+url;
                      };


                   function reloadTable(selVal){
                       $(".spin").show();
                       var id = selVal.value;
                       console.log("ID here is "+id);
                       if(id=="-1"){
                          console.log("Stay my id here is "+id);
                       }
                       else{
                           var s = "emptyStr"
                           console.log("value is "+id);
                           var url="${appContext}/approvalUpdates.do";
                           console.log("fid is "+id);
                           $.ajax({
                              type: "GET",
                              url: url,
                              success: function (response) {
                                 console.log("result is "+response);
                                 window.location.href ="${pageContext.request.contextPath}/approvalUpdates.do?op=" + id+ "&s=" + s
                              },
                              error: function (e) {
                                 alert('Error: ' + e);
                              }
                           });
                       }
                   }
window.onload = function exampleFunction() {
                        $(".loader").hide();
                    }

        </script>
    </body>
</html>
