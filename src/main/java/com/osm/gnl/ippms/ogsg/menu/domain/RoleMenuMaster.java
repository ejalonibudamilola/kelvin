package com.osm.gnl.ippms.ogsg.menu.domain;

import com.osm.gnl.ippms.ogsg.auth.domain.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ippms_role_menu_master")
@SequenceGenerator(name = "roleMenuMasterSeq", sequenceName = "ippms_role_menu_master_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class RoleMenuMaster extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "roleMenuMasterSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "role_menu_master_inst_id")
    private Long id;

    /**
     * The role. Every role can have only one entry in this master table.
     */
    @ManyToOne()
    @JoinColumn(name = "role_inst_id", nullable = false, updatable = false, unique = true)
    private Role role;

    @Column(name = "role_menu_master_desc", nullable = false)
    private String description;

    @OneToMany
    @JoinTable(name = "ippms_role_menu_master_links", joinColumns = @JoinColumn(name = "role_menu_master_inst_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "menu_link_inst_id", nullable = false))
    private Set<MenuLink> roleMenuLinks = new HashSet<MenuLink>();


    public RoleMenuMaster(Long id) {
        this.id = id;
    }


    public boolean isNewEntity() {
        return this.id == null;
    }

}
