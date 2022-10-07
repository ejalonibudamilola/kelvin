package com.osm.gnl.ippms.ogsg.web.ui.filter;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.dao.IMenuService;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.BusinessCertificateCreator;
import com.osm.gnl.ippms.ogsg.controllers.message.Messaging;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.menu.domain.MenuLink;
import com.osm.gnl.ippms.ogsg.menu.domain.MenuLinkCategory;
import com.osm.gnl.ippms.ogsg.menu.domain.MenuLinksWrapper;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import com.osm.gnl.ippms.ogsg.web.ui.WebHelper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

//@Component
//@WebFilter(urlPatterns = "*.do",dispatcherTypes = { DispatcherType.REQUEST,DispatcherType.FORWARD })
@Slf4j
public class MenuFilter extends BaseController implements Filter {
    
    /**
     * A collection of URLs that we want to be excluded from being filter. 
     * The URLs specified should not have the context path of the
     * application. They should be in the same format as the those used with
     * servlet mappings.
     */

    
	//private static final Logger logger = LoggerFactory.getLogger(MenuFilter.class);

    /**
     * Any number of these characters are considered delimiters between multiple
     * values in a single init-param String value.
     */

    @Autowired
    private IMenuService menuService;



	@Override
	public void destroy() {
	}

	@SneakyThrows
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
		
		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpResp = (HttpServletResponse) response;
		String URL =  httpReq.getServletPath();

        boolean ajaxRequest = WebHelper.isNotAjaxRequest(httpReq);
		
		if (httpReq.getQueryString() != null) {
			URL += ("?" + httpReq.getQueryString());
		}
		
		//TODO we need to deal with URLS that have request parameters
		if (WebHelper.isNotAjaxRequest(httpReq)) {

            if(URL.indexOf(".do") != -1) {

				BusinessCertificate bc = WebHelper.getBusinessCertificate(httpReq);

					if(bc == null) {
						bc = BusinessCertificateCreator.createBusinessCertificate( httpReq, SessionManagerService.manageSession(httpReq), genericService);

					}

					if (!this.menuService.canUserAccessURL(bc, httpReq.getServletPath(), URL)) {
						//if user has no access to page, redirect to from form or home page
						if (IppmsUtils.isNotNullOrEmpty(Navigator.getInstance(getSessionId(httpReq)).getFromSessionForm())) {
							httpResp.sendRedirect(Navigator.getInstance(getSessionId(httpReq)).getFromSessionForm().replace("redirect:", ""));
							//Navigator.getInstance(super.getSessionId(httpReq)).setFromForm("");

							addSaveMsgToSession(httpReq, "Oops!! You do not have access to requested page. Please contact your Administrator",
									Navigator.getInstance(getSessionId(httpReq)).getFromSessionForm().replace("redirect:", ""));

							log.error("User does not have access to view URL -> " + URL);
							System.out.println("User does not have access to view URL -> " + URL);
							//super.addSaveMsgToFlashMap((RedirectAttributes) httpR, "Oops!! You do not have access to requested page. Please contact your Administrator");
						} else {
							if(!httpResp.isCommitted())
							   httpResp.sendRedirect("loginForm.gnl");
							else
							{
								System.out.println("Business Client Is Null.");
							}
						}

					}

					Navigator.getInstance(getSessionId(httpReq)).setFromSessionForm("");


					final List<MenuLinkCategory> menuLinkCategories = this.menuService.getMenuLinkCategoriesForUser(bc, bc.getLoginId());

					//put in the request
					httpReq.setAttribute("_userMenuCategories", menuLinkCategories);

					//get notifications
					httpReq.setAttribute("_notificationBeans", PayrollBeanUtils.getNotificationBeans(genericService, menuService, bc));
					httpReq.setAttribute("messageCount", Messaging.countMessage(bc, genericService));

					//Check if we are in our home page
					//TODO externalize the URL as a param to this filter
					if (httpReq.getServletPath().equalsIgnoreCase(IConstants.DASHBOARD_URL)) {

						//load the links for the first tab item i.e first Menu Category
						if (menuLinkCategories != null && (!menuLinkCategories.isEmpty())) {
							MenuLinkCategory menuCategory = menuLinkCategories.get(0);
							List<MenuLink> displayOnDbMenuLinks =
									this.menuService.getMenuLinksDisplayableOnDashboardForCategory(bc, menuCategory.getId(), bc.getLoginId());

							httpReq.setAttribute("_firstTabLinks", displayOnDbMenuLinks);
						}

						httpReq.setAttribute("_isHomePageUrl", Boolean.TRUE);
					}
					//check maybe the URL is for another dashboard
					else {
						//Fetch Tab based menu links for page if required.
						//Basically we are fetching Tab values for Dashboard pages other than the home page
						MenuLinksWrapper wrapper = this.menuService.getMenuInfoForDashboard(bc, bc.getLoginId(), URL);
						if (wrapper != null) {
							//put in the request
							httpReq.setAttribute("_dbMenuCategories", wrapper.getMenuLinkCategories());
							httpReq.setAttribute("_dbFirstTabLinks", wrapper.getFirstTabs());
							httpReq.setAttribute("_dbMenuLink", wrapper.getMenuLink());
						}
					}
				}



		}
		
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) {

        this.menuService = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext()).getBean(IMenuService.class);
        this.genericService = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext()).getBean(GenericService.class);
	}

}

    