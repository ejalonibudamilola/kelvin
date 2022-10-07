package com.osm.gnl.ippms.ogsg.controllers.statistics;

import com.osm.gnl.ippms.ogsg.base.services.StatisticsDetailsService;
import com.osm.gnl.ippms.ogsg.base.services.StatisticsService;
import com.osm.gnl.ippms.ogsg.domain.beans.MaterialityDisplayBean;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;

import java.time.LocalDate;
import java.util.List;


public class PayrollStatisticsDataGen {


    public PayrollStatisticsDataGen() {
    }


    public static MaterialityDisplayBean generateModel(BusinessCertificate businessCertificate, final StatisticsDetailsService statisticsDetailsService, int pStatCode, int pRunMonth, int pRunYear) {

        MaterialityDisplayBean wMDB = new MaterialityDisplayBean();

        NamedEntityBean wNamedEntity = statisticsDetailsService.loadPaycheckSummaryInfoByMonthAndYear(businessCertificate, pRunMonth, pRunYear);


        wMDB.setNetPaySum(wNamedEntity.getNetPay());
        wMDB.setTotalPaySum(wNamedEntity.getTotalPay());
        wMDB.setTotalDeductionSum(wNamedEntity.getTotalDeductions());
        wMDB.setNoOfEmployeesPaid(wNamedEntity.getNoOfActiveEmployees());
        //wMDB.setPageSize(pStatCode);
        wMDB.setObjectInd(pStatCode);
        wMDB.setRunMonth(pRunMonth);
        wMDB.setRunYear(pRunYear);

        List<NamedEntityBean> wDisplayList = null;
        switch (pStatCode) {

            case 1:
                //No of Employees Paid.
                wDisplayList = statisticsDetailsService.loadEmployeesPaid(businessCertificate, pRunMonth, pRunYear);

                wMDB.setName(businessCertificate.getStaffTypeName() + " Paid for Period " + PayrollBeanUtils.getMonthNameAndYearFromCalendarMonth(pRunMonth, pRunYear));
                wMDB.setFileName(businessCertificate.getStaffTypeName() + " Paid for Period " + PayrollBeanUtils.getDateAsStringWidoutSeparators(LocalDate.now()));
                break;
            case 2:
                //No Of Employees not paid by Birth Date....
                wDisplayList = statisticsDetailsService.loadEmployeesNotPaidByBirthOrHireDate(businessCertificate, pRunMonth, pRunYear, 1);

                wMDB.setName(businessCertificate.getStaffTypeName() + " Over 60 years");
                wMDB.setFileName(businessCertificate.getStaffTypeName() + "_Over_60_years");
                break;
            case 3:
                //No Of Employees not paid by Hire Date.....
                wDisplayList = statisticsDetailsService.loadEmployeesNotPaidByBirthOrHireDate(businessCertificate, pRunMonth, pRunYear, 2);
                wMDB.setName(businessCertificate.getStaffTypeName() + " Over 35 years in service");
                wMDB.setFileName(businessCertificate.getStaffTypeName() + "_Over_35_years_in_service");
                break;
            case 4:
                //No of Employees not paid by Contract Date...
                wDisplayList = statisticsDetailsService.loadEmployeesNotPaidByContractDate(businessCertificate, pRunMonth, pRunYear, true);

                wMDB.setNoOfEmpRetiredByContract(wDisplayList.size());
                wMDB.setName(businessCertificate.getStaffTypeName() + " Not Paid By Contract");
                wMDB.setFileName(businessCertificate.getStaffTypeName() + "_Not_Paid_By_Contract");
                break;
            case 5:
                //No of Employees not paid by Contract Date...
                wDisplayList = statisticsDetailsService.loadEmployeesNotPaidByContractDate(businessCertificate, pRunMonth, pRunYear, false);

                wMDB.setNoOfEmpRetiredByContract(wDisplayList.size());
                wMDB.setName(businessCertificate.getStaffTypeName() + " Paid By Contract");
                wMDB.setFileName(businessCertificate.getStaffTypeName() + "_Paid_By_Contract");
                break;
            case 6:
                //No of Employees not paid by Suspension
                wDisplayList = statisticsDetailsService.loadNotPaidBySuspension(businessCertificate, pRunMonth, pRunYear);

                wMDB.setNoOfEmpRetiredBySuspension(wDisplayList.size());
                wMDB.setName(businessCertificate.getStaffTypeName() + " Not Paid (Suspension)");
                wMDB.setFileName(businessCertificate.getStaffTypeName() + "_Not_Paid_Suspension");
                break;
            case 7:
                //Employees with Negative Pay...
                wDisplayList = statisticsDetailsService.loadEmployeesWithNegativeNetPayDetails(businessCertificate, pRunMonth, pRunYear);

                wMDB.setNoOfEmployeesWithNegativePay(wDisplayList.size());
                wMDB.setName(businessCertificate.getStaffTypeName() + " With Negative Pay");
                wMDB.setFileName(businessCertificate.getStaffTypeName() + "_With_Negative_Pay");
                break;
            case 8:
                wDisplayList = statisticsDetailsService.loadEmployeesPaidByDaysDetails(businessCertificate, pRunMonth, pRunYear);

                wMDB.setNoOfEmployeesPaidByDays(wDisplayList.size());
                wMDB.setName(businessCertificate.getStaffTypeName() + " Retiring in " + wMDB.getMonthAndYearStr());
                wMDB.setFileName(businessCertificate.getStaffTypeName() + "_Retiring");
                break;
            case 9:
                wDisplayList = statisticsDetailsService.loadEmployeesPaidSpecAllowDetails(businessCertificate, pRunMonth, pRunYear);
                for (NamedEntityBean w : wDisplayList) {
                    wMDB.setSpecAllowNetPay(wMDB.getSpecAllowNetPay() + w.getNetPay());
                    wMDB.setSpecAllowTotalPay(wMDB.getSpecAllowTotalPay() + w.getTotalPay());
                }
                wMDB.setNoOfEmpPaidSpecialAllowance(wDisplayList.size());
                wMDB.setName(businessCertificate.getStaffTypeName() + " Paid Special Allowances");
                wMDB.setFileName(businessCertificate.getStaffTypeName() + "_Paid_Special_Allowances");
                break;
            case 10:
                wDisplayList = statisticsDetailsService.loadEmployeesPaidByInterdiction(businessCertificate, pRunMonth, pRunYear);
                for (NamedEntityBean w : wDisplayList) {
                    wMDB.setSpecAllowNetPay(wMDB.getSpecAllowNetPay() + w.getNetPay());
                    wMDB.setSpecAllowTotalPay(wMDB.getSpecAllowTotalPay() + w.getTotalPay());
                }
                wMDB.setNoOfEmpPaidSpecialAllowance(wDisplayList.size());
                wMDB.setName(businessCertificate.getStaffTypeName() + " On Interdiction ");
                wMDB.setFileName(businessCertificate.getStaffTypeName() + "_On_Interdiction");
                break;
            case 11:
                wDisplayList = statisticsDetailsService.loadEmployeesNotPaidByTermination(businessCertificate, pRunMonth, pRunYear);
                for (NamedEntityBean w : wDisplayList) {
                    wMDB.setSpecAllowNetPay(wMDB.getSpecAllowNetPay() + w.getNetPay());
                    wMDB.setSpecAllowTotalPay(wMDB.getSpecAllowTotalPay() + w.getTotalPay());
                }
                wMDB.setNoOfEmpPaidSpecialAllowance(wDisplayList.size());
                wMDB.setName(businessCertificate.getStaffTypeName() + " Not Paid (Terminated) ");
                wMDB.setFileName(businessCertificate.getStaffTypeName() + "_Not_Paid_Terminated");
                break;
            case 12:
                wDisplayList = statisticsDetailsService.loadEmployeesNotPaidByApproval(businessCertificate, pRunMonth, pRunYear);
                for (NamedEntityBean w : wDisplayList) {
                    wMDB.setSpecAllowNetPay(wMDB.getSpecAllowNetPay() + w.getNetPay());
                    wMDB.setSpecAllowTotalPay(wMDB.getSpecAllowTotalPay() + w.getTotalPay());
                }
                wMDB.setNoOfEmpPaidSpecialAllowance(wDisplayList.size());
                wMDB.setName(businessCertificate.getStaffTypeName() + " Not Approved for Payroll ");
                wMDB.setFileName(businessCertificate.getStaffTypeName() + "_Not_Approved_For_Payroll");
                break;
            case 13:
                wDisplayList = statisticsDetailsService.loadEmployeesNotPaidByContractDate(businessCertificate, pRunMonth, pRunYear, true);
                wMDB.setNoOfEmpRetiredByContract(wDisplayList.size());
                wMDB.setName(businessCertificate.getStaffTypeName() + " Not Paid (Contract Employees)");
                wMDB.setFileName(businessCertificate.getStaffTypeName() + "_Not_Paid_Contracted_Employees");
                break;

            case 14:
                wDisplayList = statisticsDetailsService.loadPensionersAwaitingCalc(businessCertificate, pRunMonth, pRunYear, true);
                wMDB.setNoOfEmpRetiredByContract(wDisplayList.size());
                wMDB.setName(businessCertificate.getStaffTypeName() + " Awaiting Recalculation");
                wMDB.setFileName(businessCertificate.getStaffTypeName() + "_Not_Paid_Contracted_Employees");
                break;

        }

        wMDB.setObjectList(wDisplayList);

        return wMDB;


    }

    public static MaterialityDisplayBean genStatsSummary(StatisticsService pPayrollService, BusinessCertificate businessCertificate, int pRunMonth, int pRunYear) {

        MaterialityDisplayBean wMDB = new MaterialityDisplayBean();
        //Net Pay for Active Employees...
        NamedEntityBean wNamedEntity = pPayrollService.loadPaycheckSummaryInfoByMonthAndYear(businessCertificate, pRunMonth, pRunYear);


        wMDB.setNetPaySum(wNamedEntity.getNetPay());
        wMDB.setTotalPaySum(wNamedEntity.getTotalPay());
        wMDB.setTotalDeductionSum(wNamedEntity.getTotalDeductions());
        wMDB.setNoOfEmployeesPaid(wNamedEntity.getNoOfActiveEmployees());

        //--- Employees NOT Paid.
        int wSerialNum = 0;
        wMDB = pPayrollService.loadEmployeesNotPaidByMonthAndYear(businessCertificate, pRunMonth, pRunYear, wMDB);
        // wMDB.setEmployeesNotPaidByBirthDate(wNamedEntity.getRetiredByBirthDate());
        if (wMDB.getEmployeesNotPaidByBirthDate() > 0)
            wMDB.setNoOfEmpRetiredByBirthDate(++wSerialNum);


        // wMDB.setEmployeesNotPaidByHireDate(wNamedEntity.getRetiredByHireDate());

        if (wMDB.getEmployeesNotPaidByHireDate() > 0)
            wMDB.setNoOfEmpRetiredByHireDate(++wSerialNum);

        wNamedEntity = pPayrollService.loadEmployeesPaidByContract(businessCertificate, pRunMonth, pRunYear, false);
        wMDB.setEmployeesPaidByContract(wNamedEntity.getNoOfActiveEmployees());
        wMDB.setNetPayContract(wNamedEntity.getNetPay());
        wMDB.setTotalPayContract(wNamedEntity.getTotalPay());
        if (wMDB.getEmployeesNotPaidByContract() > 0)
            wMDB.setNoOfEmpRetiredByContract(++wSerialNum);

        wMDB.setEmployeesNotPaidBySuspension(pPayrollService.loadEmpNotPaidDueToReason(businessCertificate, pRunMonth, pRunYear, true, false, false));
        if (wMDB.getEmployeesNotPaidBySuspension() > 0)
            wMDB.setNoOfEmpRetiredBySuspension(++wSerialNum);

        wMDB.setEmployeesNotPaidByTermination(pPayrollService.loadEmpNotPaidDueToReason(businessCertificate, pRunMonth, pRunYear, false, true, false));
        if (wMDB.getEmployeesNotPaidByTermination() > 0)
            wMDB.setNoOfEmpRetiredByTermination(++wSerialNum);

        wMDB.setEmployeesNotPaidByApproval(pPayrollService.loadEmpNotPaidDueToReason(businessCertificate, pRunMonth, pRunYear, false, false, true));
        if (wMDB.getEmployeesNotPaidByApproval() > 0)
            wMDB.setNoOfEmpNotPaidByApproval(++wSerialNum);

        wNamedEntity = pPayrollService.loadNoOfEmployeesPaidByInterdiction(businessCertificate, pRunMonth, pRunYear);
        wMDB.setEmployeesOnInterdiction(wNamedEntity.getNoOfActiveEmployees());
        if (wMDB.getEmployeesOnInterdiction() > 0) {
            wMDB.setNoOfEmpPaidByInterdiction(++wSerialNum);
            wMDB.setNetPayInterdiction(wNamedEntity.getNetPay());
            wMDB.setTotalPayInterdiction(wNamedEntity.getTotalPay());

        }
        //Now Get Negative pay Employes

        wNamedEntity = pPayrollService.loadEmployeesWithNegativeNetPay(businessCertificate, pRunMonth, pRunYear);

        wMDB.setNoOfEmployeesWithNegativePay(wNamedEntity.getNoOfEmployeesWithNegPay());
        wMDB.setNegativePaySum(wNamedEntity.getNegativePay());
        if (wMDB.getEmployeesNotPaidBySuspension() > 0)
            wMDB.setNoOfEmpWithNegPay(++wSerialNum);


        //Now Get the Number of Employees Paid By Days...

        wNamedEntity = pPayrollService.loadEmployeesPaidByDays(businessCertificate, pRunMonth, pRunYear);

        wMDB.setNetPayByDays(wNamedEntity.getNetPayByDays());
        wMDB.setNoOfEmployeesPaidByDays(wNamedEntity.getNoOfEmployeesPaidByDays());
        if (wMDB.getNoOfEmployeesPaidByDays() > 0)
            wMDB.setNoOfEmpPaidByDays(++wSerialNum);


        //Now Get the Number of Employees Paid Special Allowances...
        wNamedEntity = pPayrollService.loadEmployeesPaidSpecialAllowancesByMonthAndYear(businessCertificate, pRunMonth, pRunYear);

        wMDB.setSpecialAllowance(wNamedEntity.getNetPayByDays());
        wMDB.setNoOfEmpPaidSpecAllow(wNamedEntity.getNoOfEmployeesPaidByDays());
        if (wMDB.getNoOfEmpPaidSpecAllow() > 0)
            wMDB.setNoOfEmpPaidSpecialAllowance(++wSerialNum);


        wMDB.setRunMonth(pRunMonth);
        wMDB.setRunYear(pRunYear);

        return wMDB;

    }

}
