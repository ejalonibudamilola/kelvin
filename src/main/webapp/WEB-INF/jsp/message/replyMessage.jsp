
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<style>
    textarea {
       overflow-y: scroll !important;
       height: 100px !important;
       resize: none;
    }
    b{
        font-size:11px !important;
    }
    hr{
        margin-left:-9px;
    }
    .multiselect-container {
           width: 80%;
           overflow: auto;
    }

    button.multiselect {
           background-color: initial;
           text-align: left;
           height:30px;
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

    .multiselect-all{
       width:100%;
       text-align:left;
    }
</style>
<!-- Modal -->
<div class="modal fade right" id="modalShow" tabindex="-1" role="dialog" aria-labelledby="exampleModalPreviewLabel" aria-hidden="true">
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
                    <div style="color:red; padding-top:1%; padding-left:1%; width:85%; font-weight:bold; font-size:11pt">Chat Reply</div>
                    <div>
                       <span class="navTopSignOff">
                          <a href="#" data-dismiss="modal">Close Window</a>
                       </span>
                     </div>
                </div>
                <hr style="margin-left:0px!important; margin-top:0px">
                <div style="margin-top:-12px; padding-left:8px;">
                     <p style="margin-bottom: -2%;"><b>From:</b>${sender}</p>
                     <hr>
                     <p style="display:none" id="rId">${msg.senderId}</p>
                     <p style="margin-top: -12px; margin-bottom: -2%;"><b>To:</b> ${msg.sender} <span><a id="addMore" href="#">add more</a></span></p>
                     <hr>
                     <p id="cc" style="margin-top: -20px; margin-bottom: -25px; font-weight:bold; display:none">
                        <select name="msg"  id="to" multiple="multiple" data-placeholder="Cc:" class="browser-default custom-select ms">
                           <c:forEach items="${emp}" var="names">
                             <option value="<c:out value="${names.id}"/>"><c:out value="${names.lastName}"/> <c:out value="${names.firstName}"/></option>
                           </c:forEach>
                        </select>
                     </p>
                     <hr id="hcc" style="display:none">
                     <p id="subject" style="margin-top: -12px; margin-bottom: -2%;"><b>Re:</b> ${msg.subject}</p>
                     <hr>
                     <p style="margin-top: -12px; margin-bottom: -2%;"><b>Message:</b> ${msg.msgBody}</p>
                     <hr>
                      <div class="form-group" style="margin-right:1%">
                         <label style="font-size:12px" for="exampleFormControlTextarea1">Reply</label>
                         <textarea id="body" class="form-control" rows="1"></textarea>
                      </div>
                      <div style="margin-bottom: 1%;">
                          <img data-dismiss="modal" id="sendMsg" src="images/Send_Email_n.png">
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
<script src="scripts/bootstrap-multiselect.js"></script>
<script src="scripts/jquery-ui.js"></script>
<script>
    $("#addMore").click(function(e){
        $("#cc").css("display", "block");
        $("#hcc").css("display", "block");
    });

    $('#to').multiselect({
       includeSelectAllOption: true,
       maxHeight: 350,
       buttonWidth : '100%'
    });

    $("#sendMsg").click(function(e){
        var t;
        var t1 = $('#rId').text();
        var t2 = $('#to').val();
        console.log("t1 is "+t1);
        console.log("t2 is "+t2);
        var subject= $('#subject').text();
        var body= $('#body').val();

        if(t2 !== null){
            t = t1+","+t2;
        }
        else{
            t = t1;
        }
        console.log("t is "+t);
        console.log("subject is "+subject);
        console.log("body is "+body);

        $.ajax({
                    type: "GET",
                    success: function (response) {
                      // do something ...
                      window.location.href ="${pageContext.request.contextPath}/messaging.do?replyTo=" + t+ "&subject=" + subject+ "&body=" + body
                    },
                    error: function (e) {
                      alert('Error: ' + e);
                    }
        });


    });
</script>


