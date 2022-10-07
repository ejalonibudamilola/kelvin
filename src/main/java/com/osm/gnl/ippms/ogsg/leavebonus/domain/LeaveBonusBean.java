package com.osm.gnl.ippms.ogsg.leavebonus.domain;

import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import com.osm.gnl.ippms.ogsg.report.beans.ReportViewAttributes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;


@Entity
@Table(name = "ippms_leave_bonus")
@SequenceGenerator(name = "leaveBonusSeq", sequenceName = "ippms_leave_bonus_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class LeaveBonusBean extends ReportViewAttributes implements Serializable, Comparable<LeaveBonusBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1057946804412537301L;

	@Id
	@GeneratedValue(generator = "leaveBonusSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "leave_bonus_inst_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "employee_inst_id")
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "salary_info_inst_id")
	private SalaryInfo salaryInfo;

	@ManyToOne
	@JoinColumn(name = "leave_bonus_master_inst_id")
	private LeaveBonusMasterBean leaveBonusMasterBean;

	@Column(name = "leave_bonus", columnDefinition = "numeric(15,2) default '0.00'")
	private double leaveBonusAmount;


	@Column(name = "last_mod_ts", nullable = false)
	private LocalDate lastModTs;

	@Column(name = "created_time", nullable = false, length = 20)
	private String createdTime;

	@Transient private String leaveBonusAmountStr;
	@Transient private String employeeName;
	@Transient private String levelAndStep;
	@Transient private String payGroup;
	@Transient private String employeeId;
	@Transient private Long employeeInstId;
	@Transient private Long salaryInfoInstId;


	public LeaveBonusBean(Long pId) {
		this.id = pId;
	}

	public String getLeaveBonusAmountStr() {
		this.leaveBonusAmountStr = PayrollHRUtils.getDecimalFormat().format(this.getLeaveBonusAmount());
		return leaveBonusAmountStr;
	}


	public boolean isNewEntity() {
		return this.id == null;
	}

	@Override
	public int compareTo(LeaveBonusBean o) {

		return 0;
	}

	public String getEmployeeName() {
		if (!this.isNewEntity() && !this.getEmployee().isNewEntity())
			this.employeeName = this.getEmployee().getDisplayNameWivTitlePrefixed();
		return employeeName;
	}


}
