import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DBUtils {

	private static DataSource dataSource = null;
	
	static {
		// dataSource资源只能初始化一次
		dataSource = new ComboPooledDataSource("mvcApp");
	}
	
	/**
	 * 获取连接
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 释放连接
	 * 
	 * @param connection
	 */
	public static void releaseConnection(Connection connection) {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
