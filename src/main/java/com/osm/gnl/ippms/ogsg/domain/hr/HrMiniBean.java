package com.osm.gnl.ippms.ogsg.domain.hr;

import com.osm.gnl.ippms.ogsg.abstractentities.DependentEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.paygroup.AllowanceRuleMaster;
import com.osm.gnl.ippms.ogsg.domain.promotion.PromotionTracker;
import com.osm.gnl.ippms.ogsg.organization.model.Department;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntityBean;
import com.osm.gnl.ippms.ogsg.suspension.domain.SuspensionLog;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class HrMiniBean extends DependentEntity
{
  private static final long serialVersionUID = 1L;
  private List<Department> deptList;
  private List<NamedEntityBean> mdaInfoList;
  private List<?> mappedParentDeptList;
  private List<?> parentTypeList;
  private List<Employee> employeeList;
  private AllowanceRuleMaster allowanceRuleMaster;
  private int ministryId;
  private boolean disableParent;
  private int mapId;
  private Department department;
  private Long rankInstId;
  private Long oldRankId;
  private Long currentObjectId;
  private Long employeeInstId;
  private int parentObjectType;
  private String hideRow;
  private String hideRow1;
  private String hideRow2;
  private List<NamedEntityBean> payGroupList;
  private Long mdaId;
  private Long salaryTypeId;
  private boolean mdaType;
  private boolean salaryType;
  private Long salaryInfoInstId;
  private int fromLevel;
  private int toLevel;
  private boolean flagWarningIssued;
  private AbstractEmployeeEntity employee;
  private Map<Long,Long> idMap;
  private List<SalaryInfo> levelStepList;
  private boolean makeLevelStepList;
  private String objectCode;
  private String employeeId;
  private String legacyEmployeeId;
  private String firstName;
  private String lastName;
  private String middleName;
  private int monthInd;
  private int yearInd;
  private String amountStr;
  private String payeStr;
  private boolean showConfirmationRow;
  private boolean showParentRow;
  private String showForConfirm;
  private String showArrearsRow;
  private String oldLevelAndStep;
  private double oldSalary;
  private Long hiringInfoId;
  private PromotionTracker promoTracker;
  private boolean warningIssued;
  private String gradeLevelAndStep;
  private String hireDate;
  private String yearsOfService;
  private String assignedToObject;
  private String salaryScale;
  private Long oldSalaryInfoInstId;
  private Long schoolInstId;
  private boolean schoolAssigned;
  private LocalDate arrearsStartDate;
  private LocalDate arrearsEndDate;
  private String payArrearsInd;
  private String flagInd;
  private LocalDate refDate;
  private String refNumber;
  private Long terminateReasonId;
  private String hideRow3;
  private boolean terminate;
  private boolean superAdmin;
  private Long parentId;
  private String arrearsPercentageStr;
  private AbstractEmployeeEntity abstractEmployeeEntity;
  private boolean useForSingle;
  private boolean reabsorbEmployee;
  private String levelAndStepStr;
  private boolean partPayable;
  private double partPayment;
  private String suspensionDate;
  private String terminateDate;
  private String newLevelAndStep;
  private String oldSalaryStr;
  private String newSalaryStr;
  private SuspensionLog suspensionLog;
  private boolean logPresent;
  public int activeInd;



  /**
   * Special Method for PaySlip Selector View.
   * @return
   */
  public Long getCurrentObjectId()
  {
	if(IppmsUtils.isNotNullAndGreaterThanZero(this.getMdaId())) {
		this.currentObjectId = this.getMdaId();
	}else if(IppmsUtils.isNotNullAndGreaterThanZero(this.getSalaryTypeId() )) {
		this.currentObjectId = this.getSalaryTypeId();
	}else if (IppmsUtils.isNotNullAndGreaterThanZero(this.getId())) {
		this.currentObjectId = this.getId();
	}
    return this.currentObjectId;
  }

  public String getObjectCode() {
    objectCode = this.getCurrentObjectId()+"_"+this.getMapId();
    return objectCode;
  }

  //TODO This method must be removed....
  public boolean isMdaType() {
	   this.mdaType = this.mapId == 2;
	return this.mdaType;
 }

  public boolean isSalaryType() {
    salaryType = IppmsUtils.isNotNullAndGreaterThanZero(this.salaryTypeId);
    return salaryType;
  }


}

