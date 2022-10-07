package com.osm.gnl.ippms.ogsg.domain.qualification;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="ippms_edu_school_type")
@SequenceGenerator(name = "eduSchoolTypeSeq", sequenceName = "ippms_edu_school_type_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class EducationSchoolType extends AbstractControlEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1115185064476727445L;
	
	@Id
	@GeneratedValue(generator = "eduSchoolTypeSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "school_type_inst_id")
	private Long id;

	@Column(name = "school_type_name", length = 50, nullable = false)
	private String name;
	 
	@Column(name = "code_name", length = 10, nullable = false)
	private String codeName;
	
	@Column(name = "description", length = 50, nullable = false)
	private String description;
	
	@Column(name = "higher_institution_ind", columnDefinition="integer" ,nullable=false )
	private int higherInstitutionInd;
	
	@Transient
	private List<EducationSchoolType> educationSchoolTypeList;
	@Transient
	private String classification;

	/*
	 * public EducationSchoolType(Long pId, String pName) { this.id = pId; this.name
	 * = pName; }
	 */

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public boolean isNewEntity() {
		 
		return this.id == null;
	}



	public List<EducationSchoolType> getEducationSchoolTypeList() {
		return educationSchoolTypeList;
	}

	public void setEducationSchoolTypeList(List<EducationSchoolType> educationSchoolTypeList) {
		this.educationSchoolTypeList = educationSchoolTypeList;
	}


	public boolean isHasRecords() {
		return this.getEducationSchoolTypeList() != null && !this.getEducationSchoolTypeList().isEmpty();
	}

	public String getClassification() {
		switch(this.higherInstitutionInd) {
		case 0:
			this.classification = "Primary";
			break;
		case 1:
			this.classification = "Secondary";
			break;
		case 2:
			this.classification = "Tertiary";
			break;
		}
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

}
