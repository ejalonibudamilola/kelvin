package com.osm.gnl.ippms.ogsg.controllers.deduction;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.auth.domain.SessionManagerService;
import com.osm.gnl.ippms.ogsg.base.services.DeductionService;
import com.osm.gnl.ippms.ogsg.base.services.PaycheckService;
import com.osm.gnl.ippms.ogsg.controllers.BaseController;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductGarnMiniBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.DeductionDetailsBean;
import com.osm.gnl.ippms.ogsg.domain.deduction.EmpDeductionType;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.paycheck.domain.PaycheckDeduction;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/apportionedDeductionDetails.do")
@SessionAttributes(types={DeductionDetailsBean.class})
public class ApportionedDeductionDetailsForm extends BaseController {

    @Autowired
    PaycheckService paycheckService;

    @Autowired
    DeductionService deductionService;

    private final String VIEW = "deduction/otherApportionedDeductionDetails";

    @ModelAttribute("monthList")
    protected List<NamedEntity> getMonthsList(){
        return PayrollBeanUtils.makeAllMonthList();
    }
    @ModelAttribute("yearList")
    protected Collection<NamedEntity> getYearList(HttpServletRequest request){
        return this.paycheckService.makePaycheckYearList(getBusinessCertificate(request));
    }


    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, params={"did", "rm", "ry", "pid"})
    public String setupForm(@RequestParam("did") Long dedTypeId, @RequestParam("rm") int pRunMonth,
                            @RequestParam("ry") int pRunYear, @RequestParam("pid") Long pid, Model model, HttpServletRequest request) throws Exception
    {
        SessionManagerService.manageSession(request, model);
        BusinessCertificate bc = getBusinessCertificate(request);

        boolean filter = false;
        if (IppmsUtils.isNotNullAndGreaterThanZero(dedTypeId)) {
            filter = true;
        }
        DeductionDetailsBean deductDetails = new DeductionDetailsBean();
        HashMap<Long, DeductGarnMiniBean> deductionBean = new HashMap<Long,DeductGarnMiniBean>();

        List<PaycheckDeduction> paycheckDeductions;

        LocalDate sDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);
        LocalDate eDate = PayrollBeanUtils.getEndOfMonth(sDate);
        String pSDate = PayrollBeanUtils.getJavaDateAsString(sDate);
        String pEDate = PayrollBeanUtils.getJavaDateAsString(eDate);

        deductDetails.setFromDate(sDate);
        deductDetails.setToDate(eDate);
        deductDetails.setRunMonth(pRunMonth);
        deductDetails.setRunYear(pRunYear);
        if (filter)
        {
            EmpDeductionType wDt = this.genericService.loadObjectUsingRestriction(EmpDeductionType.class,
                    Arrays.asList(CustomPredicate.procurePredicate("id", dedTypeId), CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())));
            deductDetails.setCurrentDeduction(wDt.getName() + " - " + wDt.getDescription());
        }
        deductDetails.setId(pid);
        deductDetails.setFromDateStr(pSDate);
        deductDetails.setToDateStr(pEDate);

        paycheckDeductions = this.deductionService.loadEmpDeductionsByParentIdAndPayPeriod(dedTypeId, sDate.getMonthValue(), sDate.getYear(), bc);
        DeductGarnMiniBean d;
        for (PaycheckDeduction p : paycheckDeductions) {


            if (deductionBean.containsKey(p.getEmployee().getId())) {
                d = deductionBean.get(p.getEmployee().getId());

                d.setAmount(d.getAmount() + p.getAmount());

                deductDetails.setTotal(deductDetails.getTotal() + p.getAmount());

                deductionBean.put(p.getEmployee().getId(), d);
            }
            else
            {
                d = new DeductGarnMiniBean();
                d.setName(PayrollHRUtils.createDisplayName(p.getEmployee().getLastName(), p.getEmployee().getFirstName(), p.getEmployee().getInitials()));
                d.setFirstAllotment(p.getEmpDeductionType().getFirstAllotAmtStr());
                d.setFirstAllotAmt(((p.getEmpDeductionType().getFirstAllotAmt() / 100.0) * p.getAmount()));
                d.setFirstAllotPercent(p.getEmpDeductionType().getFirstAllotAmt());

                d.setSecondAllotment(p.getEmpDeductionType().getSecAllotAmtStr());
                d.setSecondAllotPercent(p.getEmpDeductionType().getSecAllotAmt());
                d.setSecondAllotAmt((p.getEmpDeductionType().getSecAllotAmt() / 100.0) * p.getAmount());

                d.setThirdAllotment(p.getEmpDeductionType().getThirdAllotAmtStr());
                d.setThirdAllotPercent(p.getEmpDeductionType().getThirdAllotAmt());
                d.setThirdAllotmentAmt((p.getEmpDeductionType().getThirdAllotAmt() / 100.0) * p.getAmount());
                d.setId(p.getEmployee().getId());
                d.setParentInstId(p.getEmpDedInfo().getId());

                d.setEmployeeId(p.getEmployee().getEmployeeId());
                d.setAmount(d.getAmount() + p.getAmount());

                deductDetails.setSecondAllotAmtTotal(deductDetails.getSecondAllotAmtTotal() + d.getSecondAllotAmt());
                deductDetails.setFirstAllotAmtTotal(deductDetails.getFirstAllotAmtTotal() + d.getFirstAllotAmt());
                deductDetails.setThirdAllotAmtTotal(deductDetails.getThirdAllotAmtTotal() + d.getThirdAllotmentAmt());

                deductDetails.setTotal(deductDetails.getTotal() + p.getAmount());
                deductDetails.setTotalBalance(deductDetails.getTotalBalance() + d.getBalanceAmount());
                deductionBean.put(p.getEmployee().getId(), d);
            }
        }

        deductDetails.setMonthAndYearStr(PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pRunMonth, pRunYear));
        deductDetails.setTotalCurrentDeduction(this.deductionService.getTotalDeductions(dedTypeId,pRunMonth, pRunYear, bc));
        deductDetails.setNoOfEmployees(paycheckDeductions.size());


        deductDetails.setDeductionId(dedTypeId);

        List <EmpDeductionType> deductionListFiltered = this.deductionService.findEmpDeductionsByBusinessClient(bc.getBusinessClientInstId());

        deductDetails.setDeductionMiniBean(EntityUtils.getDeductionList(deductionBean));
        Collections.sort(deductDetails.getDeductionMiniBean());
        deductDetails.setPageSize(deductDetails.getDeductionMiniBean().size());
        deductDetails.setDeductionMiniBean(EntityUtils.setFormDisplayStyleOther(deductDetails.getDeductionMiniBean()));

        model.addAttribute("deductionList", deductionListFiltered);
        model.addAttribute("deductionDetails", deductDetails);
        addRoleBeanToModel(model, request);
        return VIEW;
    }


    @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public String processSubmit(@RequestParam(value="_updateReport", required=false) String updRep,
                                @RequestParam(value="_cancel", required=false) String cancel,
                                @RequestParam(value="_go", required=false) String go,
                                @ModelAttribute("deductionDetails") DeductionDetailsBean pDDB, BindingResult result, SessionStatus status, Model model, HttpServletRequest request) throws Exception {
        SessionManagerService.manageSession(request, model);

        if (isButtonTypeClick(request,REQUEST_PARAM_CANCEL))
        {

            return "redirect:reportsOverview.do";
        }



        if (isButtonTypeClick(request,REQUEST_PARAM_UPDATE_REPORT)) {

            if (pDDB.getDeductionId() > 0) {
                return "redirect:apportionedDeductionDetails.do?did=" + pDDB.getDeductionId() + "&rm=" + pDDB.getRunMonth() + "&ry=" + pDDB.getRunYear() + "&pid=" + pDDB.getId();
            }
            return "redirect:apportionedDeductions.do?did=" + pDDB.getDeductionId() + "&rm=" + pDDB.getRunMonth() + "&ry=" + pDDB.getRunYear() + "&pid=" + pDDB.getId();
        }

        if (isButtonTypeClick(request, REQUEST_PARAM_GO))
        {
            return "redirect:" + pDDB.getSubLinkSelect() + ".do";
        }

        return "redirect:apportionedDeductions.do";
    }

}
