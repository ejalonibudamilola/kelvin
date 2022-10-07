package com.osm.gnl.ippms.ogsg.domain.garnishment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "ippms_garnishment_info_pen")
@SequenceGenerator(name = "garnishSeqPen", sequenceName = "ippms_garnishment_info_pen_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class EmpGarnishmentInfoPensions extends  AbstractGarnishmentEntity{
    @Id
    @GeneratedValue(generator = "garnishSeqPen", strategy = GenerationType.SEQUENCE)
    @Column(name = "garn_info_inst_id")
    private Long id;


    public EmpGarnishmentInfoPensions(Long pId, String pDesc) {
        this.id = pId;
        super.setDescription(pDesc);
    }

    public EmpGarnishmentInfoPensions(Long pId) {
        this.id = pId;
    }


    @Override
    public boolean isNewEntity() {

        return this.id == null;
    }


}
