package com.osm.gnl.ippms.ogsg.abstractentities;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;



@MappedSuperclass
@Getter
@Setter
public abstract class AbstractTriggerAuditEntity implements Serializable, Comparable<Object> {


	@Column(name = "last_mod_ts", nullable = false)
	protected LocalDate lastModTs;

	@ManyToOne
	@JoinColumn(name = "user_inst_id", nullable = false)
	protected User user;

	@ManyToOne
	@JoinColumn(name = "mda_inst_id", nullable = false)
	protected MdaInfo mdaInfo;

	@ManyToOne
	@JoinColumn(name = "school_inst_id")
	protected SchoolInfo schoolInfo;

	@ManyToOne
	@JoinColumn(name = "salary_info_inst_id", nullable = false)
	protected SalaryInfo salaryInfo;

	@Column(name = "business_client_inst_id", nullable = false)
	private Long businessClientId;

	@Column(name = "pay_period", nullable = false, length = 6)
	protected String auditPayPeriod;

	@Column(name = "old_value", nullable = false, length = 300)
	protected String oldValue;

	@Column(name = "new_value", nullable = false, length = 300)
	protected String newValue;

	@Column(name = "column_changed", nullable = false, length = 30)
	protected String columnChanged;

	@Column(name = "audit_time", nullable = false, length = 30)
	protected String auditTimeStamp;

	@Column(name = "audit_action_type", nullable = false, length = 1)
	protected String auditActionType;

	@Transient
	private String placeHolder;
	@Transient
	private String name;
	@Transient
	protected String auditTime;
	@Transient
	private String mdaName;
	@Transient
	private String mdaCodeName;
	@Transient
	private Long mdaId;
	@Transient
	private Long schoolId;
	@Transient
	private String changedDate;
	@Transient
	private String changedBy;
	@Transient
	private String employeeId;
	@Transient
	private String promotedBy;
	@Transient
	protected final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy");

	public abstract int compareTo(Object pO);

	public abstract boolean isNewEntity();


	public String getChangedDate() {
		if (this.lastModTs != null) {
			this.changedDate = PayrollHRUtils.getDisplayDateFormat().format(this.lastModTs);
		}
		return this.changedDate;
	}

	public String getChangedBy() {
		if(this.changedBy != null)
			return this.changedBy;
		if ((this.user != null) && (!this.user.isNewEntity())) {
			this.changedBy = this.user.getActualUserName();
		}
		return this.changedBy;
	}


	public String getAuditTime() {
		this.auditTime = PayrollHRUtils.getDisplayDateFormat().format(this.lastModTs) + " "
				+ this.auditTimeStamp;
		return auditTime;
	}


}
