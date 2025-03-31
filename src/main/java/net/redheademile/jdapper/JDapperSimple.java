package net.redheademile.jdapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JDapperSimple<T> extends JDapper<T> {

    private final Class<T> tClass;
    public JDapperSimple(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public T mapRow(ResultSet rs) throws SQLException {
        return super.createFilledObject(tClass, rs, 1, rs.getMetaData().getColumnCount());
    }
}