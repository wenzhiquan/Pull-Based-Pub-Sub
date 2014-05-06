package bupt.pullPubSub.database;

import java.sql.DriverManager;
import java.sql.Connection;

public class DBCon {

	private static Connection conn = null;

	public static Connection getConn() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String dbName = "PullPubSubSystem";
			String username = "root";
			String password = "000000";
			String url = "jdbc:mysql://localhost:3306/" + dbName;

			conn = DriverManager.getConnection(url, username, password);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return conn;
	}
}
