<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- Modal -->
<div class="modal fade right" id="errorModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalPreviewLabel" aria-hidden="true">
  <div style="max-width: 100% !important;" class="modal-lg modal-dialog" role="document">
     <div class="modal-content">
        <div class="modal-body">
             <div style="background-color:#339900; text-align:center">
                <img style="width:100%" src=" <c:url value ="/images/${roleBean.clientLogo}"/>">
             </div>
             <div style="background-color:#dedb6d; text-align:center; font-size:10px">
                <br><c:out value="${roleBean.clientDesc}"/><br>
             </div>
             <div style="border: 1px solid #e7e7e7">
                <div style="display:flex">
                    <div style="color:red; padding-top:1%; padding-left:1%; width:85%; font-weight:bold; font-size:14pt">View Error Logs</div>
                    <div>
                       <span class="navTopSignOff">
                          <a href="#" data-dismiss="modal">Close Window</a>
                       </span>
                    </div>
                </div>
                <hr style="margin-left:0px!important; margin-top:0px">
                <div style="border:2px solid #e7e7e7; margin-left:5px; margin-top:-10px; margin-bottom:10px; margin-right:6px">
                    <div style="background-color:#E2E2E2; padding:10px; margin-top:10px; margin-left:10px; margin-right:10px">
                       <p><b>Client:</b> <span> <c:out value="${eBean.businessClient.name}"/> </span></p>
                       <p><b>User:</b> <span> <c:out value="${eBean.user.actualUserName}"/> </span></p>
                       <p><b>Time:</b> <span> <c:out value="${eBean.errorLogTime}"/> </span></p>
                    </div>

                    <div class="form-group" style="padding:10px">
                       <label for="exampleFormControlTextarea1">Stack Trace</label>
                           <textarea style="font-size: 9pt;" class="form-control" id="exampleFormControlTextarea1" rows="3">${eBean.errorMsg}</textarea>
                    </div>
                </div>
             </div>
        </div>
     </div>
  </div>
</div>
<!-- Modal -->