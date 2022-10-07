
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<style>
    .dataTables_filter input { height: 20px !important }
    .dataTables_filter label { font-size: 10pt !important }
    .dataTables_info {font-size: 8pt !important}
     th { font-size: 10pt !important }
     .btn { padding: 2px 10px}
</style>

<!-- Modal -->            
<div class="modal fade right" id="chartModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalPreviewLabel" aria-hidden="true">
  <div style="max-width: 100% !important;" class="modal-lg modal-dialog" role="document">
    <div class="modal-content">
      <!-- <div class="modal-header">
         <table>
            <tr>
      		    <td bgcolor = "#339900" align="center">
      			    <img src=" <c:url value ="/images/${roleBean.clientLogo}"/>">
      		    </td>
            </tr>
            <tr>
      			<td bgcolor = "#dedb6d"  align="center">
      				<br><c:out value="${roleBean.clientDesc}"/><br>
      			</td>
            </tr>
         </table>
      </div>
      <div style="background-color: #ebccd18f" class="modal-header justify-content-center">
            <b><h5 class="modal-title" id="exampleModalPreviewLabel">${title}</h5></b>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
      </div>-->
        <div class="modal-body">
           <div class="table-responsive">
                <table class="table display table-striped" id ="dataDisplay">
                    <div style="background-color:#339900; text-align:center">
                        <img width="800" src=" <c:url value ="/images/${roleBean.clientLogo}"/>">
                    </div>
                    <div style="background-color:#dedb6d; text-align:center; font-size:10px">
                       <br><c:out value="${roleBean.clientDesc}"/><br>
                    </div>
                    <div style="background-color: #ebccd18f; text-align:center; padding: 10px 0 0 0; margin-bottom:5px;">
                      <p style=" font-size:12px !important">${title}</p>
                    </div>
                    <thead class="sticky-top">
                        <tr>
                           <th style="font-size:8pt">S/N</th>
                            <c:forEach begin="0" var ="i" end="${tHeaderSize - 1}">
                              <th style="font-size:8pt">${tableHeader.get(i)}</th>
                            </c:forEach>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="mapList" items="${tableData}" varStatus="status">
                           <tr>
                            <td style="text-align:center; font-size:8pt" scope="row">${status.count}</td>
                            <c:forEach var="map" items="${mapList}">
                                <td style="padding-left:13px; font-size:8pt">${map.value}</td>
                            </c:forEach>
                           </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
      <div class="modal-footer">
        <button type="button" style="margin-right: 45%" class="btn btn-secondary" data-dismiss="modal">Close</button>
        <!--<button type="button" class="btn btn-primary">Save changes</button>-->
      </div>
    </div>
  </div>
</div>
<!-- Modal -->


<!-- <script type="text/javascript">
            $('#dataDisplay').DataTable( {
                dom: 'Bfrtip',
                buttons: [
                    'excel', 'pdf', 'print'
                    ],
                pageLength : 2,
                lengthMenu: [[2, 4, 6, -1], [2, 4, 6, 'All Records']],
                searching: true
            });
</script> -->

<script>
    $('#dataDisplay').DataTable( {
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
         lengthMenu: [[20, 50, 100, -1], [20, 50, 100, 'All Records']],
         searching: true
    });

</script>