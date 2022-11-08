package grammatical;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Item {
    private Map<String, List<String>> rules;
    private String pre;
    private int seq;
    private int pos;

    public Item(String pre, int seq, int pos){
        this.pre = pre;
        this.seq = seq;
        this.pos = pos;
        GrammaticalAnalysis grammaticalAnalysis = new GrammaticalAnalysis();
        rules = grammaticalAnalysis.getRules();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return seq == item.seq && pos == item.pos && Objects.equals(pre, item.pre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pre, seq, pos);
    }

    @Override
    public String toString() {
        /*
        return "Item{" +
                "pre='" + pre + '\'' +
                ", seq=" + seq +
                ", pos=" + pos +
                '}';
        */

        String[] a = rules.get(pre).get(seq).split(" ");
        int len = a.length;
        String res = pre + " -> ";
        for (int i = 0; i < len; i++) {
            if(i==pos){
                res+=".";
            }
            res += a[i] + " ";
        }
        if(len == pos){
            res += ".";
        }
        return res;


    }

    public String getPre() {
        return pre;
    }

    public void setPre(String pre) {
        this.pre = pre;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
