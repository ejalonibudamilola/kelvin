package com.osm.gnl.ippms.ogsg.controllers.history;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedPaycheckGarnDedBeanHolder;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Damilola Ejalonibu
 * @Adapted for Ogun IPPMS
 * Mustola - 22/12/2020
 */
@Controller
@RequestMapping("/viewPayslipHistory.do")
public class PaycheckHistoryController extends BaseController {


	private final int pageLength = 20;



	public PaycheckHistoryController() {}



	@RequestMapping(method = RequestMethod.GET, params = {"eid"})
	public String setupForm(@RequestParam("eid")Long pEmpId, Model model, HttpServletRequest request) throws Exception {
		SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);

		PaginationBean paginationBean = getPaginationInfo(request);

		int wNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(new PredicateBuilder()
                .addPredicate(CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()))
                .addPredicate(CustomPredicate.procurePredicate("employee.id",pEmpId)),  IppmsUtils.getPaycheckClass(bc));

		List<AbstractPaycheckEntity> wPromoHist = null;
		if(wNoOfElements > 0){

 		wPromoHist = (List<AbstractPaycheckEntity>)this.genericService.loadPaginatedObjects(IppmsUtils.getPaycheckClass(bc), Arrays.asList(
		        CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()),
                CustomPredicate.procurePredicate("employee.id",pEmpId)),(paginationBean.getPageNumber() - 1) * pageLength,
                pageLength, paginationBean.getSortOrder(), paginationBean.getSortCriterion());
		//Collections.sort(wPromoHist);
		}else{
			wPromoHist = new ArrayList<>();
		}

		HiringInfo wHireInfo = loadHiringInfoByEmpId(request,bc,pEmpId);

     	PaginatedPaycheckGarnDedBeanHolder wPGBDH = new PaginatedPaycheckGarnDedBeanHolder(wPromoHist, paginationBean.getPageNumber(), pageLength, wNoOfElements, paginationBean.getSortCriterion(), paginationBean.getSortOrder());


     	if((IppmsUtils.isNotNull(wHireInfo)) && (IppmsUtils.isNotNull(wPGBDH))) {
			wPGBDH.setDisplayTitle(wHireInfo.getAbstractEmployeeEntity().getEmployeeId());
			wPGBDH.setId(wHireInfo.getAbstractEmployeeEntity().getId());
			wPGBDH.setEmployeeId(String.valueOf(pEmpId));
			wPGBDH.setName(wHireInfo.getAbstractEmployeeEntity().getTitle().getName() + " " + wHireInfo.getAbstractEmployeeEntity().getDisplayName());
			wPGBDH.setMode(wHireInfo.getAbstractEmployeeEntity().getParentObjectName());
			wPGBDH.setCurrentLevelAndStep(wHireInfo.getAbstractEmployeeEntity().getSalaryInfo().getSalaryType().getName() + " " + wHireInfo.getAbstractEmployeeEntity().getSalaryInfo().getLevelAndStepAsStr());

			//We need to set things like the Date of Birth, Hire Date
			wPGBDH.setBirthDate(wHireInfo.getBirthDateStr());
			wPGBDH.setHireDate(wHireInfo.getHireDateStr());
			if (wHireInfo.getAbstractEmployeeEntity().isTerminated()) {
				if (wHireInfo.getAbstractEmployeeEntity().isPensioner()) {
					wPGBDH.setNoOfYearsInService(wHireInfo.getPensionStartDate().getYear() - wHireInfo.getPensionEndDate().getYear());
					wPGBDH.setTerminationDate(PayrollHRUtils.getDisplayDateFormat().format(wHireInfo.getPensionEndDate()));

				} else {
					wPGBDH.setNoOfYearsInService(wHireInfo.getTerminateDate().getYear() - wHireInfo.getHireDate().getYear());
					wPGBDH.setTerminationDate(PayrollHRUtils.getDisplayDateFormat().format(wHireInfo.getTerminateDate()));
				}

				wPGBDH.setTerminatedEmployee(true);
				wPGBDH.setTerminationReason(wHireInfo.getTerminateReason().getName());

			} else {
				wPGBDH.setNoOfYearsInService(LocalDate.now().getYear() - wHireInfo.getHireDate().getYear());
			}
		}
     	else{
			wHireInfo = new HiringInfo();
			wPGBDH = new PaginatedPaycheckGarnDedBeanHolder();
		}

		addRoleBeanToModel(model, request);
		model.addAttribute("paycheckHistory", wPGBDH);

		return "employee/employeePaycheckHistoryForm";


	}


}
