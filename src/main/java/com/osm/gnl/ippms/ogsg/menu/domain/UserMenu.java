package com.osm.gnl.ippms.ogsg.menu.domain;

import com.osm.gnl.ippms.ogsg.auth.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * The {@link MenuLink MenuLinks} that a {@link com.osm.gnl.ippms.ogsg.auth.domain.User}
 * has access to.
 *
 * @Author Mustola
 * Adapted for IPPMS Spring Boot
 * Added Lombok support
 *
 */
@Entity
@Table(name = "ippms_user_menu")
@SequenceGenerator(name="userMenuSeq", sequenceName="ippms_user_menu_seq", allocationSize=1)
@NoArgsConstructor
@Getter
@Setter
public class UserMenu extends AbstractEntity {
	
	@Id
    @GeneratedValue(generator="userMenuSeq", strategy=GenerationType.SEQUENCE)
    @Column(name="user_menu_inst_id", nullable = false, unique = true)
	private Long id;
	
	@OneToOne
	@JoinColumn(name = "user_inst_id", nullable = false)
	private User user;
	
	@OneToMany
	@JoinTable(name = "ippms_user_menu_links",
		joinColumns = {@JoinColumn(name = "user_menu_inst_id", nullable = false)},
		inverseJoinColumns = {@JoinColumn(name = "menu_link_inst_id", nullable = false)}
	)
	private Set<MenuLink> menuLinks = new HashSet<>();
	
	public UserMenu(Long id) { 
		this.id = id;
	}

	@Override
	public boolean isNewEntity() {
		return this.id == null;
	}
 
}
