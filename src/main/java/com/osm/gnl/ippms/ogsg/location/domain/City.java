package com.osm.gnl.ippms.ogsg.location.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractNamedEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "ippms_city")
@Data
@NoArgsConstructor
@SequenceGenerator(name = "citySeq", sequenceName = "ippms_city_seq", allocationSize = 1)
public class City extends AbstractNamedEntity {
	private static final long serialVersionUID = 2737996390555994160L;

	@Id
	@GeneratedValue(generator = "citySeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "city_inst_id", nullable = false)
	private Long id;

	@Column(name="full_name", nullable=false, unique=true)
	private String fullName;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "state_inst_id")
	private State state;

	@Column(name = "default_ind", columnDefinition = "integer default '0'")
	private int defaultInd;

	@Transient
	private boolean defaultCity;

	@Transient
	private Long stateId;


	public City(Long pId, String pName, String pFullName) {
		this.id = pId;
		this.name = pName;
		this.fullName = pFullName;

	}

    public City(Long cityId, Long stateId) {
       this.id = cityId;
       this.state = new State(stateId);

    }
	public City(Long cityId, String cityName) {
		this.id = cityId;
		this.name = cityName;

	}
    public boolean isDefaultCity() {
		return this.defaultInd == 1;
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
