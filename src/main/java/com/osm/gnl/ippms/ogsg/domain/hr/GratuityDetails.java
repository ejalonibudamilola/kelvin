package com.osm.gnl.ippms.ogsg.domain.hr;


import com.osm.gnl.ippms.ogsg.abstractentities.AbstractNamedEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_gratuity_details")
@SequenceGenerator(name = "gratuityDetailsSeq", sequenceName = "ippms_gratuity_details_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class GratuityDetails extends AbstractNamedEntity {
	@Id
	@GeneratedValue(generator = "gratuityDetailsSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "gratuity_details_inst_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "gratuity_master_inst_id", nullable = false)
	private GratuityMasterBean gratuityMasterBean;

	@Column(name = "application_month", nullable = false)
	private int applyMonth;
	@Column(name = "application_year", nullable = false)
	private int applyYear;

	@Column(name = "application_percentage", columnDefinition = "numeric(15,2) default '0.00'")
	private double applicationPercentage;
	

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public boolean isNewEntity() {
		return this.id == null;
	}
}
