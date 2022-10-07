/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.auth.domain;

import com.osm.gnl.ippms.ogsg.abstractentities.AbstractNamedEntity;
import com.osm.gnl.ippms.ogsg.generic.domain.BusinessClient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ippms_role")
@SequenceGenerator(name = "roleSeq", sequenceName = "ippms_role_seq", allocationSize = 1)
@Getter
@Setter
@NoArgsConstructor
public class Role extends AbstractNamedEntity {

	@Id
	@GeneratedValue(generator = "roleSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "role_inst_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "business_client_inst_id", nullable = false)
	private BusinessClient businessClient;

	@Column(name="display_name", nullable=false, length = 100)
	private String displayName;

	@Column(name="admin_access_ind", columnDefinition = "integer default '0'")
	private int adminAccessInd;

	@Column(name="super_admin_access_ind", columnDefinition = "integer default '0'")
	private int superAdminInd;

	@Column(name="description", nullable=false)
	private String description;

	@Column(name="inherited_ind", columnDefinition = "integer default '0'")
	private int inheritedInd;

	@Transient
	private boolean adminUser;
	@Transient
	private boolean adminUserFlag;

	@Transient
	private boolean superAdmin;

	@Transient
	protected int lastEdited;
	public Role(Long id ) {
		this.id = id;
		 
	}

	public Role(Long id,String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Role other = (Role) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return name == other.name;
	}

	public boolean isSuperAdmin() {
		superAdmin = this.superAdminInd == 1;
		return superAdmin;
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
