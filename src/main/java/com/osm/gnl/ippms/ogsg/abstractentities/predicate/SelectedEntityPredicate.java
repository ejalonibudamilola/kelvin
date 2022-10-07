package com.osm.gnl.ippms.ogsg.abstractentities.predicate;

import com.osm.gnl.ippms.ogsg.menu.domain.AbstractEntity;
import org.apache.commons.collections4.Predicate;


public class SelectedEntityPredicate implements Predicate {

	@Override
	public boolean evaluate(Object obj) {
		if (obj instanceof AbstractEntity) {
			return ((AbstractEntity) obj).isSelected();
		}
		else if (obj instanceof BaseEntity) {
			return ((BaseEntity) obj).isSelected();
		}
		
		return false;
	}

}
