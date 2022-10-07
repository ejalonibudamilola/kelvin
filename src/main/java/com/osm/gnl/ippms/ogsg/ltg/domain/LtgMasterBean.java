/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.ltg.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "ippms_ltg_master")
@SequenceGenerator(name = "ltgMasterSeq", sequenceName = "ippms_ltg_master_seq", allocationSize = 1)
public class LtgMasterBean implements Comparable<LtgMasterBean>, Serializable {
	private static final long serialVersionUID = 6601742703398647337L;

	@Id
	@GeneratedValue(generator = "ltgDetailsSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "ltg_master_inst_id")
	private Long id;

	@Column(name = "ltg_name", nullable = false, length = 100)
	private String name;
	@Column(name = "simulated_month", columnDefinition = "numeric", nullable = false)
	private int simulationMonth;
	@Column(name = "simulated_year", columnDefinition = "numeric", nullable = false)
	private int simulationYear;
	@Column(name = "application_ind", columnDefinition = "numeric", nullable = false)
	private int applicationIndicator;
	@Column(name = "business_client_inst_id", nullable = false)
	private Long businessClientId;

	@Column(name = "last_mod_ts", nullable = false)
	private LocalDate lastModTs;

	@Column(name = "last_mod_by", nullable = false, length = 50)
	private String lastModBy;

	@Transient
	private boolean forPayrollRun;
	@Transient
	private Long businessClientInstId;
	@Transient
	private int noOfMdasAffected;
	@Transient
	private String simulationStatus;
	@Transient
	private String createdDateStr;
	@Transient
	private String createdBy;
	@Transient
	private int lastApprovalMonth;
	@Transient
	private int lastApprovalYear;
	@Transient
	private String simulationMonthStr;
	@Transient
	private String details;
	@Transient
	private String delete;

	public LtgMasterBean() {
	}

	public LtgMasterBean(Long pId) {
		this.id = pId;
	}

	public boolean isForPayrollRun() {
		if (getApplicationIndicator() == 1)
			this.forPayrollRun = true;
		return this.forPayrollRun;
	}

	public String getSimulationStatus() {
		if (isForPayrollRun()) {
			if (getSimulationYear() < getLastApprovalYear())
				this.simulationStatus = "APPLIED";
			else if (getSimulationYear() == getLastApprovalYear()) {
				if (getSimulationMonth() <= getLastApprovalMonth())
					this.simulationStatus = "APPLIED";
				else
					this.simulationStatus = "UNAPPLIED";
			} else {
				this.simulationStatus = "UNAPPLIED";
			}
		} else {
			this.simulationStatus = "SIMULATED";
		}
		return this.simulationStatus;
	}

	public String getDetails() {
		if (!getSimulationStatus().equalsIgnoreCase("APPLIED"))
			this.details = "Details...";
		return this.details;
	}

	public String getDelete() {
		if (!getSimulationStatus().equalsIgnoreCase("APPLIED")) {
			this.delete = "Delete";
		}
		return this.delete;
	}

	@Override
	public int compareTo(LtgMasterBean o) {

		return 0;
	}

	public boolean isNewEntity() {
		return id == null;
	}
}