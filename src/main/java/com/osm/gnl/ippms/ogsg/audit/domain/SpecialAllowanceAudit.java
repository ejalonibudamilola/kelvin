package com.osm.gnl.ippms.ogsg.audit.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractTriggerAuditEntity;
import com.osm.gnl.ippms.ogsg.domain.allowance.AbstractSpecialAllowanceEntity;
import com.osm.gnl.ippms.ogsg.domain.allowance.SpecialAllowanceInfo;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_special_allow_audit")
@SequenceGenerator(name = "specAllowAuditSeq", sequenceName = "ippms_special_allow_audit_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class SpecialAllowanceAudit extends AbstractSpecAllowAuditEntity {
	private static final long serialVersionUID = -8931512780572385476L;

	@Id
	@GeneratedValue(generator = "specAllowAuditSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "special_allow_audit_inst_id", unique = true, nullable = false)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "special_allow_info_inst_id", nullable = false)
	private SpecialAllowanceInfo specialAllowanceInfo;

	@ManyToOne
	@JoinColumn(name = "employee_inst_id")
	private Employee employee;

	@Transient
	private String allowanceType;

	public SpecialAllowanceAudit(Long id) {

		this.id = id;
	}


	@Override
	public int compareTo(Object o) {

		return 0;
	}

	@Override
	public boolean isNewEntity() {

		return this.id == null;
	}

    public boolean isPensioner(){
		return false;
	}
}