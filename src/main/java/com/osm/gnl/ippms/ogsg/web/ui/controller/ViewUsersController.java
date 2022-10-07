package com.osm.gnl.ippms.ogsg.web.ui.controller;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.auth.repository.IUserRepository;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/viewUsers.do")
public class ViewUsersController extends BaseController {

	@Autowired
	private IUserRepository userRepository;

	public ViewUsersController() {
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String displayPage(HttpServletRequest request, Model model) {
		BusinessCertificate bc = getBusinessCertificate(request);

		List<User> users = userRepository.loadActiveLoginUsingFilters(bc);

		model.addAttribute("users", users);
		addRoleBeanToModel(model, request);
		addPageTitle(model, getText("users.view.pageTitle"));
		addMainHeader(model, getText("users.view.mainHeader"));
		
		return "menu/viewUsers";
	}
}
