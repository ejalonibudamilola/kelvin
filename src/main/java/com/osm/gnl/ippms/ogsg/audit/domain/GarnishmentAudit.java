package com.osm.gnl.ippms.ogsg.audit.domain;

import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_garnishment_audit")
@SequenceGenerator(name = "garnishmentAuditSeq", sequenceName = "ippms_garnishment_audit_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class GarnishmentAudit extends AbstractGarnishmentAuditEntity {
	private static final long serialVersionUID = -4334998897876883419L;

	@Id
	@GeneratedValue(generator = "garnishmentAuditSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "audit_inst_id", unique = true, nullable = false)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "employee_inst_id")
	private Employee employee;


	public GarnishmentAudit(Long pId) {
		this.id = pId;
	}

	@Override
	public boolean isNewEntity() {
		 
		return this.id == null;
	}

	@Override
	public int compareTo(Object o) {
		 
		return 0;
	}
}