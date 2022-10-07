/*
 * Copyright (c) 2022.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.domain.employee;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ippms_biometric_error_info")
@SequenceGenerator(name = "bioErrSeq", sequenceName = "ippms_biometric_error_info_seq", allocationSize = 1)
@Setter
@Getter
@ToString
@NoArgsConstructor
public class BiometricInfoError extends AbstractControlEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "bioErrSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "biometric_err_inst_id")
    private Long id;

    @Column(name = "employee_inst_id")
    protected Long employeeInstId;

    @Column(name = "employee_id", length = 25, nullable = false)
    protected String employeeId;

    @Column(name = "first_name", length = 25, nullable = false)
    protected String firstName;

    @Column(name = "last_name", length = 25, nullable = false)
    protected String lastName;

    @Column(name = "business_client_inst_id")
    protected Long businessClientId;


    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }
}
