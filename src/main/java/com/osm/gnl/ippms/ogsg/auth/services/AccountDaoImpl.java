/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.auth.services;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.auth.repository.IAccountDao;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class AccountDaoImpl implements IAccountDao {

    protected SessionFactory sessionFactory;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public AccountDaoImpl(SessionFactory sessionFactory,PasswordEncoder passwordEncoder){

        this.sessionFactory = sessionFactory;
        this.passwordEncoder = passwordEncoder;

    }

    @Override
    public String getUserRoleDisplayName(Long userId) {
        String query = "SELECT r.displayName FROM Role r WHERE r.id = (SELECT u.role.id FROM User u WHERE u.id = :userId)";

        return (String) this.sessionFactory.getCurrentSession().createQuery(query).
                setParameter("userId", userId).uniqueResult();
    }
}
