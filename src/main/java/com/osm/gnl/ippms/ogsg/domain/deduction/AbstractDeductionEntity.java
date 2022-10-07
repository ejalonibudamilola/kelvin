/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.deduction;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractDescControlEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.domain.payment.BankInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.PayTypes;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractDeductionEntity extends AbstractDescControlEntity {


    @ManyToOne
    @JoinColumn(name = "pay_types_inst_id")
    private PayTypes payTypes;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    @ManyToOne
    @JoinColumn(name = "deduction_type_inst_id")
    private EmpDeductionType empDeductionType;

    @Column(name = "amount", columnDefinition = "numeric(10,2) default '0.00'", nullable = false)
    private double amount;

    @Column(name = "annual_max", columnDefinition = "numeric(10,2) default '0.00'", nullable = false)
    private double annualMax;


    @Column(name = "start_date")
    private LocalDate startDate;


    @Column(name = "end_date")
    private LocalDate endDate;


    @Transient private Long parentId;
    @Transient private int setEntryIndex;
    @Transient private int entryIndex;
    @Transient private boolean statutoryDeduction;
    @Transient private boolean rangedDeduction;
    @Transient private boolean apportionedDeduction;

    @Transient private AbstractEmployeeEntity parentObject;

    @Transient private String deductionAmountStr;
    @Transient private String name;
    @Transient private Long empDeductCatRef;
    @Transient private BankInfo bankInfo;
    @Transient private Long bankInstId;
    @Transient private String accountNumber;
    @Transient private String confirmAccountNumber;
    @Transient private String action;
    @Transient private Long empDeductTypeRef;
    @Transient private Long empDeductSubtypeRef;
    @Transient private Long empDeductPayTypeRef;
    @Transient private boolean editMode;
    @Transient private boolean delete;
    @Transient private boolean deleteWarningIssued;
    @Transient private double currentDeduction;
    @Transient private boolean locked;
    @Transient private boolean lockAmount;
    @Transient private boolean lockDescription;
    @Transient private boolean lockCategory;
    @Transient private boolean lockDeductType;
    @Transient private boolean lockPayType;
    @Transient private int runMonth;
    @Transient private int runYear;
    @Transient private String sortCode;
    @Transient private int statutoryInd;
    @Transient private String taxExemptInd;
    @Transient private boolean taxExempt;
    @Transient private String employeeId;
    @Transient private String showDates;
    @Transient private boolean showDateRows;
    @Transient private boolean editDenied;
    @Transient private transient double oldAmount;
    @Transient private transient Long negativePayId;
    @Transient private boolean mustDeletePayroll;
    @Transient private Employee employee;


    public abstract Long getId();

    public abstract void setId(Long pId);

    public boolean getEditMode() {
        return this.action != null && this.action.equalsIgnoreCase("e");
    }


    public boolean isStatutoryDeduction() {
        return this.statutoryInd == 1;
    }

    public boolean isTaxExempt() {
        this.taxExempt = getTaxExemptInd().equalsIgnoreCase("n");
        return this.taxExempt;
    }

    public String getDeductionAmountStr() {
        this.deductionAmountStr = PayrollHRUtils.getDecimalFormat().format(this.amount);
        return deductionAmountStr;
    }

    public void setDeductionAmountStr(String pDeductionAmountStr) {
        deductionAmountStr = pDeductionAmountStr;
    }

    public abstract Long getParentId();

    public abstract AbstractEmployeeEntity getParentObject();
    public abstract void setParentObject(AbstractEmployeeEntity abstractEmployeeEntity);
    @Override
    public int compareTo(Object pIncoming) {
        AbstractDeductionEntity _pIncoming;
        if(pIncoming != null && pIncoming.getClass().isAssignableFrom(AbstractDeductionEntity.class)) {
            _pIncoming = (AbstractDeductionEntity) pIncoming;
            if ((getEmpDeductionType() != null) && (_pIncoming.getEmpDeductionType() != null)) {
                if ((getDescription() != null) && (_pIncoming.getDescription() != null)) {
                    return getEmpDeductionType().getDescription()
                            .compareToIgnoreCase(_pIncoming.getEmpDeductionType().getDescription());
                }
                return getEmpDeductionType().getName().compareToIgnoreCase(_pIncoming.getEmpDeductionType().getName());
            }
        }
        return 0;
    }

    //public abstract void makeDeductionInfoObject(Long id);

    public abstract void makeParentObject(Long parentId);

}
