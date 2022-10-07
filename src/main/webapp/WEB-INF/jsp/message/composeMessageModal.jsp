
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<style>
    textarea {
       overflow-y: scroll !important;
       height: 100px !important;
       resize: none;
    }
    b{
        font-size:12px !important;
    }
    hr{
        margin-left:-9px;
    }
    .multiselect-container {
           width: 300px;
    }

    button.multiselect {
       //background-color: initial;
       text-align: left;
       height:30px;
       margin-left:5px;
       border-radius:5px;
       padding-left:10px;
       padding-right:10px;
    }

    .multiselect-option {
           width:100%;
           text-align: left;
    }

    .multiselect-option label{
            font-size: 11px !important;
            font-weight: normal !important;
    }

    .multiselect{
            border:none !important;
            focus:none !important;
            outline:none !important;
    }

    .multiselect-selected-text{
            font-size:12px;
    }
    .multiselect-all{
       width:100%;
       text-align:left;
    }
</style>
<!-- Modal -->
<div class="modal fade right" id="composeModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalPreviewLabel" aria-hidden="true">
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
                    <div style="color:red; padding-top:1%; padding-left:1%; width:85%; font-weight:bold; font-size:14pt">New Message</div>
                    <div>
                       <span class="navTopSignOff">
                          <a href="#" data-dismiss="modal">Close Window</a>
                       </span>
                    </div>
                </div>
                <hr style="margin-left:0px!important; margin-top:0px">
                <div id="topOfPageBoxedErrorMessage" style="display:${displayErrors}">
                   ${sent}
                </div>
                <div style="margin-left:1%; margin-top:1%">
                   <p style="font-size:12px; color:red" id="errorDiv"></p>
                </div>
                <div style="margin-top:-12px; padding-left:8px;">
                     <p style="margin-top: -10px; margin-bottom: -15px; display:flex">
                        <b style="padding-top:5px">To:</b>
                        <select name="msg"  id="to" multiple="multiple" data-placeholder="select recipient" class="browser-default custom-select ms">
                           <c:forEach items="${emp}" var="names">
                             <option value="<c:out value="${names.id}"/>" title="<c:out value="${names.email}"/>"><c:out value="${names.lastName}"/> <c:out value="${names.firstName}"/></option>
                           </c:forEach>
                        </select>
                     </p>
                     <hr id="hcc">
                     <p id="" style="margin-top: -10px; margin-bottom: -10px;">
                        <b>Subject:</b>
                        <input style="margin-top:-10px; border: none; outline:none; font-size:14px; width:790px" id="subject" type="text">
                     </p>
                     <hr>
                     <div class="form-group" style="margin-right:1%">
                         <label style="font-size:12px" for="exampleFormControlTextarea1">Message Body</label>
                         <textarea id="body" class="form-control" rows="1"></textarea>
                     </div>
                      <div style="margin-bottom: 1%;">
                          <img  id="sendMsg" src="images/Send_Email_n.png">
                      </div>
                </div>
             </div>
        </div>
        <!--<div class="modal-footer">
          <button type="button" style="margin-right: 45%" class="btn btn-secondary" data-dismiss="modal">Close</button>
          <button type="button" class="btn btn-primary">Save changes</button>
        </div>-->
     </div>
  </div>
</div>
<!-- Modal -->
        <script src="scripts/jquery-3.4.1.min.js"></script>
        <script src="scripts/bootstrap-multiselect.js"></script>
        <script src="scripts/jquery-ui.js"></script>
        <script>
            $('#to').multiselect({
               includeSelectAllOption: true,
               maxHeight: 350,
               buttonWidth : '100%',
               numberDisplayed: 7
            });

            $('#sendMsg').click(function(e) {
                var to= $('#to').val();
                var subject= $('#subject').val();
                var body= $('#body').val();
                console.log("to is "+to+" subject is "+subject+" body is "+body);
                if((subject=="") || (body=="")){
                    $('#errorDiv').html('One or more field is empty, Please fill all the fields to send message');
                }
                else if(to==null){
                    $('#errorDiv').html('Please select at least one Recipient');
                }
                else{
                    $.ajax({
                        type: "GET",
                        success: function (response) {
                             // do something ...
                             window.location.href ="${pageContext.request.contextPath}/messaging.do?to=" + to+ "&subject=" + subject+ "&body=" + body
                        },
                        error: function (e) {
                            alert('Error: ' + e);
                        }
                    });
                }
            });
        </script>

