/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.deduction;

import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "ippms_deduction_info")
@SequenceGenerator(name = "empDedInfoSeq", sequenceName = "ippms_deduction_info_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class EmpDeductionInfo extends AbstractDeductionEntity {

	private static final long serialVersionUID = 7271242807748610678L;

	@Id
	@GeneratedValue(generator = "empDedInfoSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "deduct_info_inst_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "employee_inst_id")
	private Employee employee;

	public EmpDeductionInfo(Long pId, String pDesc) {
		this.id = pId;
		this.description = pDesc;
	}

	public EmpDeductionInfo(Long pId) {
		this.id = pId;
	}


	@Override
	public boolean isNewEntity() {

		return this.id == null;
	}

	@Override
	public Long getParentId() {
		return this.employee.getId();
	}

	@Override
	public AbstractEmployeeEntity getParentObject() {
		return this.employee;
	}

	@Override
	public void setParentObject(AbstractEmployeeEntity abstractEmployeeEntity) {
		this.employee = (Employee)abstractEmployeeEntity;
	}


	@Override
	public void makeParentObject(Long pId) {
        this.employee = new Employee(pId);
	}
}