/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.generic.domain;

import com.osm.gnl.ippms.ogsg.forensic.domain.Materiality;
import com.osm.gnl.ippms.ogsg.domain.payment.BusinessPaySchedule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class BusinessCertificate implements Serializable
{
  private Object sessionId;
  private Long businessClientInstId;
  private Long businessClientContactInstId;
  private Long loginId;
  private String userName;
  private String businessName;
  private String cityName;
  private String stateName;
  private String bizAddr;
  private String loggedOnUserNames;
  private String employeeName;
  private boolean firstTimeLogin;
  private String businessClientUID;
  private String businessState;
  private BusinessPaySchedule businessPaySchedule;
  private Materiality materiality;
  private boolean hasDefPaySched;
  private String logonUserRole;
  private boolean government;
  private boolean privilegedUser;
  private int privilegedInd;
  private boolean superAdmin;
  private boolean canEditLoans;
  private boolean canEditSpecAllow;
  private boolean canEditDeductions;
  private boolean checkedForPendingStepIncrement;
  private int deductionObjectType;
  private int loanObjectType;
  private int specAllowObjectType;
  private boolean hasPendingTransfers;
  private int noOfPendingTransfers;
  private boolean hasPendingEmployeeApprovals;
  private int noOfPendingEmployeeApprovals;
  private boolean canApproveLeaveBonus;
  private boolean hasLeaveBonusErrorData;
  private boolean rerunPayrollExists;
  private int bankBranchObjectType;
  private int noOfDeletedPaychecks;
  private int semaphore;
  private String orgName;
  private List<String> excludedUrls;

  //--New Merge Variable
  private boolean civilService;
  private boolean statePension;
  private boolean subeb;
  private boolean localGovt;
  private boolean localGovtPension;
  private boolean executive;

  private String paycheckBeanName;
  private String deductionBeanName;
  private String loanBeanName;
  private String specAllowBeanName;
  private String roleDisplayName;
  private String clientLogo;
  private String clientReportLogo;
  private String clientDesc;
  private String ogNumAlphaPart;
  
  //New UI Variables
  private boolean sysUser;
  private String staffTitle;
  private String mdaTitle;

  //New merge Variable
  private String headTitle;
  private String loginHeadTitle;
  private String staffTypeName;
  private String reportStaffTypeName;
  //--For Pensioners ONLY.
  private Long parentClientId;
  private String empIdStartVal;
  //--For Ease of joins
  private String employeeIdJoinStr;
  //--For Custom Reports...
  private String employeeTableIdJoinStr;
  //For Notifications
  private boolean canApprove;
  private String targetUrl;
  private String orgFileNameDiff;
  private boolean ignorePendingPaychecks;
  private boolean ignorePendingChecksBind;

  public boolean isIgnorePendingPaychecks() {
    return ignorePendingPaychecks =  this.ignorePendingChecksBind;
  }


  public String getEmployeeIdJoinStr() {
    if(this.isPensioner()){
      employeeIdJoinStr = "pensioner.id";
    }else{
      employeeIdJoinStr = "employee.id";
    }
    return employeeIdJoinStr;
  }
  public String getEmployeeTableIdJoinStr() {
    if(this.isPensioner()){
      employeeTableIdJoinStr = "pensioner_inst_id";
    }else{
      employeeTableIdJoinStr = "employee_inst_id";
    }
    return employeeTableIdJoinStr;
  }

  public int getPrivilegedInd(){
    if(this.isPrivilegedUser())
      privilegedInd = 1;
    return privilegedInd;
  }

  public final boolean isNewEntity()
  {
    return this.sessionId == null;
  }

  public BusinessPaySchedule getBusinessPaySchedule() {
    if (this.businessPaySchedule == null)
      this.businessPaySchedule = new BusinessPaySchedule();
    return this.businessPaySchedule;
  }

  public void setBeanNames(){
    if(this.isLocalGovt()){
      staffTitle = "LG Number";
      mdaTitle = "L.G";
      staffTypeName = "LG Staff";
      reportStaffTypeName = "LGStaff";
      this.orgFileNameDiff = "_LGSC";
      this.paycheckBeanName = "EmployeePayBeanLG";
      this.deductionBeanName = "PaycheckDeductionLG";
      this.loanBeanName = "PaycheckGarnishmentLG";
      this.specAllowBeanName = "PaycheckSpecialAllowanceLG";
      this.empIdStartVal = "LG";
    }else if(this.isCivilService()){
      staffTitle = "OG Number";
      mdaTitle = "M.D.A";
      staffTypeName = "Employee";
      reportStaffTypeName = staffTypeName;
      this.paycheckBeanName = "EmployeePayBean";
      this.deductionBeanName = "PaycheckDeduction";
      this.loanBeanName = "PaycheckGarnishment";
      this.specAllowBeanName = "PaycheckSpecialAllowance";
      this.empIdStartVal = "OG";
      this.orgFileNameDiff = "_CS";
    }else if(this.isLocalGovtPension()){
      staffTypeName = "BLG Pensioner";
      reportStaffTypeName = "BLGPensioner";
      staffTitle = "Pension ID";
      mdaTitle = "L.G";
      this.paycheckBeanName = "EmployeePayBeanBLGP";
      this.deductionBeanName = "PaycheckDeductionBLGP";
      this.loanBeanName = "PaycheckGarnishmentBLGP";
      this.specAllowBeanName = "PaycheckSpecialAllowanceBLGP";
      this.empIdStartVal = "BLGP";
      this.orgFileNameDiff = "_BLGP";
    }else if(this.isSubeb()){
      staffTitle = "OGSB Number";
      mdaTitle = "LGEA";
      staffTypeName = "SUBEB Staff";
      reportStaffTypeName = "SUBEBStaff";
      this.paycheckBeanName = "EmployeePayBeanSubeb";
      this.deductionBeanName = "PaycheckDeductionSubeb";
      this.loanBeanName = "PaycheckGarnishmentSubeb";
      this.specAllowBeanName = "PaycheckSpecialAllowanceSubeb";
      this.empIdStartVal = "OGSB";
      this.orgFileNameDiff = "_OGSB";
    }else if(this.isStatePension()){
      staffTitle = "Pensioner ID";
      mdaTitle = "TCO";
      staffTypeName = "State Pensioner";
      reportStaffTypeName = "StatePensioner";
      this.paycheckBeanName = "EmployeePayBeanPension";
      this.deductionBeanName = "PaycheckDeductionPension";
      this.loanBeanName = "PaycheckGarnishmentPension";
      this.specAllowBeanName = "PaycheckSpecialAllowancePension";
      this.empIdStartVal = "PEN";
      this.orgFileNameDiff = "_SPEN";
    }
  }


  public String getOrgName(Long businessClientInstId) {
    if(businessClientInstId.equals(1000L)){
      orgName = "Civil/Public Service";
    }else  if(businessClientInstId.equals(1001L)){
      orgName = "State Pension";
    }else  if(businessClientInstId.equals(1002L)){
      orgName = "B.L.G.P";
    }else  if(businessClientInstId.equals(1003L)){
      orgName = "L.G.S.C";
    }else  if(businessClientInstId.equals(1004L)){
      orgName = "S.U.B.E.B";
    }else{
      orgName = "Unknown Org";
    }
    return orgName;
  }

  public String getOrgNameForFileNaming(Long businessClientInstId) {
    if(businessClientInstId.equals(1000L)){
      orgName = "Civil-Public Service";
    }else  if(businessClientInstId.equals(1001L)){
      orgName = "State Pension";
    }else  if(businessClientInstId.equals(1002L)){
      orgName = "Local Govt Pension";
    }else  if(businessClientInstId.equals(1003L)){
      orgName = "LG Service Comm";
    }else  if(businessClientInstId.equals(1004L)){
      orgName = "SUBEB";
    }else{
      orgName = "Unknown Org";
    }
    return orgName;
  }
  public boolean isPensioner(){
    return (this.isStatePension() || this.isLocalGovtPension());
  }


}