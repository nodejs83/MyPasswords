package com.hfad.mypasswords;


/**
 * Created by a602256 on 17/08/2017.
 */

public class Utils {


    public static final String GROUPID = "GroupId";
    public static final String ITEM_ID = "itemId";
    public static final String MODE = "mode";
    public static final String CREDENTIAL = "Credential";
    public static final String GROUPITEM_COLUMN = "group_item";
    public static final String NOINPUT = "No inputs";


    public static boolean hasText(String value){
        if(value != null && !value.isEmpty()){
            return true;
        }
        return false;
    }

    public static String capitalize(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StringBuffer(strLen)
             .append(Character.toTitleCase(str.charAt(0))).append(str.substring(1))
             .toString();
    }

}
