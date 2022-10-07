/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.hr;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.control.entities.EmployeeType;
import com.osm.gnl.ippms.ogsg.control.entities.TerminateReason;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ippms_hire_info")
@SequenceGenerator(name = "hireInfoSeq", sequenceName = "ippms_hire_info_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class HiringInfo extends AbstractHiringInfoEntity {

    private static final long serialVersionUID = 4714256687265532142L;

    @Id
    @GeneratedValue(generator = "hireInfoSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "hire_info_inst_id")
    private Long id;

    @Column(name = "service_hire_info_inst_id")
    private Long serviceHireInfo;

    @OneToOne
    @JoinColumn(name = "employee_inst_id")
    private Employee employee;

    @OneToOne
    @JoinColumn(name = "pensioner_inst_id")
    private Pensioner pensioner;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "emp_designation_inst_id")
    private EmployeeDesignation employeeDesignation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staff_type_inst_id")
    private EmployeeStaffType employeeStaffType;

    @Column(name = "ltg_last_paid")
    private LocalDate ltgLastPaid;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "term_reason_inst_id")
    private TerminateReason terminateReason;

    @Column(name = "pay_resp_allow", columnDefinition = "integer default '0'")
    private int payRespAllowanceInd;


    @Column(name = "contract_start_date")
    private LocalDate contractStartDate;

    @Column(name = "contract_end_date")
    private LocalDate contractEndDate;

    @Column(name = "incremental_date")
    private LocalDate incrementalDate;

    @Column(name = "contract_expired", columnDefinition = "integer default '0'")
    private int contractExpiredInd;

    @Column(name = "pensionable_ind", columnDefinition = "integer default '0'")
    private int pensionableInd;
    //--Pension Specific Indices
    @Column(name = "apportionment_ind", columnDefinition = "integer default '1'")
    private int apportionmentInd;

    @Column(name = "no_of_years_in_service", columnDefinition = "numeric(5,2) default '0.00'")
    private double noOfYearsInOgun;

    @Column(name = "annual_pension", columnDefinition = "numeric(15,2) default '0.00'")
    private double yearlyPensionAmount;

    @Column(name = "monthly_pension", columnDefinition = "numeric(15,2) default '0.00'")
    private double monthlyPensionAmount;

    @Column(name = "gratuity_paid", columnDefinition = "numeric(15,2) default '0.00'")
    private double gratuityAmount;

    @Column(name = "pension_end_date")
    private LocalDate pensionEndDate;

    @Column(name = "pension_start_date")
    private LocalDate pensionStartDate;

    @Column(name = "pension_end_flag" , columnDefinition = "integer default '0'")
    private int pensionEndFlag;

    @Column(name = "tax_id",columnDefinition = "varchar(14)")
    private String tin;

    @Transient private String tinMask;
    @Transient protected String rowSelected;
    @Transient private Long parentObjectId;
    @Transient private boolean hasEmployee;
    @Transient private String gratuityAmountStr;
    @Transient private boolean onApportionment;
    @Transient private String yearlyPensionAmountStr;
    @Transient private SalaryInfo salaryInfo;
    @Transient private String monthlyPensionAmountStr;
    @Transient private LocalDate activeTermDate;
    @Transient private boolean staffTypeChanged;
    /**
     * Applies to a SUBEB Staff
     */
    @Transient
    private int staffTypeInd;
    /**
     * Applies only to a pensioner.
     */
    @Transient private int lengthOfService;
    @Transient private int totalLenOfServInMonths;
    @Transient private String consolidatedStr;
    @Transient private double oldYearlyPensionAmount;
    @Transient private double oldPensionAmount;
    @Transient private LocalDate resetIAmAliveDate;
    @Transient private boolean hasGratuityInfo;
    @Transient private String monthlyGratuityAmountAsStr;
    @Transient private AbstractEmployeeEntity abstractEmployeeEntity;
    @Transient private EmployeeType employeeType;
    @Transient private boolean showGratuityAndPensionRows;
    @Transient private int schoolTransfer;
    @Transient private String monthlyPensionStr;
    @Transient private String yearlyPensionStr;
    @Transient private String gratuityStr;
    @Transient private int yearsOnPension;
    @Transient private String amAliveDateStr;
    @Transient  private boolean pensionableEmployee;
    @Transient private String suspendBind;
    @Transient private String employeeId;
    @Transient private String levelAndStepStr;
    @Transient private String salaryTypeName;

    /**
     * Prevents Null Pointer Exceptions....
     * New Bind Variables....
     * Mustola
     * 01/08/2022
     * SUBEB Bug fix.
     */
    @Transient private Long staffTypeId;
    @Transient private Long staffDesignationId;
    @Transient private Long oldStaffDesignationId;

    public HiringInfo(Long pId) {
        this.id = pId;
    }

    public String getTinMask() {
        if(StringUtils.trimToEmpty(this.tin).length() == IConstants.TIN_LENGTH) {
            this.tinMask = this.tin.substring(1,tin.length() - 3);
            this.tinMask += "XXXX";
        }
        return tinMask;
    }
    public boolean isPensionableEmployee() {
          pensionableEmployee = this.pensionableInd == 0;
        return pensionableEmployee;
    }


    public String getMonthlyPensionStr() {
        monthlyPensionStr = PayrollHRUtils.getDecimalFormat().format(this.monthlyPensionAmount);
        return monthlyPensionStr;
    }

    public String getYearlyPensionStr() {
        this.yearlyPensionStr = PayrollHRUtils.getDecimalFormat().format(this.yearlyPensionAmount);
        return yearlyPensionStr;
    }

    public String getGratuityStr() {
        if(this.gratuityAmount > 0)
            this.gratuityStr = PayrollHRUtils.getDecimalFormat().format(this.gratuityAmount);
        else
            gratuityStr = "0.00";
        return gratuityStr;
    }

    public int getYearsOnPension() {
        if(this.pensionStartDate != null){
            if(this.pensionEndDate == null)
                yearsOnPension =  LocalDate.now().getYear() - this.pensionStartDate.getYear() ;
            else
                yearsOnPension =  this.pensionEndDate.getYear() - this.pensionStartDate.getYear() ;
        }

        return yearsOnPension;
    }

    public AbstractEmployeeEntity getAbstractEmployeeEntity() {
        if(this.abstractEmployeeEntity != null) return this.abstractEmployeeEntity;

         if(this.isPensionerType())
            this.abstractEmployeeEntity = pensioner;
        else
            this.abstractEmployeeEntity = employee;
        return abstractEmployeeEntity;
    }
    public boolean isOnApportionment()
    {
        this.onApportionment = this.getApportionmentInd() == 1;
        return onApportionment;
    }
    public String getTerminatedStr() {
        if ((getTerminateInactive() != null) && (this.terminateInactive.equalsIgnoreCase("Y")))
            this.terminatedStr = "Yes";
        else
            this.terminatedStr = "No";

        return this.terminatedStr;
    }

    public String getAmAliveDateStr() {
        if (this.amAliveDate != null)
            this.amAliveDateStr = PayrollHRUtils.getDisplayDateFormat().format(this.amAliveDate);
        return amAliveDateStr;
    }

    public String getTerminatedDateStr() {
        if (getTerminateDate() != null)
            this.terminatedDateStr = PayrollHRUtils.getDisplayDateFormat().format(getTerminateDate());
        return this.terminatedDateStr;
    }

    public String getPensionStartDateStr() {
        if(this.pensionStartDate != null)
            this.pensionStartDateStr = PayrollHRUtils.getDisplayDateFormat().format(this.pensionStartDate);
        return pensionStartDateStr;
    }

    public String getPensionEndDateStr() {
        if(this.pensionEndDate != null)
            this.pensionEndDateStr = PayrollHRUtils.getDisplayDateFormat().format(this.pensionEndDate);
        return pensionEndDateStr;
    }

    public String getGenderStr() {
        if (gender != null) {
            if (gender.equalsIgnoreCase("M"))
                this.genderStr = "Male";
            else
                this.genderStr = "Female";
        }
        return this.genderStr;
    }



    public String getHireDateStr() {
        if (getHireDate() != null && StringUtils.isBlank(this.hireDateStr)) {
            this.hireDateStr = PayrollHRUtils.getDisplayDateFormat().format(getHireDate());
        }
        return this.hireDateStr;
    }


    public String getBirthDateStr() {
        if (getBirthDate() != null) {
            this.birthDateStr = PayrollHRUtils.getDisplayDateFormat().format(getBirthDate());
        }
        return this.birthDateStr;
    }



    public String getExpDateOfRetireStr() {
        if(this.isPensionerType()){
            if(this.amAliveDate != null)
                this.expDateOfRetireStr = PayrollHRUtils.getDisplayDateFormat().format(amAliveDate);
        }else {
            if (getExpectedDateOfRetirement() != null && StringUtils.isBlank(this.expDateOfRetireStr)) {
                this.expDateOfRetireStr = PayrollHRUtils.getDisplayDateFormat().format(getExpectedDateOfRetirement());
            }
        }
        return this.expDateOfRetireStr;
    }


    public int getNoOfYearsInService() {

        if(this.isTerminatedEmployee())
            if(this.isPensionerType())
                this.noOfYearsInService = this.pensionEndDate.getYear() - this.pensionStartDate.getYear();
            else
                this.noOfYearsInService = this.terminateDate.getYear() - this.hireDate.getYear();


        else
        if(this.isPensionerType())
            this.noOfYearsInService =  LocalDate.now().getYear() - this.pensionStartDate.getYear();
        else
            this.noOfYearsInService = LocalDate.now().getYear() - this.hireDate.getYear();



        return noOfYearsInService;

    }


    public boolean isLtgPaidForCurrentYear() {
        if (this.ltgLastPaid != null) {
            if(this.ltgLastPaid.getYear() == LocalDate.now().getYear()){
                this.ltgPaidForCurrentYear = true;
            }

        }
        return this.ltgPaidForCurrentYear;
    }

    public boolean isPayRespAllowance() {
        if (getPayRespAllowanceInd() > 0)
            this.payRespAllowance = true;
        return this.payRespAllowance;
    }



    public boolean isContractStaff() {
        this.contractStaff = this.getStaffInd() == 1;
        return this.contractStaff;
    }


    public String getContractEndDateStr() {
        if (this.contractEndDate != null)
            this.contractEndDateStr = PayrollHRUtils.getDisplayDateFormat().format(this.contractEndDate);
        else
            this.contractEndDateStr = "";
        return this.contractEndDateStr;
    }

    public String getContractStartDateStr() {
        if (this.contractStartDate != null)
            this.contractStartDateStr = PayrollHRUtils.getDisplayDateFormat().format(this.contractStartDate );
        else
            this.contractStartDateStr = "";
        return this.contractStartDateStr;
    }


    public boolean isNonContractStaff() {

        return this.staffInd != 1;
    }


    public boolean isSuspendedEmployee() {

        return this.suspended == 1;
    }

    public boolean isOnContract() {

        return this.staffInd == 1;

    }


    public boolean isTerminatedEmployee() {
        if(this.isPensionerType())
            return this.pensionEndDate != null;
        return (this.terminateDate != null && !isContractStaff());
    }

    public String getConfirmationDateAsStr() {
        if (this.confirmDate != null) {
            this.confirmationDateAsStr = PayrollHRUtils.getDisplayDateFormat().format(this.confirmDate);
        }
        return this.confirmationDateAsStr;
    }


    public int getAgeAtTermination() {
        if(this.isPensionerType()){
            if (this.birthDate != null && this.pensionEndDate != null) {
                this.ageAtTermination = this.pensionEndDate.getYear() - this.birthDate.getYear();
            }
        }else {
            if (this.birthDate != null && this.terminateDate != null) {
                this.ageAtTermination = this.terminateDate.getYear() - this.birthDate.getYear();
            }
        }
        return this.ageAtTermination;
    }


    public int getNoOfYearsAtRetirement() {
        if ((this.expectedDateOfRetirement != null) && (this.hireDate != null))
            this.noOfYearsAtRetirement = this.expectedDateOfRetirement.getYear() - this.hireDate.getYear();

        return this.noOfYearsAtRetirement;
    }


    public int getAgeAtRetirement() {
        if(this.isPensionerType()){
            if(this.amAliveDate != null && this.birthDate != null)
                this.ageAtRetirement = this.amAliveDate.getYear() - this.birthDate.getYear();
        }else {
            if ((this.expectedDateOfRetirement != null) && (this.birthDate != null))
                this.ageAtRetirement = this.expectedDateOfRetirement.getYear() - this.birthDate.getYear();
        }
        return this.ageAtRetirement;
    }



    public int getYearsOfService() {

        if(this.isTerminatedEmployee())
            if(this.isPensionerType())
                this.yearsOfService = this.pensionEndDate.getYear() - this.pensionStartDate.getYear();
            else
                this.yearsOfService = this.terminateDate.getYear() - this.hireDate.getYear();


        else
            if(this.isPensionerType())
                this.yearsOfService =  LocalDate.now().getYear() - this.pensionStartDate.getYear();
            else
                this.yearsOfService = LocalDate.now().getYear() - this.hireDate.getYear();



        return yearsOfService;
    }


    public boolean isContractExpired() {
      return contractExpiredInd == 1;
    }


    @Override
    public Long getParentId() {
        if(this.abstractEmployeeEntity != null)
            if(this.abstractEmployeeEntity.getId() != null)
                return this.abstractEmployeeEntity.getId();
        if(this.isPensionerType())
             return this.getPensioner().getId();
        else
            return this.getEmployee().getId();
    }

    @Override
    public int compareTo(Object pIncoming) {
        if(this.isPensionerType()){
            if (isSortByName()) {
                if (getPensioner().getLastName().compareToIgnoreCase(((HiringInfo) pIncoming).getPensioner().getLastName()) == 0) {
                    return getPensioner().getFirstName().compareToIgnoreCase(((HiringInfo) pIncoming).getPensioner().getFirstName());
                }
                return getPensioner().getLastName().compareToIgnoreCase(((HiringInfo) pIncoming).getPensioner().getLastName());
            }
            if ((isTerminatedEmployee()) && (((HiringInfo) pIncoming).isTerminatedEmployee())) {
                if (getPensioner().getLastName().compareToIgnoreCase(((HiringInfo) pIncoming).getPensioner().getLastName()) == 0) {
                    return getPensioner().getFirstName().compareToIgnoreCase(((HiringInfo) pIncoming).getPensioner().getFirstName());
                }
                return getPensioner().getLastName().compareToIgnoreCase(((HiringInfo) pIncoming).getPensioner().getLastName());
            }
        }else {
            if (pIncoming.getClass().isAssignableFrom(HiringInfo.class)) {

                if (isSortByName()) {
                    if (getEmployee().getLastName().compareToIgnoreCase(((HiringInfo) pIncoming).getEmployee().getLastName()) == 0) {
                        return getEmployee().getFirstName().compareToIgnoreCase(((HiringInfo) pIncoming).getEmployee().getFirstName());
                    }
                    return getEmployee().getLastName().compareToIgnoreCase(((HiringInfo) pIncoming).getEmployee().getLastName());
                }
                if ((isTerminatedEmployee()) && (((HiringInfo) pIncoming).isTerminatedEmployee())) {
                    if (getEmployee().getLastName().compareToIgnoreCase(((HiringInfo) pIncoming).getEmployee().getLastName()) == 0) {
                        return getEmployee().getFirstName().compareToIgnoreCase(((HiringInfo) pIncoming).getEmployee().getFirstName());
                    }
                    return getEmployee().getLastName().compareToIgnoreCase(((HiringInfo) pIncoming).getEmployee().getLastName());
                }

                return getNoOfYearsInService() - ((HiringInfo) pIncoming).getNoOfYearsInService();
            }
        }
        return 0;
    }

    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }

    public int getLengthOfService() {
        if(this.isPensionerType())
            this.lengthOfService = this.getTerminateDate().getYear() - this.getHireDate().getYear() ;

        return lengthOfService;
    }

    public int getTotalLenOfServInMonths() {
        if(this.isPensionerType()){
            totalLenOfServInMonths = this.getLengthOfService() * 12;
        }
        return totalLenOfServInMonths;
    }

    /**
     * this method differentiates a Pensioner
     * that has information in the CASP DB versus
     * one that comes from a revenue generating
     * subvention collecting agency e.g Water Corporation
     * @return
     */
    public boolean isHasEmployee() {
        if(this.isPensionerType()){
            return this.getPensioner().isHasEmployee();
        }
        return false;
    }

    public boolean isPensionerType(){
        return this.pensioner != null && !this.pensioner.isNewEntity();
    }


    public Long getOldStaffDesignationId() {
        return oldStaffDesignationId;
    }
}
