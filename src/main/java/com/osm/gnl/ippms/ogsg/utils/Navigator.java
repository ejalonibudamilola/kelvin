package com.osm.gnl.ippms.ogsg.utils;

import com.osm.gnl.ippms.ogsg.constants.IConstants;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;


@Getter
@Setter
public class Navigator {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Class<?> fromClass;
    private Class<?> toClass;
    private String fromForm;
    private boolean returnToMe;
    private String toFrom;
    private static Navigator fInstance;
    private static Map<Object, Navigator> navigators;
    private int errorCode;
    private String addendum;
    private boolean useAddendum;
    private String fromSessionForm;

    private Navigator(){}

    public static Navigator getInstance(Object pKey) {
        if (navigators == null)
            navigators = new HashMap<>();
        fInstance = navigators.get(pKey);

        if (fInstance == null) {
            fInstance = new Navigator();
            fInstance.setFromClass(fInstance.getClass());
            navigators.put(pKey, fInstance);
        }
        return fInstance;
    }

    public static void invalidateInstance(Object pKey) {
        if (navigators != null) {
            navigators.remove(pKey);
            if (navigators.isEmpty())
                navigators = null;
        }

    }

    public String getFromForm() {
        if(fromForm == null)
            fromForm = IConstants.REDIRECT_TO_DASHBOARD;
        return fromForm;
    }

    public boolean isUseAddendum() {
        return this.addendum != null;
    }


}