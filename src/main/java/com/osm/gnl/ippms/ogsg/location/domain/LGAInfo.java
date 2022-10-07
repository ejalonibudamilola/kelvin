package com.osm.gnl.ippms.ogsg.location.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "ippms_lga_info")
@SequenceGenerator(name = "lgaSeq", sequenceName = "ippms_lga_info_seq",  allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class LGAInfo extends AbstractControlEntity  {
	private static final long serialVersionUID = 2737996390555994160L;

	@Id
	@GeneratedValue(generator = "lgaSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "lga_inst_id")
	private Long id;

	@Column(name = "lga_name", nullable = false, length = 50, unique = true)
	private String name;

	@Column(name = "record_code", nullable = false, length = 20, unique = true)
	private String recordCode;

	@Column(name = "default_ind", columnDefinition = "integer default '0'")
	private int defaultInd;

	@ManyToOne
	@JoinColumn(name = "state_inst_id")
	private State state;

	@Transient private boolean defaultLgaInfo;
    @Transient private Long stateId;

	public LGAInfo(Long pLgaId) {
		this.id = pLgaId;
	}

	public boolean isDefaultLgaInfo() {
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