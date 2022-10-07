package com.osm.gnl.ippms.ogsg.abstractentities;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.organization.model.MdaInfo;
import com.osm.gnl.ippms.ogsg.organization.model.SchoolInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractLogEntity implements Comparable<Object>, Serializable {

	@ManyToOne
	@JoinColumn(name = "user_inst_id", nullable = false)
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "employee_inst_id")
	private Employee employee;
	
	@ManyToOne
	@JoinColumn(name = "mda_inst_id", nullable = false)
	private MdaInfo mdaInfo;

	@ManyToOne
	@JoinColumn(name = "pensioner_inst_id")
	private Pensioner pensioner;

	@ManyToOne
	@JoinColumn(name = "school_inst_id")
	private SchoolInfo schoolInfo;

	@ManyToOne
	@JoinColumn(name = "salary_info_inst_id")
	private SalaryInfo salaryInfo;
	
	@Column(name = "pay_period", nullable = false, length = 6)
	private String auditPayPeriod;
	
	@Column(name = "audit_time", nullable = false, length = 30)
	private String auditTime;
	
	@Transient protected String auditTimeStamp;

	@Transient protected String employeeId;

	@Transient protected AbstractEmployeeEntity abstractEmployeeEntity;

	public String getEmployeeId() {
		if(this.employeeId != null)
			return employeeId;
		return IppmsUtils.treatNull(this.getAbstractEmployeeEntity().getEmployeeId());
	}
    public AbstractEmployeeEntity getParentObject(){
		if (this.employee != null && !this.employee.isNewEntity())
			abstractEmployeeEntity = this.employee;
		else if (this.pensioner != null && !this.pensioner.isNewEntity())
			abstractEmployeeEntity = this.pensioner;

		return abstractEmployeeEntity;

	}


	public abstract boolean isNewEntity();

	public boolean isPensioner(){
		return this.pensioner != null && !this.pensioner.isNewEntity();
	}

}
