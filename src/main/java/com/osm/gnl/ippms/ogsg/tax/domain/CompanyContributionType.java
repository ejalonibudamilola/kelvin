package com.osm.gnl.ippms.ogsg.tax.domain;


import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "ippms_coy_cont_type")
@SequenceGenerator(name = "coyContTypeSeq", sequenceName = "coy_cont_type_seq", allocationSize = 1)
public class CompanyContributionType extends AbstractControlEntity {

	@Id
	@GeneratedValue(generator = "coyContTypeSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "coy_cont_type_inst_id")
	private Long id;

	@Column(name = "coy_cont_type_name", length = 100, nullable = false)
	private String name;
	@ManyToOne
	@JoinColumn(name = "coy_cont_cat_inst_id")
	private CompanyContributionCategory companyContributionCategory;

	@Column(name = "sub_type_enable", length = 1, nullable = false)
	private String subTypeEnable;

	@Column(name = "ded_type_enable", length = 1, nullable = false)
	private String dedTypeEnable;

	@Column(name = "inheritable", length = 1, nullable = false)
	private String inheritable;
	@Transient
	private boolean inherit;
	@Transient
	private boolean deductionType;

	public CompanyContributionType() {
	}

	public boolean isDeductionType() {
		if ((this.dedTypeEnable != null) && (this.dedTypeEnable.equalsIgnoreCase("y"))) {
			this.deductionType = true;
		}
		return this.deductionType;
	}

	public boolean isInherit() {
		if ((getInheritable() != null) && (getInheritable().equalsIgnoreCase("y")))
			this.inherit = true;
		return this.inherit;
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