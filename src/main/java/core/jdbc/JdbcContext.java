package core.jdbc;

import support.exception.FunctionWithException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcContext {

    public void executeUpdate(String sql, Object... parameter) {
        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement pstmt = populatePrepareStatement(con.prepareStatement(sql), parameter)) {
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            throw new JdbcException(ex);
        }
    }

    public <T> List<T> execute(String sql, FunctionWithException<ResultSet, T, SQLException> resultSetMapper, Object... parameter) {
        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement pstmt = populatePrepareStatement(con.prepareStatement(sql), parameter);
             ResultSet rs = pstmt.executeQuery()) {
            List<T> result = new ArrayList<>();
            while (rs.next()) {
                result.add(resultSetMapper.apply(rs));
            }
            return result;
        } catch (SQLException ex) {
            throw new JdbcException(ex);
        }
    }

    public <T> T executeOne(String sql, FunctionWithException<ResultSet, T, SQLException> resultSetMapper, Object... parameter) {
        List<T> results = execute(sql, resultSetMapper, parameter);
        if (results.isEmpty()) {
            return null;
        }

        return results.get(0);
    }

    private PreparedStatement populatePrepareStatement(PreparedStatement pstmt, Object[] parameter) throws SQLException {
        for (int i = 1; i <= parameter.length; i++) {
            pstmt.setObject(i, parameter[i-1]);
        }

        return pstmt;
    }

}
