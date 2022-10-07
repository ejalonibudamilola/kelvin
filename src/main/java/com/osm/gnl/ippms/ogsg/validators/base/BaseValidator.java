
/*
 * Copyright (c) 2020.
 * This code is proprietary to GNL Systems Ltd. All rights reserved.
 */

package com.osm.gnl.ippms.ogsg.validators.base;

import com.osm.gnl.ippms.ogsg.abstractentities.predicate.CustomPredicate;
import com.osm.gnl.ippms.ogsg.base.services.GenericService;
import com.osm.gnl.ippms.ogsg.payroll.utils.IppmsUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

@Component
public abstract class BaseValidator implements Validator {


    protected CustomPredicate customPredicate;

    protected final GenericService genericService;

    protected BaseValidator(GenericService genericService) {
        this.genericService = genericService;
    }


    protected String parseIt(String pArrearsPercentageStr) {
        StringBuffer wStrBuff = new StringBuffer();
        char[] wChar = pArrearsPercentageStr.toCharArray();
        int dotCount = 0;
        for (char c : wChar) {
            if (!Character.isDigit(c)) {
                if ((c != '.') ||
                        (dotCount != 0)) continue;
                dotCount++;
            }
            wStrBuff.append(c);
        }

        return wStrBuff.toString();
    }

    public static boolean allNumeric(String pNum) {
        boolean retVal = true;
        try {
            char[] arr$ = pNum.toCharArray();
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; i$++) {
                Character c = Character.valueOf(arr$[i$]);
                if (!Character.isDigit(c.charValue())) {
                    retVal = false;
                    break;
                }
            }
        } catch (Exception ex) {
            retVal = false;
        }

        return retVal;
    }

    public static boolean isEmailValid(String pEmailAddress) {


        //Check for One and Only One @.
        if (IppmsUtils.isNullOrEmpty(pEmailAddress))
            return false;
        if (pEmailAddress.startsWith("@") || pEmailAddress.endsWith("@"))
            return false;
        if (pEmailAddress.indexOf("@") == -1)
            return false;
        if (pEmailAddress.indexOf("-@") != -1 || pEmailAddress.indexOf("@-") != -1)
            return false;
        if (pEmailAddress.indexOf(".") == -1)
            return false;

        if (!permissibleBeforeOrAfterAt(pEmailAddress.substring(0,pEmailAddress.indexOf("@"))))
            return false;
        if(!permissibleBeforeOrAfterAt(pEmailAddress.substring(pEmailAddress.indexOf("@") + 1)))
            return false;

        return true;
    }



    private static boolean permissibleBeforeOrAfterAt(String substring) {

        boolean retVal = true;
        try {
            char[] arr$ = substring.toCharArray();
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; i$++) {
                Character c = Character.valueOf(arr$[i$]);
                if (!Character.isLetterOrDigit(c.charValue()) && c != '.' && c != '-' && c != '_') {
                    retVal = false;
                    break;
                }
            }
        } catch (Exception ex) {
            retVal = false;
        }

        return retVal;
    }

}