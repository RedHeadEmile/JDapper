package net.redheademile.jdapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JDapperTriple<A, B, C, Z> extends JDapper<Z> {

    private final Class<A> aClass;
    private final Class<B> bClass;
    private final Class<C> cClass;
    private final JDapperMergeFunction<A, B, C, Z> mergeFunction;
    private final String firstAColumn;
    private final String firstBColumn;
    private final String firstCColumn;

    public JDapperTriple(Class<A> aClass, Class<B> bClass, Class<C> cClass, JDapperMergeFunction<A, B, C, Z> mergeFunction, String firstAColumn, String firstBColumn, String firstCColumn) {
        this.aClass = aClass;
        this.bClass = bClass;
        this.cClass = cClass;
        this.mergeFunction = mergeFunction;
        this.firstAColumn = firstAColumn.toLowerCase();
        this.firstBColumn = firstBColumn.toLowerCase();
        this.firstCColumn = firstCColumn.toLowerCase();
    }

    @Override
    public Z mapRow(ResultSet rs, int rowNum) throws SQLException {
        int[] indexes = super.findColumnIndexes(rs.getMetaData(), this.firstAColumn, this.firstBColumn, this.firstCColumn);

        A a = super.createFilledObject(aClass, rs, indexes[0], indexes[1] - 1);
        B b = super.createFilledObject(bClass, rs, indexes[1], indexes[2] - 1);
        C c = super.createFilledObject(cClass, rs, indexes[2], rs.getMetaData().getColumnCount());

        return this.mergeFunction.merge(a, b, c);
    }

    @FunctionalInterface
    public interface JDapperMergeFunction<A, B, C, Z> {
        Z merge(A a, B b, C c);
    }
}
