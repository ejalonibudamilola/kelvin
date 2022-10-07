package com.osm.gnl.ippms.ogsg.control.entities;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractDescControlEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "ippms_marital_status")
@SequenceGenerator(name = "maritalStatusSeq", sequenceName = "ippms_marital_status_seq", allocationSize = 1)
@Getter
@Setter
@NoArgsConstructor
public class MaritalStatus extends AbstractDescControlEntity {

    @Id
    @GeneratedValue(generator = "maritalStatusSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "marital_status_inst_id")
    private Long id;

    @Column(name = "marital_status", nullable = false, length = 25, unique = true)
    private String name;

    public MaritalStatus(Long pId) {
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