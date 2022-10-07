package com.osm.gnl.ippms.ogsg.domain.garnishment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_garnishment_info_subeb")
@SequenceGenerator(name = "garnishSeqSUBEB", sequenceName = "ippms_garnishment_info_subeb_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class EmpGarnishmentInfoSubeb extends  AbstractGarnishmentEntity{
    @Id
    @GeneratedValue(generator = "garnishSeqSUBEB", strategy = GenerationType.SEQUENCE)
    @Column(name = "garn_info_inst_id")
    private Long id;


    public EmpGarnishmentInfoSubeb(Long pId, String pDesc) {
        this.id = pId;
        super.setDescription(pDesc);
    }

    public EmpGarnishmentInfoSubeb(Long pId) {
        this.id = pId;
    }


    @Override
    public boolean isNewEntity() {

        return this.id == null;
    }

}
