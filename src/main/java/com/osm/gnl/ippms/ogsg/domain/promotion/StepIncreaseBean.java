package com.osm.gnl.ippms.ogsg.domain.promotion;

import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "ippms_salary_step_increase")
@NoArgsConstructor
@Getter
@Setter
public class StepIncreaseBean implements Comparable<StepIncreaseBean> {
	@Id
	@Column(name = "employee_inst_id" , nullable = false)
	private Long id;

	@Column(name = "employee_name", length = 200 , nullable = false)
	private String name;

	@Column(name = "employee_id", length = 20, nullable = false)
	private String employeeId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "mda_inst_id", nullable = false)
	private MdaInfo mdaInfo;

	@Column(name = "old_salary_info_inst_id", columnDefinition = "numeric", nullable = false)
	private Long salaryInfoInstId;
	@Column(name = "salary_type_inst_id", columnDefinition = "numeric", nullable = false)
	private Long salaryTypeInstId;
	@Column(name = "new_salary_info_inst_id", columnDefinition = "numeric", nullable = false)
	private Long newSalaryInfoInstId;
	@Column(name = "salary_level", columnDefinition = "numeric", nullable = false)
	private int level;
	@Column(name = "old_salary_step", columnDefinition = "numeric", nullable = false)
	private int step;
	@Column(name = "new_salary_step", columnDefinition = "numeric", nullable = false)
	private int newStep;
	@Column(name = "salary_scale_name", length = 50)
	private String salaryScaleName;

	@Column(name = "business_client_inst_id" , nullable = false)
	private Long businessClientId;

	@Transient
	private String oldLevelAndStepStr;
	@Transient
	private String newLevelAndStepStr;

	@Transient
	private String salaryTypeLevelAndStepStr;


	public int compareTo(StepIncreaseBean pIncoming) {
		if ((this != null) && (pIncoming != null) && (!isNewEntity()) && (!pIncoming.isNewEntity())) {
			return getSalaryScaleName().compareToIgnoreCase(pIncoming.getSalaryScaleName());
		}

		return 0;
	}


	public String getOldLevelAndStepStr() {
		if ((getLevel() > 0) && (getStep() > 0)) {
			if (getStep() < 10)
				this.oldLevelAndStepStr = (getLevel() + ".0" + getStep());
			else {
				this.oldLevelAndStepStr = (getLevel() + "." + getStep());
			}
		}
		return this.oldLevelAndStepStr;
	}


	public String getNewLevelAndStepStr() {
		if ((getLevel() > 0) && (getNewStep() > 0)) {
			if (getNewStep() < 10)
				this.newLevelAndStepStr = (getLevel() + ".0" + getNewStep());
			else {
				this.newLevelAndStepStr = (getLevel() + "." + getNewStep());
			}
		}
		return this.newLevelAndStepStr;
	}


	public boolean isNewEntity() {
		return this.id == null;
	}

	public String getSalaryTypeLevelAndStepStr() {
		if(IppmsUtils.isNotNullAndGreaterThanZero(this.salaryTypeInstId) && this.level > 0 && this.step > 0)
			this.salaryTypeLevelAndStepStr = this.salaryTypeInstId + ":" + this.level + ":" + this.step;
		return salaryTypeLevelAndStepStr;
	}


}