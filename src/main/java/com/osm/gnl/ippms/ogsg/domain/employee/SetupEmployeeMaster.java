/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.employee;

import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;


@Entity
@Table(name = "ippms_setup_emp_master")
@SequenceGenerator(name = "setupEmpMasterSeq", sequenceName = "ippms_setup_emp_master_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class SetupEmployeeMaster implements Serializable, Comparable<SetupEmployeeMaster>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3944774180625378055L;

	@Id
	@GeneratedValue(generator = "setupEmpMasterSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "setup_emp_inst_id")
	private Long id;

	@Column(name = "first_name", length = 25, nullable = false)
	private String firstName;

	@Column(name = "last_name", length = 25, nullable = false)
	private String lastName;

	@Column(name = "initials", length = 25)
	private String initials;
	@Column(name = "employee_id", length = 20, nullable = false)
	private String employeeId;

	@Column(name = "business_client_inst_id", nullable = false)
	private Long businessClientId;

	@Column(name = "approved_date")
	private LocalDate approvedDate;
	@Column(name = "approved_by", length = 32)
	private String approvedBy;
	@Column(name = "rejected_status", columnDefinition = "integer default '0'")
	private int rejectedStatus;
	/**
	 * We might need to remove this column. Not sure if it is not duplicated by
	 * function
	 */
	@Column(name = "rejected_ind", columnDefinition = "integer default '0'")
	private int rejectedInd;
	@Column(name = "approved_ind", columnDefinition = "integer default '0'")
	private int approvedInd;
	@Column(name = "deleted_by", length = 32, nullable = true)
	private String deletedBy;


	@Column(name = "last_mod_ts", nullable = false)
	private LocalDate lastModTs;

	@Column(name = "last_mod_by", nullable = false, length = 32)
	private String lastModBy;
	@Column(name = "pay_group", nullable = false, length = 120)
	private String payGroup;
	@Column(name = "pay_period", nullable = false, length = 6)
	private String payPeriod;

	@Column(name = "mda_name", nullable = false, length = 120)
	private String mdaName;
	@Column(name = "school_name", length = 120)
	private String schoolName;

	@Transient
	private boolean rejected;
	@Transient
	private boolean approved;
	@Transient
	private boolean schoolExists;
	@Transient
	private String approvedDateStr;
	@Transient
	private String initiatedDateStr;
	@Transient
	private boolean rejectedOrApproved;
	@Transient
	private String finalStatus;

	@Transient
	private String generatedCaptcha;

	@Transient
	private String enteredCaptcha;


	public SetupEmployeeMaster(Long pSetupEmpMasterId) {
		this.id = pSetupEmpMasterId;
	}


	public boolean isRejected() {
		return this.rejectedStatus == 1;
	}

	public boolean isApproved() {
		return this.approvedInd == 1;
	}


	public String getApprovedDateStr() {
		if (this.approvedDate != null) {
			this.approvedDateStr = PayrollHRUtils.getDisplayDateFormat().format(this.approvedDate);
		}
		return approvedDateStr;
	}

	 public String getInitiatedDateStr() {
		if (this.lastModTs != null)
			this.initiatedDateStr = PayrollHRUtils.getDisplayDateFormat().format(this.lastModTs);

		return this.initiatedDateStr;
	}
   public boolean isRejectedOrApproved() {
		return this.approvedDate != null;
	}


	public String getFinalStatus() {
		if (this.isRejected()) {
			finalStatus = "Rejected";
		} else if (this.isApproved()) {
			finalStatus = "Approved";
		}
		return finalStatus;
	}


	public boolean isNewEntity() {
		return this.id == null;
	}


	@Override
	public int compareTo(SetupEmployeeMaster setupEmployeeMaster) {
		return 0;
	}
}
