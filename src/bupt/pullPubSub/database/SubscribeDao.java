package bupt.pullPubSub.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SubscribeDao {
	private static SubscribeDao instance = null;

	public static SubscribeDao getInstance() {
		if (instance == null) {
			instance = new SubscribeDao();
		}
		return instance;
	}

	public List<Subscribe> getAllSubcribes() {
		List<Subscribe> subscribeList = new ArrayList<Subscribe>();
		Connection conn = null;
		try {
			conn = DBCon.getConn();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from subcribes");
			while (rs.next()) {
				Subscribe subscribe = new Subscribe();
				subscribe.setUsername(rs.getString(1));
				subscribe.setTopicName(rs.getString(2));

				subscribeList.add(subscribe);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return subscribeList;
	}

	public boolean saveSubscribe(String username, String topicName) {
		boolean result = false;
		Connection conn = null;
		try {
			conn = DBCon.getConn();
			String sql = "insert into subscribes(username, topicName) values(?, ?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			stmt.setString(2, topicName);

			int i = stmt.executeUpdate();
			if (i == 1)
				result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public boolean deleteSubscribe(String username) {
		boolean result = false;
		Connection conn = null;
		try {
			conn = DBCon.getConn();
			String sql = "delete from subscribes where username = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			int i = stmt.executeUpdate();
			if (i == 1)
				result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public List<Subscribe> selectSubscribeByUserName(String username) {
		List<Subscribe> subscribeList = new ArrayList<Subscribe>();
		Connection conn = null;
		try {
			conn = DBCon.getConn();
			String sql = "select * from subscribes where username = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Subscribe subscribe = new Subscribe();
				subscribe.setTopicName(rs.getString(1));
				subscribe.setUsername(rs.getString(2));

				subscribeList.add(subscribe);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return subscribeList;
	}
}
