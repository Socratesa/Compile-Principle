package util;

public class CustomException extends Exception {
    private String ex;
    public CustomException(String ex){
        super(ex);
        this.ex = ex;
    }
}
