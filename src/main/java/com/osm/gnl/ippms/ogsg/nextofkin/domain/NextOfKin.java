/*
 * Copyright (c) 2020. 
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.nextofkin.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import com.osm.gnl.ippms.ogsg.control.entities.Title;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.location.domain.City;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_next_of_kin")
@SequenceGenerator(name = "nexOfKinSeq", sequenceName = "ippms_next_of_kin_seq", allocationSize = 1)
@Getter
@Setter
@NoArgsConstructor
public class NextOfKin extends AbstractControlEntity   {


	@Id
	@GeneratedValue(generator = "nexOfKinSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "next_of_kin_inst_id", nullable = false, unique = true)
	private Long id;


	@Column(name = "business_client_inst_id", nullable = false)
	private Long businessClientId;

	@ManyToOne
	@JoinColumn(name = "employee_inst_id")
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "pension_inst_id")
	private Pensioner pensioner;

	@ManyToOne
	@JoinColumn(name = "relationship_type_inst_id", nullable = false)
	private RelationshipType relationshipType;

	@Column(name = "first_name", length = 50, nullable = false)
	private String firstName;

	@Column(name = "last_name", length = 50, nullable = false)
	private String lastName;

	@Column(name = "middle_name", length = 25)
	private String middleName;
 
	@Column(name = "email", length = 40)
	private String emailAddress;

	@Column(name = "gsm_number_1", length = 13, nullable = false)
	private String gsmNumber;

	@Column(name = "gsm_number_2", length = 13, nullable = false)
	private String gsmNumber2;

	@Column(name = "address", length = 120, nullable = false)
	private String address;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "city_inst_id", nullable = false)
	private City city;

	@Transient
	private Title epmTitle;
	@Transient
	private Long stateId;
	@Transient
	private Long parentId;
	@Transient
	private AbstractEmployeeEntity abstractEmployeeEntity;
	@Transient
	private AbstractEmployeeEntity parentObject;



	public NextOfKin(Long pId) {
		this.id = pId;
	}

	public AbstractEmployeeEntity getParentObject() {
		if(abstractEmployeeEntity != null && !abstractEmployeeEntity.isNewEntity())
			parentObject = abstractEmployeeEntity;
		else {
			if (isPensioner())
				parentObject = this.pensioner;
			else
				parentObject = this.employee;
		}
		return parentObject;
	}
	public Long getParentId() {
		if(abstractEmployeeEntity != null && !abstractEmployeeEntity.isNewEntity())
			parentId = abstractEmployeeEntity.getId();
		else {
			if (isPensioner())
				parentId = pensioner.getId();
			else if (employee != null && !employee.isNewEntity())
				parentId = employee.getId();
		}

		return parentId;
	}

	public AbstractEmployeeEntity getAbstractEmployeeEntity() {
		return abstractEmployeeEntity;
	}

	public boolean isPensioner(){
        return this.pensioner != null && !this.pensioner.isNewEntity();
    }
	@Override
	public int compareTo(Object o) {
		return 0;
	}

	public boolean isNewEntity() {
		return this.id == null;
	}


}
