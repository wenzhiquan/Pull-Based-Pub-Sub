package bupt.pullPubSub.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TopicDao {
	private static TopicDao instance = null;

	public static TopicDao getInstance() {
		if (instance == null) {
			instance = new TopicDao();
		}
		return instance;
	}

	public List<Topic> getAllTopics() {
		List<Topic> topicList = new ArrayList<Topic>();
		Connection conn = null;
		try {
			conn = DBCon.getConn();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from topics");
			while (rs.next()) {
				Topic topic = new Topic();
				topic.setTopicName(rs.getString(1));

				topicList.add(topic);
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
		return topicList;
	}

	public boolean saveTopic(Topic topic) {
		boolean result = false;
		Connection conn = null;
		try {
			conn = DBCon.getConn();
			String sql = "insert into topics(topicName) values(?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(2, topic.getTopicName());

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

	public boolean deleteTopic(String topicName) {
		boolean result = false;
		Connection conn = null;
		try {
			conn = DBCon.getConn();
			String sql = "delete from topics where topicName = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, topicName);
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

	public String selectTopicByName(String topicName) {
		Connection conn = null;
		try {
			conn = DBCon.getConn();
			String sql = "select * from topics where topicName = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, topicName);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				String topic;
				topic = rs.getString(2);
				return topic;
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
		return null;
	}

}
