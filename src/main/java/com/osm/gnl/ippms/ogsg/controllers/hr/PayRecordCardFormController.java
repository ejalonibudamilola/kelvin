package com.osm.gnl.ippms.ogsg.controllers.hr;
/**
 * This software code is the proprietary and intellectual property of GNL Systems Nigeria Limited.
 * ALL RIGHTS Reserved (C)2008-2011
 */

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractPaycheckEntity;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.Operation;
import com.osm.gnl.ippms.ogsg.abstractentities.predicate.PredicateBuilder;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.beans.PayRecordBean;
import com.osm.gnl.ippms.ogsg.domain.beans.PayRecordMiniBean;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.exception.EpmAuthenticationException;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.pagination.beans.PaginatedBean;
//import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.paycheck.domain.PaycheckDeduction;
import com.osm.gnl.ippms.ogsg.paycheck.domain.PaycheckGarnishment;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author Damilola Ejalonibu
 */

@Controller
@RequestMapping("/generatePRC.do")
@SessionAttributes(types = PayRecordBean.class)
public class PayRecordCardFormController extends BaseController {

	private final int PAGE_LENGTH = 12;

	@Autowired
	PaycheckService paycheckService;


	public PayRecordCardFormController(){

	}


	@RequestMapping(method = RequestMethod.GET,params={"eid"})
	public String setupForm(@RequestParam("eid")Long pEmpId,Model model,HttpServletRequest request) throws HttpSessionRequiredException, EpmAuthenticationException, InstantiationException, IllegalAccessException{
		SessionManagerService.manageSession(request, model);
		BusinessCertificate bc = super.getBusinessCertificate(request);

//		HiringInfo h = (HiringInfo)this.payrollService.loadObjectByClassAndKeyValue(HiringInfo.class,"employee.id",pEmpId);

		HiringInfo h = this.genericService.loadObjectUsingRestriction(HiringInfo.class, Arrays.asList(
				getBusinessClientIdPredicate(request), CustomPredicate.procurePredicate("employee.id",pEmpId)));

		PaginationBean paginationBean = getPaginationInfo(request);

		//First get the last paycheck created for this dude...

		AbstractPaycheckEntity wLastPaycheck = null;
		AbstractPaycheckEntity empPayBean = IppmsUtils.makePaycheckObject(bc);

		Long pid = paycheckService.getMaxPaycheckIdForEmployee(bc, pEmpId);
		if (!pid.equals(0L)) {
			wLastPaycheck = (AbstractPaycheckEntity) genericService.loadObjectById(IppmsUtils.getPaycheckClass(bc), pid);
		}
		List<AbstractPaycheckEntity> wList = null;

		PaginatedBean p  = null;

		if(wLastPaycheck == null || wLastPaycheck.isNewEntity()){
			p = new PaginatedBean(new ArrayList<AbstractPaycheckEntity>(), paginationBean.getPageNumber(), PAGE_LENGTH, 0, paginationBean.getSortCriterion(), paginationBean.getSortCriterion());
			p.setCanSendToExcel(false);
		}else{
//			wList  = this.payrollService.getEmployeePaychecksForPRC((pageNumber - 1) * PAGE_LENGTH, PAGE_LENGTH, sortOrder,sortCriterion,pEmpId,wLastPaycheck.getRunYear() - 2);
			wList = (List<AbstractPaycheckEntity>)this.genericService.loadPaginatedObjects(IppmsUtils.getPaycheckClass(bc), Arrays.asList(
					CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()),
					CustomPredicate.procurePredicate("employee.id",pEmpId),
					CustomPredicate.procurePredicate("runYear",wLastPaycheck.getRunYear() - 2, Operation.GREATER_OR_EQUAL)),
					(paginationBean.getPageNumber() - 1) * PAGE_LENGTH, PAGE_LENGTH, paginationBean.getSortOrder(),paginationBean.getSortCriterion());

			PredicateBuilder predicateBuilder = new PredicateBuilder();

			predicateBuilder.addPredicate(CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()));
			predicateBuilder.addPredicate(CustomPredicate.procurePredicate("employee.id",pEmpId));
			predicateBuilder.addPredicate(CustomPredicate.procurePredicate("runYear", wLastPaycheck.getRunYear() - 2, Operation.GREATER_OR_EQUAL));
//			final int wGLNoOfElements = this.payrollService.getTotalExistingPaychecksByEmployee(pEmpId,wLastPaycheck.getRunYear() - 2);
			final int wGLNoOfElements = this.genericService.countObjectsUsingPredicateBuilder(predicateBuilder, IppmsUtils.getPaycheckClass(bc));

			p = new PaginatedBean(wList, paginationBean.getPageNumber(), PAGE_LENGTH, wGLNoOfElements, paginationBean.getSortOrder(), paginationBean.getSortCriterion());
			if(wGLNoOfElements > 0){
				p.setCanSendToExcel(true);
			}
		}

		//Set Settables...

		p.setEmployeeName(h.getEmployee().getDisplayName());
		p.setEmpId(h.getEmployee().getEmployeeId());


		p.setBirthDateStr(PayrollHRUtils.getDisplayDateFormat().format(h.getBirthDate()));
		p.setHireDateStr(PayrollHRUtils.getDisplayDateFormat().format(h.getHireDate()));
		if(!(wLastPaycheck == null)) {
			if (wLastPaycheck.getRunYear() == 0) {
				if (h.getTerminateDate() != null) {
					p.setNoOfYearsInService(h.getTerminateDate().getYear() - h.getHireDate().getYear());

				} else {
					p.setNoOfYearsInService(0);
				}


			} else {
				p.setNoOfYearsInService(wLastPaycheck.getRunYear() - h.getHireDate().getYear());

			}
		}


		p.setSalaryScaleLevelAndStep(h.getEmployee().getSalaryInfo().getSalaryType().getName()+" - "+h.getEmployee().getSalaryInfo().getLevelAndStepAsStr());

		p.setId(pEmpId);

		model.addAttribute("miniBean", p);

		addRoleBeanToModel(model, request);
		return "payment/payRecordCardForm";
	}



	@SuppressWarnings("unused")
	private LocalDate determinePayPeriodEndFromPayPeriodStart(LocalDate pPayPeriodStart)
	{
		//System.out.println("Incoming.. "+PayrollHRUtils.getFullDateFormat().format(pPayPeriodStart));
		LocalDate wRetDate;
		LocalDate wDate;
		wDate = pPayPeriodStart;

//		wRetDate.set(wDate.get(Calendar.YEAR), wDate.get(Calendar.MONTH), wDate.getActualMaximum(Calendar.DAY_OF_MONTH));
		wRetDate = LocalDate.of(wDate.getYear(),wDate.getMonthValue(), wDate.getDayOfMonth());
		//System.out.println("Outgoing.. "+PayrollHRUtils.getFullDateFormat().format(wRetDate.getTime()));
		return wRetDate;
	}



	@SuppressWarnings("unused")
	private LocalDate determinePreceedingPayPeriodStart(LocalDate pPayPeriodStart)
	{
		//What is the logic here...
		LocalDate wRetDate;
		LocalDate wDate = null;
		wDate = pPayPeriodStart;
		int wMonth = wDate.getMonthValue();
		int wYear = wDate.getYear();

		//System.out.println("Incoming.. "+PayrollHRUtils.getFullDateFormat().format(pPayPeriodStart));

		//Now see if the last paycheck date is January...
		if(wMonth == IConstants.ON){
			//Now we need to create a Date for last year....
//			wRetDate.set(wYear - 1, Calendar.DECEMBER, 1);
			wRetDate = LocalDate.of(wYear - 1, 12, 1);

		}else{
			wRetDate = LocalDate.of(wYear, wMonth - 1, 1);
		}
		//System.out.println("Outgoing.. "+PayrollHRUtils.getFullDateFormat().format(wRetDate.getTime()));
		return wRetDate;
	}



	@SuppressWarnings({ "unused", "unchecked" })
	private PayRecordMiniBean setDeductionsAndContributions(Long pId, PayRecordMiniBean pBean, HttpServletRequest request){
		BusinessCertificate bc = super.getBusinessCertificate(request);
//		List<PaycheckGarnishment> wPG = (List<PaycheckGarnishment>) this.payrollService.loadObjectByClassAndKeyValue(PaycheckGarnishment.class, pId, "employeePayBean.id");

		List<PaycheckGarnishment> wPG = (List<PaycheckGarnishment>)this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getPaycheckGarnishmentClass(bc),
				Arrays.asList(CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()),
						CustomPredicate.procurePredicate("employeePayBean.id",pId)), null);

		double otherDeductions = 0;

		for(PaycheckGarnishment p : wPG){
			otherDeductions += p.getAmount();
		}
		pBean.setOtherDeductions(otherDeductions);

//		List<PaycheckDeduction> wPD = (List<PaycheckDeduction>) this.payrollService.loadObjectByClassAndKeyValue(PaycheckDeduction.class, pId, "employeePayBean.id");

		List<PaycheckDeduction> wPD = (List<PaycheckDeduction>)this.genericService.loadAllObjectsUsingRestrictions(IppmsUtils.getPaycheckDeductionClass(bc),
				Arrays.asList(CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()),
				CustomPredicate.procurePredicate("employeePayBean.id",pId)), null);

		double otherCont = 0;

		for(PaycheckDeduction p : wPD){
			otherCont += p.getAmount();
		}

		pBean.setOtherContributions(otherCont);

		return pBean;
	}



	@SuppressWarnings("unused")
	private PayRecordMiniBean setInformation(AbstractPaycheckEntity pEpb, PayRecordMiniBean pBean)
	{
		pBean.setMonthAndYearStr(PayrollHRUtils.getMiniMonthYearDateFormat().format(pEpb.getPayPeriodEnd()));
		pBean.setBasic(pEpb.getSalaryInfo().getMonthlyBasicSalary());
		pBean.setRent(pEpb.getRent());
		pBean.setTransport(pEpb.getTransport());
		pBean.setUtility(pEpb.getUtility());
		pBean.setMeal(pEpb.getMeal());
		pBean.setFurniture(pEpb.getFurniture());
		pBean.setDomesticServant(pEpb.getDomesticServant());
		pBean.setEntertainment(pEpb.getEntertainment());
		pBean.setHazard(pEpb.getHazard());
		pBean.setCallDuty(pEpb.getCallDuty());
		pBean.setJournal(pEpb.getJournal());
		pBean.setNhf(pEpb.getNhf());
		pBean.setPaye(pEpb.getMonthlyTax());
		pBean.setAcademicAllowance(pEpb.getAcademicAllowance());
		pBean.setRuralPosting(pEpb.getRuralPosting());
		pBean.setUnionDues(pEpb.getUnionDues());
		pBean.setTws(pEpb.getTws());
		pBean.setNetPay(pEpb.getNetPay());
		pBean.setPrincipalAllowance(pEpb.getPrincipalAllowance());
		pBean.setTss(pEpb.getTss());

		return pBean;
	}

	@SuppressWarnings("unused")
	private PayRecordMiniBean setZeroValueInformation(LocalDate pDate, PayRecordMiniBean pBean)
	{
		pBean.setMonthAndYearStr(PayrollHRUtils.getMiniMonthYearDateFormat().format(pDate));
		pBean.setBasic(0.00);
		pBean.setRent(0.00);
		pBean.setTransport(0.00);
		pBean.setUtility(0.00);
		pBean.setMeal(0.00);
		pBean.setFurniture(0.00);
		pBean.setDomesticServant(0.00);
		pBean.setEntertainment(0.00);
		pBean.setHazard(0.00);
		pBean.setCallDuty(0.00);
		pBean.setJournal(0.00);
		pBean.setNhf(0.00);
		pBean.setPaye(0.00);
		pBean.setAcademicAllowance(0.00);
		pBean.setRuralPosting(0.00);
		pBean.setUnionDues(0.00);
		pBean.setTws(0.00);
		pBean.setNetPay(0.00);
		pBean.setPrincipalAllowance(0.00);
		pBean.setTss(0.00);

		return pBean;
	}


}
