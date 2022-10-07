package com.osm.gnl.ippms.ogsg.tax.domain;


import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import com.osm.gnl.ippms.ogsg.domain.payment.PayTypes;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "ippms_coy_cont_info")
@SequenceGenerator(name = "coyContInfoSeq", sequenceName = "coy_cont_info_seq", allocationSize = 1)
public class CompanyContributionInfo extends AbstractControlEntity {
	private static final long serialVersionUID = 3177262365579959819L;

	@Id
	@GeneratedValue(generator = "coyContInfoSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "coy_cont_info_inst_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "coy_cont_type_inst_id")
	private CompanyContributionType companyContributionType;

	@ManyToOne
	@JoinColumn(name = "coy_cont_subtype_inst_id")
	private CompanyContributionSubtype companyContributionSubtype;

	@ManyToOne
	@JoinColumn(name = "pay_types_inst_id")
	private PayTypes payTypes;

	@Column(name = "business_client_inst_id")
	private Long businessClientId;
	@Column(name = "contribution_amount", columnDefinition = "numeric(10,2) default '0.00'")
	private double contributionAmount;
	@Column(name = "annual_max", columnDefinition = "numeric(10,2) default '0.00'")
	private double annualMax;

	@Transient
	private long compContCatRef;
	@Transient
	private long compContCatRef1;
	@Transient
	private long compContPayTypeRef;
	@Transient
	private long compContSubtypeRef;
	@Transient
	private long compContTypeRef;
	@Transient
	private long compContTypeRef1;
	@Transient
	private boolean editMode;
	@Transient
	private String action;
	@Transient
	private double currentContribution;

	public boolean getEditMode() {
        this.editMode = (getAction() != null) && (getAction().equalsIgnoreCase("e"));
		return this.editMode;
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public boolean isNewEntity() {

		return this.id == null;
	}
}