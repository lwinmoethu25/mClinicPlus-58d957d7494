package com.lucentinsight.mclinicplus.common;


public class StringUtil {

    /**
     * null to empty string
     * @param str
     * @return
     */
   public static String nullSafeString(String str){
        return str == null? "" : str;
    }


    /**
     * empty string to null
     * @param value
     * @return
     */
   public static String emptyToNull(String value){
        return (value != null && value.isEmpty())?null : value;
   }

    /**
     * capitalize string
     * @param str
     * @return
     */
   public static String capitalizeString(String str){
       if(str == null)return str;
       String s = "";
       for(int i = 0; i < str.length(); i++){
           if(i == 0){
                s += ("" +str.charAt(i)).toUpperCase();
           }
           else{
               s += ("" +str.charAt(i)).toLowerCase();
           }
       }
       return s;
   }

}
