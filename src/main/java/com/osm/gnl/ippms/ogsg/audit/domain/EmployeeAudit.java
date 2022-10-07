package com.osm.gnl.ippms.ogsg.audit.domain;

import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_employee_audit")
@SequenceGenerator(name = "empTriggAuditSeq", sequenceName = "ippms_employee_audit_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class EmployeeAudit extends AbstractEmployeeAuditEntity {
	private static final long serialVersionUID = -6113370778116820774L;

	@Id
	@GeneratedValue(generator = "empTriggAuditSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "audit_inst_id", unique = true, nullable = false)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "employee_inst_id")
	private Employee employee;

	@Override
	public boolean isNewEntity() {

		return this.id == null;
	}


	@Override
	public int compareTo(Object o) {

		return 0;
	}

	@Override
	public AbstractEmployeeEntity getParentObject() {
		return this.employee;
	}

	@Override
	public boolean isPensionerEntity() {
		return false;
	}

}