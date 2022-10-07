/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.approval;

import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.organization.model.MdaDeptMap;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ippms_transfer_approval")
@SequenceGenerator(name = "transferApprovalSeq", sequenceName = "ippms_transfer_approval_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class TransferApproval extends AbstractApprovalEntity implements Comparable<TransferApproval> {

	@Id
	@GeneratedValue(generator = "transferApprovalSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "transfer_approval_inst_id", nullable =  false, unique = true)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "employee_inst_id")
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "pensioner_inst_id")
	private Pensioner pensioner;

	@ManyToOne
	@JoinColumn(name = "mda_dept_map_inst_id", nullable = false)
	private MdaDeptMap mdaDeptMap;

	@ManyToOne
	@JoinColumn(name = "school_inst_id")
	private SchoolInfo schoolInfo;

	@Column(name = "audit_time", length = 20, nullable = false)
	private String auditTime;

	@Column(name = "transfer_date" , nullable = false)
	private LocalDate transferDate;


	@Column(name = "deletion_date")
	private LocalDate deleteDate;

	@Column(name = "rejection_reason", length = 100)
	private String rejectionReason;



	//--Denotes whether it is a School transfer or MDA transfer
	@Column(name = "object_ind", columnDefinition = "integer default '0'", nullable = false)
	private int objectInd;

	@Transient protected String rowSelected;
	@Transient private String initiatedDateStr;
	@Transient private String deletionDateStr;
	@Transient private String rejectionDateStr;
	@Transient private String oldMda;
	@Transient private String newMda;
	@Transient private boolean rejection;
	@Transient private boolean approval;
	@Transient private int overrideInd;
	@Transient private boolean showOverride;
	@Transient private Long parentId;
	@Transient private AbstractEmployeeEntity parentObject;
	@Transient private String transferDateStr;


	public String getInitiatedDateStr() {
		if (this.initiatedDate != null)
			initiatedDateStr = PayrollHRUtils.getDisplayDateFormat().format(this.initiatedDate);
		return initiatedDateStr;
	}


	public String getTransferDateStr() {
		if (this.transferDate != null)
			transferDateStr = PayrollHRUtils.getDisplayDateFormat().format(this.transferDate);
		return transferDateStr;
	}

	@Override
	public int compareTo(TransferApproval pIncoming) {
		return 0;
	}


	public String getRejectionDateStr() {
		if (this.getApprovedDate() != null)
			rejectionDateStr = PayrollBeanUtils.getJavaDateAsString(this.getApprovedDate());
		return rejectionDateStr;

	}
 public String getDeletionDateStr() {
		if (this.deleteDate != null)
			deletionDateStr = PayrollBeanUtils.getJavaDateAsString(this.deleteDate);
		return deletionDateStr;
	}

	public boolean isNewEntity() {

		return this.id == null;
	}

	public Long getParentId() {
		if(this.isPensioner())
			parentId = this.pensioner.getId();
		else
			parentId = this.employee.getId();
		return parentId;
	}

	public AbstractEmployeeEntity getParentObject() {
		if(this.parentObject != null)
			return parentObject;
		else
			if(this.isPensioner())
				parentObject = this.pensioner;
			else
				parentObject = this.employee;
		return parentObject;
	}

	public boolean isPensioner() {
        return this.pensioner != null && !this.pensioner.isNewEntity();
    }
}
