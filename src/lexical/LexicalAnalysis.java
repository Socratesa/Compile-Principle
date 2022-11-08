package lexical;

//import util.CustomException;
import util.CustomException;
import util.StrType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

/*
    本词法分析器主要处理以下内容：
        √ 1.关键字识别（int、float、if else while)
        √ 2.标志符识别
        √ 3.数字识别，整数及浮点数，且浮点数默认识别为float
        4.运算符识别 含 + - * / = > < ==         不做： >= <= !=
        5.分解符识别 含 , ; { } " " ' '          不做：[]
        对注释不进行处理
 */
public class LexicalAnalysis {

    public int line;
    private Hashtable<String,Word> words = new Hashtable();
    //private ArrayList<Token> tokens = new ArrayList<>();
    private ArrayList<Word> tokens = new ArrayList<>();
    private List<String> keywords;

    public LexicalAnalysis() {
        line = 1;
        /*
        keywords = Arrays.asList("auto","double","int","struct","break","else","long","switch",
                                "case","enum","register","typedef","char","extern","return","union",
                                "const","float","short","unsigned","continue","for","signed","void",
                                "default","goto","sizeof","volatile","do","if","while","static");
        */
        //精简版keyword
        keywords = Arrays.asList("int","float","if","else","while","void","return");
    }

    //public ArrayList<Token> getTokens() {
    //    return tokens;
    //}
    public ArrayList<Word> getTokens() {
        return tokens;
    }

    public void save(Word t){
        words.put(t.getLexeme(),t);
    }

    //查看输入内含多少回车 [start,end)
    public int enterNum(String input, int start,int end){
        int num = input.substring(start,end).split("\n").length -1 ;
        return num;
    }

    //生成字符串常量Token
    private int makeStringConstantToken(char[] str, int peek) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str[peek]);
        int len = str.length;
        peek++;
        int i = peek;
        for (; i < len; i++) {
            stringBuilder.append(str[i]);
            if(str[i]=='\"'){
                break;
            }
        }
        tokens.add(new Word(Tag.STRINGCONSTANT,stringBuilder.toString()));
        return i+1;
    }

    //生成分隔符Token
    private int makeDelimiterToken(char[] str, int peek) {
        tokens.add(new Word(Tag.DELIMITER,str[peek]+""));
        peek++;
        return peek;
    }

    //生成操作符Token
    private int makeOperatorToken(char[] str, int peek) throws CustomException {
        if(str[peek+1]=='='){
            tokens.add(new Word(Tag.OPERATOR,str[peek]+"="));
            peek = peek + 2;
            return peek;
        }
        /*
        语法分析未涉及
        else if(str[peek+1]=='+' && str[peek]=='+'){
            tokens.add(new Word(Tag.OPERATOR,"++"));
            peek = peek + 2;
            return peek;
        } else if(str[peek+1]=='-' && str[peek]=='-'){
            tokens.add(new Word(Tag.OPERATOR,"--"));
            peek = peek + 2;
            return peek;
        }
         */
        // 已知： str[peek]是运算符 在我们文法里 只有 == 是两个长度的操作符
        // 根据逻辑 str[peek+1]不是 = 在我们文法中不存在 抛异常
        else if(StrType.isOperator(str[peek+1])){
            throw new CustomException("无法构造运算符");
        }
        //长度为1的操作符
        else{
            tokens.add(new Word(Tag.OPERATOR,str[peek]+""));
            peek++;
            return peek;
        }
    }

    //生成 关键字和标志符 token
    private int makeLetterToken(char[] str, int peek) {
        StringBuilder sb = new StringBuilder(String.valueOf(str[peek]));
        peek++;
        //这是我们重写的isLetter 包含下划线
        while(StrType.isLetter(str[peek]) || StrType.isDigit(str[peek])){
            sb.append(str[peek++]);
        }
        String value = sb.toString();
        if(this.keywords.contains(value)){
            tokens.add(new Word(Tag.KEYWORD,value));
            return peek;
        }
        tokens.add(new Word(Tag.IDENTIFIER,value));
        return peek;
    }

    //仅支持十进制
    private int makeNumberToken(char[] str, int peek) throws CustomException {
        StringBuilder sb = new StringBuilder(String.valueOf(str[peek]));
        boolean isFloat = false;
        // eg:06
        if(str[peek]=='0' && str[peek+1]=='.'){
            isFloat = true;
            peek++;
            sb.append(str[peek]);
        }
        if(str[peek]=='0'){
            tokens.add(new Word(Tag.INTNUM,"0"));
            //tokens.add( new IntNum(Tag.NUM,0));
            peek++;
            return peek;
        }
        peek = peek + 1;
        while(StrType.isDigit(str[peek])){
            sb.append(str[peek++]);
        }
        int tag = isFloat? Tag.FLOATNUM : Tag.INTNUM;
        //Token token = isFloat? new FloatNum(Tag.NUM,Float.valueOf(sb.toString()))
        //        :new IntNum(Tag.NUM,Integer.valueOf(sb.toString()));
        this.tokens.add(new Word(tag,sb.toString()));
        return peek;
    }

    /*
    //字符常量  语法分析不涉及该部分 故舍弃
    //简单起见，我们仅考虑以下几种情况
    //字符型 数字型 部分转义(\n,\t)
    private int makeStrConstantToken(char[] str, int peek) throws CustomException {
        peek++;
        if(str[peek+1]=='\''){
            tokens.add(new Word(Tag.STRCONSTANT,"\'"+str[peek]+"\'"));
            peek = peek + 2;
            return peek;
        }
        if(str[peek]=='\\' && str[peek+1]=='n' && str[peek+2]=='\''){
            tokens.add(new Word(Tag.STRCONSTANT,"\\n"));
            peek = peek + 3;
            return peek;
        }else if(str[peek]=='\\' && str[peek+1]=='t' && str[peek+2]=='\''){
            tokens.add(new Word(Tag.STRCONSTANT,"\\t"));
            peek = peek + 3;
            return peek;
        }else{
            throw new CustomException("无法生成字符常量");
        }
    }

    */

    //打印token序列
    private void PrintTokens(){
        System.out.println("tokens序列如下：");
        for (Word token : this.tokens) {
            System.out.println(token);
        }
    }

    //跳过头文件
    private int skipHeader(String input){
        int lastHeadIndex  = input.lastIndexOf("#");
        if(lastHeadIndex == -1){
            //输入不包含头文件
            return 0;
        }
        //含头文件
        int peek = input.indexOf("\n",lastHeadIndex);
        //当前行号
        this.line += enterNum(input,0,peek);
        peek = peek+1;
        this.line += 1;
        return peek;
    }

    //跳过回车、制表符、空格、注释
    private int skipBlank(char[] str,String input,int peek,int strlen) throws CustomException {
        //Max(peek) = strlen-1
        for (int i = 0; i < 5; i++) {
            //跳过单行注释
            if(peek <= strlen-2 && str[peek]=='/' && str[peek+1] == '/'){
                int cur = peek;
                if(input.indexOf('\n',cur)==-1){
                    throw new CustomException("单行注释错误");
                }
                peek = input.indexOf('\n',cur)+1;
                this.line++;
            }
            //跳过多行注释
            if(peek <= strlen-2 && str[peek]=='/' && str[peek+1] == '*'){
                int cur = peek;
                if(input.indexOf("*/",cur) == -1){
                    throw  new CustomException("多行注释不完整");
                }
                peek = input.indexOf("*/",cur)+2;
                this.line += enterNum(input,cur,peek);
            }
            //跳回车
            while(peek<=strlen-1 && str[peek]=='\n'){
                this.line++;
                peek++;
            }
            //跳空格
            while(peek<=strlen-1 && str[peek]==' '){
                peek++;
            }
            //跳制表符
            while(peek<=strlen-1 && str[peek]=='\t'){
                peek++;
            }
        }
        return peek;
    }
    public void scan(String input) throws CustomException {
        char[] str = input.toCharArray();
        System.out.println(str);
        //为简便，我们对头文件声明、注释不做处理
        //直接跳过头文件  此处深究其实是有bug的
        int peek = skipHeader(input);
        int strlen = input.length();
        while(peek<strlen){
            //跳过回车、制表符、空格、注释
            peek = skipBlank(str,input,peek,strlen);
            if(peek>=strlen) break;

            //System.out.println("开始分析的符号为" + str[peek]);
            //System.out.println("此时行号为 " + this.line);

            if (StrType.isDigit(str[peek])) {
                //开始匹配数字
                int cur = peek;
                peek = makeNumberToken(str,cur);
            }
            else if(StrType.isLetter(str[peek])){
                //匹配标志符
                int cur = peek;
                peek = makeLetterToken(str,cur);
            }
            else if(StrType.isOperator(str[peek])){
                //匹配运算符
                int cur = peek;
                peek = makeOperatorToken(str,cur);
            }
            else if(StrType.isDelimiter(str[peek])){
                //匹配分隔符
                int cur = peek;
                peek = makeDelimiterToken(str,cur);
            }
            /*
            else if(StrType.isStrConstant(str[peek])){
                //字符常数
                int cur = peek;
                peek = makeStrConstantToken(str,cur);
            }
             */
            else if(StrType.isStringConstant(str[peek])){
                int cur = peek;
                peek = makeStringConstantToken(str,cur);
            } else{
                throw new CustomException(str[peek]+"不在C语言文法内");
            }
        }
        System.out.println("词法分析结束");
        System.out.println("line = " + line);
        PrintTokens();
    }

    public static void main(String[] args) throws CustomException {
        LexicalAnalysis lexicalAnalysys = new LexicalAnalysis();
        String demo1 = "#include <stdio.h>\n" +
                "#include <stdlib.h>\n" +
                "//int a=3,b=2;\n" +
                "/*\n" +
                "int myfunction(int a,int b){\n" +
                "    if(a>b){\n" +
                "        return a;\n" +
                "    }\n" +
                "    return b;\n" +
                "}*/\n" +
                "\n" +
                "int main()\n" +
                "{\n" +
                "    int a=3,b=2;\n" +
                "    int res = myfunction(a,b);\n" +
                "    \n" +
                "    printf(\"res = %d\\n\",res);\n" +
                "    for(int i=1;i<5;i++){\n" +
                "        printf(\"oopop\\n\");\n" +
                "    }\n" +
                "\n" +
                "    //printf(\"Hello world!\\n\");\n" +
                "    return 0;\n" +
                "}\n";
        String demo2 = "    int a=3,b=2;\n" +
                "    int res = myfunction(a,b);\n" +
                "    \n" +
                "    printf(\"res = %d\\n\",res);\n" +
                "    for(int i=1;i<5;i++){\n" +
                "        printf(\"oopop\\n\");\n" +
                "    }\n" +
                "\n" +
                "    //printf(\"Hello world!\\n\");\n" + " return 0;} ";
        String demo3 = "//zhe shi zhu shi\n" +
                "int main()\n" +
                "{\n" +
                "int a=3;\n" +
                "int b=4;\n" +
                "float c;\n" +
                "c=(a+b)/b;\n" +
                "printf(\"%d\",c);\n" +
                "return 0;\n" +
                "}\n";
        lexicalAnalysys.scan(demo3);

    }
}
