package com.osm.gnl.ippms.ogsg.domain.report;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import com.osm.gnl.ippms.ogsg.domain.employee.Employee;
import com.osm.gnl.ippms.ogsg.domain.hr.HiringInfo;
import com.osm.gnl.ippms.ogsg.domain.paygroup.SalaryInfo;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ippms_contract_history")
@SequenceGenerator(name = "contractSeq", sequenceName = "ippms_contract_history_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class ContractHistory extends AbstractControlEntity {

	@Id
	@GeneratedValue(generator = "contractSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "contract_inst_id")
	private Long id;

	@Column(name = "contract_name", length = 20, nullable = false)
	private String name;

	@ManyToOne
	@JoinColumn(name = "employee_inst_id", nullable = false)
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "salary_info_inst_id", nullable = false)
	private SalaryInfo salaryInfo;

	@Column(name = "business_client_inst_id", nullable = false)
	private Long businessClientId;

	@Column(name = "contract_start_date", nullable = false)
	private LocalDate contractStartDate;
	@Column(name = "contract_end_date", nullable = false)
	private LocalDate contractEndDate;

	@Column(name = "contract_start_year", columnDefinition="numeric(4,0)")
	private int contractStartYear;
	@Column(name = "contract_start_month", columnDefinition="numeric(2,0)")
	private int contractStartMonth;
	@Column(name = "contract_start_day", columnDefinition="numeric(2,0)")
	private int contractStartDay;
	@Column(name = "contract_end_year", columnDefinition="numeric(4,0)")
	private int contractEndYear;
	@Column(name = "contract_end_month", columnDefinition="numeric(2,0)")
	private int contractEndMonth;
	@Column(name = "contract_end_day", columnDefinition="numeric(2,0)")
	private int contractEndDay;
	@Column(name = "expired_ind")
	private int expiredInd;
	@Column(name = "expired_date")
	private LocalDate expiredDate;
	@Column(name = "reference_date", nullable = false)
	private LocalDate referenceDate;
	@Column(name = "locked_ind")
	private int lockedInd;
	@Column(name = "reference_number", length = 45, nullable = false)
	private String referenceNumber;

	@Transient private LocalDate contractExtEndDate;
	@Transient private String contractStartDateStr;
	@Transient private String contractEndDateStr;
	@Transient private String referenceDateStr;
	@Transient private String expirationDateStr;
	@Transient private boolean expired;
	@Transient private HiringInfo hiringInfo;
	@Transient private String contractType;
	@Transient private String employeeName;
	@Transient private boolean terminationWarning;
	@Transient private boolean locked;
	@Transient private String contractLength;
	@Transient private String ogNumber;
    @Transient private Long mdaMapId;
    @Transient private Long mdaInfoId;
    @Transient private String mdaName;
    @Transient private LocalDate terminationDate;
    @Transient private String mode;
    @Transient private String auditTimeStamp;
    @Transient private String changedBy;
    @Transient private String expiredBy;


	public String getContractStartDateStr() {
		if (getContractStartDate() != null)
			this.contractStartDateStr = PayrollHRUtils.getDateFormat().format(getContractStartDate());
		return this.contractStartDateStr;
	}

	public String getContractEndDateStr() {
		if (getContractEndDate() != null)
			this.contractEndDateStr = PayrollHRUtils.getDateFormat().format(getContractEndDate());
		return this.contractEndDateStr;
	}

	public boolean isExpired() {
        this.expired = getExpiredInd() == 1;
		return this.expired;
	}


	public boolean isTerminationWarning() {
		return this.terminationWarning;
	}

	public void setTerminationWarning(boolean pTerminationWarning) {
		this.terminationWarning = pTerminationWarning;
	}

	public String getReferenceDateStr() {
		if (getReferenceDate() != null)
			this.referenceDateStr = PayrollHRUtils.getDateFormat().format(getReferenceDate());
		return this.referenceDateStr;
	}

	public String getEmployeeName() {
		if(this.employee != null && !this.employee.isNewEntity())
			employeeName = this.employee.getDisplayNameWivTitlePrefixed();
		return employeeName;
	}

	public boolean isLocked() {
		this.locked = (getLockedInd() == 1);
		return this.locked;
	}

	public String getExpirationDateStr() {
		if (getExpiredDate() != null)
			this.expirationDateStr = PayrollHRUtils.getDateFormat().format(this.getExpiredDate());
		return expirationDateStr;
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	public boolean isNewEntity() {
		return this.id == null;
	}

}