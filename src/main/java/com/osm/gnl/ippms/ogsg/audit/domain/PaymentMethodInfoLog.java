package com.osm.gnl.ippms.ogsg.audit.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractTriggerAuditEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_payment_method_info_log")
@SequenceGenerator(name = "payMethodAuditSeq", sequenceName = "ippms_payment_method_info_log_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PaymentMethodInfoLog  extends AbstractTriggerAuditEntity {
	private static final long serialVersionUID = -4110302786575128302L;

	@Id
	@GeneratedValue(generator = "payMethodAuditSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "audit_inst_id", unique = true, nullable = false)
	private Long id;


	@ManyToOne
	@JoinColumn(name = "employee_inst_id")
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "pensioner_inst_id")
	private Pensioner pensioner;

	public PaymentMethodInfoLog(Long pId){
		this.id = pId;
	}


	public boolean isNewEntity() {

		return this.id == null;
	}



	public int compareTo(Object o) {
		 
		return 0;
	}

	 public boolean isPensioner(){
		return this.pensioner == null || pensioner.isNewEntity();
	 }
}
