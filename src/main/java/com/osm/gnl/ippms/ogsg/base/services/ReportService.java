package com.osm.gnl.ippms.ogsg.base.services;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.domain.beans.SalarySummaryBean;
import com.osm.gnl.ippms.ogsg.domain.payroll.PayrollRunMasterBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.EntityUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.report.beans.WageSummaryBean;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Service("reportService")
@Repository
@Transactional(readOnly = true)
public class ReportService {
    private final GenericService genericService;
    private final PaycheckService paycheckService;
    private final SessionFactory sessionFactory;
    private final EmployeeService employeeService;

    public ReportService(GenericService genericService, PaycheckService paycheckService, SessionFactory sessionFactory, EmployeeService employeeService) {
        this.genericService = genericService;
        this.paycheckService = paycheckService;
        this.sessionFactory = sessionFactory;
        this.employeeService = employeeService;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public List<SalarySummaryBean> loadSalarySummary(int pRunMonth, int pRunYear, BusinessCertificate bc) throws Exception {

        HashMap<Long,SalarySummaryBean> salarySummaryBeanHashMap = new HashMap<>();
        HashMap<Long,MdaInfo> wMdaMap = this.genericService.loadObjectAsMapWithConditions(MdaInfo.class,
                Arrays.asList(CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId())),"id");
       // List<MdaInfo> wAgencyList = this.genericService.loadAllObjectsWithSingleCondition(MdaInfo.class,
       //         CustomPredicate.procurePredicate("businessClientId", bc.getBusinessClientInstId()), null);
     //   for (MdaInfo a : wAgencyList)
      //      salarySummaryBeanHashMap.put(a.getId(), new SalarySummaryBean(a.getName()));

        PayrollRunMasterBean payrollRunMasterBean = this.genericService.loadObjectUsingRestriction(PayrollRunMasterBean.class, Arrays.asList(CustomPredicate.procurePredicate("businessClientId",bc.getBusinessClientInstId()),
                CustomPredicate.procurePredicate("runMonth", pRunMonth), CustomPredicate.procurePredicate("runYear", pRunYear)));
        if(payrollRunMasterBean.isNewEntity())
            return new ArrayList<>(); //technically should never happen....
        List<EmployeePayBean> wEPBList = this.paycheckService.loadEmployeePayBeanByParentIdFromDateToDate(bc.getBusinessClientInstId(),pRunMonth, pRunYear, bc,null,false);
        LocalDate fDate = PayrollBeanUtils.getDateFromMonthAndYear(pRunMonth, pRunYear);
        HashMap<Long, Double> wDeductionMap = null; //Don't worry there will be no NPE....
        if(!bc.isPensioner())
            wDeductionMap  = this.employeeService.loadDeductionsForActiveEmployees(fDate, bc);

        SalarySummaryBean salarySummaryBean;
        MdaInfo mdaInfo;
         for (EmployeePayBean e : wEPBList) {
             salarySummaryBean = salarySummaryBeanHashMap.get(e.getMdaDeptMap().getMdaInfo().getId());
             if(salarySummaryBean == null){
                 mdaInfo = wMdaMap.get(e.getMdaDeptMap().getMdaInfo().getId());
                 salarySummaryBean = new SalarySummaryBean(mdaInfo.getName());
             }
             salarySummaryBean.setStaffCount(salarySummaryBean.getStaffCount() + 1);
             if(bc.isPensioner()){
                 salarySummaryBean.setTotalAllowance(salarySummaryBean.getTotalAllowance() + e.getSpecialAllowance());
                 salarySummaryBean.setMonthlyPension(salarySummaryBean.getMonthlyPension() + e.getMonthlyPension());
             }else {
                 salarySummaryBean.setTaxPaid(salarySummaryBean.getTaxPaid() + e.getTaxesPaid());
                 if (bc.isSubeb())
                     salarySummaryBean.setUnionDues(salarySummaryBean.getUnionDues() + e.getUnionDues());

                 if (wDeductionMap.containsKey(e.getEmployee().getId()))
                     salarySummaryBean.setOtherDeduction(salarySummaryBean.getOtherDeduction() + wDeductionMap.get(e.getEmployee().getId()).doubleValue());

                 salarySummaryBean.setTotalLoan(salarySummaryBean.getTotalLoan() + e.getTotalGarnishments());
                 salarySummaryBean.setPenContEmp(salarySummaryBean.getPenContEmp() + e.getContributoryPension());

                 salarySummaryBean.setBasicPay(salarySummaryBean.getBasicPay() + e.getMonthlyBasic());
                 salarySummaryBean.setTotalAllowance(salarySummaryBean.getTotalAllowance() + e.getTotalAllowance());
             }
             salarySummaryBean.setGrossPay(salarySummaryBean.getGrossPay() + e.getTotalPay());
             salarySummaryBean.setTotalDeduction(salarySummaryBean.getTotalDeduction() + e.getTotalDeductions());
             salarySummaryBean.setPayableAmount(salarySummaryBean.getPayableAmount() + e.getNetPay());
             salarySummaryBeanHashMap.put(e.getMdaDeptMap().getMdaInfo().getId(),salarySummaryBean);
        }
        List<SalarySummaryBean> wRetList = new ArrayList<>(salarySummaryBeanHashMap.values());

         if(payrollRunMasterBean.getRbaPercentage() > 0)
              for(SalarySummaryBean s : wRetList)
                   s.setRbaPaid(EntityUtils.convertDoubleToEpmStandard(EntityUtils.convertDoubleToEpmStandard(s.getGrossPay() * (payrollRunMasterBean.getRbaPercentage()/100.0D))));


        return wRetList;
    }

}
