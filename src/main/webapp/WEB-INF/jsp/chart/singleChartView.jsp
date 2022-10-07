
<style>
        #rhead {background: #EBF6FD;}
        #rhead td{font-size:11pt; font-weight: bold;}
        .report tr:nth-child(even) {background: #EBF6FD; font-size:8pt}
        .report tr:nth-child(odd) {background: white; font-size:8pt}
    </style>
    <table class="main" width="90%" border="1" bordercolor="#33c0c8"  cellspacing="0" cellpadding="0" align="center">
        <%@ include file="/WEB-INF/jsp/headerFile.jsp" %>
        <tr>
            <td>
        		<div class="title">Chart Report For ${cBean.chartTitle}</div>
        		<div class="" style="display:none">
                    <p id="mId">${cBean.runMonth}</p>
                	<p id="yId">${cBean.runYear}</p>
                </div>
        	</td>
        </tr>
       <!-- <tr>
           <td>
                 <p style="padding-left: 12px; color:#b76b33" class="">
                	Select Month &amp; Year
                	<span class>
                       <select id="" style="width:20%; font-size:13px; margin-left:3px; margin-top:4px">
                          <option value = "">Select Month</option>
                             <c:forEach items="${cBean.monthList}" var="mList">
                                <option value="${mList.id}">${mList.name}</option>
                             </c:forEach>
                       </select>
                       <select id="" style="width:20%; font-size:13px; margin-left:3px; margin-top:4px">
                          <option value="">Select Year</option>
                             <c:forEach items="${cBean.yearList}" var="yList">
                                <option value="${yList.id}">${yList.id}</option>
                             </c:forEach>
                       </select>
                       <button style="height:27px; margin-bottom:2px; background-color:#b76b33; border-color:#b76b33" type="submit" class="btn btn-primary btn-sm">Update</button>
                   </span>
                 </p>
           </td>
        </tr> -->
        <tr>
           <td>
               <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
                    <tr>
                        <td>
                        <div class="row">
                            <div style="font-size: 12px; padding-top:10px; text-decoration: underline; padding-left:20px" class="col-md-12" id="back">
                                <a href="showChartDashboard.do?runMonth=${cBean.runMonth}&runYear=${cBean.runYear}">&lt;&lt;--Go Back</a>
                            </div>
                        </div>
                        <!-- <a style = "margin:8px 0 0 8px" class="btn btn-outline-secondary" href="showChartDashboard.do?runMonth=${cBean.runMonth}&runYear=${cBean.runYear}" role="button">Back</a> -->
                        <div class="row">
                            <div class="col-md-2"></div>
                            <div style = "text-align:center" class="col-md-8">
                                <select id="scch" class="custom-select mt-2" style="width:30%; font-size:13px; border-radius:15px">
                                    <option value="bar">Change Chart Type</option>
                                    <option value="bar">Bar Chart</option>
                                    <option value="line">Line Chart</option>
                                </select>
                                <canvas style="height: 300px !important" id="sChart"></canvas>
                            </div>
                            <div class="col-md-2"></div>
                        </div>

                        <div class="row">
                            <div class="col-md-2"></div>
                            <div class="col-md-8" style="margin-bottom: 2%">
                               <!-- <table class="table table-bordered table-hover table-sm">
                                  <thead>
                                     <tr>
                                        <td scope="col" colspan="2">
                                           <strong>
                                                <h6 style = "color:#495057; text-align:center;">${tableTitle}</h6>
                                            </strong>
                                        </td>
                                     </tr>
                                  </thead>
                                  <tbody>
                                     <c:forEach items="${displayData}" var="entry">
                                        <tr>
                                            <td><h6 style = "color:#495057">${entry.key}</h6></td>
                                            <td><h6 style = "color:#495057">${entry.value}</h6></td>
                                        </tr>
                                     </c:forEach>
                                  </tbody>
                               </table> -->

                               <table class="report" cellspacing="0" cellpadding="0">
                                  <thead>
                               	     <tr id="rhead">
                               		    <td scope="col" colspan="2" class="tableCell" valign="top"  align="center">${tableTitle}</td>
                               	    </tr>
                               	  </thead>
                               	  <tbody>
                               	     <c:forEach items="${displayData}" var="entry">
                                        <tr>
                                          <td class="tableCell" valign="top">${entry.key}</td>
                                          <td class="tableCell" valign="top">${entry.value}</td>
                                        </tr>
                                     </c:forEach>
                                  </tbody>
                               </table>
                            </div>
                            <div class="col-md-2"></div>
                        </div>
                        <div class="spinSC"></div>
                        </td>
                    </tr>
               </table>
           </td>
        </tr>
        <tr>
			<%@ include file="/WEB-INF/jsp/footerForChart.jsp" %>
		</tr>
    </table>

         <table id="BarX" class="table" style="visibility:hidden; display:none">
                <thead>
                    <tr>
                        <th>x-variable</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach begin="0" var ="i" end="${cBean.barXAxisSize -1}">
                        <tr id="trBX_${i}">
                            <td>${cBean.barXAxis.get(i)}</td>
                        </tr>
                    </c:forEach>
                </tbody>
         </table>

         <table id="BarY" class="table" style="visibility:hidden; display:none">
                <thead>
                    <tr>
                        <th>y-variable</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach begin="0" var ="i" end="${cBean.barYAxisSize - 1}">
                        <tr id="trBY_${i}"><td>${cBean.barYAxis.get(i)}</td></tr>
                    </c:forEach>
                 </tbody>
                 <div id="result1"></div>
         </table>

        <script>
        var surl = "${cBean.url}";
        console.log("url in single chart view is "+url);
        var chartTitle = "${cBean.chartTitle}";
        console.log("chartTitle is "+chartTitle);
        var ylabel = "${cBean.verticalLabel}";
        var labelForCD = "${cBean.labelForCD}";
        var xList = [];
        for(let i=0; i<${cBean.barXAxisSize}; i++){
        $("#trBX_"+(i)).children().each(function() {
        var cellText = $(this).html();
        console.log("celltext.."+cellText);
        xList.push(cellText);
        });
        }
        console.log("x axis is now... "+xList);
        var srm = document.getElementById('mId').innerHTML;
        var sry = document.getElementById('yId').innerHTML;
        console.log("run month and year is "+rm + " "+ry);
        </script>
        <script>
                var yList = [];
                for(let i=0; i<${cBean.barYAxisSize}; i++){
                $("#trBY_"+(i)).children().each(function() {
                var cellText = $(this).html();
                console.log("celltext.."+cellText);
                yList.push(cellText);
                });
                }
                console.log("y axis is now... "+yList);
        </script>
        <script src="scripts/datatables.min.js"></script>
        <script src="scripts/popper.min.js"></script>
        <script src="scripts/bootstrap.js"></script>
        <script src="scripts/mainForSC.js"></script>


