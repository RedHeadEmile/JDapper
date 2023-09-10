package net.redheademile.jdapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JDapperQuintuple<A, B, C, D, E, Z> extends JDapper<Z> {

    private final Class<A> aClass;
    private final Class<B> bClass;
    private final Class<C> cClass;
    private final Class<D> dClass;
    private final Class<E> eClass;
    private final JDapperMergeFunction<A, B, C, D, E, Z> mergeFunction;
    private final String firstAColumn;
    private final String firstBColumn;
    private final String firstCColumn;
    private final String firstDColumn;
    private final String firstEColumn;

    public JDapperQuintuple(Class<A> aClass, Class<B> bClass, Class<C> cClass, Class<D> dClass, Class<E> eClass, JDapperMergeFunction<A, B, C, D, E, Z> mergeFunction, String firstAColumn, String firstBColumn, String firstCColumn, String firstDColumn, String firstEColumn) {
        this.aClass = aClass;
        this.bClass = bClass;
        this.cClass = cClass;
        this.dClass = dClass;
        this.eClass = eClass;
        this.mergeFunction = mergeFunction;
        this.firstAColumn = firstAColumn.toLowerCase();
        this.firstBColumn = firstBColumn.toLowerCase();
        this.firstCColumn = firstCColumn.toLowerCase();
        this.firstDColumn = firstDColumn.toLowerCase();
        this.firstEColumn = firstEColumn.toLowerCase();
    }

    @Override
    public Z mapRow(ResultSet rs, int rowNum) throws SQLException {
        int[] indexes = super.findColumnIndexes(rs.getMetaData(), this.firstAColumn, this.firstBColumn, this.firstCColumn, this.firstDColumn, this.firstEColumn);

        A a = super.createFilledObject(aClass, rs, indexes[0], indexes[1] - 1);
        B b = super.createFilledObject(bClass, rs, indexes[1], indexes[2] - 1);
        C c = super.createFilledObject(cClass, rs, indexes[2], indexes[3] - 1);
        D d = super.createFilledObject(dClass, rs, indexes[3], indexes[4] - 1);
        E e = super.createFilledObject(eClass, rs, indexes[4], rs.getMetaData().getColumnCount());

        return this.mergeFunction.merge(a, b, c, d, e);
    }

    @FunctionalInterface
    public interface JDapperMergeFunction<A, B, C, D, E, Z> {
        Z merge(A a, B b, C c, D d, E e);
    }
}
