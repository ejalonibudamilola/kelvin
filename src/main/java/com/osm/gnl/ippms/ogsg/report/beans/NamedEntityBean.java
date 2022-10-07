package com.osm.gnl.ippms.ogsg.report.beans;


import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.paycheck.domain.EmployeePayBean;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.osm.gnl.ippms.ogsg.constants.IConstants.EMPTY_STR;

@Data
public class NamedEntityBean extends HRReportBean
        implements Comparable<Object> {


    public NamedEntityBean(Long pId, String pName) {
        this.setId(pId);
        this.setName(pName);

    }

    public NamedEntityBean() {

    }

    private static final long serialVersionUID = -579864309951308140L;

    private boolean hasName;
    private String showErrors;


    // -- For Payroll Run Statistics....
    private double netPay;
    private String netPayStr;
    private double totalPay;
    private String totalPayStr;
    private double totalDeductions;
    private String totalDeductionsStr;
    private int noOfActiveEmployees;
    private int retiredByBirthDate;
    private int retiredByHireDate;
    private int suspendedEmployee;
    private int contractEnded;
    private Double negativePay;
    private Integer noOfEmployeesWithNegPay;
    private Integer noOfEmployeesPaidByDays;
    private Double netPayByDays;

    //-- Values for Details Display
    private LocalDate birthDate;
    private LocalDate hireDate;
    private double projectedNetPay;
    private SalaryInfo salaryInfo;
    private String birthDateStr;
    private String hireDateStr;
    private LocalDate expectedDateOfRetirement;
    private String expectedDateOfRetirementStr;
    private String projectedNetPayStr;
    private String terminationDateStr;
    private LocalDate terminationDate;
    private Double specAllow;
    private Double monthlyPension;
    private Double annualPension;
    private String monthlyPensionStr;
    private String annualPensionStr;

    //--  value for identifying type
    //-- of GL or Pay Group
    private String typeOfEmpType;

    private String specAllowStr;

    private Integer currentOtherId;
    //-- For PaySlip Generation...
    private boolean mdaType;
    private int fromLevel;
    private int toLevel;
    private String remove = "Remove Entry";
    private String fontColor;
    private Set<Long> idList;
    private String paySlipObjType;
    private String paySlipDisplayObjId;
    private List<EmployeePayBean> empPayBeanList;

    private boolean salaryType;

    private int paySlipObjTypeInd;

    public boolean isHasName() {
        this.hasName = super.getName() != null;
        return this.hasName;
    }

    @Override
    public int compareTo(Object pArg0) {
        return 0;
    }


    public double getNetPay() {
        return netPay;
    }

    public String getNetPayStr() {
        if (netPayStr == null || netPayStr.equals(EMPTY_STR))
            netPayStr = PayrollHRUtils.getDecimalFormat().format(this.getNetPay());

        return netPayStr;
    }

    public String getTotalPayStr() {
        if (totalPayStr == null || totalPayStr.equals(EMPTY_STR))
            totalPayStr = PayrollHRUtils.getDecimalFormat().format(this.getTotalPay());

        return totalPayStr;
    }

    public String getTotalDeductionsStr() {
        if (totalDeductionsStr == null || totalDeductionsStr.equals(EMPTY_STR))
            totalDeductionsStr = PayrollHRUtils.getDecimalFormat().format(this.getTotalDeductions());
        return totalDeductionsStr;
    }

    public String getBirthDateStr() {
        if (this.getBirthDate() != null)
            this.birthDateStr = PayrollHRUtils.getFullDateFormat().format(this.getBirthDate());
        return birthDateStr;
    }

    public String getHireDateStr() {
        if (this.getHireDate() != null)
            this.hireDateStr = PayrollHRUtils.getFullDateFormat().format(this.getHireDate());
        return hireDateStr;
    }

    public String getProjectedNetPayStr() {
        this.projectedNetPayStr = PayrollHRUtils.getDecimalFormat().format(this.getProjectedNetPay());
        return projectedNetPayStr;
    }

    public String getExpectedDateOfRetirementStr() {
        if (this.getExpectedDateOfRetirement() != null)
            this.expectedDateOfRetirementStr = PayrollHRUtils.getFullDateFormat().format(this.getExpectedDateOfRetirement());
        return expectedDateOfRetirementStr;
    }

    public String getTerminationDateStr() {
        if (this.getTerminationDate() != null)
            terminationDateStr = PayrollHRUtils.getFullDateFormat().format(this.getTerminationDate());
        return terminationDateStr;
    }

    public boolean isMdaType() {
        this.mdaType = this.getPaySlipObjTypeInd() == 2;
        return mdaType;
    }

    public Set<Long> getIdList() {
        if (idList == null)
            idList = new HashSet<>();
        return idList;
    }

    public void setIdList(Set<Long> idList) {
        this.idList = idList;
    }

    public String getPaySlipObjType() {
        if (this.isMdaType())
            paySlipObjType = "Organization";
        else if (this.isSalaryType())
            paySlipObjType = "Pay Group";
        else
            paySlipObjType = "Staff";
        return paySlipObjType;
    }

    public boolean isSalaryType() {
        this.salaryType = this.getPaySlipObjTypeInd() == 1;
        return salaryType;
    }

    /**
     * @return "id_paySlipObjTypeInd"
     */
    public String getPaySlipDisplayObjId() {
        if (this.getPaySlipObjTypeInd() > 0 && IppmsUtils.isNotNullAndGreaterThanZero(this.getId())) {
            paySlipDisplayObjId = this.getId() + "_" + this.getPaySlipObjTypeInd();
        }
        return paySlipDisplayObjId;
    }


}