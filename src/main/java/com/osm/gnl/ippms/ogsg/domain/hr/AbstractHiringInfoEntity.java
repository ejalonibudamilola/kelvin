/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import com.osm.gnl.ippms.ogsg.contribution.domain.PfaInfo;
import com.osm.gnl.ippms.ogsg.control.entities.MaritalStatus;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.payment.PayPeriod;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractHiringInfoEntity extends AbstractControlEntity implements Serializable, Comparable<Object> {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "marital_status_inst_id")
    protected MaritalStatus maritalStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pfa_inst_id")
    protected PfaInfo pfaInfo;


    @Column(name = "birth_date", nullable = false)
    protected LocalDate birthDate;


    @Column(name = "hire_date", nullable = false)
    protected LocalDate hireDate;

    @Column(name = "gender", columnDefinition = "varchar(1)", nullable = false)
    protected String gender;


    @Column(name = "last_pay_date")
    protected LocalDate lastPayDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pay_period_inst_id")
    protected PayPeriod payPeriod;

    @Column(name = "hire_report_filed", columnDefinition = "varchar(1) default 'Y'", nullable = false)
    protected String hireReportFiled;

    @Column(name = "employee_verify_info", columnDefinition = "varchar(1) default 'Y'")
    protected String employeeVerifyInfo;

    @Column(name = "terminate_inactive", columnDefinition = "varchar(1) default 'N'", nullable = false)
    protected String terminateInactive;


    @Column(name = "confirmation_date")
    protected LocalDate confirmDate;

    @Column(name = "terminate_date")
    protected LocalDate terminateDate;

    @Column(name = "salary_stop_code", columnDefinition = "integer default '0'")
    protected int suspended;


    @Column(name = "suspension_start_date")
    protected LocalDate suspensionDate;


    @Column(name = "expected_date_of_retirement")
    protected LocalDate expectedDateOfRetirement;


    @Column(name = "last_promotion_date")
    protected LocalDate lastPromotionDate;

    @Column(name = "i_am_alive_date")
    protected LocalDate amAliveDate;

    @Column(name = "next_promotion_date")
    protected LocalDate nextPromotionDate;

    @Column(name = "last_pay_period", length = 23)
    protected String lastPayPeriod;

    @Column(name = "current_pay_period", length = 23)
    protected String currentPayPeriod;

    @Column(name = "pay_period_ph", length = 23)
    protected String lastPayPeriodHolder;

    @Column(name = "pension_pin_code", length = 20)
    protected String pensionPinCode;

    @Column(name = "staff_indicator", columnDefinition = "integer default '0'")
    protected int staffInd;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    @Column(name = "vol_ded_ind", columnDefinition = "integer default '0'")
    protected int volDedInd;

   /* @Column(name = "contract_inst_id")
    protected Long contractId;*/

    @Transient protected boolean needsApproval;
    @Transient private boolean terminatedEmployee;
    @Transient private boolean suspendedEmployee;
    @Transient protected String proposedMda;
    @Transient protected int yearsOfService;
    @Transient protected boolean payRespAllowance;
    @Transient protected String terminatedStr;
    @Transient protected boolean ltgPaidForCurrentYear;
    @Transient protected String terminatedDateStr;
    @Transient protected String terminatedDateAsStr;
    @Transient protected String pensionStartDateStr;
    @Transient protected String pensionEndDateStr;
    @Transient protected String expDateOfRetireStr;
    @Transient protected String genderStr;
    @Transient protected String hireDateStr;
    @Transient protected String birthDateStr;
    @Transient protected int noOfYearsInService;
    @Transient protected boolean contractStaff;
    @Transient protected String contractEndDateStr;
    @Transient protected String contractStartDateStr;
    @Transient protected String bvnNo;
    @Transient protected boolean contractExpired;
    @Transient protected String confirmationDateAsStr;
    @Transient protected int ageAtRetirement;
    @Transient protected int ageAtTermination;
    @Transient protected int noOfYearsAtRetirement;
    @Transient protected boolean sortByName;
    @Transient protected AbstractEmployeeEntity abstractEmployeeEntity;
    @Transient protected double totalEmoluments;
    @Transient protected String totalEmolumentsStr;
    @Transient private String pfaName;


    public String getTerminatedDateAsStr() {
        if(this.terminateDate != null)
            terminatedDateAsStr = PayrollHRUtils.getDisplayDateFormat().format(this.terminateDate);
        return terminatedDateAsStr;
    }



    public abstract Long getId();

    public abstract Long getParentId();




    @Override
    public int compareTo(Object t) {
        return 0;
    }

    @Override
    public abstract boolean isNewEntity();


    public void setPfaName(String pfaName) {
        this.pfaName = pfaName;
    }

    public String getPfaName() {
        return pfaName;
    }
}
