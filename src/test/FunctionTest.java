package test;

import grammatical.GrammaticalAnalysis;
import grammatical.Item;
import util.CustomException;
import org.junit.Test;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FunctionTest {

    @Test
    public void testException() throws CustomException {
        if(true){
            throw new CustomException("测试成功");
        }
    }

    @Test
    public void testHashMap(){
        List<String> a = List.of("a", "b", "c");
        System.out.println(a);
    }

    @Test
    public void testSplit(){
        String a = "a";
        String b = "a ";
        String c = "a b";
        String d = "";
        System.out.println(a.split(" ")+" :" + a.split(" ").length);
        System.out.println(b.split(" ")+" :" + b.split(" ").length);
        System.out.println(c.split(" ")+" :" + c.split(" ").length);
        System.out.println(d.split(" ")+" :" + d.split(" ").length);

    }

    @Test
    public void testgetClosure(){
        Item e = new Item("S", 0, 0);
        //Item e2 = new Item("S", 4, 0);
        GrammaticalAnalysis grammaticalAnalysis = new GrammaticalAnalysis();
        Set<Item> clArrayListosure = grammaticalAnalysis.getClosure(List.of(e));
        for (Item item : clArrayListosure) {
            System.out.println(item);
        }
    }

    @Test
    public void testgetGoto(){
        Item e = new Item("E", 0, 1);
        GrammaticalAnalysis grammaticalAnalysis = new GrammaticalAnalysis();
        Set<Item> s = grammaticalAnalysis.getGoto(List.of(e), "+");
        for (Item item : s) {
            System.out.println(item);
        }
    }

    @Test
    public void testCalcFamily(){
        GrammaticalAnalysis grammaticalAnalysis = new GrammaticalAnalysis();
        // 用PPT文法进行测试
        Map<Integer, Set<Item>> integerSetMap = grammaticalAnalysis.calcFamily(List.of(new Item("S'", 0, 0)));
        //简单C语言文法测试
        //grammaticalAnalysis.calcFamily(List.of(new Item("P'", 0, 0)));
        int len = integerSetMap.size();
        for (int i = 0; i < len; i++) {
            System.out.println(integerSetMap.get(i));
        }
    }

    @Test
    public void testSet(){
        GrammaticalAnalysis grammaticalAnalysis = new GrammaticalAnalysis();
        grammaticalAnalysis.calcFamily(List.of(new Item("P'", 0, 0)));
    }

    @Test
    public void testMap(){
        Map<Integer,String> hashmap = new HashMap<>();
        {
            String demo = new String("demo");
            String demo2 = new String("demo2");
            hashmap.put(0,demo);
            hashmap.put(1,demo2);
        }
        for (int i = 0; i < hashmap.size(); i++) {
            System.out.println(hashmap.get(i));
        }

        System.out.println(hashmap.get(10));
    }


    @Test
    public void testFunc(){
        Set<List<Integer>> hash = new HashSet<>();
        hash.add(List.of(4));
        func(hash);
        System.out.println(hash);
        System.out.println(hash.size());

    }

    private void func( Set<List<Integer>> hash){
        hash.add(List.of(1));
        hash.add(List.of(2));
        hash.add(List.of(3,5,6));
    }

    @Test
    public void testWordFirst(){
        GrammaticalAnalysis grammaticalAnalysis = new GrammaticalAnalysis();
        grammaticalAnalysis.calcWordFirst();
        Map<String, Set<String>> first = grammaticalAnalysis.getFirst();
        System.out.println(first);
    }

    @Test
    public void testSentenceFirst(){
        GrammaticalAnalysis grammaticalAnalysis = new GrammaticalAnalysis();
        grammaticalAnalysis.calcWordFirst();
        System.out.println(grammaticalAnalysis.getFirst());
        Set<String> e_e = grammaticalAnalysis.calcSentenceFirst("E E");
        System.out.println(e_e);
        Set<String> ttf = grammaticalAnalysis.calcSentenceFirst("T' T' F");
        System.out.println(ttf);
        Set<String> ttt = grammaticalAnalysis.calcSentenceFirst("T' E' T'");
        System.out.println(ttt);
    }
    @Test
    public void testElementNum(){
        Map<Integer,List<Integer>> map = new HashMap<>();
        List<Integer> ll = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ll.add(i);
            map.put(i,ll);
        }


        System.out.println(map.size());
        System.out.println(map);
    }

    @Test
    public void testStream() {
        Map<Integer, Set<String>> hash = new HashMap<>();
        Set<String> set1 = Set.of("op", "102", "34");
        Set<String> set2 = Set.of("oppp", "11202", "op34");
        hash.put(1,set1);
        hash.put(2,set2);
        long count = hash.values().stream().flatMap(Collection::stream).count();
        System.out.println(count);
    }


    @Test
    public void testAddAll(){
        Set<Integer> set1 = new HashSet<>();
        set1.add(1);
        set1.add(2);
        set1.add(3);
        Set<Integer> set2 = new HashSet<>();
        set2.add(4);
        set2.add(5);
        set2.add(6);
        set1.remove(1);
        set2.addAll(set1);
        set1.add(1);
        System.out.println(set1);
        System.out.println(set2);

    }

    @Test
    public void testStringArray(){
        String s = "op wir gbjv kds";
        String a = new String(s);
        a = a.substring(a.indexOf(" "));
        System.out.println(s);
        System.out.println(a);

        String afterSentence = "b";
        int index = afterSentence.indexOf(" ");
        System.out.println(index);
        String beforeData = afterSentence.substring(0,index);
        afterSentence = afterSentence.substring(index+1);
        System.out.println(beforeData);
        System.out.println(afterSentence);
    }

    @Test
    public void testFollow(){
        GrammaticalAnalysis grammaticalAnalysis = new GrammaticalAnalysis();
        grammaticalAnalysis.calcWordFirst();
        System.out.println(grammaticalAnalysis.getFirst());
        System.out.println("---------------------------------");
        grammaticalAnalysis.calcFollow();
        System.out.println(grammaticalAnalysis.getFollow());
        System.out.println("---------------------------------");
        Map<Integer, Set<Item>> family = grammaticalAnalysis.calcFamily(List.of(new Item("E", 0, 0)));
        System.out.println(family.size());
        System.out.println(family);
    }

    @Test
    public void testRetainAll(){
        Set<Integer> s1 = new HashSet<>();
        Set<Integer> s2 = new HashSet<>();
        s1.add(1);
        s1.add(2);
        s1.add(3);
        s2.add(3);
        s2.add(2);
        System.out.println(s1.retainAll(s2));
    }


    @Test
    public void testActionAndGoto(){
        GrammaticalAnalysis grammaticalAnalysis = new GrammaticalAnalysis();
        grammaticalAnalysis.calcWordFirst();
        System.out.println(grammaticalAnalysis.getFirst());
        System.out.println("---------------------------------");
        grammaticalAnalysis.calcFollow();
        System.out.println(grammaticalAnalysis.getFollow());
        System.out.println("---------------------------------");
        Map<Integer, Set<Item>> family = grammaticalAnalysis.calcFamily(List.of(new Item("E'", 0, 0)));
        System.out.println(family.size());
        System.out.println(family);
        System.out.println("---------------------------------");
        grammaticalAnalysis.calcChart();
        grammaticalAnalysis.printChart();
        System.out.println("----------------------------------");
        Set<Item> id1 = grammaticalAnalysis.getGoto(family.get(0), "id");
        System.out.println(id1);
        int id = grammaticalAnalysis.getGotoK(family.get(0), "id");
        System.out.println(id);
        System.out.println(family.get(id));
    }

    @Test
    public void testGotoK(){
        GrammaticalAnalysis grammaticalAnalysis = new GrammaticalAnalysis();
        grammaticalAnalysis.calcWordFirst();
        grammaticalAnalysis.calcFollow();
        Map<Integer, Set<Item>> family = grammaticalAnalysis.calcFamily(List.of(new Item("E'", 0, 0)));
        System.out.println(family);
        System.out.println("---------------------------------");
        Set<Item> set3 = family.get(2);
        System.out.println("------------------------");
        Set<Item> set = grammaticalAnalysis.getGoto(family.get(4), "+");
        //System.out.println(set3);
        System.out.println(set);

        System.out.println(set3.retainAll(set));
    }

    @Test
    public void testLinkedList(){
        LinkedList<Integer> linkedList = new LinkedList<>();
        linkedList.add(1);
        linkedList.add(2);
        linkedList.add(3);
        System.out.println(linkedList.peek());
        System.out.println(linkedList.peekFirst());
        System.out.println(linkedList.peekLast());
        System.out.println(linkedList.size());
    }

    @Test
    public void testRun() throws CustomException {
        GrammaticalAnalysis grammaticalAnalysis = new GrammaticalAnalysis();
        String demo1 = "#include <iostream>\n" +
                "\n" +
                "// this is zhu shi \n" +
                "int func(int a,int b){\n" +
                "\tint c;\n" +
                "\tc = a+b;\n" +
                "\treturn c; \n" +
                "}\n" +
                "\n" +
                "int func2(int a){\n" +
                "\treturn a; \n" +
                "}\n" +
                "\n" +
                "int main(){\n" +
                "\t// this is zhu shi \n" +
                "\tint a;\n" +
                "\tint b;\n" +
                "\ta = 2;\n" +
                "\tb = 6;\n" +
                "\t\n" +
                "\tif(a < b){\n" +
                "\t\ta = a + 2;\n" +
                "\t}else{\n" +
                "\t\tb = b + 2;\n" +
                "\t}\n" +
                "\twhile(a>0){\n" +
                "\t\ta=a-1;\n" +
                "\t}\t\n" +
                "}";
        String demo2 = "int main(){\n" +
                "\t// this is zhu shi \n" +
                "\tint a;\n" +
                "\tint b;\n" +
                "\ta = 2;\n" +
                "\tb = 6;\n" +
                "\t\n" +
                "\tif(a < b){\n" +
                "\t\ta = a + 2;\n" +
                "\t}else{\n" +
                "\t\tb = b + 2;\n" +
                "\t}\n" +
                "\twhile(a>0){\n" +
                "\t\ta=a-1;\n" +
                "\t}\t\n" +
                "}";
        String demo3 = "// this is zhu shi \n" +
                "int func(int a,int b){\n" +
                "\tint c;\n" +
                "\tc = a+b;\n" +
                "\treturn c; \n" +
                "}\n" +
                "\n" +
                "int func2(int a){\n" +
                "\treturn a; \n" +
                "}\n" +
                "\n" +
                "int main(){\n" +
                "\t// this is zhu shi \n" +
                "\tint a;\n" +
                "\tint b;\n" +
                "\ta = 2;\n" +
                "\tb = 6;\n" +
                "\t\n" +
                "\tif(a < b){\n" +
                "\t\ta = a + 2;\n" +
                "\t}else{\n" +
                "\t\tb = b + 2;\n" +
                "\t}\n" +
                "\twhile(a>0){\n" +
                "\t\ta=a-1;\n" +
                "\t}\t\n" +
                "}";
        grammaticalAnalysis.run(demo1);
    }

    @Test
    public void test(){
        LinkedList<Integer> ll = new LinkedList<>();
        ll.push(1);
        ll.push(2);
        ll.push(3);
        System.out.println(ll.peek());
        System.out.println(ll);
    }

    @Test
    public void testLastIndexOf(){
        String a = "ssbd";
        System.out.println(a.lastIndexOf("#"));
    }
}
