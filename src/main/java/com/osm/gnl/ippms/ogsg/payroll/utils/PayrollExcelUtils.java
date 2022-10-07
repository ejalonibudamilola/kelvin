package com.osm.gnl.ippms.ogsg.payroll.utils;


public abstract class PayrollExcelUtils {

    public static String makeXlsCompatibleName(String pString) {
        char[] wChar = pString.toCharArray();
        StringBuffer wStr = new StringBuffer();
        for (char c : wChar) {
            if ((Character.isLetterOrDigit(c)) || (Character.isWhitespace(c))) {
                wStr.append(c);
            } else if ((c == '/') || (c == '-') || (c == '_') || (c == '?'))
                wStr.append("_");
            else if ((c == '[') || (c == '{'))
                wStr.append("(");
            else if ((c == ']') || (c == '}'))
                wStr.append(")");
            else {
                wStr.append(" ");
            }

        }
        String wRetVal = wStr.toString();
        if (wRetVal.length() > 31) {
            wRetVal = wRetVal.substring(0, 31);
        }
        return wRetVal;
    }
    public static String makePdfCompatibleName(String pString) {
        char[] wChar = pString.toCharArray();
        StringBuffer wStr = new StringBuffer();
        for (char c : wChar) {
            if (Character.isLetterOrDigit(c)) {
                wStr.append(c);
            } else if ((c == '/') || (c == '-') || (c == '_') || (c == '?'))
                wStr.append("_");
            else if ((c == '[') || (c == '{'))
                wStr.append("_");
            else if ((c == ']') || (c == '}'))
                wStr.append("_");
            else if( Character.isWhitespace(c)){
                wStr.append("_");
            }

        }
        String wRetVal = wStr.toString();
        if (wRetVal.length() > 31) {
            wRetVal = wRetVal.substring(0, 31);
        }
        return wRetVal;
    }
    public static boolean isXlsNameCompatible(String pString) {

        char[] wChar = pString.toCharArray();
        StringBuffer wStr = new StringBuffer();
        for (char c : wChar) {

            if ((c == '/') || (c == '-') || (c == '_') || (c == '?') || (c == '[') || (c == '{') || (c == ']') || (c == '}')
                || c == '%' || c == '$' || c == '#' || c == '@' || c == '+' || c == '|' || c == '=') {
                return false;
            }


        }

        return true;
    }
}