package com.osm.gnl.ippms.ogsg.domain.suspension;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractDescControlEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "ippms_absorption_reason")
@SequenceGenerator(name = "absorptionReasonSeq", sequenceName = "ippms_absorption_reason_seq",allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class AbsorptionReason extends AbstractDescControlEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8833360673912941127L;

	@Id
	@GeneratedValue(generator = "absorptionReasonSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "absorption_reason_inst_id", nullable = false)
	private Long id;

	public AbsorptionReason(Long pId, String pName) {
		this.id = pId;
		this.name = pName;
	}
	@Column(name = "business_client_inst_id", nullable = false)
	private Long businessClientId;

	public AbsorptionReason(Long pAbsorptionReasonId) {
		this.id = pAbsorptionReasonId;
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
