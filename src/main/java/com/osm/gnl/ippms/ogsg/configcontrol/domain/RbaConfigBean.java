package com.osm.gnl.ippms.ogsg.configcontrol.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_rba_config")
@SequenceGenerator(name = "rbaConfigBeanSeq", sequenceName = "ippms_rba_config_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class RbaConfigBean extends AbstractControlEntity {

    /**
     *
     */
    private static final long serialVersionUID = -1530309754510413165L;

    @Id
    @GeneratedValue(generator = "rbaConfigBeanSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "rba_config_inst_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "rba_percentage", columnDefinition = "numeric(5,2) default '0.00'", nullable = false)
    private double rbaPercentage;

    @Column(name = "business_client_inst_id", nullable = false)
    private Long businessClientId;


    @Transient
    private String rbaPercentageStr;
    @Transient
    private String name;
    @Transient
    private String displayErrors;


    public String getRbaPercentageStr() {
        this.rbaPercentageStr = this.rbaPercentage + "%";
        return this.rbaPercentageStr;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    public boolean isNewEntity() {
        return this.id == null;
    }

}
