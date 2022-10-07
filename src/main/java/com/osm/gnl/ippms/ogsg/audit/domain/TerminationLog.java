package com.osm.gnl.ippms.ogsg.audit.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractAuditEntity;
import com.osm.gnl.ippms.ogsg.control.entities.TerminateReason;
import com.osm.gnl.ippms.ogsg.domain.employee.AbstractEmployeeEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;


@Entity
@Table(name = "ippms_termination_audit")
@SequenceGenerator(name = "termLogSeq", sequenceName = "ippms_termination_audit_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class TerminationLog extends AbstractAuditEntity {

	 
	private static final long serialVersionUID = -6270462681074129727L;
	
	@Id
	  @GeneratedValue(generator = "termLogSeq", strategy = GenerationType.SEQUENCE)
	  @Column(name = "audit_inst_id")
	  private Long id;
	
	 
	@ManyToOne
	@JoinColumn(name = "term_reason_inst_id")
	private TerminateReason terminateReason;
	

	@Column(name = "termination_date", nullable = false, updatable = false)
	private LocalDate terminationDate;
	
	@Column(name = "audit_time", nullable = false, length = 12)
	 private String auditTime;

	@ManyToOne
	@JoinColumn(name = "employee_inst_id")
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "pensioner_inst_id")
	private Pensioner pensioner;


	@Transient
	private int terminatedInd;

	@Transient
	private boolean aPensioner;

	@Transient
	private AbstractEmployeeEntity parentObject;
	

	public boolean isaPensioner() {
		if(this.pensioner != null && !this.pensioner.isNewEntity())
			aPensioner =  true;

		return aPensioner;
	}

	public AbstractEmployeeEntity getParentObject() {
		if(this.isaPensioner())
			parentObject =  this.pensioner;
		else
			parentObject = this.employee;
		return parentObject;
	}

	public TerminationLog(Long pId) { this.id = pId;}
	

	@Override
	public boolean isNewEntity() {
		 
		return this.id == null;
	}


	public String getAuditTime() {
	    if(this.getLastModTs() != null )
	    	 this.auditTime = (PayrollHRUtils.getFullDateFormat().format(getLastModTs()) + " " + this.auditTime);
		return null;
	}


	@Override
	public int compareTo(Object o) {
		return 0;
	}
}
