/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.employee;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ippms_setup_emp_details")
@SequenceGenerator(name = "setupEmpDetailsSeq", sequenceName = "ippms_setup_emp_details_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class SetupEmployeeDetails implements Serializable,Comparable<SetupEmployeeDetails> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2719233884443025491L;

	@Id
	@GeneratedValue(generator = "setupEmpDetailsSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "setup_error_inst_id")
	private Long id;
	@Column(name = "gms_number", length = 12, nullable = true)
	private String gsmNumber;
	@Column(name = "first_name", length = 25, nullable = false)
	private String firstName;

	@Column(name = "last_name", length = 25, nullable = false)
	private String lastName;

	@Column(name = "initials", length = 25, nullable = true)
	private String initials;
	
	@Column(name = "employee_id", length = 20, nullable = false)
	private String employeeId;

	@Column(name = "business_client_name", length = 50, nullable = false)
	private String businessClientName;

	@Column(name = "business_client_inst_id", nullable = false)
	private Long businessClientId;

	@ManyToOne
	@JoinColumn(name = "setup_emp_inst_id")
	private SetupEmployeeMaster setupEmployeeMaster;

	@ManyToOne
	@JoinColumn(name = "employee_inst_id")
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "pensioner_inst_id")
	private Pensioner pensioner;

	@Transient
	private String mdaName;
	@Transient
	private String schoolName;
	@Transient
	private String payGroup;
	@Transient
	private AbstractEmployeeEntity parentObject;
	@Transient
	private Long parentId;


	public SetupEmployeeDetails(Long pId) {
		this.id = pId;
	}

	public AbstractEmployeeEntity getParentObject() {
		if(parentObject == null){
			if (isPensionerType()){
				parentObject = pensioner;
			}else{
				parentObject = employee;
			}
		}
		return parentObject;
	}

	public Long getParentId() {
		if(parentId == null)
			parentId = this.getParentObject().getId();
		return parentId;
	}
    private boolean isPensionerType(){
        return this.pensioner != null && !this.pensioner.isNewEntity();
	}
	public boolean isNewEntity() {
		return this.id == null;
	}


	@Override
	public int compareTo(SetupEmployeeDetails setupEmployeeDetails) {
		return 0;
	}
}
