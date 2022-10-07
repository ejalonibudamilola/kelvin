package com.osm.gnl.ippms.ogsg.controllers.report;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PaginationService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.statistics.domain.MdaPayrollStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping({"/empByMDAPDetails.do"})
@SessionAttributes(types={PaginatedBean.class})
public class ViewEmployeesByMDAPFormController extends BaseController
{

	@Autowired
	private PaginationService paginationService;

	private final String VIEW = "report/viewEmployeeByMDAPForm";

  public ViewEmployeesByMDAPFormController() {}

  @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"oid"})
  public String setupForm(@RequestParam("oid") Long pMid, Model model, HttpServletRequest request)
    throws Exception
	{
		SessionManagerService.manageSession(request, model);
		BusinessCertificate bc = super.getBusinessCertificate(request);

		String wObjectName = "";
		String wObject = "";

		MdaInfo wADM = genericService.loadObjectById(MdaInfo.class, pMid);

		wObject = bc.getMdaTitle();
		wObjectName = wADM.getName();

		 PaginationBean paginationBean = getPaginationInfo(request);

		List<HiringInfo> empList = this.paginationService.getActiveEmployeesByObjectAndCode(bc,
				(paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion(), pMid);

		int wNoOfElements = this.paginationService.getTotalNoOfActiveEmployeesByObjectAndCode(bc,pMid);

		PaginatedBean wPHDB = new PaginatedBean(empList, (paginationBean.getPageNumber() - 1) * this.pageLength, this.pageLength,
				wNoOfElements, paginationBean.getSortOrder(), paginationBean.getSortCriterion());

		int noOfFemales = this.paginationService.getTotalNoOfActiveEmployeesByGenderObjectAndCode(bc,"F", pMid);
		int noOfMales = this.paginationService.getTotalNoOfActiveEmployeesByGenderObjectAndCode(bc,"M", pMid);

		// --Now Lets get the Latest Statistics...
		PayrollFlag wPF = IppmsUtilsExt.getPayrollFlagForClient(genericService,bc);

		MdaPayrollStatistics wMPS = genericService.loadObjectUsingRestriction(MdaPayrollStatistics.class, Arrays.asList(CustomPredicate.procurePredicate("payrollRunMasterBean.id",  wPF.getPayrollRunMasterBean().getId()),
				CustomPredicate.procurePredicate("mdaInfo.id", pMid) ));
		wPHDB.setNoOfFemales(noOfFemales);
		wPHDB.setNoOfMales(noOfMales);

		wPHDB.setMbapName(wObject);
		wPHDB.setMinistryName(wObjectName);
		wPHDB.setNoOfEmployees(wNoOfElements);
		wPHDB.setId(pMid);
		wPHDB.setObjectInd(wADM.getMdaType().getMdaTypeCode());

		model.addAttribute("miniBean", wPHDB);
		model.addAttribute("stats", wMPS);
		addRoleBeanToModel(model, request);
		return VIEW;
	}

}