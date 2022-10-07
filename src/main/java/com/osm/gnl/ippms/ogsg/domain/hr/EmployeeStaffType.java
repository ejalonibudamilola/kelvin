/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.hr;


import com.osm.gnl.ippms.ogsg.abstractentities.AbstractDescControlEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_employee_staff_type")
@SequenceGenerator(name = "empStaffTypeSeq", sequenceName = "ippms_employee_staff_type_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class EmployeeStaffType extends AbstractDescControlEntity
{

	@Id
	@GeneratedValue(generator = "empStaffTypeSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "emp_staff_type_inst_id")
	private Long id;

	@Column(name = "business_client_inst_id")
	private Long businessClientId;

	@Column(name = "staff_type_ind")
	private int staffTypeInd;

    public EmployeeStaffType(Long staffTypeId) {
        this.id = staffTypeId;
    }


    @Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public boolean isNewEntity() {
		return this.id == null;
	}
}
