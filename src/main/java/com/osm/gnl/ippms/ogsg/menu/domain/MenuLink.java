package com.osm.gnl.ippms.ogsg.menu.domain;

import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


/**
 * A category for grouping of related {@link MenuLink MenuLinks}.
 *
 * @author Femi Adegbesan
 * @Aythor Mustola
 * Adapted for IPPMS Spring Boot
 */
@Entity
@Table(name = "ippms_menu_link")
@SequenceGenerator(name = "menuLinkSeq", sequenceName = "ippms_menu_link_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class MenuLink extends AbstractEntity {

	@Id
	@GeneratedValue(generator = "menuLinkSeq", strategy = GenerationType.SEQUENCE)
	@Column(name = "menu_link_inst_id")
	private Long id;

	@Column(name = "menu_link_name", nullable = false, length = 100, unique = true)
	private String name;

	@Column(name = "menu_link_desc")
	private String description;

	/**
	 * The URL for the menu. Not that this can be empty if it is to be used only as
	 * a parent link and not necessarily leading anywhere.
	 *
	 * The URL should always start with a forward slash (/) and should not include
	 * the context path of the URL i.e for
	 * 'http://localhost:8080/oysg_gov_ippms/doSomethingURL.do' our link URL will then
	 * be '/doSomethingURL.do'.
	 */
	@Column(name = "menu_link_url")
	private String linkUrl;

	@ManyToOne
	@JoinColumn(name = "parent_menu_link_inst_id")
	private MenuLink parentMenuLink;

	/**
	 * The menu link category of for this link.
	 *
	 * Note that if a link has a parent, then it automatically inherits the parents
	 * menu link category
	 */
	@ManyToOne
	@JoinColumn(name = "menu_link_cat_inst_id")
	private MenuLinkCategory menuLinkCategory;

	/**
	 * Should a link be displayed on the dashboard? This is for the home page only.
	 */
	@Column(name = "display_on_db")
	private int displayOnDb;

	@Transient
	private boolean displayOnDashboard;

	@Transient
	private boolean displayOnDashboardBind;

	@Column(name = "is_db_link")
	private int dashboardMenuLinkInd;

	@Transient
	private boolean dashboardMenuLink;

	@Column(name = "is_sys_link")
	private int systemUser;

	@Transient
	private boolean sysUser;

	// This designates whether the link is to be displayed under configuration of
	// user profile
	@Column(name = "is_inner_link")
	private int innerLinkInd;

	@Column(name = "privileged_ind" , columnDefinition = "integer default '0'")
	private int privilegedInd;



	// if a link is a dashboard link then it may have menu categories under it.
	@OneToMany
	@JoinTable(name = "ippms_menu_link_db_cats", joinColumns = @JoinColumn(name = "menu_link_inst_id", nullable = false),
			inverseJoinColumns = @JoinColumn(name = "menu_link_cat_inst_id", nullable = false), uniqueConstraints = @UniqueConstraint(columnNames = {
			"menu_link_cat_inst_id", "menu_link_inst_id" }))
	private Set<MenuLinkCategory> tabMenuCategories = new HashSet<MenuLinkCategory>();

	// Non-persistent fields
	@Transient private boolean innerLink;
	@Transient private String oldName;
	@Transient private int numOfChildren;
	@Transient private boolean directAccess;
	@Transient private boolean innerLinkBind;
	@Transient private boolean systemUserBind;
	@Transient private boolean caspBind;
	@Transient private boolean subebBind;
	@Transient private boolean lgBind;
	@Transient private boolean statePensionBind;
	@Transient private boolean lgPensionBind;
	@Transient private boolean executiveBind;



	public MenuLink(Long id) {
		this.id = id;
	}


	/**
	 * @return {@code true} if this link has a URL
	 */
	public boolean isHasLinkUrl() {
		return IppmsUtils.isNotNullOrEmpty(this.linkUrl);
	}

	/**
	 * @return {@code true} if the link can be displayed on the dashboard.
	 */
	public boolean isDashboardDisplayable() {
		return this.isHasLinkUrl() && this.isDisplayOnDashboard();
	}

	public boolean isHasParentLink() {
		return this.parentMenuLink != null && IppmsUtils.isNotNullAndGreaterThanZero(this.parentMenuLink.getId());
	}

	@Override
	public boolean isNewEntity() {
		return this.id == null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}

	public boolean isDisplayOnDashboard() {
		return this.displayOnDb == 1;
	}

	public boolean isDashboardMenuLink() {
		return this.dashboardMenuLinkInd == 1;
	}

	public boolean isSysUser() {
		return this.systemUser == 1;
	}

	public boolean isInnerLink() {
		return this.innerLinkInd == 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MenuLink other = (MenuLink) obj;
		if (this.name == null) {
			return other.name == null;
		} else return this.name.equals(other.name);
	}

}
