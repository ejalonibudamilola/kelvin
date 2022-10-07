package com.osm.gnl.ippms.ogsg.leavebonus.domain;


import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "ippms_leave_bonus_err_master")
@SequenceGenerator(name = "leaveBonusErrMasterSeq", sequenceName = "ippms_leave_bonus_err_master_seq", allocationSize = 1)
@Getter
@Setter
@NoArgsConstructor
public class LeaveBonusErrorBean extends AbstractControlEntity {

    /**
     *
     */
    private static final long serialVersionUID = -1615375472647820421L;
    @Id
    @GeneratedValue(generator = "leaveBonusErrMasterSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "leave_bonus_err_master_inst_id")
    private Long id;

    @Column(name = "ltg_name", nullable = false, length = 80)
    private String ltgName;

    @Column(name = "description", nullable = false, length = 80)
    private String description;

    @Column(name = "mda_name", nullable = false, length = 100)
    private String mdaName;

    @Column(name = "ltg_year")
    private int ltgYearNum;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    @Transient
    private String name;

    public LeaveBonusErrorBean(Long pId) {
        this.id = pId;
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
