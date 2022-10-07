/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



$(document).ready(function(){

    $("#myForm").submit(function(e){      

        //For Section heads,Divisional users and super admin's
        if(document.getElementById("approveAll") != null){

            //if no radio for data status is enabled then modification of data cannot be carried out.
            var input = document.getElementsByTagName("input");
            var editable = false;            

            for(var y = 0; y < input.length; y++){
                if(input[y].getAttribute("type") == "radio"){
                    if(!input[y].disabled){//if any enabled
                        editable = true;
                        break;
                    }
                }
            }

            if(!editable){
               alert('No data on display currently is modifiable.');
               return editable;//should be false.
            }

            //if(document.getElementById("approveAll").checked)
                //return confirm("Are you sure you want to approve all unapproved displayed data. \n\"Note that approved data cannot be modified.");
            
            //if(document.getElementById("rejectAll").checked)
                //return confirm("Are you sure you want to reject all unapproved displayed data.");

            //This is to display a confirmation message to the user when edit individually
            //return confirm("Are you sure you want to make modification to the displayed data.");
        }

        /*
         *If the checkboxes for approveAll and rejectAll are not displayed then
         *the user is not a Section,Division aor Super Admin, which means he can only modify data
         *and not approve or reject.
         */
        else{

            if(document.getElementById("hide") != null)
                return true;

            //if no data is selected for modification then display a message
            //to the user to select data for modification.
           //Get all checkboxes and if none is checked(in editMode) then display an alert
            //telling the user that no data is in edit mode
            var inputs = document.getElementsByTagName("input");

            for(var x = 0; x < inputs.length;x++){
                if(inputs[x].getAttribute("type") == "checkbox"){
                    if(inputs[x].checked){
                        //This is to display a confirmation message to the user when edit individually
                        return true;
                    }
                }
            }
            alert('No data is currently selected for Modification.');
            return false;
        }           
    })

    /*
     * Slide in the .success div
     */
    jQuery(".success").hide();
    jQuery(".success").slideDown('slow');

    /*
     * Register a timeout for the success div
     */
     setTimeout(function(){
         jQuery(".success").slideUp('slow', function(){
             jQuery(this).remove();
         });
         
     }, 7000);
    
});



//Utility fuction to toggle the visibility of controls in the table
function changeState(checkbox, rowId){

    var labels = jQuery("#"+rowId+ " .editLabel").get();//get the controls that have class = "editLabel" in that row
    var controls = jQuery("#"+rowId+" .editControl").get()//get controls that have class = "editControl" in that row

    //check the current state of the control
    if(checkbox.checked){//if checked
        //if checkebox is checked then it was unchecked before and the labels were displayed.
        //Now hide all labels on that row.
        for(var i = 0; i < labels.length; i++){
            labels[i].style.display = "none";
        }

        //display controls on that row
        for(var y = 0; y < controls.length; y++){
            controls[y].style.display = "inline-block";
        }
    }else{
        //reverse steps above
        //hide all controls
        for(var x = 0; x < controls.length; x++){
            controls[x].style.display = "none";
        }
        //display labels
        for(var z = 0; z < labels.length; z++){
            labels[z].style.display = "inline-block";
        }
    }
}

//toggle approveAll and rejectAll
function approveRejectAll(checkbox){
    if(checkbox.id == "approveAll")
      document.getElementById("rejectAll").checked = false;
    else
      document.getElementById("approveAll").checked = false;
}




//$(document).ready(function(){
//
//    //called when on focus
//    $("#quantity").click(function (e)
//	{
//	  return document.getElementById("quantity").value = "";
//      }
//	);
//    //called when key is pressed in textbox
//	$("#quantity").keypress(function (e)
//	{
//	  //if the letter is not digit then display error and don't type anything
//	  if( e.which!=8 && e.which!=0 && (e.which<48 || e.which>57))
//	  {
//		//display error message
//		$("#errmsg").html("Digits Only").show().fadeOut("slow");
//              // alert('Enter only Number');
//	    return false;
//      }
//	});
//
//  });
  
////  function clearNum(evt){
////
////    var elem = (evt.which) ? evt.which : event.keyCode
////   if (elem.which!=8 && elem.which!=0 && (elem < 48 || elem > 57)){
////     //display error message
////		$("#errmsg").html("Digits Only").show().fadeOut("slow");
////              // alert('Enter only Number');
////	    return false;
////}
//
//
//  }

//This function handles the display of upload controls
function hideDisplayUploadControls(elem){
    //If the check is checked(upload about to be performed.)

    var wDisabled = "disabled";
    var wTrue = "true";
   
    //get all normal controls.
    var normalControls = jQuery(".normalControl").get();
    //get all upload controls.
    var uploadControls = jQuery(".uploadControl").get();

    if(elem.checked){
        //disable normal controls.
        for(var i = 0; i < normalControls.length; i++)
            normalControls[i].setAttribute(wDisabled, wTrue);

        //display hidden upload controls(file upload control).
        for(var c = 0; c < uploadControls.length; c++){
            if(uploadControls[c].style.display == "none")
                uploadControls[c].style.display = "inline-block";
        }
    }
    //Not checked.
    else{
         //enable normal controls.
        for(var x = 0; x < normalControls.length; x++)
           // normalControls[x].removeAttr("disabled");
           normalControls[x].disabled =false;

        //display hidden upload controls(file upload control).
        for(var y= 0; y < uploadControls.length; y++){
            if(uploadControls[y].style.display == "inline-block")
                uploadControls[y].style.display = "none";
        }
    }
}


/*
 * This function handles creation of a new account.
 * Enables and disables the role controls depending on selection
 *
 * createNewUserForm.jsp
 */
function disableRoleControls(elem){   
     //disable all role controls
     var roleControls = jQuery(" .roleControls").get();

    if(elem.value == "0") {        
        //If no role is selected then disable all role controls
        for(var x = 0; x < roleControls.length; x++)
            roleControls[x].disabled = true;
    }
    else{
        //if the id of the role control is equal to the role selected then enable and disable others
        for(var y = 0; y < roleControls.length; y++){
            if(elem.value == roleControls[y].id)
                roleControls[y].disabled = false;
            else
                roleControls[y].disabled = true;
        }
    }

}