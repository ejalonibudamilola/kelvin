package com.osm.gnl.ippms.ogsg.auth.repository;


import com.osm.gnl.ippms.ogsg.auth.domain.PasswordResetBean;
import com.osm.gnl.ippms.ogsg.auth.domain.User;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessCertificate;

import java.io.Serializable;
import java.util.List;


public interface IUserRepository extends Serializable {

	
	User findByUsername(String pUserName);

	List<User> loadActiveLoginUsingFilters(BusinessCertificate businessCertificate);

	List<User> loadLockedAccount(BusinessCertificate businessCertificate);

	PasswordResetBean loadPasswordResetBean(Long id) throws IllegalAccessException, InstantiationException;
}
