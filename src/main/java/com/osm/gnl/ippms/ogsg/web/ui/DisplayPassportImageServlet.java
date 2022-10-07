package com.osm.gnl.ippms.ogsg.web.ui;

import com.osm.gnl.ippms.ogsg.controllers.employee.EmployeeEnquiryOverviewForm;
import com.osm.gnl.ippms.ogsg.domain.employee.HrPassportInfo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;



public class DisplayPassportImageServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	public void doPost( HttpServletRequest request, HttpServletResponse response )
			throws ServletException, IOException {
		this.doGet(request, response);
	}

	public void doGet( HttpServletRequest request, HttpServletResponse response )
			throws ServletException, IOException {
		
		HrPassportInfo passport = (HrPassportInfo)request.getSession().getAttribute( EmployeeEnquiryOverviewForm.PASSPORT_KEY );

		if( passport != null ) {
			response.reset();
			response.setContentType( passport.getPhotoType() );
			response.getOutputStream().write( passport.getPhoto() );
			response.getOutputStream().flush();
			response.getOutputStream().close();
		}
		 super.destroy();
	}
}
