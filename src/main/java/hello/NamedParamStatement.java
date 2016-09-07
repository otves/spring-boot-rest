package hello;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by otves on 07.09.2016.
 */
public class NamedParamStatement implements AutoCloseable {
    public NamedParamStatement(Connection conn, String sql) throws SQLException {
        int pos;
        while((pos = sql.indexOf(":")) != -1) {
            int end = sql.substring(pos).indexOf(" ");
            if (end == -1)
                end = sql.length();
            else
                end += pos;
            fields.add(sql.substring(pos+1,end));
            sql = sql.substring(0, pos) + "?" + sql.substring(end);
        }
        prepStmt = conn.prepareStatement(sql);
    }

    public PreparedStatement getPreparedStatement() {
        return prepStmt;
    }
    public ResultSet executeQuery() throws SQLException {
        return prepStmt.executeQuery();
    }
    public void close() throws SQLException {
        prepStmt.close();
    }

    public void setDate(String name, java.sql.Date value) throws SQLException {
        prepStmt.setDate(getIndex(name), value);
    }

    public void setInt(String name, int value) throws SQLException {
        prepStmt.setInt(getIndex(name), value);
    }
    public void setLong(String name, long value) throws SQLException {
        prepStmt.setLong(getIndex(name), value);
    }

    private int getIndex(String name) {
        return fields.indexOf(name)+1;
    }
    private PreparedStatement prepStmt;
    private List<String> fields = new ArrayList<String>();
}