package com.osm.gnl.ippms.ogsg.domain.qualification;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import com.osm.gnl.ippms.ogsg.location.domain.State;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="ippms_edu_schools")
@SequenceGenerator(name = "eduSchoolSeq", sequenceName = "ippms_edu_schools_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class EducationSchool extends AbstractControlEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6541821895641386310L;

	@Id
	@GeneratedValue(generator = "eduSchoolSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "edu_school_inst_id")
	private Long id;

	@Column(name = "school_name", length = 100, nullable = false)
	private String name;
	 
	@ManyToOne
	@JoinColumn(name="school_type_inst_id")
	private EducationSchoolType educationSchoolType;
	
	@Column(name = "code_name", length = 10, nullable = false)
	private String codeName;
	
	@Column(name = "address", length = 200, nullable = true)
	private String address;
	
	@ManyToOne 
	@JoinColumn(name="state_inst_id")
	private State stateInfo;
	 
	
	@Column(name = "int_ind", columnDefinition="integer default '0'")
	private int localInd;
	
	@Transient
	private boolean foreignInstitution;


	public EducationSchool(Long pId, String pName, String pCodeName) {
		this.id = pId;
		this.name = pName;
		this.codeName = pCodeName;
	}
	

	public boolean isForeignInstitution() {
		
		 return this.localInd != 0;
	}


	public void setForeignInstitution(boolean foreignInstitution) {
		//this.foreignInstitution = foreignInstitution;
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
