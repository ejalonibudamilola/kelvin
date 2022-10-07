package com.osm.gnl.ippms.ogsg.menu.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Helper class for working with {@link MenuLink}, 
 * {@link MenuLinkCategory}
 *
 */
@NoArgsConstructor
@Getter
@Setter
public class MenuLinksWrapper {
	
	private List<MenuLinkCategory> menuLinkCategories;
	private List<MenuLink> firstTabs;
	private MenuLink menuLink;


}
