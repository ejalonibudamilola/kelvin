/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.employee;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.control.entities.EmployeeType;
import com.osm.gnl.ippms.ogsg.control.entities.Rank;
import com.osm.gnl.ippms.ogsg.control.entities.Religion;
import com.osm.gnl.ippms.ogsg.control.entities.Title;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.location.domain.City;
import com.osm.gnl.ippms.ogsg.location.domain.LGAInfo;
import com.osm.gnl.ippms.ogsg.location.domain.State;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractEmployeeEntity extends AbstractControlEntity implements Serializable, Comparable<Object> {

    @Column(name = "employee_id", length = 20, nullable = false, unique = true)
    protected String employeeId;

    @Column(name = "legacy_employee_id", length = 30)
    protected String legacyEmployeeId;

    @Column(name = "business_client_inst_id", nullable = false)
    protected Long businessClientId;

    @Column(name = "first_name", length = 30, nullable = false)
    protected String firstName;

    @Column(name = "last_name", length = 30, nullable = false)
    protected String lastName;

    @Column(name = "initials", length = 30)
    protected String initials;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rank_inst_id", nullable = false)
    protected Rank rank;

    @Column(name = "residence_id", length = 20)
    protected String residenceId;

    @Column(name = "nin", length = 16)
    protected String nin;

    @Column(name = "email", length = 40)
    protected String email;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "title_inst_id", nullable = false)
    protected Title title;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_info_inst_id", nullable = false)
    protected SalaryInfo salaryInfo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lga_inst_id", nullable = false)
    protected LGAInfo lgaInfo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "religion_inst_id", nullable = false)
    private Religion religion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_type_inst_id", nullable = false)
    protected EmployeeType employeeType;

    @Column(name = "address1", length = 50, nullable = false)
    protected String address1;

    @Column(name = "address2", length = 50)
    protected String address2;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "city_inst_id")
    protected City city;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "state_inst_id", nullable =  false)
    protected State stateOfOrigin;

    @Column(name = "country", columnDefinition = "varchar(15) default 'Nigeria'")
    protected String country;

    @Column(name = "zip_code", length = 10)
    protected String zipCode;

    @Column(name = "gsm_number", length = 20)
    protected String gsmNumber;

    @Column(name = "file_number", length = 20)
    protected String fileNo;


    @Column(name = "status_ind", columnDefinition = "integer default '0'")
    protected int statusIndicator;

    @Column(name = "pay_appr_inst_id")
    protected Long payApprInstId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mda_dept_map_inst_id")
    protected MdaDeptMap mdaDeptMap;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "biometric_inst_id")
    protected BiometricInfo biometricInfo;


    @Transient
    protected String payEmployee;
    @Transient
    protected String assignedToObject;
    @Transient
    protected String salaryScale;
    @Transient
    protected String salaryScaleName;
    @Transient
    protected String levelAndStep;
    @Transient
    protected String levelStepStr;
    @Transient
    protected String ninMask;
    @Transient
    protected Long titleId;
    @Transient
    protected int salaryInfoId;
    @Transient
    protected List<LGAInfo> lgaList;
    @Transient
    protected int gradeLevelInstId;
    @Transient
    protected int ministryInstId;
    @Transient
    protected Long deptInstId;
    @Transient
    protected Long relId;
    @Transient
    protected String assignInd;
    @Transient
    protected String terminatedDate;
    @Transient
    protected String terminatedReason;
    @Transient
    protected Long lgaId;
    @Transient
    protected int objectInd;
    @Transient
    protected int mapId;
    @Transient
    protected String parentObjectName;
    @Transient
    protected String parentObjectParentName;
    @Transient
    protected boolean terminated;
    @Transient
    protected String disableEmployeeId;
    @Transient
    protected Long schoolInstId;
    @Transient
    protected boolean schoolEnabled;
    @Transient
    protected String displayName;
    @Transient
    protected String displayNameWivShortInitials;
    @Transient
    protected String displayNameWivTitlePrefixed;
    @Transient
    protected String displayNameWivTitleNamePrefixed;
    @Transient
    protected String displayNameWivTitlePostFixed;
    @Transient
    protected String employeeTypeStr;
    @Transient
    protected String mdaName;
    @Transient
    protected String currentMdaName;
    @Transient
    protected double totalTaxesPaid;
    @Transient
    protected double totalGrossPay;
    @Transient
    protected Long stateInstId;
    @Transient
    protected Long stateOfOriginId;
    @Transient
    protected String approvalStatus;
    @Transient
    protected boolean approvedForPayrolling;
    @Transient
    protected Long mapInstId;
    @Transient
    protected Long mdaInstId;
    @Transient
    protected String displayNameWivAsterix;
    @Transient
    protected double totalNetPay;
    @Transient
    protected double totalMonthlyBasic;
    @Transient
    protected String schoolName;
    @Transient
    protected String currentMdaFullName;
    @Transient
    protected boolean schoolStaff;
    @Transient
    protected String bvnNumber;
    @Transient
    protected String accountNumber;
    @Transient
    protected boolean canEdit;
    @Transient
    protected String gradeLevelAndStep;
    @Transient
    protected double deductionAmount;
    @Transient
    protected boolean schoolAssigned;
    @Transient
    protected boolean showContractLink;
    @Transient
    protected HiringInfo hiringInfo;
    @Transient
    protected String salaryTypeName;
    @Transient
    private boolean openResidenceId;
    @Transient
    private String businessName;
    @Transient
    private String biometricStatus;
    @Transient
    private Long biometricId;
    @Transient
    private Long cityId;
    @Transient
    private Double pensionContribution;


    public abstract SchoolInfo getSchoolInfo();

    public abstract String getParentObjectName();

    public String getNinMask() {
        if (StringUtils.trimToEmpty(this.nin).length() == 11) {
            this.ninMask = this.nin.substring(1, nin.length() - 5);
            this.ninMask += "XXXXXX";
        }
        return ninMask;
    }

    public String getSalaryTypeName() {
        if (this.salaryInfo != null && !this.salaryInfo.isNewEntity()) {
            if (this.salaryInfo.getSalaryType() != null && !this.salaryInfo.getSalaryType().isNewEntity()) {
                salaryTypeName = this.salaryInfo.getSalaryType().getName();
            }
        }
        return salaryTypeName;
    }

    public String getParentObjectParentName() {

        this.parentObjectParentName = this.mdaDeptMap.getMdaInfo().getMdaType().getName();
        return this.parentObjectParentName;
    }


    public final String getAssignedToObject() {
        this.assignedToObject = this.getMdaDeptMap().getMdaInfo().getCodeName();
        return this.assignedToObject;
    }


    public boolean isTerminated() {
        return this.statusIndicator == 1;
    }


    public int compareTo(Employee pEmployee) {
        return String.CASE_INSENSITIVE_ORDER.compare(this.getDisplayName(), pEmployee.getDisplayName());
    }


    public boolean isSchoolEnabled() {
        if (this.getMdaDeptMap() == null || this.getMdaDeptMap().getMdaInfo() == null)
            return this.schoolEnabled;

        this.schoolEnabled = this.getMdaDeptMap().getMdaInfo().isSchoolAttached();

        return this.schoolEnabled;
    }


    public String getDisplayName() {
        this.displayName = PayrollHRUtils.createDisplayName(getLastName(), getFirstName(), getInitials());
        return this.displayName;
    }

    public String getDisplayNameWivShortInitials() {
        this.displayNameWivShortInitials = PayrollHRUtils.createDisplayNameWivShortInitials(getLastName(), getFirstName(), getInitials());
        return this.displayNameWivShortInitials;
    }

    public String getApprovalStatus() {
        if (this.getPayApprInstId() != null && this.getPayApprInstId() > 0)
            approvalStatus = IConstants.APPROVED;
        else
            approvalStatus = IConstants.UNAPPROVED;
        return approvalStatus;
    }

    public String getBiometricStatus() {
        if (this.biometricInfo != null && !this.biometricInfo.isNewEntity())
            biometricStatus = IConstants.APPROVED_BIOMETRIC;
        else
            biometricStatus = IConstants.UNAPPROVED_BIOMETRIC;
        return biometricStatus;
    }

    public boolean isApprovedForPayrolling() {
        approvedForPayrolling = this.getPayApprInstId() != null && this.getPayApprInstId() > 0L;
        return approvedForPayrolling;
    }


    public String getDisplayNameWivTitlePrefixed() {
        if (this.getTitle() != null && !this.getTitle().isNewEntity())
            this.displayNameWivTitlePrefixed = this.getTitle().getName() + " " + this.getDisplayName();
        else
            this.displayNameWivTitlePrefixed = this.getDisplayName();
        return displayNameWivTitlePrefixed;
    }

    public String getDisplayNameWivTitleNamePrefixed() {
        if (this.getDisplayTitle() != null)
            this.displayNameWivTitleNamePrefixed = this.getDisplayTitle() + " " + " " + this.getDisplayName();
        else
            this.displayNameWivTitleNamePrefixed = this.getDisplayName();
        return displayNameWivTitleNamePrefixed;
    }


    public String getDisplayNameWivTitlePostFixed() {
        return displayNameWivTitlePostFixed;
    }

    public abstract boolean isSchoolStaff();


    public String getGradeLevelAndStep() {
        if (!this.isNewEntity() && !this.getSalaryInfo().isNewEntity())
            this.gradeLevelAndStep = this.getSalaryInfo().getSalaryScaleLevelAndStepStr();
        return gradeLevelAndStep;
    }

    public HiringInfo getHiringInfo() {
        if (this.hiringInfo == null) {
            this.hiringInfo = new HiringInfo();
        }
        return this.hiringInfo;
    }


    public String getCurrentMdaName() {
        if (this.getMdaDeptMap() != null && this.getMdaDeptMap().getMdaInfo() != null && !this.getMdaDeptMap().getMdaInfo().isNewEntity())
            this.currentMdaName = this.getMdaDeptMap().getMdaInfo().getName();
        return currentMdaName;
    }

    public abstract boolean isPensioner();

    protected abstract Object getChildEntity();

    public abstract Long getId();

    public abstract void setId(Long pId);


    public void setPensionContribution(Double pensionContribution) {
        this.pensionContribution = pensionContribution;
    }

    public Double getPensionContribution() {
        return pensionContribution;
    }
}
