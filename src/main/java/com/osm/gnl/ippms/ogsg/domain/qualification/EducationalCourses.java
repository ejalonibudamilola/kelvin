package com.osm.gnl.ippms.ogsg.domain.qualification;

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
@Table(name="ippms_educational_courses")
@SequenceGenerator(name = "eduCourseSeq", sequenceName = "ippms_educational_courses_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class EducationalCourses extends AbstractControlEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2398825653709423538L;

	@Id
	@GeneratedValue(generator = "eduCourseSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "course_inst_id")
	private Long id;

	@Column(name = "course_name", length = 50, nullable = false)
	private String name;
	 
	@ManyToOne() 
	@JoinColumn(name="school_type_inst_id")
	private EducationSchoolType educationSchoolType;
	
	 
	@Transient
	private int schoolTypeInd;


	public EducationalCourses(Long pId,String pName)
	{
		this.id = pId;
		this.name = pName;
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
