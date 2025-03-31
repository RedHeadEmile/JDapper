package net.redheademile.jdapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JDapperSevenfold<A, B, C, D, E, F, G, Z> extends JDapper<Z> {

    private final Class<A> aClass;
    private final Class<B> bClass;
    private final Class<C> cClass;
    private final Class<D> dClass;
    private final Class<E> eClass;
    private final Class<F> fClass;
    private final Class<G> gClass;
    private final JDapperMergeFunction<A, B, C, D, E, F, G, Z> mergeFunction;
    private final String firstAColumn;
    private final String firstBColumn;
    private final String firstCColumn;
    private final String firstDColumn;
    private final String firstEColumn;
    private final String firstFColumn;
    private final String firstGColumn;

    public JDapperSevenfold(Class<A> aClass, Class<B> bClass, Class<C> cClass, Class<D> dClass, Class<E> eClass, Class<F> fClass, Class<G> gClass, JDapperMergeFunction<A, B, C, D, E, F, G, Z> mergeFunction, String firstAColumn, String firstBColumn, String firstCColumn, String firstDColumn, String firstEColumn, String firstFColumn, String firstGColumn) {
        this.aClass = aClass;
        this.bClass = bClass;
        this.cClass = cClass;
        this.dClass = dClass;
        this.eClass = eClass;
        this.fClass = fClass;
        this.gClass = gClass;
        this.mergeFunction = mergeFunction;
        this.firstAColumn = firstAColumn.toLowerCase();
        this.firstBColumn = firstBColumn.toLowerCase();
        this.firstCColumn = firstCColumn.toLowerCase();
        this.firstDColumn = firstDColumn.toLowerCase();
        this.firstEColumn = firstEColumn.toLowerCase();
        this.firstFColumn = firstFColumn.toLowerCase();
        this.firstGColumn = firstGColumn.toLowerCase();
    }

    @Override
    public Z mapRow(ResultSet rs) throws SQLException {
        int[] indexes = super.findColumnIndexes(rs.getMetaData(), this.firstAColumn, this.firstBColumn, this.firstCColumn, this.firstDColumn, this.firstEColumn, this.firstFColumn, this.firstGColumn);

        A a = super.createFilledObject(aClass, rs, indexes[0], indexes[1] - 1);
        B b = super.createFilledObject(bClass, rs, indexes[1], indexes[2] - 1);
        C c = super.createFilledObject(cClass, rs, indexes[2], indexes[3] - 1);
        D d = super.createFilledObject(dClass, rs, indexes[3], indexes[4] - 1);
        E e = super.createFilledObject(eClass, rs, indexes[4], indexes[5] - 1);
        F f = super.createFilledObject(fClass, rs, indexes[5], indexes[6] - 1);
        G g = super.createFilledObject(gClass, rs, indexes[6], rs.getMetaData().getColumnCount());

        return this.mergeFunction.merge(a, b, c, d, e, f, g);
    }

    @FunctionalInterface
    public interface JDapperMergeFunction<A, B, C, D, E, F, G, Z> {
        Z merge(A a, B b, C c, D d, E e, F f, G g);
    }
}
