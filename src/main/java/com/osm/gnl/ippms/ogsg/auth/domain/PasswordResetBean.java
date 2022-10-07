/*
 * Copyright (c) 2021.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.auth.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "ippms_password_reset_log")
@SequenceGenerator(name = "passwordResetSeq", sequenceName = "ippms_password_reset_log_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class PasswordResetBean {

    @Id
    @GeneratedValue(generator = "passwordResetSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "password_reset_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Email
    @Column(name="email", nullable = false)
    private String email;

    @Column(name="password", nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reset_by", nullable = false)
    protected User resetBy;

    @Column(name = "reset_date", nullable = false)
    protected Timestamp resetDate = Timestamp.from(Instant.now());

    @Column(name = "expiration_date", nullable = false)
    protected Timestamp expirationDate;

    public boolean isNewEntity(){
        return this.id == null;
    }
}
