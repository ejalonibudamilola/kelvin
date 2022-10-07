package com.osm.gnl.ippms.ogsg.domain.subvention;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractDescControlEntity;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollHRUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ippms_subvention_info")
@SequenceGenerator(name = "subventionInfoSeq", sequenceName = "ippms_subvention_info_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class Subvention extends AbstractDescControlEntity {
	@Id
	@GeneratedValue(generator = "subventionInfoSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "subvention_inst_id", unique = true, nullable = false)
	private Long id;

	@Column(name = "expire", nullable = false, columnDefinition = "integer default '0'")
	private int expire;

	@Column(name = "amount", nullable = false, columnDefinition = "numeric(15,2)")
	private double amount;

	@Column(name = "expiration_date")
	private LocalDate expirationDate;


	@Column(name = "business_client_inst_id", nullable = false)
	private Long businessClientId;

	@Column(name = "employee_bound", nullable = false, columnDefinition = "integer default '0'")
	private int employeeBoundInd;

	@Column(name = "base_amount", nullable = false, columnDefinition = "numeric(5,2)")
	private double baseAmount;

    @Transient private String subventionAmountStr;
	@Transient private String baseAmountStr;
    @Transient private boolean employeeBound;
    @Transient private String amountStr;



	public Subvention(Long pId) {
		this.id = pId;
	}

	public boolean isEmployeeBound() {
		employeeBound = this.employeeBoundInd == 1;
		return employeeBound;
	}
	public boolean isExpired() {
		return this.expire == 1;
	}


	public String getExpirationDateStr() {
		if (this.expirationDate != null) {
			super.expirationDateStr = PayrollHRUtils.getDisplayDateFormat().format(this.expirationDate);
		}
		return super.expirationDateStr;
	}


	public String getCreatedDateStr() {
		if ( this.creationDate != null) {
			this.createdDateStr = PayrollHRUtils.getDisplayDateFormat().format(this.creationDate.toLocalDateTime().toLocalDate());
		}
		return this.createdDateStr;
	}


	public String getAmountAsStr() {
		return IConstants.naira + PayrollHRUtils.getDecimalFormat().format(this.amount);
	}

	public String getSubventionAmountStr() {
		subventionAmountStr = PayrollHRUtils.getDecimalFormat().format(this.amount);
		return subventionAmountStr;
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	public boolean isNewEntity() {
		return this.id == null;
	}


}