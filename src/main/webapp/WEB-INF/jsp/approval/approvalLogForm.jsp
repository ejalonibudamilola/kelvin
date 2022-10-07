<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<html>
    <head>
        <title>
            Approval Discussion History Form
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

    <form:form modelAttribute="sBean">
	<c:set value="${sBean}" var="dispBean" scope="request"/>
        <table class="main" width="70%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">

                        <tr>
                            <td colspan="2">
                                <div class="title">
                                    Approval Request Chat<br>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td valign="top" class="mainBody" id="mainbody">

                                <div class="col-12">
								    <p style="color: blue"><b>Name : <c:out value="${sBean.entityName}"/></b></p>
								    <p style="color: blue"><b>Unique ID : <c:out value="${sBean.employeeId}"/></b></p>
								    <p style="color: blue"><b>Subject : <c:out value="${sBean.subject}"/></b></p>
								</div>

								<c:if test="${sBean.ticketOpen eq 1}">
								<div class="col-12">
								    <p style="color: #FF0000">This ticket has been closed!</p>
								</div>
								</c:if>
                                 <div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
 								     <spring:hasBindErrors name="sBean">
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
                                <thead>
                                    <tr>
                                    <td valign="top">
                                <c:forEach var="appr" items="${miniBean}">
                                                <c:choose>
                                                <c:when test="${roleBean.loginId eq appr.sender}">
                                                <div class="row">
                                                <div class="col-4" style="background-color:#E2E2E2; margin-top: 10px; border-radius: 9px; margin-left:20px; margin-right:550px;">
                                                                       <p style="padding: 10px;"><b><c:out value="${appr.initiator.actualUserName}"/>:</b> <br/> <span style="padding:30px;">
                                                                       <c:out value="${appr.approvalMemo}"/> </span>
                                                                       </p>
                                                <span style="margin-left: 400px;"><c:out value="${appr.ticketTime}"/></span>
                                                </div>
                                                </div>
                                                </c:when>
                                                <c:otherwise>
                                                <div class="row">
                                                <div class="col-4" style="background-color:#FFFDD0; margin-top: 10px;   border-radius: 9px; width: 550px; margin-right:20px; float: right;">
                                                                       <p style="padding: 10px;"><b><c:out value="${appr.senderName}"/>:</b><br/>
                                                                        <span style="padding:30px;"> <c:out value="${appr.approvalMemo}"/> </span>
                                                                        </p>
                                                 <span style="float: left;"><c:out value="${appr.ticketTime}"/></span>
                                                </div>
                                                </div>
                                                </c:otherwise>
                                                </c:choose>
                                                </c:forEach>

                                <c:if test="${sBean.ticketOpen eq 0}">
                                                    <div class="form-group" style="padding:10px">
                                                       <label for="exampleFormControlTextarea1">Reply</label>
                                                        <form:textarea path="response" class="form-control" rows="3"/>
                                                    </div>
                                  </c:if>
                                                    <table>
                                                    <tr>
                                                    <td class="buttonRow" align="right">
                                                    <c:if test="${sBean.ticketOpen eq 0}">
                                                    <input type="image" name="_reply" value="reply" alt="Reply" src="images/reply.png">

                                                    <input type="image" name="_close" value="close" alt="Close Ticket" src="images/close_ticket.png">
                                                     </c:if>

                                                    <input type="image" name="_cancel" value="cancel" title="Cancel" class="" src="images/close.png">
                                                    </td>


                                                    </tr>
                                                    </table>


										</td>
                                    </tr>

                                </table>

                            </td>
                        </tr>
                    </table>
                     <div id="result1"></div>
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
                    var url="${appContext}/approvalUpdates.do";
                    console.log("pid is "+id);
                    $.ajax({
                       type: "GET",
                       url: url,
                       data: {
                          tid: tId
                       },
                       success: function (response) {
                          $('#result1').html(response);
                          $('#errorModal').modal('show');
                       },
                       error: function (e) {
                          alert('Error: ' + e);
                       }
                    });
           };
        </script>
    </body>
</html>
