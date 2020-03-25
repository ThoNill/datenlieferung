package tho.nill.datenlieferung.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

public class ResultSetEnvelope implements AutoCloseable {
	private Statement stmt;
	private ResultSet result;
	private Connection con;

	public ResultSetEnvelope(DataSource dataSource, String stmtString) throws SQLException {
		super();
		this.con = dataSource.getConnection();
		this.stmt = con.createStatement();
		this.result = stmt.executeQuery(stmtString);
	}

	public boolean next() throws SQLException {
		return result.next();
	}

	@Override
	public void close() throws Exception {
		if (!result.isClosed()) {
			result.close();
		}
		if (stmt.isClosed()) {
			stmt.close();
		}
		con.close();
	}

	public Object getObject(int columnIndex) throws SQLException {
		return result.getObject(columnIndex);
	}

	public Object getObject(String columnLabel) throws SQLException {
		return result.getObject(columnLabel);
	}

	public int getColumnCount() throws SQLException {
		return result.getMetaData().getColumnCount();
	}

	public Object[] getArray() throws SQLException {
		int size = getColumnCount();
		Object[] array = new Object[size];
		for (int i = 1; i <= size; i++) {
			array[i - 1] = getObject(i);
		}
		return array;
	}
}
