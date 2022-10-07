/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.simulation;

import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollBeanUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;


@Entity
@Table(name = "ippms_future_sim_master")
@SequenceGenerator(name = "futureSimMasterSeq", sequenceName = "ippms_future_sim_master_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class FuturePaycheckMaster {

	@Id
	@GeneratedValue(generator = "futureSimMasterSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "future_sim_inst_id")
	private Long id;

	@Column(name = "future_sim_name", length = 50, nullable = false)
	private String name;
	@Column(name = "simulation_month")
	private int simulationMonth;
	@Column(name = "simulation_year")
	private int simulationYear;
	@Column(name = "business_client_inst_id", nullable = false)
	private Long businessClientId;

	@Column(name = "last_mod_ts", nullable = false)
	private LocalDate lastModTs;

	@Column(name = "last_mod_by", nullable = false, length = 50)
	private String lastModBy;
	@Transient
	private LocalDate compareDate;
	@Transient
	private String simulationMonthStr;
	@Transient
	private String createdDateStr;
	@Transient
	private String delete;
	@Transient
	private boolean confirmDeletion;
	@Transient
	private boolean canEdit;
	
	@Transient
	private LocalDate createdDate;

	public FuturePaycheckMaster(Long parentId) {
		this.id = parentId;
	}

	public String getSimulationMonthStr() {
		if (this.simulationYear > 0)
			this.simulationMonthStr = PayrollBeanUtils.createPayPeriodFromInt(this.simulationMonth,
					this.simulationYear);
		return this.simulationMonthStr;
	}

	public String getCreatedDateStr() {
		if (this.lastModTs != null)
			this.createdDateStr = PayrollHRUtils.getDisplayDateFormat().format(this.lastModTs);
		return this.createdDateStr;
	}

	public boolean isNewEntity(){
		return this.id == null;
	}

	public String getDelete() {
		return "Delete...";
	}

}