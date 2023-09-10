package net.redheademile.jdapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JDapperDouble<A, B, Z> extends JDapper<Z> {

    private final Class<A> aClass;
    private final Class<B> bClass;
    private final JDapperMergeFunction<A, B, Z> mergeFunction;
    private final String firstAColumn;
    private final String firstBColumn;

    public JDapperDouble(Class<A> aClass, Class<B> bClass, JDapperMergeFunction<A, B, Z> mergeFunction, String firstAColumn, String firstBColumn) {
        this.aClass = aClass;
        this.bClass = bClass;
        this.mergeFunction = mergeFunction;
        this.firstAColumn = firstAColumn.toLowerCase();
        this.firstBColumn = firstBColumn.toLowerCase();
    }

    @Override
    public Z mapRow(ResultSet rs, int rowNum) throws SQLException {
        int[] indexes = super.findColumnIndexes(rs.getMetaData(), this.firstAColumn, this.firstBColumn);

        A a = super.createFilledObject(aClass, rs, indexes[0], indexes[1] - 1);
        B b = super.createFilledObject(bClass, rs, indexes[1], rs.getMetaData().getColumnCount());

        return this.mergeFunction.merge(a, b);
    }

    @FunctionalInterface
    public interface JDapperMergeFunction<A, B, Z> {
        Z merge(A a, B b);
    }
}
