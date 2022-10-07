/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.payment;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import com.osm.gnl.ippms.ogsg.report.beans.EmployeeMiniBean;
import com.osm.gnl.ippms.ogsg.report.beans.PaymentReportBean;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Entity
@Table(name = "ippms_bank_branches")
@SequenceGenerator(name = "branchSeq", sequenceName = "ippms_bank_branches_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class BankBranch extends AbstractControlEntity implements Serializable {

	@Id
	@GeneratedValue(generator = "branchSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "branch_inst_id", nullable = false, unique = true)
	private Long id;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "bank_inst_id")
	private BankInfo bankInfo;
	@Column(name = "branch_name", length = 100, nullable = false)
	private String name;
	@Column(name = "branch_id", length = 20, nullable = false)
	private String branchSortCode;

	@Column(name = "data_status", columnDefinition = "integer default '0'", nullable = false)
	private int dataStatus;

	@Column(name = "default_ind", columnDefinition = "integer default '0'", nullable = false)
	private int defaultInd;

	@Transient private boolean active;
	@Transient private Long bankId;
	@Transient private List<EmployeeMiniBean> employeeMiniBeanList;
	@Transient private double totalNetPay;
	@Transient private Long fromBranchInstId;
	@Transient private Long toBranchInstId;
	@Transient private boolean canDelete;
	@Transient private HashMap<Long, PaymentReportBean> empDeductMiniBeanMap;
	@Transient private boolean editMode;
	@Transient private boolean defaultObject;
	@Transient private boolean bankStatusDesignate;
	@Transient private Long designationId;


	public BankBranch(Long pId) {
		setId(pId);
	}

	public BankBranch(Long pBranchId, String pBranchName) {
		this.id = pBranchId;
		this.name = pBranchName;
	}

	public boolean isActive() {
		return this.dataStatus == 0;
	}
	public List<EmployeeMiniBean> getEmployeeMiniBeanList() {
		if (this.employeeMiniBeanList == null)
			this.employeeMiniBeanList = new ArrayList<>();
		return this.employeeMiniBeanList;
	}

	public boolean isDefaultObject() {
		return this.defaultInd == 1;
	}

	public HashMap<Long, PaymentReportBean> getEmpDeductMiniBeanMap() {
		if (empDeductMiniBeanMap == null)
			empDeductMiniBeanMap = new HashMap<>();
		return empDeductMiniBeanMap;
	}

	@Override public int compareTo(Object o) { return 0; }

	public boolean isNewEntity() {
		return this.id == null;
	}



}