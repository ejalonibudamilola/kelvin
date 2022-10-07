<%--
    Document   : receivedMessages
    Created on : Apr 24, 2021, 8:50:46 PM
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
        <script src="scripts/jquery-3.4.1.min.js"></script>


        <title>Chat Module</title>

        <style>
           textarea {
             overflow-y: scroll !important;
             height: 200px !important;
             resize: none;
             font-size:11px !important;
           }
        </style>
    </head>
    <body id="result">

        <table class="main" width="75%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
            <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
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
                                <div id="sent" style="padding-left:5px; padding-top:5px; margin-bottom:5px;>
                                    <p style="border:3px; border-style:solid; border-color:#FF0000; color:red">${sent}</p>
                                </div>
                                <div style="border:1px solid #e7e7e7">
                                    <div style="margin:2%" class="panel panel-danger">
                                        <div class="panel-heading">
                                            <p style="font-size:12px" class="panel-title lead">Messages Received</p>
                                        </div>
                                        <div class="panel-body">
                                            <table id ='mtable' class="hundredPercentWidth table display" cellspacing="0" cellpadding="0">
                                                <thead>
                                                    <tr>
                                                        <td class="twentyPercentWidth">From</td>
                                                        <td class="twentyPercentWidth">Time</td>
                                                        <td class="thirtyPercentWidth">Subject</td>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach items="${msg}" var ="msg">
                                                        <c:choose>
                                                            <c:when test="${msg.dataStatus eq 0}">
                                                                <tr>
                                                                    <td><b><a href="#" onClick="showMessage(${msg.id}); return false">${msg.sender}</a></b></td>
                                                                    <td><b>${msg.timeSent}</b></td>
                                                                    <td><b>${msg.subject}</b></td>
                                                                </tr>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <tr>
                                                                    <td><a href="#" onClick="showMessage(${msg.id}); return false">${msg.sender}</a></td>
                                                                    <td>${msg.timeSent}</td>
                                                                    <td>${msg.subject}</td>
                                                                </tr>
                                                            </c:otherwise>
                                                        </c:choose>
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
                                        <div id="replyDiv" style="display:none">
                                            <img id="reply" style="margin-left:12px" src="images/reply.png">
                                            <p style="display:none" id="showId">${fid}</p>
                                        </div>
                                    </div>
                                </div>
                                <div id="result1"></div>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>

           <tr>
              <%@ include file="/WEB-INF/jsp/footerFile.jsp" %>
           </tr>
        </table>
        <script src="dataTables/js/jquery.dataTables.js"></script>
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

                function popup(url,windowname){
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

                function showMessage(id){
                    var url="${appContext}/messaging.do";
                    console.log("fid is "+id);
                    $.ajax({
                       type: "GET",
                       url: url,
                       data: {
                          rid: id
                       },
                       success: function (response) {
                          for (var i = 0; i < 2; i++) {
                            $('#showId').html(response[0]);
                            $('#showBody').html(response[1]);
                            $('#replyDiv').css("display", "block");
                          }
                       },
                       error: function (e) {
                          alert('Error: ' + e);
                       }
                    });
                };

                $("#reply").click(function(e){
                    var id = $('#showId').text();
                    var url="${appContext}/messaging.do";
                    console.log("id in reply is "+id);
                    $.ajax({
                       type: "GET",
                       url: url,
                       data: {
                          replyId: id
                       },
                       success: function (response) {
                            $('#result1').html(response);
                            $('#modalShow').modal('show');
                       },
                       error: function (e) {
                          alert('Error: ' + e);
                       }
                    });
                });
        </script>
    </body>
</html>
