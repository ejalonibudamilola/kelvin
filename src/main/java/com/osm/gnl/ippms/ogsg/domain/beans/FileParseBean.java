/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.beans;

import com.osm.gnl.ippms.ogsg.configcontrol.domain.ConfigurationBean;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryTemp;
import com.osm.gnl.ippms.ogsg.domain.payment.BankInfo;
import com.osm.gnl.ippms.ogsg.domain.payment.PayTypes;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

@NoArgsConstructor
@Setter
@Getter
public class FileParseBean extends NamedEntity {
    private static final long serialVersionUID = -3998761404799588388L;
    private int objectTypeInd;
    private String typeIndCode;
    private Class<?> objectTypeClass;
    private List<MdaInfo> mdaInfoList;
    private HashMap<String, NamedEntity> employeeMap;
    private HashMap<String, ?> objectMap;
    private ConfigurationBean configurationBean;
    private Vector<NamedEntity> listToSave;
    private Vector<NamedEntity> errorList;
    private Vector<SalaryTemp> payGroupList;
    private volatile String uniqueUploadId;
    private int totalNumberOfRecords;
    private int successfulRecords;
    private int failedRecords;
    private boolean hasSaveList;
    private boolean hasErrorList;
    private boolean deduction;
    private boolean loan;
    private boolean specialAllowance;
    private String postActionUrl;
    private boolean employeeFileUpload; //set in the get method
    private int employeeFileUploadInd;
    private String objectTypeName;
    private HashMap<String, PayTypes> payTypeMap;
    private HashMap<String, NamedEntity> inactiveEmpMap;
    private HashMap<String, NamedEntity> suspendedMap;
    private String displayTitle;
    private boolean leaveBonus;
    private boolean bankBranch;
    private boolean salaryInfo;
    private boolean stepIncrement;
    private boolean updateAccountOnly;
    private int bankUpdateInd;
    private List<BankInfo> bankInfoList;
    private Long salaryTypeId;
    private Long bankId;
    private boolean deleteWarningIssued;
    private boolean saveMode;
    private String displayName;


    public boolean isHasSaveList() {
        if(this.isSalaryInfo())
            this.hasSaveList = IppmsUtils.isNotNullOrEmpty(getPayGroupList());
        else
            this.hasSaveList = IppmsUtils.isNotNullOrEmpty(getListToSave());
        return this.hasSaveList;
    }

    public boolean isHasErrorList() {
        this.hasErrorList = IppmsUtils.isNotNullOrEmpty(this.getErrorList());
        return this.hasErrorList;
    }

    public boolean isStepIncrement(){
        this.stepIncrement = this.objectTypeInd == IConstants.STEP_INCREMENT;
        return this.stepIncrement;
    }
    public boolean isDeduction() {
        this.deduction = this.objectTypeInd == IConstants.DEDUCTION;
        return this.deduction;
    }

    public boolean isLoan() {
        this.loan = this.objectTypeInd == IConstants.LOAN;
        return this.loan;
    }

    public boolean isSpecialAllowance() {
        this.specialAllowance = this.objectTypeInd == IConstants.SPEC_ALLOW_IND;
        return this.specialAllowance;
    }


    public int getSuccessfulRecords() {
        if (getListToSave() != null)
            this.successfulRecords = getListToSave().size();
        return this.successfulRecords;
    }

    public int getFailedRecords() {
        if (getErrorList() != null)
            this.failedRecords = getErrorList().size();

        return this.failedRecords;
    }

    public String getObjectTypeName() {
        if (this.isDeduction())
            objectTypeName = "Deduction";
        else if (this.isLoan())
            objectTypeName = "Loans";
        else if (this.isSpecialAllowance())
            objectTypeName = "Special Allowance";
        else if(this.isStepIncrement())
            objectTypeName = "Step Increment";

        return objectTypeName;
    }


}