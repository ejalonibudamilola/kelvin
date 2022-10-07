package com.osm.gnl.ippms.ogsg.controllers.employee;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.dao.IMenuService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.base.services.PromotionService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.controllers.employee.service.EmpGenOverviewService;
import com.osm.gnl.ippms.ogsg.employee.beans.EmployeeBean;
import com.osm.gnl.ippms.ogsg.domain.employee.HrPassportInfo;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import com.osm.gnl.ippms.ogsg.utils.Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping({ "/employeeEnquiryForm.do" })
@SessionAttributes(types = { EmployeeBean.class })
public class EmployeeEnquiryOverviewForm extends BaseController {


 	private final IMenuService menuService;
 	private final PromotionService promotionService;
 	private final PaycheckService paycheckService;
	public static final String PASSPORT_KEY = "_any_thing";

    private final String VIEW = "employee/employeeEnquiryViewForm";

	@Autowired
	public EmployeeEnquiryOverviewForm(IMenuService menuService, PromotionService promotionService, PaycheckService paycheckService) {
		this.menuService = menuService;
		this.promotionService = promotionService;
		this.paycheckService = paycheckService;
	}

	
	@RequestMapping(method = { org.springframework.web.bind.annotation.RequestMethod.GET }, params = { "eid" })
	public String setupForm(@RequestParam("eid") Long empId, Model model, HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);
		BusinessCertificate bc = this.getBusinessCertificate(request);
		Object userId = getSessionId(request);
		NamedEntity ne = new NamedEntity();
		ne.setId(empId);

		EmployeeBean pEB = new EmployeeBean();
		ne.setMode("edit");
		pEB.setEmployee(IppmsUtils.loadEmployee(genericService,empId,bc));
		ne.setName(pEB.getEmployee().getDisplayNameWivTitlePrefixed());

		Object wObj =  EmpGenOverviewService.makeModeAndReturnView(pEB,ne, bc,genericService,paycheckService,promotionService,loadConfigurationBean(request));

		if(!(wObj instanceof EmployeeBean))
			return (String.valueOf(wObj));

		pEB = (EmployeeBean)wObj;

		addSessionAttribute(request, NAMED_ENTITY, ne);
		model.addAttribute("namedEntity", ne);
		model.addAttribute("employeeBean", pEB);
		model.addAttribute("roleBean", bc);
		Navigator.getInstance(userId).setFromClass(getClass());
		Navigator.getInstance(userId).setFromForm("redirect:employeeEnquiryForm.do?eid=" + ne.getId());


		HrPassportInfo pPhoto = this.genericService.loadObjectWithSingleCondition(HrPassportInfo.class,CustomPredicate.procurePredicate(
				"employee.id", ne.getId()));

		if (pPhoto.getId() == null) {
			model.addAttribute("photo", "");
		} else {
			addSessionAttribute(request, EmployeeEnquiryOverviewForm.PASSPORT_KEY, pPhoto);
			model.addAttribute("photo", pPhoto);
		}

		// Set Context Menu Items On Right Click Event
		model.addAttribute("contextMenuItems", getContextMenuItems(request, pEB.getEmployee().getId(), bc));

		return VIEW;
	}

	private List<NamedEntity> getContextMenuItems(HttpServletRequest request, Long pHI, BusinessCertificate bc ) {
		List<NamedEntity> wRetList = new ArrayList<NamedEntity>();
		 
		if (menuService.canUserAccessURL(bc, "/paySlip.do", "/paySlip.do"))
			wRetList.add(new NamedEntity("View Last Paycheck",
					request.getContextPath() + "/paySlip.do?eid=" + pHI));
		if (menuService.canUserAccessURL(bc, "/viewPayslipHistory.do", "/viewPayslipHistory.do"))
			wRetList.add(new NamedEntity("View Payslip History",
					request.getContextPath() + "/viewPayslipHistory.do?eid=" + pHI));
		if (menuService.canUserAccessURL(bc, "/generatePRC.do", "/generatePRC.do"))
			wRetList.add(new NamedEntity("View Pay Record Card",
					request.getContextPath() + "/generatePRC.do?eid=" + pHI));
		if (menuService.canUserAccessURL(bc, "/viewEmpPromoHistory.do", "/viewEmpPromoHistory.do"))
			wRetList.add(new NamedEntity("View Promotion History",
					request.getContextPath() + "/viewEmpPromoHistory.do?eid=" + pHI));
		if (menuService.canUserAccessURL(bc, "/viewEmpTransferHistory.do", "/viewEmpTransferHistory.do"))
			wRetList.add(new NamedEntity("View Transfer History",
					request.getContextPath() + "/viewEmpTransferHistory.do?eid=" + pHI));

		return wRetList;

	}

}