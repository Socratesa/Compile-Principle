package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StrType {

    public static boolean isDigit(char str){
        return Character.isDigit(str);
    }

    public static boolean isLetter(char str){
        if(str == '_'){
            return true;
        }
        return Character.isLetter(str);
    }
    public static boolean isOperator(char str){
        List<Character> arrayList = Arrays.asList('+','-','*','/','%','=','>','<','!');
        if(arrayList.contains(str)){
            return true;
        }
        return false;
    }
    public static boolean isDelimiter(char str){
        List<Character> arrayList = Arrays.asList(',',';','{','}','[',']','(',')');
        if(arrayList.contains(str)){
            return true;
        }
        return false;
    }
    public static boolean isStrConstant(char str){
        if(str == '\''){
            return true;
        }
        return false;
    }
    public static boolean isStringConstant(char str){
        if(str == '\"'){
            return true;
        }
        return false;
    }
}
