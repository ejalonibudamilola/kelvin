package com.osm.gnl.ippms.ogsg.payroll.utils;

import java.util.Random;

public class PassPhrase
{
  //private static final int MIN_LENGTH = 9;
  private static final Random r = new Random();

  private static final char[] goodChar = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

  private static final char[] goodChar3 = {'_', '*', '#', '2', '3', '4', '5', '6', '7', '8', '9', '+', '-', '@','0','$' };

  private static final char[] goodChar4 = { 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', '2', '3', '4', '5', '6', '7', '8', '9', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K' };

  public static final char[] numbers = {'0','1','2', '3', '4', '5', '6', '7', '8', '9'};

  public static final char[] special = {'_', '*', '#', '.', ';', ':', '!', '&', '^', '+', '-', '@','$'};

  public static final char[] upperCases = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I','J', 'K', 'L','M', 'N', 'O','P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };


  public static String generateFileNamingPseudo()
  {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < 2; i++) {
      sb.append(numbers[r.nextInt(numbers.length)]);
    }
    for (int i = 0; i < 2; i++) {
      sb.append(goodChar4[r.nextInt(goodChar4.length)]);
    }
    for (int i = 0; i < 2; i++) {
      sb.append(goodChar4[r.nextInt(goodChar4.length)]);
    }
    return sb.toString();
  }

  private static String getNext(int pLen) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < pLen; i++) {
      sb.append(goodChar[r.nextInt(goodChar.length)]);
    }
    return sb.toString();
  }
  public static String generatePassword() {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < 6; i++) {
      sb.append(goodChar[r.nextInt(goodChar.length)]);
    }
    for (int i = 0; i < 3; i++) {
      sb.append(goodChar3[r.nextInt(goodChar3.length)]);
    }
    for (int i = 0; i < 3; i++) {
      sb.append(goodChar4[r.nextInt(goodChar4.length)]);
    }
    return sb.toString();
  }

  public static String generateCapcha(int pLength) {
    return getNext(pLength);
  }

  public static String generateFileUploadId(String pUserName, String pRoleName)
  {
    StringBuffer sb = new StringBuffer();
    StringBuffer sb2 = new StringBuffer();
    for (int i = 0; i < 4; i++) {
      sb.append(goodChar[r.nextInt(goodChar.length)]);
    }

    for (int i = 0; i < 4; i++) {
      sb2.append(goodChar4[r.nextInt(goodChar4.length)]);
    }

    String userName = getDesiredUserLength(pUserName);
    return userName+"_" + sb.toString() + sb2.toString() + pRoleName.substring(0, 3);
  }

  private static String getDesiredUserLength(String pUserName) {
    if (pUserName == null) {
      return "XXXX";
    }
    if (pUserName.length() == 4)
      return pUserName.toUpperCase();
    if (pUserName.length() > 4) {
      return pUserName.substring(0, 4).toUpperCase();
    }

    return null;
  }

}