package bupt.pullPubSub.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MessageDao {
	private static MessageDao instance = null;

	public static MessageDao getInstance() {
		if (instance == null)
			instance = new MessageDao();
		return instance;
	}

	public List<Message> getAllMessages() {
		List<Message> messageList = new ArrayList<Message>();
		Connection conn = null;
		try {
			conn = DBCon.getConn();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from messages");
			while (rs.next()) {
				Message message = new Message();
				message.setMsgID(rs.getInt(1));
				message.setPublisher(rs.getString(2));
				message.setTopicName(rs.getString(3));
				message.setMsgContent(rs.getString(4));
				message.setPubTime(rs.getString(5));

				messageList.add(message);
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
		return messageList;
	}

	public boolean saveMessage(Message message) {
		boolean result = false;
		Connection conn = null;
		try {
			conn = DBCon.getConn();
			String sql = "insert into messages(publisher, topicName, msgContent, pubTime) values(?, ?, ?, ?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, message.getPublisher());
			stmt.setString(2, message.getTopicName());
			stmt.setString(3, message.getMsgContent());
			stmt.setString(4, message.getPubTime());

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

	public boolean deleteMessage(int id) {
		boolean result = false;
		Connection conn = null;
		try {
			conn = DBCon.getConn();
			String sql = "delete from messages where msgID = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
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

	public List<Message> selectMessageByTopicNameAndRefreshTime(
			String topicName, String oldRefreshTime, String newRefreshTime) {
		List<Message> messageList = new ArrayList<Message>();
		Connection conn = null;
		try {
			conn = DBCon.getConn();
			String sql = "select * from messages where topicName = ? and pubTime > ? and pubTime < ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, topicName);
			stmt.setString(2, oldRefreshTime);
			stmt.setString(3, newRefreshTime);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				Message message = new Message();
				message.setMsgID(rs.getInt(1));
				message.setPublisher(rs.getString(2));
				message.setTopicName(rs.getString(3));
				message.setMsgContent(rs.getString(4));
				message.setPubTime(rs.getString(5));

				messageList.add(message);

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
		return messageList;
	}
}
