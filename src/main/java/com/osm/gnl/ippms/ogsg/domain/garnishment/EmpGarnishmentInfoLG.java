package com.osm.gnl.ippms.ogsg.domain.garnishment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_garnishment_info_lg")
@SequenceGenerator(name = "garnishSeqLG", sequenceName = "ippms_garnishment_info_lg_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class EmpGarnishmentInfoLG extends  AbstractGarnishmentEntity{
    @Id
    @GeneratedValue(generator = "garnishSeqLG", strategy = GenerationType.SEQUENCE)
    @Column(name = "garn_info_inst_id")
    private Long id;


    public EmpGarnishmentInfoLG(Long pId, String pDesc) {
        this.id = pId;
        super.setDescription(pDesc);
    }

    public EmpGarnishmentInfoLG(Long pId) {
        this.id = pId;
    }


    @Override
    public boolean isNewEntity() {

        return this.id == null;
    }

}
