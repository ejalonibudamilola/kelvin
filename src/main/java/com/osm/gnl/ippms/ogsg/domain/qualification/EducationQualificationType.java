package com.osm.gnl.ippms.ogsg.domain.qualification;

import java.util.List;

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

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name="ippms_edu_qual_type")
@SequenceGenerator(name = "eduQualTypeSeq", sequenceName = "ippms_edu_qual_type_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class EducationQualificationType extends AbstractControlEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -874041288304788632L;
	
	@Id
	@GeneratedValue(generator = "eduQualTypeSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "edu_qual_type_inst_id")
	private Long id;

	@Column(name = "qual_type_name", length = 100, nullable = false)
	private String name;
	 
	@ManyToOne
	@JoinColumn(name="school_type_inst_id")
	private EducationSchoolType educationSchoolType;
	
	@Column(name = "code_name", length = 10, nullable = false)
	private String codeName;
	
	@Transient
	private  List<EducationQualificationType> qualificationTypeList;

	public EducationQualificationType(Long pId, String pName, String pCodeName) {
		this.id = pId;
		this.name = pName;
		this.codeName = pCodeName;
	}



	public List<EducationQualificationType> getQualificationTypeList() {
		return qualificationTypeList;
	}

	public void setQualificationTypeList(List<EducationQualificationType> qualificationTypeList) {
		this.qualificationTypeList = qualificationTypeList;
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
