package net.redheademile.jdapper;

import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class JDapper<T> implements RowMapper<T> {
    private static <U> U readValue(Field f, ResultSet rs, int columnIndex) throws SQLException {
        Object readObject = null;

        Class<?> type = f.getType();
        if (type == String.class)
            readObject = rs.getString(columnIndex);

        else if (type == boolean.class || type == Boolean.class)
            readObject = rs.getBoolean(columnIndex);

        else if (type == byte.class || type == Byte.class)
            readObject = rs.getByte(columnIndex);

        else if (type == short.class || type == Short.class)
            readObject = rs.getShort(columnIndex);

        else if (type == int.class || type == Integer.class)
            readObject = rs.getInt(columnIndex);

        else if (type == long.class || type == Long.class)
            readObject = rs.getLong(columnIndex);

        else if (type == float.class || type == Float.class)
            readObject = rs.getFloat(columnIndex);

        else if (type == double.class || type == Double.class)
            readObject = rs.getDouble(columnIndex);

        else if (type == BigDecimal.class)
            readObject = rs.getBigDecimal(columnIndex);

        else if (type == byte[].class)
            readObject = rs.getBytes(columnIndex);

        else if (type == java.sql.Date.class)
            readObject = rs.getDate(columnIndex);

        else if (type == java.sql.Time.class)
            readObject = rs.getTime(columnIndex);

        else if (type == java.sql.Timestamp.class)
            readObject = rs.getTimestamp(columnIndex);

        else if (type == java.io.InputStream.class) {
            if (f.getAnnotation(JDapperAsciiStream.class) != null)
                readObject = rs.getAsciiStream(columnIndex);

            else if (f.getAnnotation(JDapperBinaryStream.class) != null)
                readObject = rs.getBinaryStream(columnIndex);

            else
                throw new IllegalStateException("An input stream must have a decorator, either @JDapperAsciiStream or @JDapperBinaryStream");
        }

        else if (type == java.io.Reader.class)
            readObject = rs.getCharacterStream(columnIndex);

        else
            throw new IllegalStateException("Unsupported filed type: " + type);

        if (rs.wasNull())
            return null;
        return (U) readObject;
    }

    protected <U> U createFilledObject(Class<U> objClass, ResultSet rs, int columnIndexStart, int columnIndexEnd) {
        try {
            U u = objClass.getConstructor().newInstance();
            Map<String, Field> uFields = new HashMap<>();

            for (Field f : objClass.getDeclaredFields()) {
                JDapperColumnName columnName = f.getAnnotation(JDapperColumnName.class);
                if (columnName != null)
                    uFields.put(columnName.value().toLowerCase(), f);
                else
                    uFields.put(f.getName().toLowerCase(), f);
            }

            for (int i = columnIndexStart; i <= columnIndexEnd; i++) {
                String columnName = rs.getMetaData().getColumnLabel(i).toLowerCase();
                Field correspondingField = uFields.get(columnName);

                if (correspondingField == null)
                    continue;

                correspondingField.setAccessible(true);
                correspondingField.set(u, readValue(correspondingField, rs, i));
            }

            return u;
        }
        catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException |
               InvocationTargetException | SQLException e) {
            throw new JDapperException(e);
        }
    }

    protected int[] findColumnIndexes(ResultSetMetaData rsMetaData, String... columnNames) throws SQLException {
        int[] indexes = new int[columnNames.length];
        int lastFoundColumnNameIndex = 0;
        int columnAmount = rsMetaData.getColumnCount();

        for (int i = 1; i <= columnAmount; i++)
            if (columnNames[lastFoundColumnNameIndex].equals(rsMetaData.getColumnLabel(i).toLowerCase())) {
                indexes[lastFoundColumnNameIndex] = i;
                if (lastFoundColumnNameIndex++ == columnNames.length - 1)
                    break;
            }

        if (indexes[indexes.length - 1] == 0)
            throw new JDapperException();

        return indexes;
    }

    public static <A> RowMapper<A> getMapper(Class<A> aClass) {
        return new JDapperSimple<A>(aClass);
    }

    public static <A, B, Z> RowMapper<Z> getMapper(
            Class<A> aClass, Class<B> bClass,
            JDapperDouble.JDapperMergeFunction<A, B, Z> mergeFunction,
            String firstAColumn, String firstBColumn) {
        return new JDapperDouble<A, B, Z>(aClass, bClass, mergeFunction, firstAColumn, firstBColumn);
    }

    public static <A, B, C, Z> RowMapper<Z> getMapper(
            Class<A> aClass, Class<B> bClass, Class<C> cClass,
            JDapperTriple.JDapperMergeFunction<A, B, C, Z> mergeFunction,
            String firstAColumn, String firstBColumn, String firstCColumn) {
        return new JDapperTriple<A, B, C, Z>(aClass, bClass, cClass, mergeFunction, firstAColumn, firstBColumn, firstCColumn);
    }

    public static <A, B, C, D, Z> RowMapper<Z> getMapper(
            Class<A> aClass, Class<B> bClass, Class<C> cClass, Class<D> dClass,
            JDapperQuadruple.JDapperMergeFunction<A, B, C, D, Z> mergeFunction,
            String firstAColumn, String firstBColumn, String firstCColumn, String firstDColumn) {
        return new JDapperQuadruple<A, B, C, D, Z>(aClass, bClass, cClass, dClass, mergeFunction, firstAColumn, firstBColumn, firstCColumn, firstDColumn);
    }

    public static <A, B, C, D, E, Z> RowMapper<Z> getMapper(
            Class<A> aClass, Class<B> bClass, Class<C> cClass, Class<D> dClass, Class<E> eClass,
            JDapperQuintuple.JDapperMergeFunction<A, B, C, D, E, Z> mergeFunction,
            String firstAColumn, String firstBColumn, String firstCColumn, String firstDColumn, String firstEColumn) {
        return new JDapperQuintuple<A, B, C, D, E, Z>(aClass, bClass, cClass, dClass, eClass, mergeFunction, firstAColumn, firstBColumn, firstCColumn, firstDColumn, firstEColumn);
    }

    public static <A, B, C, D, E, F, Z> RowMapper<Z> getMapper(
            Class<A> aClass, Class<B> bClass, Class<C> cClass, Class<D> dClass, Class<E> eClass, Class<F> fClass,
            JDapperSixfold.JDapperMergeFunction<A, B, C, D, E, F, Z> mergeFunction,
            String firstAColumn, String firstBColumn, String firstCColumn, String firstDColumn, String firstEColumn, String firstFColumn) {
        return new JDapperSixfold<A, B, C, D, E, F, Z>(aClass, bClass, cClass, dClass, eClass, fClass, mergeFunction, firstAColumn, firstBColumn, firstCColumn, firstDColumn, firstEColumn, firstFColumn);
    }

    public static <A, B, C, D, E, F, G, Z> RowMapper<Z> getMapper(
            Class<A> aClass, Class<B> bClass, Class<C> cClass, Class<D> dClass, Class<E> eClass, Class<F> fClass, Class<G> gClass,
            JDapperSevenfold.JDapperMergeFunction<A, B, C, D, E, F, G, Z> mergeFunction,
            String firstAColumn, String firstBColumn, String firstCColumn, String firstDColumn, String firstEColumn, String firstFColumn, String firstGColumn) {
        return new JDapperSevenfold<A, B, C, D, E, F, G, Z>(aClass, bClass, cClass, dClass, eClass, fClass, gClass, mergeFunction, firstAColumn, firstBColumn, firstCColumn, firstDColumn, firstEColumn, firstFColumn, firstGColumn);
    }
}
