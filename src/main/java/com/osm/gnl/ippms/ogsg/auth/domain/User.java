/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.auth.domain;
/**
 * This software is confidential and exclusive property of GNL Systems Ltd.
 * All rights reserved (c) 2020
 */

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractControlEntity;
import com.osm.gnl.ippms.ogsg.constants.IConstants;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import com.osm.gnl.ippms.ogsg.payroll.utils.PayrollUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
 
@Entity
@Table(name = "ippms_user")
@SequenceGenerator(name = "userSeq", sequenceName = "ippms_user_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class User extends AbstractControlEntity implements UserDetails {

	private static final long serialVersionUID = -7224704252046168740L;

	@Id
	@GeneratedValue(generator = "userSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "user_id")
	private Long id;

	@Column(name="username", nullable = false, unique = true) 
	private String username;

	@Email
	@Column(name="email", nullable = false, unique = true) 
	private String email;
 
	@Column(name="password", nullable = false)
	private String password;
	
	@Column(name="account_expired" ,columnDefinition = "integer default '0'")
 	private int accountExpired;
	 
	@Column(name="account_enabled",columnDefinition = "integer default '0'")
	private int accountEnabled;
	 
	@Column(name="account_locked",columnDefinition = "integer default '0'")
	private int accountLocked;

	@Column(name="deactivatedInd", columnDefinition = "integer default '0'")
	private int deactivatedInd;
	 
	@Column(name="password_expired",columnDefinition = "integer default '0'")
	private int passwordExpired;

	@Column(name="change_password_ind" , columnDefinition = "integer default '0'")
	private int changePasswordInd;
	
	@Column(name="password_monitor_date")
	private LocalDate passwordMonitorDate;

	@Column(name="last_login_date")
	private Timestamp lastLoginDate;

	@Column(name="first_name", nullable = false)
    private String firstName;

	@Column(name="last_name", nullable = false)
	private String lastName;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;

	@Column(name="system_user_ind", columnDefinition = "integer default '0'")
	private int systemUserInd;

	@Column(name="privileged_ind", columnDefinition = "integer default '0'")
	private int privilegedInd;

	@Column(name="allow_weekend_login_ind", columnDefinition = "integer default '0'")
	private int allowWeekendLoginInd;

	@Column(name="reset_id")
	private Long passwordResetId;

	@Transient private int attemptCounter;
	@Transient private String displayErrors;
	@Transient private String newPassword;
	@Transient private String confirmNewPassword;
	@Transient private boolean chgPassword;
	@Transient private String showRow;
	@Transient private String oldPassword;
	@Transient private boolean deactivated;
	@Transient private boolean locked;
	@Transient private boolean notLocked;
	@Transient private String lockedStr;
	@Transient public static final int LOCK_ACCOUNT = 1;
	@Transient public static final int UNLOCK_ACCOUNT = 0;
	@Transient private String oldRoleName;
	@Transient private String actualUserName;
    @Transient private boolean privilegedUser;
	@Transient private boolean editMode;
    @Transient private String lastLoginDateAsString;
	@Transient private boolean logoutMessageShown;
	@Transient private boolean firstTimeLogin;
	@Transient public static final int ENABLE_ACCOUNT = 1;
	@Transient public static final int DISABLE_ACCOUNT = 0;
	@Transient private Collection<GrantedAuthority> authorities = new HashSet<>();
	@Transient private boolean gnlSystemsStaff;
	@Transient private boolean sysUser;
	@Transient private boolean allowWeekendLogin;
    @Transient private String userName;

    public User(Long pId){
    	this.id = pId;
	}

	public User(String username,String email,String password, Role role ) {
		this.username = username;
		this.userName = username;
		this.email = email;
		this.password = password;
		this.role =  role;
	}
	public User(Long pLoginId, String pUserName, String pFirstName, String pLastName) {
 		this.id = pLoginId;
 		this.userName = pUserName;
 		this.username = pUserName;
		this.firstName = pFirstName;
		this.lastName = pLastName;
	}

	public User(Long pLoginId, String pUserName) {
		this.id = pLoginId;
		this.userName = pUserName;
		this.username = pUserName;
	}

	public User(Long pId, String pFirstName, String pLastName) {
		this.id = pId;
		this.firstName = pFirstName;
		this.lastName = pLastName;
	}
	public User(String pFirstName, String pLastName) {

		this.firstName = pFirstName;
		this.lastName = pLastName;
	}

	public String getUserName(){
    	return this.username;
	}

	public String getActualUserName() {
		this.actualUserName = this.firstName +" "+this.lastName;
		return actualUserName;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (this.id != other.id)
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;

		if (username == null) {
			return other.username == null;
		} else return username.equals(other.username);
	}

	public boolean isPrivilegedUser() {
    	privilegedUser = this.privilegedInd == 1;
		return privilegedUser;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if(this.role != null) {
			GrantedAuthority g =new SimpleGrantedAuthority(role.getName());
			this.authorities = new ArrayList<>();
			this.authorities.add(g);
		} 
	    return this.authorities;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	public boolean isSysUser() {

		return this.systemUserInd == 1;
	}
    public boolean isAllowWeekendLogin(){
    	return this.allowWeekendLoginInd == 1;
	}
	@Override
	public boolean isAccountNonExpired() {

		return this.accountExpired == 0;
	}

	@Override
	public boolean isAccountNonLocked() {

		return this.accountLocked == 0;
	}

	@Override
	public boolean isCredentialsNonExpired() {

		return this.passwordExpired == 0;
	}

	@Override
	public boolean isEnabled() {

		return this.accountEnabled == 0;
	}

	public boolean isDeactivated() {
		return this.deactivatedInd == 1;
	}

	public boolean isChgPassword() {
    	chgPassword = this.changePasswordInd == 1;
		return chgPassword;
	}

	@Override
	public int compareTo(Object o) {
		 
		return 0;
	}

	@Override
	public boolean isNewEntity() {
		return this.id == null;
	}
	public String getLastLoginDateAsString(){
    	lastLoginDateAsString = IConstants.EMPTY_STR;
    	if(this.lastLoginDate != null)
    		lastLoginDateAsString = PayrollUtils.formatTimeStamp(this.lastLoginDate);
		return lastLoginDateAsString;
	}
}
