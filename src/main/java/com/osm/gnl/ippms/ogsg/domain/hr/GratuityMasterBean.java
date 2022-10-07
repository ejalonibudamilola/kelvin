package com.osm.gnl.ippms.ogsg.domain.hr;


import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import com.osm.gnl.ippms.ogsg.report.beans.NamedEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ippms_gratuity_master")
@SequenceGenerator(name = "gratuityMasterSeq", sequenceName = "ippms_gratuity_master_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class GratuityMasterBean extends AbstractControlEntity {

	@Id
	@GeneratedValue(generator = "gratuityMasterSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "gratuity_master_inst_id")
	private Long id;

	@Column(name = "run_month", nullable = false)
	private int runMonth;
	@Column(name = "run_year", nullable = false)
	private int runYear;
	@Column(name = "application_ind", nullable = false)
	private int applicationIndicator;

	@Transient
	private List<NamedEntity> gratuityList;
	@Transient
	private int noOfEmployees;
	@Transient
	private String codeName;


	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public boolean isNewEntity() {
		return false;
	}
}
