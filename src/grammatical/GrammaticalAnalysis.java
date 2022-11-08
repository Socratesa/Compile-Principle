package grammatical;

import lexical.LexicalAnalysis;
import lexical.Tag;
import lexical.Word;
import util.CustomException;
import java.util.*;

import static lexical.Tag.*;

public class GrammaticalAnalysis {
    //文法
    Map<String, List<String>> rules;
    //文法开始符号
    String S;
    //非终结符
    List<String> V;
    //终结符
    List<String> T;
    //非终结符 U 终结符
    List<String> VT;
    //记录包含空串的非终结符
    List<String> EmptyV;
    //first集合
    Map<String,Set<String>> first;
    //follow集合
    Map<String,Set<String>> follow;
    //LR(0)项目集规范族
    Map<Integer, Set<Item>> familyMap;
    //action表
    String[][] actionChart;
    //goto表
    Integer[][] gotoChart;

/*
    // 测试 闭包、Goto(I,K)、项目集规范族的文法
    public GrammaticalAnalysis(){
        S = "S'";
        rules = new HashMap<>();
        rules.put("S'",List.of("S"));
        rules.put("S",List.of("B B"));
        rules.put("B",List.of("a B","b"));
        V = List.of("S'","S","B");
        T = List.of("a","b");
        VT = new ArrayList<>();
        VT.addAll(V);
        VT.addAll(T);
        calcEmptyV();
    }
*/
/*
    //测试first、follow的文法
    public GrammaticalAnalysis(){
        S = "E";
        rules = new HashMap<>();
        rules.put("E",List.of("T E'"));
        rules.put("E'",List.of("+ T E'",""));
        rules.put("T",List.of("F T'"));
        rules.put("T'",List.of("* F T'",""));
        rules.put("F",List.of("( E )","id"));
        V = List.of("E","E'","T","T'","F");
        T = List.of("+","*","id","(",")");
        VT = new ArrayList<>();
        VT.addAll(V);
        VT.addAll(T);
        calcEmptyV();
    }
*/

    //文法初始化 真正的文法
    public GrammaticalAnalysis(){
        S = "P'";
        rules = new HashMap<>();
        rules.put("P'", List.of("P"));
        //rules.put("P",List.of("B D S"));
        rules.put("P",List.of("D S"));
        rules.put("D",List.of("L id ; D",""));
        rules.put("L",List.of("int","float"));
        //rules.put("S",List.of("id = E","if ( C ) { S }","if ( C ) { S } else { S }","while ( C ) { S }","S ; S"));
        //修正
        rules.put("S",List.of("id = E ;","if ( C ) { S }","if ( C ) { S } else { S }","while ( C ) { S }","S S"));
        rules.put("C",List.of("E > E","E < E","E == E"));
        rules.put("E",List.of("E + T","E - T","T"));
        rules.put("T",List.of("F","T / F","T * F"));
        rules.put("F", List.of("( E )","id","num"));
        //添加函数定义文法
        //rules.put("B",List.of("R id ( L id , L id ) { S return E ; }",""));
        //rules.put("R",List.of("void","L"));
        //V = List.of("P'","P","D","S","L","C","E","T","F","R");
        //T = List.of("void","return","int","float","id","num","(",")",";","if","else","while","{","}","*","/","+","-","=","<",">","==");
        V = List.of("P'","P","D","S","L","C","E","T","F");
        T = List.of("int","float","id","num","(",")",";","if","else","while","{","}","*","/","+","-","=","<",">","==");
        VT = new ArrayList<>();
        VT.addAll(V);
        VT.addAll(T);
        calcEmptyV();
    }

/*
    //测试action、goto的文法
    public GrammaticalAnalysis(){
        S = "E'";
        rules = new HashMap<>();
        rules.put("E'",List.of("E"));
        rules.put("E",List.of("E + T","T"));
        rules.put("T",List.of("T * F","F"));
        rules.put("F",List.of("( E )","id"));

        V = List.of("E'","E","T","F");
        T = List.of("+","*","id","(",")");
        VT = new ArrayList<>();
        VT.addAll(V);
        VT.addAll(T);
        calcEmptyV();
    }
*/

    private void calcEmptyV(){
        EmptyV = new ArrayList<>();
        for (String v : V) {
            List<String> list = rules.get(v);
            if (list.contains("")) {
                EmptyV.add(v);
            }
        }
    }

    public Map<String, List<String>> getRules() {
        return rules;
    }

    public List<String> getV() {
        return V;
    }

    public List<String> getT() {
        return T;
    }

    public List<String> getVT() {
        return VT;
    }

    public Map<String, Set<String>> getFirst() {
        return first;
    }

    public Map<String, Set<String>> getFollow() {
        return follow;
    }

    //计算闭包   测试无问题
    public Set<Item> getClosure(Collection<Item> source){
        Set<Item> res = new HashSet<>();
        res.addAll(source);
        int flag = -1;
        //记录中间结果
        ArrayList<Item> tt = new ArrayList<>();
        tt.addAll(res);
        while(res.size()!=flag){
            //用于循环
            ArrayList<Item> temp = new ArrayList<>();
            temp.addAll(tt);
            tt.clear();
            for (Item re : temp) {
                String[] a = rules.get(re.getPre()).get(re.getSeq()).split(" ");
                if(a.length == re.getPos()){
                    //此时，点在末尾 无需添加
                    continue;
                }
                String prefix = a[re.getPos()];
                if(rules.get(prefix)==null){
                    continue;
                }
                int prefix_length = rules.get(prefix).size();
                for (int i = 0; i < prefix_length; i++) {
                    tt.add(new Item(prefix,i,0));
                }
            }
            flag = res.size();
            res.addAll(tt);
        }
        return res;
    }

    //计算Goto(I,K)
    public Set<Item> getGoto(Collection<Item> source,String word){
        List<Item> res = new ArrayList<>();
        for (Item re : source) {
            //查看下一个位置是不是word 如果是，加入set 否则继续
            String[] a = rules.get(re.getPre()).get(re.getSeq()).split(" ");
            if (a.length == re.getPos()) {
                //此时，点在末尾 无需添加
                continue;
            }
            String prefix = a[re.getPos()];
            if (word.equals(prefix)) {
                //后移一位点
                res.add(new Item(re.getPre(), re.getSeq(), re.getPos() + 1));
            }
        }
        //System.out.println(res);
        //System.out.println("----------------");
        return getClosure(res);
    }

    //计算项目集规范族
    public Map<Integer, Set<Item>> calcFamily(Collection<Item> source){
        Set<Item> closure = getClosure(source);
        Set<Set<Item>> family = new HashSet<>();
        //记录中间结果
        Set<Set<Item>> tt = new HashSet<>();
        family.add(closure);
        tt.addAll(family);
        int flag = -1;
        while(family.size()!=flag){
            Set<Set<Item>> temp = new HashSet<>();
            temp.addAll(tt);
            tt.clear();
            for (Set<Item> items : temp) {
                for (String s : VT) {
                    Set<Item> itemSet = getGoto(items, s);
                    if(itemSet!=null && itemSet.size()!=0 ){
                        tt.add(itemSet);
                    }
                }
            }
            flag = family.size();
            family.addAll(tt);
        }

        //此时，family已经是项目集规范族了，为了方便后续操作，套个map壳子，便于利用序号去取数据
        //让familyMap.get(0) 拿到的是初始状态 closure，做个交换
        familyMap = new HashMap<>();
        int cnt = 0;
        Set<Item> tp = new HashSet<>();
        for (Set<Item> items : family) {
            if(cnt==0){
                familyMap.put(cnt++,closure);
                tp = items;
                continue;
            }
            else if(items.containsAll(closure)){
                familyMap.put(cnt++,tp);
                continue;
            }
            familyMap.put(cnt++,items);
        }
        return familyMap;
    }

    //计算非终结符first集
    public void calcWordFirst(){
        //计算终结符的first集合
        first = new HashMap<>();
        for (String s : T) {
            first.put(s,Set.of(s));
        }
        //计算非终结符的first
        //分为两部分
        // 开头为终结符号
        for (String s : V) {
            List<String> list = rules.get(s);
            for (String ll : list) {
                String data = ll.split(" ")[0];
                // data不是终结符，且不是"" 直接跳过
                if (!T.contains(data) && !"".equals(data)) {
                    continue;
                }
                Set<String> setData = first.get(s);
                if(setData==null){
                    setData = new HashSet<>();
                }
                setData.add(data);
                first.put(s,setData);
            }
        }

        System.out.println("-----------");
        //开头为非终结符号
        while(true){
            //查看开始时所有First集合元素总数
            long beginCount = first.values().stream().flatMap(Collection::stream).count();
            //System.out.println(beginCount);
            //第一个非终结符号不能为空串
            for (String s : V) {
                //计算first(s) 不含空串
                List<String> list = rules.get(s);
                for (String ll : list) {
                    //s-> 第一个字符
                    String data = ll.split(" ")[0];
                    if(!V.contains(data)){
                        continue;
                    }
                    Set<String> sfirst = first.get(s);
                    Set<String> dfirst = first.get(data);
                    sfirst = unionSet(sfirst,dfirst);
                    first.put(s,sfirst);
                }
            }
            //前几个非终结符号可以为空串
            //s->XYZ XY均可为空
            for (String s : V) {
                //计算first(s) 不含空串
                List<String> list = rules.get(s);
                for (String ll : list) {
                    //s-> 第一个字符
                    String[] splits = ll.split(" ");
                    int splitsLen = splits.length;
                    int cnt = 0;
                    String data = splits[cnt++];
                    while(V.contains(data)){
                        //此时不包含空字符串
                        //System.out.println(data);
                        if(first.get(data)==null || !first.get(data).contains("")){
                            break;
                        }
                        //判断整体能不能为空串
                        if(cnt==splitsLen){
                            //此时可以加入空串
                            Set<String> sfirst = first.get(s);
                            sfirst.add("");
                            first.put(s,sfirst);
                            break;
                        }
                        //更换头
                        data = splits[cnt++];
                        //加进来
                        Set<String> sfirst = first.get(s);
                        Set<String> dfirst = first.get(data);
                        sfirst = unionSet(sfirst,dfirst);
                        first.put(s,sfirst);
                    }

                }
            }

            long endCount = first.values().stream().flatMap(Collection::stream).count();
            if(beginCount==endCount){
                break;
            }
        }
    }

    //封装去除空串，两个集合的合并 合并结果保存在sfirst中
    public Set<String> unionSet(Set<String> sfirst, Set<String> dfirst){
        if(sfirst == null){
            sfirst = new HashSet<>();
        }
        if(dfirst == null){
            dfirst = new HashSet<>();
        }
        //注意 此处不能直接操作 dfirst 引用传递 和成员变量指向一样
        Set<String> temp = new HashSet<>();
        temp.addAll(dfirst);
        if(temp.contains("")){
            temp.remove("");
        }
        sfirst.addAll(temp);
        return sfirst;
    }

    //计算句子first集合
    public Set<String> calcSentenceFirst(String sentence){
        String[] s = sentence.split(" ");
        int len = s.length;
        int cnt = 0;
        Set<String> res = new HashSet<>();
        Set<String> dfirst = first.get(s[cnt++]);
        res = unionSet(res,dfirst);
        while (dfirst.contains("") && cnt < len){
            dfirst = first.get(s[cnt++]);
            res = unionSet(res,dfirst);
        }
        if(dfirst.contains("") && cnt==len){
            res.add("");
        }
        return res;
    }

    //计算follow集
    public void calcFollow(){
        follow = new HashMap<>();
        Set<String> Sfollow = new HashSet<>();
        Sfollow.add("#");
        follow.put(S,Sfollow);
        while(true){
            //查看开始时所有First集合元素总数
            long beginCount = follow.values().stream().flatMap(Collection::stream).count();
            for (String v : V) {
                //通过这种方式获得所有文法
                List<String> list = rules.get(v);
                for (String s : list) {
                    String afterSentence = new String(s);
                    //对每一个文法，进行以下操作
                    while (true){
                        int index = afterSentence.indexOf(" ");
                        //此时，已经到最后一个单词了
                        if(index == -1){
                            //最后一个为 非终结符 可以加 #
                            String beforeData = afterSentence;
                            if(V.contains(beforeData)){
                                // s -> aaaaa beforedata
                                Set<String> sset = follow.get(beforeData);
                                Set<String> dset = follow.get(v);
                                sset = unionSet(sset,dset);
                                sset.add("#");
                                follow.put(beforeData,sset);
                            }
                            break;
                        }
                        String beforeData = afterSentence.substring(0,index);
                        afterSentence = afterSentence.substring(index+1);
                        if(V.contains(beforeData)){
                            Set<String> sset = follow.get(beforeData);
                            Set<String> dset = calcSentenceFirst(afterSentence);
                            sset = unionSet(sset,dset);
                            follow.put(beforeData,sset);
                            //判断afterSentence是不是可以推出空串
                            if(judgeEmpty(afterSentence) && !v.equals(beforeData)){
                                Set<String> ssset = follow.get(beforeData);
                                Set<String> ddset = follow.get(v);
                                ssset = unionSet(ssset,ddset);
                                ssset.add("#");
                                follow.put(beforeData,ssset);
                            }
                        }
                    }
                }
            }
            long endCount = follow.values().stream().flatMap(Collection::stream).count();
            if(beginCount == endCount){
                break;
            }
        }


    }

    private boolean judgeEmpty(String sentence){
        String[] s = sentence.split(" ");
        for (String data : s) {
            if(!EmptyV.contains(data)){
                return false;
            }
        }
        return true;
    }

    private boolean judgeEqualSet(Set<Item> set1,Set<Item> set2){
        if(set1.size()!=set2.size()){
            return false;
        }
        //长度一致，且set内部均不相等
        Map<Item,Integer> map = new HashMap<>();
        for (Item item : set1) {
            map.put(item,1);
        }
        for (Item item : set2) {
            if(map.get(item)==null){
                return false;
            }
        }
        return true;

    }

    public int getGotoK(Collection<Item> source,String word){
        Set<Item> aGoto = getGoto(source, word);
        int len = familyMap.size();

        for (int i = 0; i < len; i++) {
            // 判断aGoto和family.get(i)哪个完全相等
            if(judgeEqualSet(aGoto,familyMap.get(i))){
                return i;
            }
        }
        return -1;
    }

    //生成goto表和action表
    public void calcChart(){
        //在此，我们假设已经生成了family
        int len = familyMap.size();
        //+1 最后一个是 #
        actionChart = new String[len][T.size()+1];
        gotoChart = new Integer[len][V.size()];
        for (int i = 0; i < len; i++) {
            //第i个项集
            Set<Item> items = familyMap.get(i);
            for (Item item : items) {
                String[] rule = rules.get(item.getPre()).get(item.getSeq()).split(" ");
                int ruleLen = rule.length;
                int dotpos = item.getPos();
                int rowIndex = i;
                if(dotpos == ruleLen){
                    //进行规约，在此和PPT表示方式不一致
                    Set<String> preFollows = follow.get(item.getPre());
                    for (String p : preFollows) {
                        if ("#".equals(p)) {
                            actionChart[rowIndex][T.size()] = "r" + item.getPre() + item.getSeq();
                            continue;
                        }
                        int colIndex = T.indexOf(p);
                        actionChart[rowIndex][colIndex] = "r" + item.getPre() + item.getSeq();
                    }
                    if(S.equals(item.getPre())){
                        actionChart[rowIndex][T.size()] = "acc";
                    }
                    continue;
                }
                String cur = rule[dotpos];
                int res = getGotoK(items, cur);
                if(V.contains(cur)){
                    //此时为非终结符
                    int colIndex = V.indexOf(cur);
                    gotoChart[rowIndex][colIndex] = res;
                }
                else if(T.contains(cur)){
                    //此时为终结符
                    int colIndex = T.indexOf(cur);
                    actionChart[rowIndex][colIndex] = "s" + res;
                }
            }
        }
    }

    //打印action、goto表格
    public void printChart(){
        System.out.println("action---------");
        int len1 = actionChart.length;
        int len2 = actionChart[0].length;

        //System.out.print("  \t");
        //for (String t : T) {
        //    System.out.printf("%4s\t",t);
        //}
        //System.out.println("   #");
        //for (int i = 0; i < len1; i++) {
        //    System.out.printf("%2d\t",i);
        //    for (int j = 0; j < len2; j++) {
        //        if(actionChart[i][j] == null){
        //            System.out.print("    \t");
        //            continue;
        //        }
        //        System.out.printf("%4s\t",actionChart[i][j]);
        //    }
        //    System.out.println();
        //}
        System.out.print("  \t");
        System.out.println("#");
        for (int i = 0; i < len1; i++) {
            System.out.printf("%2d\t",i);
            if(actionChart[i][len2-1] == null){
                System.out.println("    \t");
                continue;
            }
            System.out.printf("%4s\t",actionChart[i][len2-1]);
            System.out.println();
        }

        System.out.println("goto---------");
        int glen1 = gotoChart.length;
        int glen2 = gotoChart[0].length;
        System.out.print("  \t");
        for (String v : V) {
            System.out.printf("%4s\t",v);
        }
        System.out.println();
        for (int i = 0; i < glen1; i++) {
            System.out.printf("%2d\t",i);
            for (int j = 0; j < glen2; j++) {
                if(gotoChart[i][j] == null){
                    System.out.print("    \t");
                    continue;
                }
                System.out.printf("%4s\t",gotoChart[i][j]);
            }
            System.out.println();
        }
    }

    //将token转化为对应 T中标志
    //暂时不处理字符串、(字符）形式
    private int getStrIndex(Word str) {
        int tag = str.getTag();
        String lexeme = str.getLexeme();
        if(tag == INTNUM || tag == FLOATNUM){
            return T.indexOf("num");
        }else if(tag == IDENTIFIER){
            return T.indexOf("id");
        }else if(tag == KEYWORD || tag == OPERATOR || tag == DELIMITER){
            return T.indexOf(lexeme);
        }else if(tag == END){
            //System.out.println(T.size());
            return T.size();
        }
        return -1;
    }

    public void run(String input) throws CustomException {

        //获取tokens序列
        LexicalAnalysis lexicalAnalysis = new LexicalAnalysis();
        lexicalAnalysis.scan(input);
        //ArrayList<Token> tokens = lexicalAnalysis.getTokens();
        ArrayList<Word> tokens = lexicalAnalysis.getTokens();

        //获取action、goto表
        calcWordFirst();
        calcFollow();
        calcFamily(List.of(new Item(S,0,0)));
        calcChart();
        //System.out.println("first:---------------");
        //System.out.println(first);
        //System.out.println("follow:---------------");
        //System.out.println(follow);
        System.out.println("family:---------------");
        System.out.println(familyMap);
        printChart();

        //此时，正式开始分析
        tokens.add(new Word(Tag.END,"#"));
        int len = tokens.size();
        int cursor = 0;
        //创建两个栈:状态栈、输入符号栈1
        LinkedList<Integer> stateStack = new LinkedList<>();
        LinkedList<String> symbolStack = new LinkedList<>();
        stateStack.push(0);
        symbolStack.push("#");
        while(true){
            //获取栈顶状态
            int topState = stateStack.peek();
            //获取输入缓冲流中第一个token
            Word handleWord = tokens.get(cursor);
            //根据输入找到对应编号 数字要转化为 num  标志符转化为id
            int strIndex = getStrIndex(handleWord);
            String res = actionChart[topState][strIndex];
            if(res == null){
                //查看当前栈顶项集是否包含包含空串
                Set<Item> curSet = familyMap.get(topState);
                boolean flag = false;
                for (Item item : curSet) {
                    if (EmptyV.contains(item.getPre()) && "".equals(rules.get(item.getPre()).get(item.getSeq()))) {
                        int colIndex = V.indexOf(item.getPre());
                        stateStack.push(gotoChart[topState][colIndex]);
                        symbolStack.push("");
                        flag = true;
                        break;
                    }
                }
                if(flag){
                    continue;
                }
                //抛异常
                throw new CustomException("语法分析出错");
            }
            if("acc".equals(res)){
                return;
            }
            if("s".equals(String.valueOf(res.charAt(0)))){
                int newState = Integer.parseInt(res.substring(1));
                stateStack.push(newState);
                symbolStack.push(T.get(strIndex));
                cursor++;
            }
            else if("r".equals(String.valueOf(res.charAt(0)))){
                int reslen = res.length();
                int i = 1;
                //eg: rF1  目的是区分开 pre 和 seq
                //注意到seq由数字构成，而pre不含数字
                for (; i < reslen; i++) {
                    if(Character.isDigit(res.charAt(i))){
                        break;
                    }
                }
                String pre = res.substring(1,i);
                Integer seq = Integer.parseInt(res.substring(i));
                String rule = rules.get(pre).get(seq);
                int ruleLen = rule.split(" ").length;
                while(ruleLen > 0){
                    ruleLen--;
                    stateStack.pop();
                    symbolStack.pop();
                }
                //当前栈顶
                topState = stateStack.peek();
                int colIndex = V.indexOf(pre);
                stateStack.push(gotoChart[topState][colIndex]);
                symbolStack.push(pre);
                System.out.println(pre + " -> " + rule);
                //cursor++;
            }
        }



    }


}
