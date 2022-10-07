package com.osm.gnl.ippms.ogsg.controllers.statistics;

import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.StatisticsDetailsService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.MaterialityDisplayBean;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping({ "/showStatDetails.do" })
@SessionAttributes(types = { MaterialityDisplayBean.class, NamedEntityBean.class })
public class PayrollRunStatisticDetailsFormController extends BaseController {

	private final String VIEW = "statistics/payrollStatisticDetailForm";

	@Autowired
	private StatisticsDetailsService statisticsDetailsService;

	public PayrollRunStatisticDetailsFormController() {
	}

	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.GET }, params = { "sc", "rm", "ry" })
	public String setupForm(@RequestParam("sc") int pStatCode, @RequestParam("rm") int pRunMonth,
			@RequestParam("ry") int pRunYear, Model model, HttpServletRequest request)
			throws HttpSessionRequiredException, EpmAuthenticationException {
		SessionManagerService.manageSession(request, model);

		 
		MaterialityDisplayBean wMDB = PayrollStatisticsDataGen.generateModel(getBusinessCertificate(request),statisticsDetailsService, pStatCode, pRunMonth, pRunYear);
		
		 addRoleBeanToModel(model, request);
		model.addAttribute("statBean", wMDB);
 		addPageTitle(model, getText("stats.view.pageTitle"));
		addMainHeader(model, getText("stats.view.mainHeader", new Object[] { wMDB.getName() }));
 		return VIEW;
	}

}
