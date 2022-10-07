package com.osm.gnl.ippms.ogsg.tax.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="ippms_coy_cont_cat")
@SequenceGenerator(name = "coyContCatSeq", sequenceName="coy_cont_cat_seq", allocationSize = 1)
public class CompanyContributionCategory extends AbstractControlEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7204019823324359113L;

	@Id
    @GeneratedValue(generator = "coyContCatSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "coy_cont_cat_inst_id")
    private Long id;
      
	@Column(name="contribution_name", length = 100, nullable = false)
	private String name;
	
	public CompanyContributionCategory() {}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public boolean isNewEntity() {
	 
		return this.id == null;
	}
  
}