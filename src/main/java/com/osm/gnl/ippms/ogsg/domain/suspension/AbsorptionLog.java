package com.osm.gnl.ippms.ogsg.domain.suspension;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractAuditEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.employee.Pensioner;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ippms_absorption_log")
@SequenceGenerator(name = "absorptionLogSeq", sequenceName = "ippms_absorption_log_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class AbsorptionLog extends AbstractAuditEntity {

	private static final long serialVersionUID = -4182810428016738582L;

	@Id
	@GeneratedValue(generator = "absorptionLogSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "absorption_inst_id")
	private Long id;
	@ManyToOne
	@JoinColumn(name = "employee_inst_id" )
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "pensioner_inst_id" )
	private Pensioner pensioner;

	@ManyToOne
	@JoinColumn(name = "absorption_reason_inst_id", nullable = false)
	private AbsorptionReason absorptionReason;

	@Column(name = "suspension_date", nullable = false, updatable = false)
    private LocalDate suspensionDate;

	@Column(name = "absorption_date", nullable = false, updatable = false)
	private LocalDate absorptionDate;
	@Transient
	private int absorptionCode;

	@Column(name = "pay_arrears_ind", columnDefinition = "numeric(1) default '0'")
	private int payArrearsInd;

	@Column(name = "arrears_start_date", updatable = false)
	private LocalDate arrearsStartDate;

	@Column(name = "arrears_end_date", updatable = false)
	private LocalDate arrearsEndDate;

	@Column(name = "reference_number", length = 32, nullable = false)
	private String referenceNumber;

	@Column(name = "audit_time", nullable = false, length = 12)
	private String auditTime;
	@Transient
	private double arrearsPercentage;
	@Transient
	private String arrearsPercentageStr;
	@Transient
	private boolean payArrears;



	public boolean isPayArrears() {
		if (getPayArrearsInd() >= 1)
			this.payArrears = true;
		return this.payArrears;
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