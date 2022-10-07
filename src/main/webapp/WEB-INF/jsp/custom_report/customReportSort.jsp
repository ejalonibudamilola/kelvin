<%--
    Document   : customReport
    Created on : Mar 16, 2021, 12:15:20 PM
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

        <title>Custom Report</title>
    </head>
    <body id="result">
    <style>
            #rhead {background: #EBF6FD;}
            #rhead td{font-size:8pt; font-weight: bold;}
            .report tr:nth-child(even) {background: #EBF6FD; font-size:8pt}
            .report tr:nth-child(odd) {background: white; font-size:8pt}
            .report td{ text-align:center}
    </style>
        <table class="main" width="60%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
           <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
           <tr>
              <td>
                 <div style="color:red; padding: 4px 10px" class="title">Custom Report Generator</div>
              </td>
           </tr>
           <tr>
              <td>
                 <div style="margin:2% 0 ">
                    <table style="width:60% !important" class="report" cellspacing="0" cellpadding="0" align="center">
                        <thead>
                            <tr id="rhead">
                                <td scope="col" class="tableCell" valign="top">#</td>
                                <td scope="col" class="tableCell" valign="top">Total Indicator</td>
                                <td scope="col" class="tableCell" valign="top">Group By</td>
                                <td scope="col" class="tableCell" valign="top">Sub Group By</td>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach begin="0" var ="i" end="${headerSize - 1}">
                                <tr>
                                    <td class="tableCell" valign="top">${headers.get(i)}</td>
                                    <td class="tableCell" valign="top"><input value="${i}"  name="totalInd" type="checkbox"/></td>
                                    <td class="tableCell" valign="top"><input value="${i}" name="groupBy" type="checkbox"/></td>
                                    <td class="tableCell" valign="top"><input value="${i}" name="subGroupBy" type="checkbox"/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <div style="text-align:center; margin-top:1%">
                    <img id="go" src="images/go_h.png">
                    <!-- <a href="customReportGenerator.do?checked=checked"><img id="go" src="images/go_h.png"></a> -->
                    </div>
                 </div>
              </td>
           </tr>

           <tr>
              <td style="background-color: #33C0C8; font-size: 12px; text-align: center">
                ©2020-2021 GNL Systems Ltd.
              </td>
           </tr>
        </table>

        <script src="scripts/jquery-3.4.1.min.js"></script>

        <script>
            $('#go').click(function(e) {

                var totalInd=[];
                var groupBy=[];
                var subGroupBy=[];

                $('input[name="totalInd"]:checked').each(function() {
                    totalInd.push(this.value);
                });

                $('input[name="groupBy"]:checked').each(function() {
                   groupBy.push(this.value);
                });

                $('input[name="subGroupBy"]:checked').each(function() {
                   subGroupBy.push(this.value);
                });

                var total = totalInd.toString();
                var group = groupBy.toString();
                var sub = subGroupBy.toString();

                console.log("totalInd is "+total);
                console.log("groupBy is "+group);
                console.log("subGroupBy is "+sub);

                var url ="customReportGenerator.do";

                $.ajax({
                   type: "GET",
                   url: url,
                   success: function (response) {
                     // do something ...
                       window.location.href ="${pageContext.request.contextPath}/customReportGenerator.do?total=" + total+ "&group=" + group+ "&sub=" + sub
                   },
                   error: function (e) {
                      alert('Error: ' + e);
                   }
                });
            });
        </script>
    </body>
</html>