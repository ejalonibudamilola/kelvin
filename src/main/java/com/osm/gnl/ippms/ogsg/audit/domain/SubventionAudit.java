package com.osm.gnl.ippms.ogsg.audit.domain;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.domain.subvention.Subvention;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "ippms_subvention_log")
@SequenceGenerator(name = "subventionAuditSeq", sequenceName = "ippms_subvention_log_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class SubventionAudit implements Comparable<SubventionAudit>{

	@Id
	@GeneratedValue(generator = "subventionAuditSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "subvention_log_inst_id", unique = true, nullable = false)
	private Long id;


	@Column(name = "last_mod_ts", nullable = false)
	private LocalDate lastModTs;

	@Column(name = "last_mod_by", nullable = false, length = 50)
	private String lastModBy;

	@Column(name = "pay_period", nullable = false, length = 6)
	private String auditPayPeriod;

	@Column(name = "audit_time", nullable = false, length = 12)
	protected String auditTime;

	@Column(name = "audit_action_type", columnDefinition = "character(1) DEFAULT 'I'")
	private String auditActionType;

	@ManyToOne
	@JoinColumn(name = "subvention_inst_id", nullable = false)
	private Subvention subvention;

	@Column(name = "business_client_inst_id", nullable = false)
	private Long businessClientId;


	@ManyToOne
	@JoinColumn(name = "user_inst_id")
	private User user;

	@Column(name = "old_value", nullable = false, length = 30)
	private String oldValue;
	@Column(name = "new_value", nullable = false, length = 30)
	private String newValue;
	@Column(name = "column_changed", nullable = false, length = 30)
	private String columnChanged;
	@Transient
	private String name;

	@Transient
	private String changedBy;

	@Transient
	private String changedDate;

	@Transient
	DateTimeFormatter sdf = DateTimeFormatter.ofPattern("MMM dd, yyyy");

	public String getAuditTime() {
		if (getLastModTs() != null)
				this.auditTime = (sdf.format(getLastModTs()));

		return this.auditTime;
	}


	public SubventionAudit(Long pId) {
		this.id = pId;
	}


	public String getName() {
		if ((getSubvention() != null) && (!getSubvention().isNewEntity()))
			this.name = getSubvention().getName();
		return this.name;
	}


	public String getChangedBy() {
		if ((getUser() != null) && (!getUser().isNewEntity())) {
			this.changedBy = (getUser().getFirstName() + " " + getUser().getLastName());
		}
		return this.changedBy;
	}

	public void setChangedBy(String pChangedBy) {
		this.changedBy = pChangedBy;
	}

	public String getChangedDate() {
		if (getLastModTs() != null)
			this.changedDate = PayrollHRUtils.getDisplayDateFormat().format(getLastModTs());
		return this.changedDate;
	}

	public  boolean isNewEntity() {
		return this.id == null;
	}

	@Override
	public int compareTo(SubventionAudit o) {
	 
		return 0;
	}
	
}