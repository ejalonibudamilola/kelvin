<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<style>
        .ltgTable th  {background: #EBF6FD; font-size:8pt}
        .ltgTable tr:nth-child(even) {background: #EBF6FD; font-size:8pt}
        .ltgTable tr:nth-child(odd) {background: white; font-size:8pt}
        .ltgTable thead tr th {padding-top:2px; padding-left:5px; padding-bottom:2px}
        .ltgTable tbody tr td {padding-top:2px; padding-left:5px; padding-bottom:2px}
        .dataTables_filter input {margin: 2px 0; height: 20px !important }
        .btn { margin-top: 2px; margin-bottom: 3px; padding: 2px 10px; border: 2px solid #dcdcdc}
</style>

<!-- Modal -->

<div class="modal fade right" id="employeeToMDAModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalPreviewLabel" aria-hidden="true">
  <div style="max-width: 100% !important;" class="modal-lg modal-dialog" role="document">
     <div class="modal-content">
        <div class="modal-body">
             <div style="background-color:#339900; text-align:center">
                <img src=" <c:url value ="/images/${roleBean.clientLogo}"/>">
             </div>
             <div style="background-color:#dedb6d; text-align:center; font-size:10px">
                <br><c:out value="${roleBean.clientDesc}"/><br>
             </div>
             <div style="border: 1px solid #e7e7e7">
                <div style="display:flex">
                    <div style="color:red; padding-top:1%; padding-left:1%; width:85%; font-weight:bold; font-size:14pt">
                        <c:out value="${roleBean.staffTypeName}"/> list for <c:out value="${miniBean.objectName}" />
                    </div>
                    <div>
                       <span class="navTopSignOff">
                          <a href="#" data-dismiss="modal">Close Window</a>
                       </span>
                    </div>
                </div>
                <hr style="margin-left:0px!important; margin-top:0px">
                <div style="margin-left:5px; margin-top:-10px; margin-bottom:10px; margin-right:6px">
                    <div>
                       <p style="padding-left:5px; padding-top:10px">List of employee(s).</p>
                    </div>

                    <div>
                       <table border="0" cellspacing="0" cellpadding="0" width="100%"><tr><td>
                       			<table class="formtable" border="0" cellspacing="0" cellpadding="3" width="100%" align="left" >
                       						<tr align="left">
                       							<td class="activeTH">Employees</td>

                       						</tr>
                       						<!-- <tr>
                       							<td class="activeTD">
                       								<table width="95%" border="0" cellspacing="0" cellpadding="0">
                       									<display:table name="dispBean" class="register2" export="true" sort="page" defaultsort="1" requestURI="${appContext}/ltgByMDAPEmpDetails.do">
                       									<display:column property="employeeId" title="${roleBean.staffTitle}" ></display:column>
                       									<display:column property="name" title="${roleBean.staffTypeName} Name" ></display:column>
                       									<display:column property="salaryScaleName" title="Pay Group"></display:column>
                       									<display:column property="salarylevelAndStepStr" title="Level & Step"></display:column>
                       									<display:column property="basicSalaryStr" title="Basic Salary" media="html"></display:column>
                       									<display:column property="basicSalaryStrSansNaira" title="Basic Salary" media="excel"></display:column>
                       									<display:column property="ltgCostStr" title="With LTG Increase" media="html"></display:column>
                       									<display:column property="ltgCostStrSansNaira" title="With LTG Increase" media="excel"></display:column>
                       									<display:column property="netIncreaseStr" title="Net Increase" media="html"></display:column>
                       									<display:column property="netIncreaseStrSansNaira" title="Net Increase" media="excel"></display:column>
                       									<display:setProperty name="paging.banner.placement" value="bottom" />
                       									</display:table>
                       								</table>
                       						    </td>
                       						</tr> -->
                       						<tr>
                       						    <td class="activeTD">
                       						        <table class="ltgTable" style="background-color:#9BA9B3" width="100%" border="0" cellspacing="0" cellpadding="0" >
                       						            <thead style="background-color:#EBF6FD; padding:2px 2px">
                       						                <tr>
                                                                <th><c:out value="${roleBean.staffTitle}"/></th>
                                                                <th><c:out value="${roleBean.staffTypeName}"/> Name</th>
                                                                <th>Pay Group</th>
                                                                <th>Level & Step</th>
                                                                <th>Basic Salary</th>
                                                                <th>With LTG Increase</th>
                                                                <th>Net Increase</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody>
                                                            <c:forEach items="${ltgBean}" var="wBean" varStatus="gridRow">
                                                                <tr>
                                                                   <td><c:out value="${wBean.employeeId}"/></td>
                                                                   <td><c:out value="${wBean.name}"/></td>
                                                                   <td><c:out value="${wBean.salaryScaleName}"/></td>
                                                                   <td><c:out value="${wBean.salarylevelAndStepStr}"/></td>
                                                                   <td><c:out value="${wBean.basicSalaryStr}"/></td>
                                                                   <td><c:out value="${wBean.ltgCostStr}"/></td>
                                                                   <td><c:out value="${wBean.netIncreaseStr}"/></td>
                                                                </tr>
                                                            </c:forEach>
                                                        </tbody>
                       						        </table>
                       						    </td>
                       						</tr>
                       			</table>
                       </table>
                    </div>
                </div>
             </div>
        </div>
     </div>
  </div>
</div>
<!-- Modal -->
<script>
    $('.ltgTable').DataTable( {
         dom: 'Bfrtip',
         buttons: [
            'excel', 'pdf', 'print'
          ],
         initComplete: function () {
            var btns = $('.dt-button');
            btns.removeClass('dt-button');
            btns.addClass('btn');
         },
         pageLength : 10,
         searching: true
    });

</script>