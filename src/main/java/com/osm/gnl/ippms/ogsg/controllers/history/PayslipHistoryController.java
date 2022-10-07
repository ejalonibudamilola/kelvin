package com.osm.gnl.ippms.ogsg.controllers.history;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PaySlipService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.report.ContractHistory;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PaycheckGenerator;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payslip.beans.EmployeePayMiniBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



@Controller
@RequestMapping("/paySlipView.do")
public class PayslipHistoryController extends BaseController {

	private final PaySlipService paySlipService;
	@Autowired
	public PayslipHistoryController(PaySlipService paySlipService) {
		this.paySlipService = paySlipService;
	}

	
 	@RequestMapping(method = RequestMethod.GET,params={"psid"})
	public String setupForm(@RequestParam ("psid") Long pid,Model model, HttpServletRequest request) throws Exception {
		
		SessionManagerService.manageSession(request, model);
	      BusinessCertificate bc = super.getBusinessCertificate(request);

		//pid is always the PaycheckInstId.
		EmployeePayMiniBean empPayMiniBean = new EmployeePayMiniBean();
		empPayMiniBean.setAdmin(bc.isSuperAdmin());
		AbstractPaycheckEntity empPayBean = (AbstractPaycheckEntity) this.genericService.loadObjectUsingRestriction(IppmsUtils.getPaycheckClass(bc),Arrays.asList(getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("id", pid)));
		//-- Parent Object is an instance of AbstractEmployeeEntity
		empPayMiniBean.setParentInstId(empPayBean.getParentObject().getId());
		 // if(empPayBean.getNetPay() == 0) {
		    	HiringInfo wHI = loadHiringInfoByEmpId(request,bc,empPayMiniBean.getParentInstId());
		    	if(!bc.isPensioner()) {
					LocalDate retireDate = PayrollBeanUtils.calculateExpDateOfRetirement(wHI.getBirthDate(), wHI.getHireDate(),loadConfigurationBean(request),bc);
					wHI.setExpectedDateOfRetirement(retireDate);
				}else{
					wHI.setExpectedDateOfRetirement(null);
				}
		    	empPayBean.setHiringInfo(wHI);
		//    }
		//Collection<State> pStateInfo = this.getStateInfoList();
		
		//BusinessClient bs = empPayBean.getBusinessClient() ;
		 /*if(empPayBean.getSuspendedInd() == 1) {
		    	LocalDate wStartDate = PayrollBeanUtils.getNextORPreviousDay(PayrollBeanUtils.getDateFromMonthAndYear(empPayBean.getRunMonth(), empPayBean.getRunYear(), false),false);
		    	LocalDate wEndDate = PayrollBeanUtils.getNextORPreviousDay(PayrollBeanUtils.getDateFromMonthAndYear(empPayBean.getRunMonth(), empPayBean.getRunYear(), true),true);
		    	
		    	List<SuspensionLog> wSL = genericService.loadAllObjectsUsingRestrictions(SuspensionLog.class,Arrays.asList(CustomPredicate.procurePredicate("suspensionDate",wStartDate, Operation.GREATER),
						CustomPredicate.procurePredicate("suspensionDate",wEndDate, Operation.LESS),CustomPredicate.procurePredicate("employee.id",empPayBean.getAbstractEmployeeEntity().getId())),null);
		        if(wSL.size() > 1) {
		        	Comparator<SuspensionLog> wComp = Comparator.comparing(SuspensionLog::getSuspensionDate).reversed();
		            Collections.sort(wSL,wComp);
		        }
		    	 empPayBean.setSuspensionLog(wSL.get(0));
		    }
		   */
		    if(empPayBean.getContractIndicator() == ON && empPayBean.getNetPay() < 1) {
		   	 //Look for a Contract that ended before the beginning of that Month & Year...
		   	 LocalDate wStartDate = PayrollBeanUtils.getDateFromMonthAndYear(empPayBean.getRunMonth(),empPayBean.getRunYear(), false);
		    	
		    	 
				List<ContractHistory> wSL = this.genericService.loadAllObjectsUsingRestrictions(ContractHistory.class,Arrays.asList(CustomPredicate.procurePredicate("contractEndDate",wStartDate, Operation.LESS_OR_EQUAL),
						CustomPredicate.procurePredicate("employee.id",empPayBean.getParentObject().getId())), null);
		        if(wSL.size() > 1) {
		        	Comparator<ContractHistory> wComp = Comparator.comparing(ContractHistory::getContractEndDate).reversed();
		            Collections.sort(wSL,wComp);
		        }
		    	 empPayBean.getHiringInfo().setContractEndDate((wSL.get(0).getContractEndDate()));
		    }

		model = (Model) new PaycheckGenerator().generatePaySlipModel(empPayMiniBean, empPayBean,genericService, bc, model,loadConfigurationBean(request),paySlipService);
		addRoleBeanToModel(model, request);
		model.addAttribute("roleBean", bc);
		return "payment/paySlipHistoryForm";
	}
	

}
