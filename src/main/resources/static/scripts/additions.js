function ajaxSessionTimeout() {
	alert('Session timed out');
}

//$(function() { $( "#dialog" ).dialog(); });
 
!function($) {
	$.ajaxSetup({
		statusCode : {
			901 : ajaxSessionTimeout
		}
	});
}(window.jQuery);

$(function() {
	$("div.mainHeaderNav").on("mouseover", "li.childLinkStopPropagation",
			function(e) {
				e.stopPropagation();
			});
});
function goToLink(elem) {
	var tSpace = $(elem).attr("work");

	window.location = APP_CONTEXT + "/" + tSpace;

}
function transverse(elem) {
	var tSpace = $(elem).attr("linkurl");

	window.location.replace(APP_CONTEXT + '/' + tSpace);

}
function filter() {

	var textfromView = document.getElementById('search').value;

	if (textfromView == '') {
		alert("Field is empty");
	} else {
		// First clear all rows . change them to status quo..
		returnToStatusQuo();
		// Do the intended coloration of affected rows
		var table = document.getElementById('datatab');

		var rowLength = table.rows.length;
		var rowCount = 0;

		for (var i = 0; i < rowLength; i += 1) {
			var row = table.rows[i];
			var cellLength = row.cells.length;
			var cell = row.cells[1];
			// only ist
			var txt = cell.innerHTML;

			if (txt.toLowerCase().indexOf(textfromView.toLowerCase()) >= 0) {
				rowCount++;
				row.style.background = "#afd7ff";
				$("#" + row.id).css('box-shadow', 'inset 0 0 3px #000000');

			}
		}
		$("#hideorshow").css('display', 'block');
		$("#count").text("[ " + rowCount + " ]");
		$("#typedin").text("'" + textfromView + "'");
	}
}
function returnToStatusQuo() {
	$("#hideorshow").css('display', 'none');

	var table = document.getElementById('datatab');
	var textfromView = document.getElementById('search').value;
	var rowLength = table.rows.length;

	for (var i = 0; i < rowLength; i += 1) {
		var row = table.rows[i];
		if (i % 2 == 0) {
			row.style.background = "#EBEBEB"
			$("#" + row.id).css('box-shadow', 'none');
		} else {
			row.style.background = "#FCFCFC"
			$("#" + row.id).css('box-shadow', 'none');
		}

	}

}

function createChildMenuLinks(menuElem) {
	$("li#" + menuElem.id).find("ul").eq(0).find("li").remove();
	var linkStr = "";
	
	$.ajax({
		url: APP_CONTEXT + "/menu/getChildMenuLinksForMenu.do?menuId=" + menuElem.id.substring(5),
		dataType : 'json',
		type : 'GET',
		async : false,
		success : function(data) {
			if (data && data.length > 0) {
				for (var i = 0; i < data.length; i++) {
					var menuLink = data[i];
					
					if (menuLink.numOfChildren > 0) {
						if(menuLink.id == 0){
							linkStr += "<li class='dropdown-submenu childLinkStopPropagation' onmouseover='createRootMenuLinks(this);' id='menu_cat_"
								+ menuLink.menuLinkCategory.id +"' ";
						}
						else{
							linkStr += "<li class='dropdown-submenu childLinkStopPropagation' onmouseover='createChildMenuLinks(this);' id='menu_"
								+ menuLink.id +"' ";
						}
						
						if (menuLink.linkUrl && menuLink.linkUrl != "" && menuLink.directAccess) {
							linkStr += "ondblclick='goToLink(this);'> ";
							linkStr += "<a tabindex='-1' href='" + APP_CONTEXT + menuLink.linkUrl + "'>" + menuLink.name + "</a>";
						}
						else {
							linkStr += "><a tabindex='-1' href='#'>" + menuLink.name + "</a>"
						}
						
						linkStr += "<ul class='dropdown-menu multi-level' role='menu' aria-labelledby='dropdownMenu'></ul>";
						linkStr += "</li>";
					}
					else {//no children
						if (menuLink.linkUrl && menuLink.linkUrl != "") {
							linkStr += "<li class='childLinkStopPropagation'><a href='" + APP_CONTEXT + menuLink.linkUrl + "'>" + menuLink.name + "</a></li>";
						}
						else {
							linkStr += "<li class='childLinkStopPropagation'><a tabindex='-1' href='#'>" + menuLink.name + "</a></li>";
						}
					}
				}
			}
		}
	});
	
	$("#" + menuElem.id).find("ul").eq(0).append(linkStr);
	$("#" + menuElem.id).find("ul").eq(0).find("li.childLinkStopPropagation").mouseover(function(e) { e.stopPropagation(); });
}


function createRootMenuLinks(menuCatElem) {
	$("li#" + menuCatElem.id).find("ul").eq(0).find("li").remove();
	var linkStr = "";
	
	$.ajax({
		url: APP_CONTEXT + "/menu/getRootMenuLinksForMenuCategory.do?catId=" + menuCatElem.id.substring(9),
		dataType : 'json',
		type : 'GET',
		async : false,
		success : function(data) {
			if (data && data.length > 0) {
				for (var i = 0; i < data.length; i++) {
					var menuLink = data[i];
					
					if (menuLink.numOfChildren > 0 || menuLink.dashboardMenuLink) {
						linkStr += "<li class='dropdown-submenu' onmouseover='createChildMenuLinks(this);' id='menu_" + menuLink.id +"' ";
						
						if (menuLink.linkUrl && menuLink.linkUrl != "" && menuLink.directAccess) {
							linkStr += "ondblclick='goToLink(this);'> ";
							linkStr += "<a tabindex='-1' href='" + APP_CONTEXT + menuLink.linkUrl + "'>" + menuLink.name + "</a>";
						}
						else {
							linkStr += "><a tabindex='-1' href='#'>" + menuLink.name + "</a>"
						}
						
						linkStr += "<ul class='dropdown-menu multi-level' role='menu' aria-labelledby='dropdownMenu'></ul>";
						linkStr += "</li>";
					}
					
					else { //no children
						if (menuLink.linkUrl && menuLink.linkUrl != "") {
							linkStr += "<li class='childLinkStopPropagation'><a href='" + APP_CONTEXT + menuLink.linkUrl + "'>" + menuLink.name + "</a></li>";
						}
						else {
							linkStr += "<li class='childLinkStopPropagation'><a tabindex='-1' href='#'>" + menuLink.name + "</a></li>";
						}
					}
				}
			}
		}
	});
	
	$("li#" + menuCatElem.id).find("ul").eq(0).append(linkStr);
	$("#" + menuCatElem.id).find("ul").eq(0).find("li.childLinkStopPropagation").mouseover(function(e) { e.stopPropagation(); });

}

function handleTabClick(elem) {
	var tabplace = $(elem).attr("tabplace");
	$("div#tab" + tabplace + "default").find("ul").empty();
	var linkStr = "";
	
	$.ajax({
		url: APP_CONTEXT + "/menu/getMenuLinksForCategoryForDashboard.do?catId=" + elem.id.substring(9),
		dataType : 'json',
		type : 'GET',
		async : false,
		success : function(data) {
			if (data && data.length > 0) {
				for (var i = 0; i < data.length; i++) {
					var menuLink = data[i];
					
					linkStr += "<li role='presentation' class='li_role'>"
								+ "<div class='shadow'>"
									+ "<a  href=" + APP_CONTEXT + menuLink.linkUrl + ">"
										+ "<b>"+ menuLink.name + "</b>"
									+"</a>"
									+ "<br/>"
									+ menuLink.description
									+ "<br/>"
								+ "</div>"
							+ "</li>";
				}
			}
			else {
				$("div#tab" + tabplace + "default").find("div").show();
			}
		}
	});
	
	$("div#tab" + tabplace + "default").find("ul").append(linkStr);
}

function createJSON(elem) {

	$("li#" + elem.id).find("ul").eq(0).find("li").remove();
	
	var wholeLength = "";
	var dropDownOpt = ""
	$
			.ajax({
				url : APP_CONTEXT + "/getSubCategories.do?headerid=" + elem.id,
				dataType : 'json',
				type : 'GET',
				async : false,
				success : function(data) {
					if (data && data.length > 0) {
						for (var i = 0; i < data.length; i++) {
							var subLink = data[i];

							if (subLink.linkUrl == "#") {
								wholeLength += "<li class='dropdown-submenu' onmouseover='createJsonSub(this);' id = '"
										+ subLink.id
										+ "'>"
										+ "<a tabindex='-1' href='#'>"
										+ subLink.linkName
										+ "</a>"
										+ "<ul class='dropdown-menu multi-level' role='menu' aria-labelledby='dropdownMenu'></ul>"
										+ "</li>";
							} else {
								wholeLength += "<li><a href='" + APP_CONTEXT
										+ "/" + subLink.linkUrl + "'>"
										+ subLink.linkName + "</a></li>";
							}

						}
					}
				},
				complete : function() {
				},
				error : function() {
					alert("Session Expired! Pls return to Home Page")
				}
			});

	$("li#" + elem.id).find("ul").eq(0).append(wholeLength);
}

function createJsonSub(elem) {
	// var wholeLength = "<li class='dropdown-submenu'><a href='#'>Test
	// Link1</a></li>";
	$("li#" + elem.id).find("ul").eq(0).find("li").remove();
	var wholeLength = "";
	var dropDownOpt = "";
	$
			.ajax({
				url : APP_CONTEXT + "/getSubLinks.do?linkid=" + elem.id,
				dataType : 'json',
				type : 'GET',
				async : false,
				success : function(data) {
					if (data && data.length > 0) {
						for (var i = 0; i < data.length; i++) {
							var subLink = data[i];

							if (subLink.isParent == true) {
								wholeLength += "<li class='dropdown-submenu childLinkStopPropagation' onmouseover='createJsonSub(this);' id = '"
										+ subLink.id
										+ "'>"
										+ "<a tabindex='-1' href='#'>"
										+ subLink.linkName
										+ "</a>"
										+ "<ul class='dropdown-menu multi-level' role='menu' aria-labelledby='dropdownMenu'></ul>"
										+ "</li>";
							} else {
								wholeLength += "<li class='childLinkStopPropagation'><a href='"
										+ APP_CONTEXT
										+ "/"
										+ subLink.linkUrl
										+ "'>" + subLink.linkName + "</a></li>";
							}
						}
					}
				},
				complete : function() {
				},
				error : function() {
					alert("Session Expired! Pls return to Home Page")
				}
			});

	$("#" + elem.id).find("ul").eq(0).append(wholeLength);

	$("#" + elem.id).find("ul").eq(0).find("li.childLinkStopPropagation")
			.mouseover(function(e) {
				e.stopPropagation();
			});
}
function test(elem) {
	window.location.replace(APP_CONTEXT + elem.url);
}
function reloadTab(elem) {
	// $('.loadingrover').show();
	var tSpace = $(elem).attr("tabplace");
	// alert("div#tab"+tSpace+"default");
	$("div#tab" + tSpace + "default").find("ul").empty();
	var wholeLength = "";
	var dropDownOpt = ""
	$
			.ajax({
				url : APP_CONTEXT + "/getDashBoardLinks.do?headerid=" + elem.id,
				dataType : 'json',
				type : 'GET',
				async : false,
				success : function(data) {
					// $('.loadingrover').hide();
					if (data && data.length > 0) {
						for (var i = 0; i < data.length; i++) {
							var subLink = data[i];

							var requiredUrl;

							if (subLink.dualAlternateLink) {
								requiredUrl = subLink.dualAlternateLink;
							} else {
								requiredUrl = "/" + subLink.linkUrl;
							}

							wholeLength += "<li role='presentation' class='li_role'>"
									+ "<div class='shadow'>"
									+ "<a  href="
									+ APP_CONTEXT
									+ requiredUrl
									+ ">"
									+ "<b>"
									+ subLink.linkName
									+ "</b></a>"
									+ "<br/>"
									+ subLink.description
									+ "<br/></div>"
									+ "</li>";

						}

					} else {
						$("div#tab" + tSpace + "default").find("div").show();
					}
				},
				complete : function() {

				},
				error : function() {
					alert("Session Expired! Pls return to Home Page")
				}
			});

	$("div#tab" + tSpace + "default").find("ul").append(wholeLength);

}

function reloadOtherTab(elem) {
	var tSpace = $(elem).attr("tabplace");
	// alert("div#tab"+tSpace+"default");
	$("div#tab" + tSpace + "default").find("ul").empty();
	var wholeLength = "";
	var dropDownOpt = ""
	$
			.ajax({
				url : APP_CONTEXT + "/getOtherLinks.do?linkid=" + elem.id,
				dataType : 'json',
				type : 'GET',
				async : false,
				success : function(data) {

					if (data && data.length > 0) {
						for (var i = 0; i < data.length; i++) {
							var subLink = data[i];
							var requiredUrl;
							if (subLink.dualAlternateLink) {
								requiredUrl = "/" + subLink.dualAlternateLink;
							} else {
								requiredUrl = "/" + subLink.linkUrl;
							}

							wholeLength += "<li role='presentation' >"
									+ "<div class='shadow' >"
									+ "<a href="
									+ APP_CONTEXT
									+ requiredUrl
									+ ">"
									+ "<b>"
									+ subLink.linkName
									+ "</b></a>"
									+ "<br/>"
									+ subLink.description
									+ "<br/></div>"
									+ "</li>";

						}

					} else {
						$("div#tab" + tSpace + "default").find("div").show();
					}
				},
				complete : function() {
				},
				error : function() {
					alert("Session Expired! Pls return to Home Page")
				}
			});

	$("div#tab" + tSpace + "default").find("ul").append(wholeLength);

}

function resetAllPrivileges() {
	$('.toggles').bootstrapToggle('off');
}

function addAllPrivileges() {
	$('.toggles').bootstrapToggle('on');
}

function createJSONPr() {
	jsonObj = [];
	var ownedBy = $('.holder').attr("owner");
	$("input[class=toggles]").each(function() {

		var id = $(this).attr("id");
		var rowid = $(this).attr("rownmbr");
		var status = $(this).prop("checked");

		// var link =$(this).attr("linkid");
		item = {}
		item["id"] = id;
		item["rownmbr"] = rowid;
		item["owner"] = ownedBy;
		item["status"] = status;
		// item["link"] = link;

		jsonObj.push(item);
	});
	$.ajax({
		url : APP_CONTEXT + "/ajaxcall.do",
		dataType : 'json',
		contentType : 'application/json; charset=utf-8',
		data : JSON.stringify({
			name : ownedBy,
			result : JSON.stringify(jsonObj)
		}),
		type : 'POST',
		async : false,
		success : function(data) {
			alert("User" + ownedBy + "'s profile has been edited!!");

		},
		complete : function() {
			//			alert("User" + ownedBy +"'s profile has been edited!!" );
		},
		error : function() {
			alert("no reach here o");
		}
	});

}

$(function() {
	$('.toggles').change(function() {
		//alert($(this).prop('id') + "clicked");
		// $('#console-event').html('Toggle: ' + $(this).prop('checked'))
	})
})
