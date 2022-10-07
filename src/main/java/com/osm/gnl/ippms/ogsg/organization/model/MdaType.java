package com.osm.gnl.ippms.ogsg.organization.model;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractNamedEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "ippms_mda_type")
@SequenceGenerator(name = "mdaTypeSeq", sequenceName = "ippms_mda_type_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class MdaType extends AbstractNamedEntity {
	
	
	@Id
    @GeneratedValue(generator = "mdaTypeSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "mda_type_inst_id")
    private Long id;

    @Column(name = "mda_type_code", nullable = false, length = 1, unique = true)
    private int mdaTypeCode;
    
    @Column(name = "display_name", nullable = false, length = 50, unique = true)
    private String statusDisplayName;

	@Column(name = "business_client_inst_id")
	private Long businessClientId;

    
    public MdaType(Long id, String pName) {
    	this.id = id;
    	this.name = pName;
    }
    

	public MdaType(Long l) {
		 this.id = l;
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
