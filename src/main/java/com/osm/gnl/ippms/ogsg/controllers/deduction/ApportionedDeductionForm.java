package com.osm.gnl.ippms.ogsg.controllers.deduction;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.DeductionService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductGarnMiniBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductionDetailsBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollFlag;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.paycheck.domain.PaycheckDeduction;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRun;
import com.osm.gnl.ippms.ogsg.payroll.utils.*;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping({ "/apportionedDeductions.do" })
@SessionAttributes(types = { EmpDeductionType.class })
public class ApportionedDeductionForm extends BaseController {

    private final PaycheckService paycheckService;
    private final DeductionService deductionService;

    @Autowired
    public ApportionedDeductionForm(PaycheckService paycheckService, DeductionService deductionService) {
        this.paycheckService = paycheckService;
        this.deductionService = deductionService;
    }

    @ModelAttribute("monthList")
    protected List<NamedEntity> getMonthsList(){
        return PayrollBeanUtils.makeAllMonthList();
    }

    private static String VIEW_NAME = "deduction/apportionedDeductions";

    @ModelAttribute("yearList")
    protected Collection<NamedEntity> getYearList(HttpServletRequest request){
        return this.paycheckService.makePaycheckYearList(super.getBusinessCertificate(request));
    }

    @RequestMapping(method={RequestMethod.GET})
    public String setupForm(Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        BusinessCertificate bc = this.getBusinessCertificate(request);

        DeductionDetailsBean deductDetails = new DeductionDetailsBean();
        HashMap<Long, DeductGarnMiniBean> deductionBean = new HashMap<>();

        List <PaycheckDeduction> paycheckDeductions;

        List<PayrollRun> listPf = this.genericService.loadAllObjectsUsingRestrictions(PayrollRun.class, Arrays.asList(
                CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())), "id");
        Comparator<PayrollRun> c = Comparator.comparing(PayrollRun::getId);
        Collections.sort(listPf,c.reversed());

        if(IppmsUtils.isNotNullAndGreaterThanZero(listPf.size())){

            PayrollRun pf = listPf.get(0);

            if (!pf.isNewEntity()) {
                int noOfEmpWivNegPay = IppmsUtilsExt.countNoOfNegativePay(IppmsUtils.getPaycheckClass(bc),genericService,bc);

                if (noOfEmpWivNegPay > 0)
                {
                    return "redirect:negativePayWarningForm.do?noe=" + noOfEmpWivNegPay + "&rm=" + pf.getPayPeriodEnd().getMonthValue() + "&ry=" + pf.getPayPeriodEnd().getYear();
                }
                deductDetails.setFromDate(pf.getPayPeriodStart());
                deductDetails.setToDate(pf.getPayPeriodEnd());
                deductDetails.setCurrentDeduction("All Apportioned Deductions");
                deductDetails.setId(bc.getBusinessClientInstId());
                deductDetails.setFromDateStr(PayrollBeanUtils.getJavaDateAsString(pf.getPayPeriodStart()));
                deductDetails.setToDateStr(PayrollBeanUtils.getJavaDateAsString(pf.getPayPeriodEnd()));


                LocalDate wCal = pf.getPayPeriodEnd();
                deductDetails.setRunMonth(wCal.getMonthValue());
                deductDetails.setRunYear(wCal.getYear());



                paycheckDeductions = this.deductionService.loadEmpDeductionsByParentIdAndPayPeriod(null, wCal.getMonthValue(),wCal.getYear(), bc);
                DeductGarnMiniBean d;
                for (PaycheckDeduction p : paycheckDeductions) {
                    if (deductionBean.containsKey(p.getEmpDeductionType().getId())) {
                        d = deductionBean.get(p.getEmpDeductionType().getId());


                    } else {
                        d = new DeductGarnMiniBean();
                        d.setDeductionId(p.getEmpDeductionType().getId());

                    }
                    d.setAmount(d.getAmount() + p.getAmount());
                    d.setDescription(p.getEmpDeductionType().getDescription());
                    d.setName(p.getEmpDeductionType().getDescription());
                    d.setFirstAllotAmt((p.getEmpDeductionType().getFirstAllotAmt()  / 100) * p.getAmount());
                    d.setFirstAllotment(p.getEmpDeductionType().getFirstAllotment());
                    d.setSecondAllotAmt((p.getEmpDeductionType().getSecAllotAmt() / 100) * p.getAmount());
                    d.setSecondAllotment(p.getEmpDeductionType().getSecAllotment());
                    d.setThirdAllotment(p.getEmpDeductionType().getThirdAllotment());
                    d.setSecondAllotAmt((p.getEmpDeductionType().getThirdAllotAmt() / 100) * p.getAmount());
                    deductionBean.put(p.getEmpDeductionType().getId(), d);
                    deductDetails.setTotal(deductDetails.getTotal() + p.getAmount());
                }
                //Give a little overview...
                int wNoOfEmployees = paycheckDeductions.size();
                double wTotalLoanDeducted = this.deductionService.getTotalDeductions(0L,wCal.getMonthValue(), wCal.getYear(), bc);

                //String totalInWords = CurrencyWordGenerator.getInstance().convertToWords(wTotalLoanDeducted);
                deductDetails.setAmountInWords(CurrencyWordGenerator.getInstance().convertToWords(wTotalLoanDeducted));
                deductDetails.setNoOfEmployees(wNoOfEmployees);
                deductDetails.setTotalCurrentDeduction(wTotalLoanDeducted);
                deductDetails.setMonthAndYearStr(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(wCal.getMonthValue(), wCal.getYear()));
            }
        }
        else{
            PayrollFlag wPf = IppmsUtilsExt.getPayrollFlagForClient(genericService, bc);
            if (wPf.isNewEntity()) {
                //do some kind of notification here....
            } else {
                deductDetails.setFromDate(wPf.getPayPeriodStart());
                deductDetails.setToDate(wPf.getPayPeriodEnd());
                deductDetails.setCurrentDeduction("All Apportioned Deductions");
                deductDetails.setId(bc.getBusinessClientInstId());
//      deductDetails.setId(null);
                deductDetails.setFromDateStr(PayrollBeanUtils.getJavaDateAsString(wPf.getPayPeriodStart()));
                deductDetails.setToDateStr(PayrollBeanUtils.getJavaDateAsString(wPf.getPayPeriodEnd()));
                deductDetails.setRunMonth(wPf.getPayPeriodStart().getMonthValue());
                deductDetails.setRunYear(wPf.getPayPeriodStart().getYear());
            }
        }

        List <EmpDeductionType> deductionListFiltered = this.deductionService.findEmpDeductionsByBusinessClient(bc.getBusinessClientInstId());

        deductDetails.setDeductionMiniBean(EntityUtils.getDeductionList(deductionBean));
        deductDetails.setDeductionMiniBean(EntityUtils.setFormDisplayStyleOther(deductDetails.getDeductionMiniBean()));
        model.addAttribute("deductionList", deductionListFiltered);
        addRoleBeanToModel(model, request);
        addDisplayErrorsToModel(model, request);
        model.addAttribute("deductionDetails", deductDetails);
        return VIEW_NAME;
    }


}
