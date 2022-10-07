<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
       <link rel="stylesheet" href="styles/omg.css" type="text/css" />
       <link rel="stylesheet" href="styles/skye.css" type="text/css" media="screen">
       <link href="css/datatables.min.css" rel="stylesheet">
       <link rel="icon" type="image/png" href="/ogsg_ippms/images/coatOfArms.png">
       <link rel="stylesheet" href="styles/spinner.css" TYPE="text/css" />


        <title>Chart</title>
    </head>

    <body id="result">

    <table class="main" width="90%" border="1" bordercolor="#33c0c8" cellspacing="0" cellpadding="0" align="center">
        <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
       <tr>
        	<td>
        		<div class="title">Executive Governor</div>
        	</td>
       </tr>
        <tr>
			<td>
				<div class="title">Dashboard for ${month} , ${year}</div>
				<div class="" style="display:none">
				    <p id="mId">${monthValue}</p>
				    <p id="yId">${yearValue}</p>
				</div>
			</td>
		</tr>
		<tr style="background-color: #cccccc2e">
            <td>
        		<form action="showChartDashboard.do" method="get" modelAttribute="chartMiniBean">
                    <div style="display:flex">
                        <h6 style="padding-left: 12px;"> Select Month &amp; Year</h6>
                        <span style="margin-top: 6px;margin-left: 5px;">
                            <select name="runMonth" id="rMonth" style="width:120px; height:23px; padding-top:1px">
                                    <option value = "${monthValue}">${month}</option>
                                   <c:forEach items="${monthList}" var="mList">
                                      <option value="${mList.id}">${mList.name}</option>
                                   </c:forEach>
                            </select>
                            <select name="runYear" id="rYear" style="width:120px; height:23px; padding-top:1px">
                                    <option value="${yearValue}">${year}</option>
                                    <c:forEach items="${yearList}" var="yList">
                                       <option value="${yList.id}">${yList.id}</option>
                                    </c:forEach>
                                    <option value="2014">2014</option>
                            </select>
                        </span>
                        <span style="padding-left:5px; padding-top:5px">
                            <input style="width:80%" type="image" name="submit" value="ok" title="Update" class="" src="images/update.png">
                        </span>
                    </div>
        		</form>
        	</td>
        </tr>
        <tr>
           <td>
               <table style="background-color: #cccccc2e" width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    <tr>
               			<td>
               			    <div style ="padding: 6px 8px 0 8px" class="row">
                                <div style="padding-right: 3px !important;" class="col-md-6 card1">
                                    <div style="background-color: white"  class="card h-100">
                                        <select id="cch1" style="width:30%; font-size:13px; margin-left:3px; margin-top:4px; border-radius:15px">
                                           <option value="bar">Change Chart Type</option>
                                           <option value="bar">Bar Chart</option>
                                           <option value="line">Line Chart</option>
                                        </select>
                                        <canvas id="barChart1"></canvas>
                                    </div>
                                </div>
                                <div style="padding-left: 3px !important;" class="col-md-6 card2">
                                   <div style="background-color: white" class ="card h-100">
                                      <select id="cch2" class="custom-select ml-4 mt-2" style="width:30%; font-size:13px; margin-left:3px; margin-top:4px; border-radius:15px">
                                         <option value="bar">Change Chart Type</option>
                                         <option value="bar">Bar Chart</option>
                                         <option value="line">Line Chart</option>
                                      </select>
                                      <canvas id="barChart2"></canvas>
                                   </div>
                                </div>
                            </div>
               			</td>
               		</tr>

               		<tr>
               		   <td>
               		      <div style ="padding: 6px 8px 4px 8px" class="row">
                             <div class="col-md-4 card3">
                                <div style="background-color: white" class="card h-100">
                                   <canvas height=200 id="pieChart1"></canvas>
                                </div>
                             </div>
                              <div class="col-md-4 card4">
                                <div style="background-color: white" class="card h-100">
                                   <canvas height=200 id="pieChart2"></canvas>
                                </div>
                             </div>
                             <div class="col-md-4 card5">
                                <div style="background-color: white" class="card h-100">
                                   <canvas height=200 id="pieChart3"></canvas>
                                </div>
                             </div>
                          </div>
               		   </td>
               		</tr>

               		<tr>
               		   <td>
               		      <div style ="padding: 6px 8px 6px 8px" class="row">
                             <div style="padding-right: 3px !important;"  class="col-md-6 card6">
                                <div style="background-color: white"  class="card h-100">
                                   <select id="cch6" style="width:30%; font-size:13px; margin-left:3px; margin-top:4px; border-radius:15px">
                                      <option value="bar">Change Chart Type</option>
                                      <option value="bar">Bar Chart</option>
                                      <option value="line">Line Chart</option>
                                   </select>
                                   <canvas id="barChart3"></canvas>
                                </div>
                             </div>
                             <div style="padding-left: 3px !important;"  class="col-md-6 card7">
                                <div style="background-color: white" class ="card h-100">
                                   <select id="cch7" class="custom-select ml-4 mt-2" style="width:30%; font-size:13px; margin-left:3px; margin-top:4px; border-radius:15px">
                                      <option value="bar">Change Chart Type</option>
                                      <option value="bar">Bar Chart</option>
                                      <option value="line">Line Chart</option>
                                   </select>
                                   <canvas id="barChart4"></canvas>
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
        <div class="spin"></div>
      <!--  <div id="result"></div> -->
    </table>

    <table id="BarX1" class="table" style="visibility:hidden; display:none">
                <tbody>
                <c:forEach begin="0" var ="i" end="${BarX1AxisSize - 1}">
                 <tr id="trBX1_${i}">
                 <td>${BarX1Axis.get(i)}</td>
                 </tr>
                </c:forEach>
                 </tbody>
    </table>

    <table id="BarY1" class="table" style="visibility:hidden; display:none">
                <tbody>
                <c:forEach begin="0" var ="i" end="${BarY1AxisSize - 1}">
                 <tr id="trBY1_${i}"><td>${BarY1Axis.get(i)}</td></tr>
                </c:forEach>
                 </tbody>
    </table>

    <table id="BarX2" class="table" style="visibility:hidden; display:none">
                    <tbody>
                    <c:forEach begin="0" var ="i" end="${BarX2AxisSize - 1}">
                     <tr id="trBX2_${i}">
                     <td>${BarX2Axis.get(i)}</td>
                     </tr>
                    </c:forEach>
                     </tbody>
    </table>

    <table id="BarY2" class="table" style="visibility:hidden; display:none">
                    <tbody>
                    <c:forEach begin="0" var ="i" end="${BarY2AxisSize - 1}">
                     <tr id="trBY2_${i}"><td>${BarY2Axis.get(i)}</td></tr>
                    </c:forEach>
                     </tbody>
    </table>

    <table id="PieLabel1" class="table" style="visibility:hidden; display:none">
                        <tbody>
                        <c:forEach begin="0" var ="i" end="${pieLabel1Size - 1}">
                         <tr id="trPX_${i}">
                         <td>${pieLabels1.get(i)}</td>
                         </tr>
                        </c:forEach>
                         </tbody>
    </table>
    <table id="PieData" class="table" style="visibility:hidden; display:none">
                            <tbody>
                            <c:forEach begin="0" var ="i" end="${pieData1Size - 1}">
                             <tr id="trPY_${i}">
                             <td>${pieData1.get(i)}</td>
                             </tr>
                            </c:forEach>
                             </tbody>
    </table>

    <table id="pieLabels2" class="table" style="visibility:hidden; display:none">
                            <tbody>
                            <c:forEach begin="0" var ="i" end="${pieLabel2Size - 1}">
                             <tr id="trPX2_${i}">
                             <td>${pieLabels2.get(i)}</td>
                             </tr>
                            </c:forEach>
                             </tbody>
    </table>
    <table id="PieData2" class="table" style="visibility:hidden; display:none">
                                <tbody>
                                <c:forEach begin="0" var ="i" end="${pieData2Size - 1}">
                                 <tr id="trPY2_${i}">
                                 <td>${pieData2.get(i)}</td>
                                 </tr>
                                </c:forEach>
                                 </tbody>
    </table>

    <table id="pieLabels3" class="table" style="visibility:hidden; display:none">
                            <tbody>
                            <c:forEach begin="0" var ="i" end="${pieLabel3Size - 1}">
                             <tr id="trPX3_${i}">
                             <td>${pieLabels3.get(i)}</td>
                             </tr>
                            </c:forEach>
                             </tbody>
    </table>
    <table id="PieData2" class="table" style="visibility:hidden; display:none">
                                <tbody>
                                <c:forEach begin="0" var ="i" end="${pieData3Size - 1}">
                                 <tr id="trPY3_${i}">
                                 <td>${pieData3.get(i)}</td>
                                 </tr>
                                </c:forEach>
                                 </tbody>
    </table>

    <table id="BarX3" class="table" style="visibility:hidden; display:none">
                    <tbody>
                    <c:forEach begin="0" var ="i" end="${BarX3AxisSize - 1}">
                     <tr id="trBX3_${i}">
                     <td>${BarX3Axis.get(i)}</td>
                     </tr>
                    </c:forEach>
                     </tbody>
        </table>

        <table id="BarY3" class="table" style="visibility:hidden; display:none">
                    <tbody>
                    <c:forEach begin="0" var ="i" end="${BarY3AxisSize - 1}">
                     <tr id="trBY3_${i}"><td>${BarY3Axis.get(i)}</td></tr>
                    </c:forEach>
                     </tbody>
        </table>

        <table id="BarX4" class="table" style="visibility:hidden; display:none">
                    <tbody>
                    <c:forEach begin="0" var ="i" end="${BarX4AxisSize - 1}">
                     <tr id="trBX4_${i}">
                     <td>${BarX4Axis.get(i)}</td>
                     </tr>
                    </c:forEach>
                     </tbody>
        </table>

        <table id="BarY4" class="table" style="visibility:hidden; display:none">
                    <tbody>
                    <c:forEach begin="0" var ="i" end="${BarY4AxisSize - 1}">
                     <tr id="trBY4_${i}"><td>${BarY4Axis.get(i)}</td></tr>
                    </c:forEach>
                     </tbody>
        </table>




    <script src="scripts/jquery-3.4.1.min.js"></script>
       <!-- <script src="scripts/mdb.min.js"></script> -->
        <script src="scripts/chart.js"></script>
        <script src="scripts/chartjs-plugin-datalabels.min.js"></script>
        <script>
        var urll = "${pageContext.request.contextPath}"+"${url}";
        console.log("urll is "+urll);
        var card1Url = "${card1Url}";
        var card2Url = "${card2Url}";
        var card3Url = "${card3Url}";
        var card4Url = "${card4Url}";
        var card5Url = "${card5Url}";
        var card6Url = "${card6Url}";
        var card7Url = "${card7Url}";
        console.log("url is "+urll);
        console.log("card5Url is "+card5Url);
        var xList = [];
        var x2List = [];
        for(let i=0; i<${BarX1AxisSize}; i++){
        $("#trBX1_"+(i)).children().each(function() {
        var cellText = $(this).html();
        xList.push(cellText);
        });
        }

         for(let i=0; i<${BarX2AxisSize}; i++){
                $("#trBX2_"+(i)).children().each(function() {
                var cellText = $(this).html();
                x2List.push(cellText);
                });
         }
        </script>
        <script>
                var yList = [];
                var y2List = [];
                for(let i=0; i<${BarY1AxisSize}; i++){
                $("#trBY1_"+(i)).children().each(function() {
                var cellText = $(this).html();
                yList.push(cellText);
                });
                }
                for(let i=0; i<${BarY2AxisSize}; i++){
                $("#trBY2_"+(i)).children().each(function() {
                var cellText = $(this).html();
                y2List.push(cellText);
                });
                }
        </script>
        <script>
              var pieLabel = [];
              var pieData = [];
              for(let i=0; i<${pieLabel1Size}; i++){
              $("#trPX_"+(i)).children().each(function() {
              var cellText = $(this).html();
              pieLabel.push(cellText);
              });
              }

              for(let i=0; i<${pieData1Size}; i++){
              $("#trPY_"+(i)).children().each(function() {
              var cellText = $(this).html();
              pieData.push(cellText);
              });
              }
        </script>

        <script>
                      var pieLabel2 = [];
                      var pieData2 = [];
                      for(let i=0; i<${pieLabel2Size}; i++){
                      $("#trPX2_"+(i)).children().each(function() {
                      var cellText = $(this).html();
                      pieLabel2.push(cellText);
                      });
                      }

                      for(let i=0; i<${pieData2Size}; i++){
                      $("#trPY2_"+(i)).children().each(function() {
                      var cellText = $(this).html();
                      pieData2.push(cellText);
                      });
                      }
                      console.log("PieLabel2 is "+pieLabel2);
                      console.log("PieData2 is "+pieData2);
        </script>

        <script>
             var pieLabel3 = [];
             var pieData3 = [];
             for(let i=0; i<${pieLabel3Size}; i++){
             $("#trPX3_"+(i)).children().each(function() {
             var cellText = $(this).html();
             pieLabel3.push(cellText);
             });
             }

             for(let i=0; i<${pieData3Size}; i++){
             $("#trPY3_"+(i)).children().each(function() {
             var cellText = $(this).html();
             pieData3.push(cellText);
             });
             }
        </script>

        <script>
        var x3List = [];
        var y3List = [];
             for(let i=0; i<${BarX3AxisSize}; i++){
                    $("#trBX3_"+(i)).children().each(function() {
                    var cellText = $(this).html();
                    x3List.push(cellText);
                    });
             }
             for(let i=0; i<${BarY3AxisSize}; i++){
                             $("#trBY3_"+(i)).children().each(function() {
                             var cellText = $(this).html();
                             y3List.push(cellText);
                             });
             }
        </script>

        <script>
                var x4List = [];
                var y4List = [];
                     for(let i=0; i<${BarX4AxisSize}; i++){
                            $("#trBX4_"+(i)).children().each(function() {
                            var cellText = $(this).html();
                            x4List.push(cellText);
                            });
                     }
                     for(let i=0; i<${BarY4AxisSize}; i++){
                                     $("#trBY4_"+(i)).children().each(function() {
                                     var cellText = $(this).html();
                                     y4List.push(cellText);
                                     });
                     }
        </script>

         <script>
            var rm = document.getElementById('mId').innerHTML;
            var ry = document.getElementById('yId').innerHTML;
            console.log("run month and year is "+rm + " "+ry);
         </script>
         <script src="scripts/main.js"></script>
    </body>
</html>