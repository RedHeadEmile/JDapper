package net.redheademile.jdapper;

public class JDapperException extends IllegalStateException {
    public JDapperException() {}

    public JDapperException(Exception e) {
        super(e);
    }
}
