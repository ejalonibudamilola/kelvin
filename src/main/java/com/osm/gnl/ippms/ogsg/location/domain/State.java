package com.osm.gnl.ippms.ogsg.location.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "ippms_states")
@SequenceGenerator(name = "stateSeq", sequenceName = "ippms_states_seq", allocationSize = 1)
@Data
@NoArgsConstructor
@ToString
public class State extends AbstractControlEntity {
    @Id
    @Column(name = "state_inst_id")
    private Long id;

    @Column(name="state_name", nullable=false, unique=true)
    private String name;

    @Column(name="full_name", nullable=false, unique=true)
    private String fullName;

    @Column(name = "default_ind", columnDefinition = "integer default '0'")
    private int defaultInd;

    @Transient
    private boolean defaultState;



    public State(Long id, String fullName, String pCodeName) {
        this.id = id;
        this.fullName = fullName;
        this.name = pCodeName;
    }

    public State(Long stateId) {
       this.id = stateId;
    }

    public boolean isDefaultState() {
        return this.defaultInd == 1;
    }

    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

}
