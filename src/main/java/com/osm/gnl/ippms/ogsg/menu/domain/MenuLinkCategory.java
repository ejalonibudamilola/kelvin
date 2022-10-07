package com.osm.gnl.ippms.ogsg.menu.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Objects;

/**
 * A category for grouping of related {@link MenuLink MenuLinks}.
 *
 * @author Femi Adegbesan
 * @Author Mustola
 * Adapted for Spring Boot
 * Added Lombok support
 */
@Entity
@Table(name = "ippms_menu_link_cat")
@SequenceGenerator(name = "menuLinkCatSeq", sequenceName = "ippms_menu_link_cat_seq", allocationSize = 1)
@NoArgsConstructor
@Getter
@Setter
public class MenuLinkCategory extends AbstractEntity {

    @Id
    @GeneratedValue(generator = "menuLinkCatSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "menu_link_cat_inst_id")
    private Long id;

    @Column(name = "menu_link_cat_name", nullable = false, length = 100)
    private String name;

    @Column(name = "menu_link_cat_desc", length = 255)
    private String description;

    /**
     * Menu Categories of this kind are used only with menu links that are dashboards
     * @see MenuLink#isDashboardMenuLink()
     */
    @Column(name = "disp_only_on_db_tabs")
    private int displayOnlyOnDbTabs;

    //Non-Persistent fields
    @Transient private String oldName;

    @Transient
    private boolean displayOnlyOnDbTabsBind;


    public MenuLinkCategory(Long id) {
        this.id = id;
    }

    public MenuLinkCategory(Long id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.description = desc;
    }


    @Override
    public boolean isNewEntity() {
        return this.id == null;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

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
        final MenuLinkCategory other = (MenuLinkCategory) obj;
        return Objects.equals(this.name, other.name);
    }


}
