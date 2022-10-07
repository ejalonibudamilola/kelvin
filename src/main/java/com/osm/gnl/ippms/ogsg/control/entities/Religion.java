package com.osm.gnl.ippms.ogsg.control.entities;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractDescControlEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "ippms_religion")
@SequenceGenerator(name = "religionSeq", sequenceName = "ippms_religion_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class Religion extends AbstractDescControlEntity
{
	@Id
	  @GeneratedValue(generator = "religionSeq", strategy = GenerationType.SEQUENCE)
	  @Column(name = "religion_inst_id")
	  private Long id;

	 @Column(name = "religion_code", length = 1, nullable = false)
	 private String codeName;


  
  public Religion(Long pRelId) {
	 this.setId(pRelId);
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