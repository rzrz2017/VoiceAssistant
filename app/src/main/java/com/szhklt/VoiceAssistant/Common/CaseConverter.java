package com.szhklt.VoiceAssistant.Common;

public class CaseConverter {
    public static String convertString(String str, Boolean beginUp){
        char[] ch = str.toCharArray();
        StringBuffer sbf = new StringBuffer();
        for(int i=0; i< ch.length; i++){
            if(i == 0 && beginUp){//如果首字母需大写
                sbf.append(charToUpperCase(ch[i]));
            }else{
                sbf.append(charToLowerCase(ch[i]));
            }
        }
        return sbf.toString();
    }

    /**转大写**/
    private static char charToUpperCase(char ch){
        if(ch <= 122 && ch >= 97){
            ch -= 32;
        }
        return ch;
    }

    /***转小写**/
    private static char charToLowerCase(char ch){
        if(ch <= 90 && ch >= 65){
            ch += 32;
        }
        return ch;
    }
}
