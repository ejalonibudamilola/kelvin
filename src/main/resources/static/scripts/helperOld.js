/**
 * Determine browser.
 */
var isMinNS4 = (navigator.appName.indexOf("Netscape") >= 0 &&
                parseFloat(navigator.appVersion) >= 4) ? 1 : 0;
var isMinIE4 = (document.all) ? 1 : 0;
var isMinIE5 = (isMinIE4 && navigator.appVersion.indexOf("5.") >= 0) ? 1 : 0;
var isIE = navigator.userAgent.indexOf("MSIE") > -1
var isSafari = navigator.userAgent.indexOf("Safari") > -1
var isEmbeddedMac = navigator.userAgent.indexOf("QuickBooks") > -1 && navigator.userAgent.indexOf("WebKit") > -1



/**
 * String prototype enhancement - add the "standard" trim function to it.
 **/ 
String.prototype.trim = function () {
    return this.replace(/^\s*/, "").replace(/\s*$/, "");
}

/**
 * Pops up a window with the specified title displaying the passed URL.
 */
function popupWindow (url, name, left, top, width, height, msg)
{
	if (!width) width = 650;
	if (!height) height = 600;

	return popupWindowWithFeatures(url, name, left, top, width, height, ",resizable,scrollbars", msg)
}

/**
 * Pops up a window with the specified features displaying the passed URL.
 */
function popupWindowWithFeatures(url, name, left, xtop, width, height, features, msg, retry)
{
	var okToMove = !left && !xtop;
	if (!left) left = 0;
	if (!xtop) xtop = 0;
	if (!width) width = 350;
	if (!height) height = 200;
	if (!name) name = "";

	try {
		// Make sure the windowFreatures line does not contain whitespaces
		var win = window.open ("",name.replace(" ",""),
			 " ,width=" + width +
			 " ,height=" + height +
			 " ,left=" + left +
			 " ,top=" + xtop +
			 " "+ features); 
	} catch (e) {
		alert("A problem occured while trying to open a window, please check your pop-up blocker settings and retry. If the problem still occurs reboot your system and try again.");
		return null;
	}
	if (win == null) {
		alert("A window failed to open; please check your pop-up blocker settings.")
		return null;
	}

	try {
		if (isMinNS4 && okToMove)
			win.moveTo (screen.availWidth - width, 0);
			
		if (msg && (isMinNS4 || isMinIE4) && !isEmbeddedMac)
		{
			win.document.write("<div style='font-size: 10pt; font-family: Verdana, Arial, Helvetica, sans-serif;' align=center>" + msg + "</div>");
			win.document.close();
		}
	
		win.document.location.href = url;
		win.focus();
	} catch (e) {
		//common access denied error. Try to use a different window name.
		if (retry == null || retry != true) {
			return popupWindowWithFeatures(url, name + (new Date()).getMilliseconds(), left, top, width, height, features, msg, true)
		} else {
			throw e;
		}
	}
	
	return win;
}

/**
 * Pops up a form window
 */
function showForm(url, msg)
{
	if (!isEmbeddedMac)
	{
		var rightCorner = screen.width - 610;
		try {
			var win = popupWindow(url, "Form", screen.width - 610, 0, 600, screen.height-100, msg);
		} catch (e) {
			document.location.href = url;
			return null;
		}
		if (win = null) {
			document.location.href = url;
			return null;
		}
		return win;
	}
	else
	{
		document.location.href = url;
	}
}

/**
 * CheckForm - prevents form from submitting more than one time
 */
var form_submitted = false;
function checkForm ()
{
	if (form_submitted)
	{
		alert ("Your information is being processed already.");
		return false;
	}
	form_submitted = true;
	return true;
}

function CheckAll(bl, eName)
{
	var len = bl.elements.length;
	for (var i = 0; i < len; i++) {
	    var e = bl.elements[i];
	    if (e.name == eName) {
			e.checked = true;
	    }
	}
}

function CheckAll(bl, eName, eName2)
{
	var len = bl.elements.length;
	for (var i = 0; i < len; i++) {
	    var e = bl.elements[i];
	    if (e.name == eName || e.name == eName2) {
			e.checked = true;
	    }
	}
}

function CheckAllByRegEx(form, regex){
	var len = form.elements.length;
	for (var i = 0; i < len; i++) {
	    var e = form.elements[i];
	    if (e.name.match(regex)) {
			e.checked = true;
	    }
	}
}

function ClearAll(bl, eName)
{
	var len = bl.elements.length;
	for (var i = 0; i < len; i++) {
	    var e = bl.elements[i];
	    if (e.name == eName) {
			e.checked = false;
	    }
	}
}

function ClearAll(bl, eName, eName2)
{
	var len = bl.elements.length;
	for (var i = 0; i < len; i++) {
	    var e = bl.elements[i];
	    if (e.name == eName || e.name == eName2) {
			e.checked = false;
	    }
	}
}

function ClearAllByRegEx(form, regex){
	var len = form.elements.length;
	for (var i = 0; i < len; i++) {
	    var e = form.elements[i];
	    if (e.name.match(regex)) {
			e.checked = false;
	    }
	}
}

function countClicks(){
	if(!document.clicks){
		document.clicks = 1;
	}
	else
		document.clicks++;
	
	return document.clicks;
}

//return true if user has not clicked the maximum times, otherwise return false, alert message if specified 
function limitClicks(max, message){
	if(!message)
		return (countClicks() <= max); 
	
	if(countClicks() <= max)
		return true;
	else{
		alert(message);
		return false;
	}
}

/*
this function returns the stylesheet attribute of the given object, including external and global style sources; 
also includes IE / Firefox / Netscape different implementations.
Parameters:
	element - HTML element to search the style attributes for
	attributeIE - the attribute name in IE, eg, "width" or "backgroundColor"
	attributeNS - the attribute name is Netscape / Firefox, eg, "width" or "background-color:
	
	note: 	generally attribute names are the same as they are in the actual stylesheet definitions for NS/Firefox;
			and they are camel-cased if they are combinations of multiple words with dashes in between for IE.
*/
function getStyleAttribute(element, attributeIE, attributeNS)
{
	if (element.currentStyle)
	{
		if (element.currentStyle[attributeIE])
		{
			return element.currentStyle[attributeIE];
		}
		else if(element.currentStyle[attributeNS])
		{
			return element.currentStyle[attributeNS];
		}
		
		return
	}

	if (document.defaultView.getComputedStyle)
	{
		var style=document.defaultView.getComputedStyle(element, null)
		return style.getPropertyValue(attributeNS);
	}

	attributeFromId = "";
	attributeFromClassName = "";
	for (j = 1; j < document.styleSheets.length; j++)
	{
		for (i = 0; i < document.styleSheets[j].cssRules.length; i++)
		{
			selectorText = document.styleSheets[j].cssRules[i].selectorText.toLowerCase();
			if (selectorText == (element.nodeName+"[id\""+element.id+"\"]").toLowerCase())
			{
				attributeFromId = document.styleSheets[j].cssRules[i].style[attributeNS];
			}
			if (selectorText.indexOf((element.nodeName+"."+element.className).toLowerCase()) == 0)
			{
				attributeFromClassName = document.styleSheets[j].cssRules[i].style[attributeNS];
			}
		}
	}

	return attributeFromId == "" ? attributeFromClassName : attributeFromId;
}

function sensitizeField(form,elementName)
{
	modName = "_s"+elementName;
	modElement = eval(form+"."+modName);
	element = eval(form+"."+elementName);
	if( modElement!=null ) {
		if( element==null ) {
			var iform;
			value = modElement.value
			eval("iform="+form);
			aInput=document.createElement("input");
			aInput.setAttribute("type","hidden");
			aInput.setAttribute("name",elementName);
			aInput.setAttribute("value",value);
			iform.appendChild(aInput)

			eval( "element = "+form+"."+elementName)
			modElement.value = "...."+value.substring(value.length-4)
		}
	}

}

/** this function is for limiting the length of a textarea (or anything text-input field for that matter),
 because there is no inherent built-in HTML attribute support for this purpose.<b> 
 TO USE: set this function to an event-driven HTML attribute (e.g. onload, onkeypress, onclick, etc.)
 */
function limitTextLength(element, length)
{
	if(element.value.length >= length) {	//hit maxlength
		alert("You have reached the " + length + " character limit.");
		element.value = element.value.substring(0, length);
		return;
	}
}

/** This function counts the maximum columns of a table at the time it is called.  **/
function getMaxColCount(table){
	//expect node to be a table element
	if(table.tagName.toLowerCase() != 'table')
		return 0;

	var count = table.rows[0].cells.length;
	
	for(i=1; i<table.rows.length; i++){
		if(table.rows[i].cells.length > count)
			count = table.rows[i].cells.length;
	}
	
	return count;
}

function addHiddenInput(formName,name,value)
{
  var form = document.getElementsByName(formName)[0];
  var input = form.appendChild(document.createElement(isIE ? '<input type="hidden">' : 'INPUT'));
  input.setAttribute('name', name);
  if (!isIE)
  {
	  input.setAttribute('type', 'hidden');  	
  }
  input.setAttribute('value', value);
}

function findPosX(obj)
{
	var curleft = 0;
	if (obj.offsetParent)
	{
		while (obj.offsetParent)
		{
			curleft += obj.offsetLeft
			obj = obj.offsetParent;
		}
	}
	else if (obj.x)
		curleft += obj.x;
	return curleft;
}

function findPosY(obj)
{
	var curtop = 0;
	if (obj.offsetParent)
	{
		while (obj.offsetParent)
		{
			curtop += obj.offsetTop
			obj = obj.offsetParent;
		}
	}
	else if (obj.y)
		curtop += obj.y;
	return curtop;
}

function checkExport (xid)
{
    if(xid > 0)
        return confirm("Warning: This transaction has been previously exported.\n\nChoose OK to proceed with the export.");
    else
       return true;
}

// setStyleByClass: given an element type and a class selector,
// style property and value, apply the style.
// args:
//  t - type of tag to check for (e.g., SPAN)
//  c - class name
//  p - CSS property
//  v - value
var ie = (document.all) ? true : false;

function setStyleByClass(t,c,p,v){
	var elements;
	if(t == '*') {
		// '*' not supported by IE/Win 5.5 and below
		elements = (ie) ? document.all : document.getElementsByTagName('*');
	} else {
		elements = document.getElementsByTagName(t);
	}
	for(var i = 0; i < elements.length; i++){
		var node = elements.item(i);
		for(var j = 1; j < node.attributes.length; j++) {
			if(node.attributes.item(j).nodeName == 'class') {
				if(node.attributes.item(j).nodeValue == c) {
					eval('node.style.' + p + " = '" +v + "'");
				}
			}
		}
	}
}

function launchDemoWindow(url) 
{
	smallWindow = window.open(url,"qtw","toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=0,width=800,height=600");
}

function setCookie(name, value, days, path, domain, secure)
{
	var cookie_string = name + "=" + escape ( value );

	if ( days )
	{
		var today = new Date();
 		var expires = new Date();
    	expires.setTime(today.getTime() + 3600000*24*days);
    	cookie_string += "; expires=" + expires.toGMTString();
	}

  	if ( path )
    	cookie_string += "; path=" + escape ( path );

	if ( domain )
    	cookie_string += "; domain=" + escape ( domain );
  
	if ( secure )
    	cookie_string += "; secure";
  
  	document.cookie = cookie_string;
}

function deleteCookie(name)
{
	setCookie(name, "", -1);
}

function getCookie ( name )
{
	var results = document.cookie.match ( name + '=(.*?)(;|$)' );

	if ( results )
		return ( unescape ( results[1] ) );
		else
	return null;
}

function checkCookiesEnabled()
{
	//basically to check if cookies are enabled, you just set a cookie and immediately try to get it
	//it's important to do this entirely on the client end (i.e., via javascript) so the user
	//doesn't have a chance to intervene and delete the necessary cookies themselves, confusing the system
	setCookie("tempCookieCheck", "temp");
	if(getCookie("tempCookieCheck") == null){
		//cookies are NOT enabled
		return false;
	}
	else {
		//cookies are enabled, remove temporary cookie
		deleteCookie("tempCookieCheck");
	}
	
	return true;
}

function enforceCookiesEnabled(id)
{
	if(id == null)
		id = 'mainBody';
		
	if(!checkCookiesEnabled()){
		element = document.getElementById(id);
		element.innerHTML = '<div class="error">We have detected that your browser does not have cookies enabled.  Please set your browser to accept cookies and then reload the page.</div>';
	}
}
