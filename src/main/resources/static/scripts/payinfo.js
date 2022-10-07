<script>
			function showFields()
			{
				showMorePayTypes = document.forms[0].showMorePayTypes.value == 'true';
				
				setStyleByClass('TR','advancedPayType','display', showMorePayTypes ? '' : 'none');
				
				document.getElementById('lessPayTypesLink').style.display = showMorePayTypes ? 'none' : '';
				
				document.getElementById('salaryFields').style.display='none';
				document.getElementById('hourlyFields').style.display='none';
				document.getElementById('hourly2Field').style.display='none';
				document.getElementById('WageCheckBox15').disabled = false;
				document.getElementById('WageCheckBox8').disabled = false;
				document.getElementById('WageCheckBox9').disabled = false;
	
				if (document.getElementById('hourlyRadio').checked)
				{
					document.getElementById('hourly2Field').style.display='';
					document.getElementById('hourlyFields').style.display='';
				}
				if (document.getElementById('salaryRadio').checked)
				{
					document.getElementById('salaryFields').style.display='';
				}
				if (document.getElementById('commissionOnlyRadio').checked)
				{
					document.getElementById('WageCheckBox15').checked = true;
					document.getElementById('WageCheckBox15').disabled = true;
					document.getElementById('WageCheckBox8').checked = false;
					document.getElementById('WageCheckBox8').disabled = true;
					document.getElementById('WageCheckBox9').checked = false;
					document.getElementById('WageCheckBox9').disabled = true;
				}
				
				showRecurringHeader = (document.forms[0].showRecurringHeader.value == 'true') || showMorePayTypes;
				document.getElementById('recurringHeader').style.display = showRecurringHeader ? '' : 'none';
				document.getElementById('advancedPayTypeTips').style.display = showMorePayTypes ? '' : 'none';
			}
</script>

<script language="JavaScript">

 <!--
function pc_form_editPay1_form_submit(pc_form){
	if(pc_form_editPay1_form_inputValidator(pc_form)) {
		return setFormSubmitted(true);
	} else {
		return setFormSubmitted(false);
	}
}

 function  pc_form_editPay1_form_inputValidator(pc_form)
 { 



	 if (!checkNumber(pc_form.HourlyRate.value))

	{

		 alert("Please enter the dollars to pay for each hour of work.");

		 return false;

	}


	 if (!checkNumber(pc_form.HourlyRate2.value))

	{

		 alert("Please enter the dollars to pay for each hour of work.");

		 return false;

	}


	 if (!checkNumber(pc_form.Salary.value))

	{

		 alert("Please enter the dollars to pay in the specified period (no commas).");

		 return false;

	}


	 if (!hasValue(pc_form.SalaryHours, pc_form.SalaryHours.value))

	{

		 alert("Please enter the hours worked per day.");

		 return false;

	}


	 if (!checkNumber(pc_form.SalaryHours.value))

	{

		 alert("Please enter the hours worked per day.");

		 return false;

	}


	 if (!hasValue(pc_form.SalaryDays, pc_form.SalaryDays.value))

	{

		 alert("Please enter the days worked per week.");

		 return false;

	}


	 if (!checkNumber(pc_form.SalaryDays.value))

	{

		 alert("Please enter the days worked per week.");

		 return false;

	}


	 if (!checkNumber(pc_form.RecurringAmount16.value))

	{

		 alert("Please enter the Allowance amount for each check in dollars.");

		 return false;

	}


	 if (!checkNumber(pc_form.RecurringAmount19.value))

	{

		 alert("Please enter the Reimbursement amount for each check in dollars.");

		 return false;

	}


	 if (!checkNumber(pc_form.RecurringAmount24.value))

	{

		 alert("Please enter the Clergy Housing (Cash) amount for each check in dollars.");

		 return false;

	}


	 if (!checkNumber(pc_form.RecurringAmount25.value))

	{

		 alert("Please enter the Clergy Housing (In-Kind) amount for each check in dollars.");

		 return false;

	}


	 if (!checkNumber(pc_form.RecurringAmount34.value))

	{

		 alert("Please enter the Nontaxable Per Diem amount for each check in dollars.");

		 return false;

	}


	 if (!checkNumber(pc_form.RecurringAmount22.value))

	{

		 alert("Please enter the Group-Term Life Insurance amount for each check in dollars.");

		 return false;

	}


	 if (!checkNumber(pc_form.RecurringAmount23.value))

	{

		 alert("Please enter the S-Corp Owners Health Insurance amount for each check in dollars.");

		 return false;

	}


	 if (!checkNumber(pc_form.RecurringAmount33.value))

	{

		 alert("Please enter the Company HSA Contribution amount for each check in dollars.");

		 return false;

	}


	 if (!checkNumber(pc_form.RecurringAmount26.value))

	{

		 alert("Please enter the Personal Use of Company Car amount for each check in dollars.");

		 return false;

	}


	 if (!checkNumber(pc_form.RecurringAmount30.value))

	{

		 alert("Please enter the Other Earnings amount for each check in dollars.");

		 return false;

	}


	 if (!checkNumber(pc_form.RecurringAmount31.value))

	{

		 alert("Please enter the Other Earnings II amount for each check in dollars.");

		 return false;

	}
 
  
   return true;
 }
 //-->
 </script>
 
 <script>showFields()</script> 
 
 <script language="javascript">
	function onPageLoad() {
		if (document.getElementById('addDeductionSelect'))
		{
			document.getElementById('addDeductionSelect').selectedIndex = 0;
		}
		if (document.getElementById('addGarnishmentSelect'))
		{
			document.getElementById('addGarnishmentSelect').selectedIndex = 0;
		}
		if (document.getElementById('addContributionSelect'))
		{
			document.getElementById('addContributionSelect').selectedIndex = 0;
		}
	}
</script>

<script language="javascript">
var warning = document.getElementById('javascriptCheck');
warning.parentNode.removeChild(warning);
</script>

<script language="javascript" type="text/javascript">
		function SendXmlRequest(url, responseFunction){
		
			function xmlResponse(){
				if (responseFunction == null) { // because of the "closure" the responseFunction gets help onto even if the scope is lost by the caller.
					if(xmlHttp.readyState == 4){
						if(xmlHttp.status == 200){
							// --- NO-OP ---
							//var result = xmlHttp.responseText.trim();
							//As a side effect the cookies (ie. "VID" or "VIDF") get updated updated.
						}
					}
				} else {
					responseFunction();
				}
			}
		
			xmlHttp = window.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject("MSXML2.XMLHTTP.3.0");
			xmlHttp.open("GET", url);
			xmlHttp.onreadystatechange = xmlResponse;
			xmlHttp.send(null);
			
			return xmlHttp; // Caller should hold on to this to prevent garbage collection.
		}
</script>

		<script language="JavaScript1.0">
	<!--
	function refresh() {
	    window.location.href = '/in/todo/default.jsp'
	}
	//-->
</script>
		
<script language="JavaScript1.1">
	<!--
	function refresh() {
		window.location.reload(true);
	}
	//-->

</script>

		<script language="JavaScript">
	<!--
	function getCookie(name) {
		var cookie = document.cookie
		var start = cookie.indexOf(name + "=");
		if (start == -1)
			return null;
		start = start + name.length + 1;
		end = cookie.indexOf(';',start);
		if (end == -1)
			end = cookie.length;
		return unescape(cookie.substring(start,end))
	}
	//-->
</script>

		<script language="JavaScript">
	<!--
	function checkSession() {
		usr = getCookie("USR")
		if (usr==null || usr=="no") {
			if (navigator.userAgent.toLowerCase().indexOf("firefox/") != -1 || navigator.userAgent.toLowerCase().indexOf("safari/") != -1) {
				document.write("<html><body onload='window.location.reload(true);'><a href='' onclick='window.location.reload(true);'>Your session has expired. Click here to login.</a></body></html>");
				document.close();
			} else {
				refresh();
			}
		}
	}
	//-->
</script>


		<script language="JavaScript">
			<!--
			function initPage()
			{
				checkSession();
onPageLoad()

			}
			//-->
</script>

//4rm Employee Contractor OverView

<script language="javascript">
var currentOnLoad_partner = null;
function topTabResize_partner()
{
	if (currentOnLoad_partner)
	{
		currentOnLoad_partner();
	}
	var sum = 0;

	var tab = document.getElementById("BofA2_tabset002_Image1");
	tab.style.right = sum;
	
	var width = getStyleAttribute(tab.parentNode, 'width', 'width');
	width = parseInt(width.substring(0, width.length-2));
	sum += width;
	tab.parentNode.parentNode.width = width;

	var tab = document.getElementById("BofA2_tabset002_Image2");
	tab.style.right = sum;
	
	var width = getStyleAttribute(tab.parentNode, 'width', 'width');
	width = parseInt(width.substring(0, width.length-2));
	sum += width;
	tab.parentNode.parentNode.width = width;

	var tab = document.getElementById("BofA2_tabset002_Image3");
	tab.style.right = sum;
	
	var width = getStyleAttribute(tab.parentNode, 'width', 'width');
	width = parseInt(width.substring(0, width.length-2));
	sum += width;
	tab.parentNode.parentNode.width = width;

	var tab = document.getElementById("BofA2_tabset002_Image4");
	tab.style.right = sum;
	
	var width = getStyleAttribute(tab.parentNode, 'width', 'width');
	width = parseInt(width.substring(0, width.length-2));
	sum += width;
	tab.parentNode.parentNode.width = width;

	var tab = document.getElementById("BofA2_tabset002_Image5");
	tab.style.right = sum;
	
	var width = getStyleAttribute(tab.parentNode, 'width', 'width');
	width = parseInt(width.substring(0, width.length-2));
	sum += width;
	tab.parentNode.parentNode.width = width;

	var tab = document.getElementById("BofA2_tabset002_Image6");
	tab.style.right = sum;
	
	var width = getStyleAttribute(tab.parentNode, 'width', 'width');
	width = parseInt(width.substring(0, width.length-2));
	sum += width;
	tab.parentNode.parentNode.width = width;

		var height = getStyleAttribute(tab.parentNode, 'height', 'height');
		height = parseInt(height.substring(0, height.length-2));
		tab.style.top = -1 * height;

	var tab = document.getElementById("BofA2_tabset002_Image7");
	tab.style.right = sum;
	
	var width = getStyleAttribute(tab.parentNode, 'width', 'width');
	width = parseInt(width.substring(0, width.length-2));
	sum += width;
	tab.parentNode.parentNode.width = width;

}

	topTabResize_partner();

currentOnLoad_partner = window.onload;
window.onload = topTabResize_partner;
</script>


<script language="javascript">
var currentOnLoad = null;
function topTabResize()
{
	if (currentOnLoad)
	{
		currentOnLoad();
	}
	var sum = 0;

	var tab = document.getElementById("internalToptabImage1");
	tab.style.right = sum;
	
	var width = getStyleAttribute(tab.parentNode, 'width', 'width');
	width = parseInt(width.substring(0, width.length-2));
	sum += width;
	tab.parentNode.parentNode.width = width;

	var tab = document.getElementById("internalToptabImage2");
	tab.style.right = sum;
	
	var width = getStyleAttribute(tab.parentNode, 'width', 'width');
	width = parseInt(width.substring(0, width.length-2));
	sum += width;
	tab.parentNode.parentNode.width = width;

	var tab = document.getElementById("internalToptabImage3");
	tab.style.right = sum;
	
	var width = getStyleAttribute(tab.parentNode, 'width', 'width');
	width = parseInt(width.substring(0, width.length-2));
	sum += width;
	tab.parentNode.parentNode.width = width;

	var tab = document.getElementById("internalToptabImage4");
	tab.style.right = sum;
	
	var width = getStyleAttribute(tab.parentNode, 'width', 'width');
	width = parseInt(width.substring(0, width.length-2));
	sum += width;
	tab.parentNode.parentNode.width = width;

		var height = getStyleAttribute(tab.parentNode, 'height', 'height');
		height = parseInt(height.substring(0, height.length-2));
		tab.style.top = -1 * height;

	var tab = document.getElementById("internalToptabImage5");
	tab.style.right = sum;
	
	var width = getStyleAttribute(tab.parentNode, 'width', 'width');
	width = parseInt(width.substring(0, width.length-2));
	sum += width;
	tab.parentNode.parentNode.width = width;

}

	topTabResize();

currentOnLoad = window.onload;
window.onload = topTabResize;
</script>

//From Tax Info

		<script>
			function ResStateTaxOverrideWarning()
			{
				if (!alreadyNotifiedOfTaxOverride && parseInt(document.getElementsByName('ResStateFilingStatus')[0].value) == -1 
					&& document.getElementsByName('ResStateExtraTaxAmount')[0].value != null 
					&& parseInt(document.getElementsByName('ResStateExtraTaxAmount')[0].value) > 0)
				{
					return confirm('You are overriding the regular income tax withholding tax calculation.  We will withhold the stated amount for each regular paycheck. In some cases there may not be sufficient net pay to collect all tax or deduction amounts. Click OK to confirm. Click Cancel to edit your choices.');
				}
				
				return true;		
			}
		</script>
		
					<script>
				alreadyNotifiedOfTaxOverride = false;
				function FederalTaxOverrideWarning()
				{
					if (parseInt(document.getElementsByName("FedFilingStatus")[0].value) == -1 && document.getElementsByName("FedExtraTaxAmount")[0].value != null && parseInt(document.getElementsByName("FedExtraTaxAmount")[0].value) > 0)
					{
						alreadyNotifiedOfTaxOverride = true;
						return confirm("You are overriding the regular income tax withholding tax calculation.  We will withhold the stated amount for each regular paycheck. In some cases there may not be sufficient net pay to collect all tax or deduction amounts.  Click OK to confirm. Click Cancel to edit your choices.");
					}
					
					return true;
				}
			</script>	
			
			
	<script language="JavaScript">
		function reloadForm () {
			document.editTax_form.submit();
		}
	</script>
	
// from vaction sticky enter

<script language="javascript">
<!--
	function checkmax()
	{
		var max = document.editptopolicy_form.accrualmaximum.value;
		if (max != 0)
		{
			
					if (80.0 > max)
						alert("one or more employees assigned this policy have hours available that exceed the accrual maximum.");
			
		}
	}
	
	function checkaccrualrate()
	{
		var rate = document.editptopolicy_form.accrualrate.value;
		if (rate > 1.0 && "each pay period" == "per hour worked") {
			alert("you have entered an accrual rate of " + rate + " hours per hour worked. please enter the rate of accrual per hour worked or change the accrual schedule you have selected. accrual cannot be greater than 1.0 hours per hour worked.");
			return false;
		}
		return true;
	}
	
	function checkaccrualfrequencyselected(){
		if(document.editptopolicy_form.accrualfrequency.value == 'null'){
			alert("please select how your hours will be accrued.");
			return false;
		}
		return true;
	}
	
	function changesmadeinptopolicyvalues(){

		if(document.editptopolicy_form.description != null && document.editptopolicy_form.description.value != '160.0 hrs/yr (accrued each pay period) with maximum of 160.0 hours')
			return true;
		if(document.editptopolicy_form.accrualfrequency.value != 'each pay period')
			return true;
		if(document.editptopolicy_form.accrualrate.value != 160.0)
			return true;
		if(document.editptopolicy_form.accrualmaximum.value != 160.0)
			return true;

		return false;

	}
	
	function extravalidation(){
		if(changesmadeinptopolicyvalues()){
			document.editptopolicy_form.changesmade.value = "true";
		}

		return checkaccrualfrequencyselected();
	}
//-->

</script>

// from vacationedit

<script language='JavaScript'>

 <!--
function pc_form_editPay2_form_submit(pc_form){
	if(pc_form_editPay2_form_inputValidator(pc_form)) {
		return setFormSubmitted(true);
	} else {
		return setFormSubmitted(false);
	}
}

 function  pc_form_editPay2_form_inputValidator(pc_form)
 { 



	 if (!hasValue(pc_form.availableF1, pc_form.availableF1.value))

	{

		 alert("Please enter the hours available for each policy.");

		 return false;

	}


	 if (!checkNumber(pc_form.availableF1.value))

	{

		 alert("Please enter the hours available for each policy.");

		 return false;

	}


	 if (!hasValue(pc_form.availableF2, pc_form.availableF2.value))

	{

		 alert("Please enter the hours available for each policy.");

		 return false;

	}


	 if (!checkNumber(pc_form.availableF2.value))

	{

		 alert("Please enter the hours available for each policy.");

		 return false;

	}
 
  


	 if (!confirmDelete1(pc_form.policyF1.value,null,null))

	{

		 if( !confirm("All hours for this employee's sick policy will be deleted. Are you sure you want to select no policy?") ) return false;

	}


	 if (!confirmDelete2(pc_form.policyF2.value,null,null))

	{

		 if( !confirm("All hours for this employee's vacation policy will be deleted. Are you sure you want to select no policy?") ) return false;

	}
   return true;
 }
 //-->
 </script>
 
 <script>function submitFormeditPay2_form_edit_policyF1() { addHiddenInput('editPay2_form','_dfn','edit_policyF1');addHiddenInput('editPay2_form','edit_policyF1_cat','Sick');addHiddenInput('editPay2_form','edit_policyF1_inlineMode','true');addHiddenInput('editPay2_form','edit_policyF1_empIndex','6');addHiddenInput('editPay2_form','edit_policyF1_dftp','policyId');addHiddenInput('editPay2_form','edit_policyF1_dfid','TaskCoEditPTO');addHiddenInput('editPay2_form','edit_policyF1_dft','policyF1');document.editPay2_form.submit();}</script>
 
 			<script language="Javascript">
				togglePolicyLink(document.editPay2_form.policyF1, 'policyF1');
			</script>
			

			<script language="JavaScript">
				function confirmDelete2(value) 
				{
					return value != 0;
				}	
			</script>
			
			
						<script language="Javascript">
				togglePolicyLink(document.editPay2_form.policyF2, 'policyF2');
			</script>
			
			<script>function submitFormeditPay2_form_edit_policyF2() { addHiddenInput('editPay2_form','_dfn','edit_policyF2');addHiddenInput('editPay2_form','edit_policyF2_cat','Vacation');addHiddenInput('editPay2_form','edit_policyF2_inlineMode','true');addHiddenInput('editPay2_form','edit_policyF2_empIndex','6');addHiddenInput('editPay2_form','edit_policyF2_dftp','policyId');addHiddenInput('editPay2_form','edit_policyF2_dfid','TaskCoEditPTO');addHiddenInput('editPay2_form','edit_policyF2_dft','policyF2');document.editPay2_form.submit();}</script>
			
						<script language="JavaScript">
				function confirmDelete1(value) 
				{
					return value != 0;
				}	
			</script>
			
				<script language="javascript">
		function togglePolicyLink(selectList, policyField)
		{
			var link = document.getElementById('edit_' + policyField);
			link.innerHTML = selectList.options[selectList.selectedIndex].value == '0' ? 'New Policy' : 'Edit Policy';
		}
	</script>