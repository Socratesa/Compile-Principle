public class Main {

    //词法分析：
    //    从左到右扫描单词，将字符转变为token
    public static String generateToken(StringBuilder line){
        //删除单行注释
        int begin = -1;
        while((begin = line.indexOf("//"))!=-1){
            System.out.println(begin);
            int end = line.indexOf("\n", begin);
            line.replace(begin,end+1, ""); // +1为了顺便消除回车
        }
        //删除多行注释
        while((begin = line.indexOf("/*"))!=-1){
            int end = line.indexOf("*/", begin);
            line.replace(begin,end+2, ""); //+2为了消除/
        }
        return String.valueOf(line);
    }
    //语法分析
    //    在词法分析的基础上，将单词序列进行组合，形成语法短语——“语句”、“表达式”  自底向上（SLR（1））
    public static void main(String[] args) {
        //System.out.println("hello world");
        //String line ="int main{\n" +
        //        "                (hello world);\n" +
        //        "         你觉得怎么样\n" +
        //        "    }";
        //String s = generateToken(new StringBuilder(line));
        String line = "#include <stdio.h>\n" +
                "#include <stdlib.h>\n" +
                "\n" +
                "int myfunction(int a,int b){\n" +
                "    if(a>b){\n" +
                "        return a;\n" +
                "    }\n" +
                "    return b;\n" +
                "}\n" +
                "\n" +
                "int main()\n" +
                "{\n" +
                "    //int a=3,b=2;\n" +
                "    int res = myfunction(a,b);\n" +
                "    /*\n" +
                "    printf(\"res = %d\\n\",res);\n" +
                "    for(int i=1;i<5;i++){\n" +
                "        printf(\"oopop\\n\");\n" +
                "    }\n" +
                "    */\n" +
                "    printf(\"Hello world!\\n\");\n" +
                "    return 0;\n" +
                "}";
        String res = generateToken(new StringBuilder(line));
        System.out.println(res);

        //String a = "odpgdf/*dfgkdfb*/";
        //System.out.println(a.indexOf("/*"));
        //System.out.println(a.indexOf("*/"));
    }

}
