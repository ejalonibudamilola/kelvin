package com.osm.gnl.ippms.ogsg.organization.model;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractDescControlEntity;
import com.osm.gnl.ippms.ogsg.location.domain.State;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ippms_schools")
@SequenceGenerator(name = "schoolSeq", sequenceName = "ippms_schools_seq", allocationSize = 1)
@NoArgsConstructor
@Setter
@Getter
public class SchoolInfo extends AbstractDescControlEntity {

	private static final long serialVersionUID = 1812217405502658897L;

	@Id
	@GeneratedValue(generator = "schoolSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "school_inst_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "mda_inst_id")
	private MdaInfo mdaInfo;

	@Column(name = "code_name", length = 20)
	private String codeName;

	@Column(name = "object_ind", columnDefinition = "numeric(1) default '0'")
	private int objectInd;

	@Column(name = "business_client_inst_id")
	private Long businessClientId;

	/**
	 * SUBEB Needs this.
	 */
	@Column(name = "rural_ind", columnDefinition = "numeric(1) default '0'")
	private int ruralInd;

	@Transient private List<?> objectList;
	@Transient private String parentObjectType;
	@Transient private Long businessClientInstId;
	@Transient private boolean rural;
	@Transient private String ruralStr;

	public SchoolInfo(Long pId) {
		this.id = pId;
	}

	public SchoolInfo(Long pId, String pName) {
		this.setId(pId);
		this.setName(pName);

	}

	public SchoolInfo(Long pId, String pName, String pCodeName) {
		this.setId(pId);
		this.setName(pName);
		if (pCodeName != null)
			this.setCodeName(pCodeName);
	}


	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public boolean isNewEntity() {
		return this.id == null;
	}

	public boolean isRural(){
		return this.ruralInd == 1;
	}
	public String getRuralStr(){

			if(this.isRural())
				this.ruralStr = "Rural";
			else
				this.ruralStr = "Non-Rural";
			return ruralStr;

	}


}