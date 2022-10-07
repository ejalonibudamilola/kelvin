package com.osm.gnl.ippms.ogsg.tax.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "ippms_coy_cont_subtype")
@SequenceGenerator(name = "coyContSubTypeSeq", sequenceName = "coy_cont_subtype_seq", allocationSize = 1)
public class CompanyContributionSubtype {
	@Id
	@GeneratedValue(generator = "coyContCatSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "coy_cont_subtype_inst_id")
	private Long id;
	@ManyToOne
	@JoinColumn(name = "coy_cont_type_inst_id")
	private CompanyContributionType companyContributionType;
	@Column(name = "provider", length = 40, nullable = true)
	private String provider;
	@Column(name = "subtype_name", length = 100, nullable = false)
	private String name;
	
	@Column(name = "business_client_inst_id", nullable = false)
	private Long businessClientId;

	public CompanyContributionSubtype() {
	}
	public boolean isNewEntity() {
		return this.id == null;
	}
}