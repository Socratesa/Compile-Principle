package lexical;

import java.util.HashMap;

public class Tag {
    public final static int
        INTNUM = 256,
        KEYWORD = 257,
        IDENTIFIER = 258,
        OPERATOR = 259,
        DELIMITER = 260,
        STRCONSTANT = 261,
        STRINGCONSTANT = 262,
        END = 263,
        FLOATNUM = 264;

    private static HashMap<Integer,String> hashMap;

    static {
        hashMap = new HashMap();
        hashMap.put(INTNUM,"INTNUM");
        hashMap.put(FLOATNUM,"FLOATNUM");
        hashMap.put(KEYWORD,"KEYWORD");
        hashMap.put(IDENTIFIER,"IDENTIFIER");
        hashMap.put(OPERATOR,"OPERATOR");
        hashMap.put(DELIMITER,"DELIMITER");
        hashMap.put(STRCONSTANT,"STRCONSTANT");
        hashMap.put(STRINGCONSTANT,"STRINGCONSTANT");
        hashMap.put(END,"#");
    }

    public static String TypeMap(int a){
        return hashMap.get(a);
    }
}
