package com.osm.gnl.ippms.ogsg.control.entities;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractDescControlEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_rank")
@SequenceGenerator(name = "rankSeq", sequenceName = "ippms_rank_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class Rank extends AbstractDescControlEntity {


    @Id
    @GeneratedValue(generator = "rankSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "rank_inst_id")
    private Long id;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;

    @ManyToOne
    @JoinColumn(name="cadre_inst_id")
    private Cadre cadre;

    @Column(name = "from_level", columnDefinition = "numeric(2) default '0'")
    private int fromLevel;

    @Column(name = "to_level", columnDefinition = "numeric(2) default '0'")
    private int toLevel;

    @Column(name = "from_step", columnDefinition = "numeric(2) default '0'")
    private int fromStep;

    @Column(name = "to_step", columnDefinition = "numeric(2) default '0'")
    private int toStep;

    /*@Column(name = "default_ind", columnDefinition = "integer default '0'")
    private int defaultRankInd;*/


    @Transient
    private String levelRange;
    @Transient
    private String stepRange;
    @Transient
    private Long cadreInstId;

    public Rank(Long pId, String pRankName) {
        this.id = pId;
        this.name = pRankName;
    }

    public Rank(Long pId) {
        this.id = pId;

    }

    public String getLevelRange() {
        if(this.fromLevel > 0 && this.toLevel > 0)
            levelRange = "From "+this.fromLevel+" To "+this.toLevel;
        else
            levelRange = "Not Set";
        return levelRange;
    }

    public String getStepRange() {
        if(this.fromStep > 0 && this.toStep > 0)
            stepRange = "From "+this.fromStep+" To "+this.toStep;
        else
            stepRange = "Not Set";
        return stepRange;
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
