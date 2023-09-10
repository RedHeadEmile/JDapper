package net.redheademile.jdapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JDapperQuadruple<A, B, C, D, Z> extends JDapper<Z> {

    private final Class<A> aClass;
    private final Class<B> bClass;
    private final Class<C> cClass;
    private final Class<D> dClass;
    private final JDapperMergeFunction<A, B, C, D, Z> mergeFunction;
    private final String firstAColumn;
    private final String firstBColumn;
    private final String firstCColumn;
    private final String firstDColumn;

    public JDapperQuadruple(Class<A> aClass, Class<B> bClass, Class<C> cClass, Class<D> dClass, JDapperMergeFunction<A, B, C, D, Z> mergeFunction, String firstAColumn, String firstBColumn, String firstCColumn, String firstDColumn) {
        this.aClass = aClass;
        this.bClass = bClass;
        this.cClass = cClass;
        this.dClass = dClass;
        this.mergeFunction = mergeFunction;
        this.firstAColumn = firstAColumn.toLowerCase();
        this.firstBColumn = firstBColumn.toLowerCase();
        this.firstCColumn = firstCColumn.toLowerCase();
        this.firstDColumn = firstDColumn.toLowerCase();
    }

    @Override
    public Z mapRow(ResultSet rs, int rowNum) throws SQLException {
        int[] indexes = super.findColumnIndexes(rs.getMetaData(), this.firstAColumn, this.firstBColumn, this.firstCColumn, this.firstDColumn);

        A a = super.createFilledObject(aClass, rs, indexes[0], indexes[1] - 1);
        B b = super.createFilledObject(bClass, rs, indexes[1], indexes[2] - 1);
        C c = super.createFilledObject(cClass, rs, indexes[2], indexes[3] - 1);
        D d = super.createFilledObject(dClass, rs, indexes[3], rs.getMetaData().getColumnCount());

        return this.mergeFunction.merge(a, b, c, d);
    }

    @FunctionalInterface
    public interface JDapperMergeFunction<A, B, C, D, Z> {
        Z merge(A a, B b, C c, D d);
    }
}
