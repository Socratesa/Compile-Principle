package lexical;

public class Word{
    //标志
    int tag;
    //lexeme 语素 及具体表现
    private String lexeme;

    public Word(int tag,String lexeme) {
        this.tag = tag;
        this.lexeme = lexeme;
    }

    public int getTag() {
        return tag;
    }

    public String getLexeme() {
        return lexeme;
    }

    @Override
    public String toString() {
        return "("+Tag.TypeMap(this.tag)+","+lexeme+")";
    }
}

/*
public class Word extends Token{
    //lexeme 语素 及具体表现
    private String lexeme;

    public Word(int tag,String lexeme) {
        super(tag);
        this.lexeme = lexeme;
    }

    public String getLexeme() {
        return lexeme;
    }

    @Override
    public String toString() {
        return "("+Tag.TypeMap(this.tag)+","+lexeme+")";
    }
}
*/
