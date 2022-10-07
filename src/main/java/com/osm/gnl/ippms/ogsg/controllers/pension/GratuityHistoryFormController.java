package com.osm.gnl.ippms.ogsg.controllers.pension;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PensionService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.paycheck.domain.PaycheckGratuity;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtilsExt;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/gratuityHistoryReport.do")
public class GratuityHistoryFormController extends BaseController {

    private final PensionService pensionService;

	@Autowired
	public GratuityHistoryFormController(PensionService pensionService) {
		this.pensionService = pensionService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String setupForm(Model model, HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request);
		BusinessCertificate bc = getBusinessCertificate(request);
		PaginationBean paginationBean = getPaginationInfo(request);
		//--Get the Latest Payroll Run
		PayrollFlag payrollFlag = IppmsUtilsExt.getPayrollFlagForClient(genericService,bc);

		double paidLoanAmount = 0.0;

		int wNoOfElements = this.pensionService.getTotalNoOfGratuityPaidByYear(bc.getBusinessClientInstId());

		List<MdaInfo> wPGList;
		if (wNoOfElements > 0) {
			wPGList = this.pensionService.loadPaidGratuityByYear(bc,payrollFlag.getApprovedYearInd(),false,paginationBean.getPageNumber(),wNoOfElements);

			Collections.sort(wPGList, Comparator.comparing(MdaInfo::getName));
		} else {
			wPGList = new ArrayList<>();
		}

		PaginatedPaycheckGarnDedBeanHolder wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(
				wPGList, paginationBean.getPageNumber(), pageLength, wNoOfElements, paginationBean.getSortOrder(),paginationBean.getSortCriterion());

		wPGBDH.setObjectInd(1);

		wPGBDH.setPaidLoanAmount(paidLoanAmount);
		addRoleBeanToModel(model, request);
		model.addAttribute("gratPayHistBean", wPGBDH);

		return "pension/genGratHistoryForm";

	}
	@RequestMapping(method = RequestMethod.GET, params = { "eid" })
	public String setupForm(@RequestParam("eid") Long pEmpId, Model model,
			HttpServletRequest request) throws Exception{
		SessionManagerService.manageSession(request);
		BusinessCertificate bc = getBusinessCertificate(request);
		 PaginationBean paginationBean = getPaginationInfo(request);
		double paidLoanAmount = 0.0;
        List<CustomPredicate> predicates = Arrays.asList(CustomPredicate.procurePredicate("pensioner.id",pEmpId)
				,getBusinessClientIdPredicate(request));
		int wNoOfElements = this.genericService.getTotalPaginatedObjects(PaycheckGratuity.class, predicates).intValue();

		List<PaycheckGratuity> wPGList = null;
		if (wNoOfElements > 0) {
			wPGList = this.genericService.loadPaginatedObjects(PaycheckGratuity.class,predicates,(paginationBean.getPageNumber() - 1) * this.pageLength,pageLength,paginationBean.getSortOrder(),paginationBean.getSortCriterion());
			// Before sorting get the total amount...
			for (PaycheckGratuity p : wPGList)
				paidLoanAmount += p.getAmount();

			Collections.sort(wPGList, Comparator.comparing(PaycheckGratuity::getPayPeriodEnd));
		} else {
			wPGList = new ArrayList<>();
		}
		HiringInfo wHI = loadHiringInfoByEmpId(request,bc,pEmpId);
		wHI.setGratuityAmountStr(IConstants.naira+PayrollHRUtils.getDecimalFormat().format(wHI.getGratuityAmount()));
		PaginatedPaycheckGarnDedBeanHolder wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(
				wPGList, paginationBean.getPageNumber(), pageLength, wNoOfElements, paginationBean.getSortOrder(),paginationBean.getSortCriterion());
		wPGBDH.setDisplayTitle(wHI.getPensioner().getEmployeeId());
		wPGBDH.setId(wHI.getPensioner().getId());

		wPGBDH.setName(wHI.getPensioner().getDisplayNameWivTitlePrefixed());
		wPGBDH.setMode(wHI.getPensioner().getCurrentMdaName());

		wPGBDH.setSomeObject(wHI);

		wPGBDH.setTaxPaidStr(IConstants.naira
				+ PayrollHRUtils.getDecimalFormat().format(
						wHI.getGratuityAmount() - paidLoanAmount));

		wPGBDH.setObjectInd(2);

		wPGBDH.setPaidLoanAmount(paidLoanAmount);
		addRoleBeanToModel(model, request);
		model.addAttribute("gratPayHistBean", wPGBDH);

		return "pension/gratuityHistoryForm";

	}

}
