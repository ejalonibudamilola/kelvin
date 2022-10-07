<%--
    Document   : messageDisplay
    Created on : May 4, 2021, 12:50:46 PM
    Author     : damilola-ejalonibu
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="styles/omg.css" type="text/css" />
        <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
        <link rel="icon" type="image/png" href="/ogsg_ippms/images/coatOfArms.png">
        <link rel="stylesheet" href="dataTables/css/jquery.dataTables.min.css" type="text/css">
		<link rel="stylesheet" href="css/bootstrap-multiselect.css">
        <link rel="stylesheet" href="css/jquery-ui.css">

        <title>Chat Platform</title>
    </head>
    <body id="result">
        <table class="main" width="75%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
            <%@ include file="/WEB-INF/jsp/message/composeMessageModal.jsp" %>
            <tr>
                <td>
                    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                        <tr>
                           <td>
                              <div class="title">Chat Room</div>
                           </td>
                        </tr>

                        <tr>
                            <td valign="top" class="mainBody hundredPercentWidth verticalTopAlign" id="mainbody">
                                <div id="sent" style="padding-left:5px; padding-top:5px; margin-bottom:5px;">
                                   <p style="color:green; font-size:13px">${sent}</p>
                                </div>
                                <div style="border:1px solid #e7e7e7">
                                    <div style="margin-top: 1%; margin-left: 2%">
                                        <a style="text-decoration:underline" href="#" data-toggle="modal" data-target="#composeModal">Compose New Message</a>
                                    </div>
                                    <div style="margin:2%" class="panel panel-danger">
                                        <div class="panel-heading">
                                            <p style="font-size:12px" class="panel-title lead">Messages Sent</p>
                                        </div>
                                        <div class="panel-body">
                                            <table id ='mtable' class="hundredPercentWidth table display" cellspacing="0" cellpadding="0">
                                                <thead>
                                                    <tr>
                                                        <td class="twentyPercentWidth">To</td>
                                                        <td class="twentyPercentWidth">Time</td>
                                                        <td class="thirtyPercentWidth">Subject</td>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach items="${msg}" var ="msg">
                                                        <tr>
                                                            <td><a href="#" onClick="showMessage(${msg.id}); return false">${msg.recipient}</a></td>
                                                            <td>${msg.timeSent}</td>
                                                            <td>${msg.subject}</td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                    <div class="panel-body">
                                        <div class="form-group" style="padding:10px">
                                           <label for="exampleFormControlTextarea1">Message Body</label>
                                           <textarea id="showBody" class="form-control" id="exampleFormControlTextarea1" rows="3" readonly>${body}</textarea>
                                        </div>
                                    </div>
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
        <script src="dataTables/js/jquery.dataTables.min.js"></script>
        <script type="text/javascript">
        		$(function() {

        			$("#mtable").DataTable({
        				//"order" : [ [ 1, "asc" ] ],
        				//can also use "columns" instead of "columnDefs". "columns" take precedence over "columnDefs"
        				//also properties higher up, take precedence over those below
        				"columnDefs":[
        					//{"targets": 0, "orderable" : false},
        					//{"targets": 5, "searchable" : false }
        					//{"targets": [0, 1], "orderable" : false }
        				]
        			});
        		});

            function showMessage(id){
                console.log("pageid is "+id);
                var url="${appContext}/messaging.do";
                $.ajax({
                    type: "GET",
                    url: url,
                    data: {
                        sid: id
                    },
                    success: function (response) {
                        $('#showBody').html(response);
                    },
                    error: function (e) {
                        alert('Error: ' + e);
                    }
                });
            };

        </script>
    </body>
</html>
