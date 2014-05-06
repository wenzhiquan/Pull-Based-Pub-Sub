package bupt.pullPubSub.database;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDao {
	private static UserDao instance = null;

	public static UserDao getInstance() {
		if (instance == null) {
			instance = new UserDao();
		}
		return instance;
	}

	public List<User> getAllUsers() {
		List<User> userList = new ArrayList<User>();
		Connection conn = null;
		try {
			conn = DBCon.getConn();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from users");
			while (rs.next()) {
				User user = new User();
				user.setUsername(rs.getString(1));
				user.setPassword(rs.getString(2));

				userList.add(user);
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
		return userList;
	}

	public boolean saveUser(User user) {
		boolean result = false;
		Connection conn = null;
		try {
			conn = DBCon.getConn();
			String sql = "insert into users(username, password) values(?, ?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, user.getUsername());
			stmt.setString(2, user.getPassword());

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

	public boolean deleteUser(String username) {
		boolean result = false;
		Connection conn = null;
		try {
			conn = DBCon.getConn();
			String sql = "delete from users where username = ?";
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

	public boolean updateUser(String Password, String username) {
		boolean result = false;
		Connection conn = null;
		try {
			conn = DBCon.getConn();
			String sql = "update users set password = ? where username = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, Password);
			stmt.setString(2, username);
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

	public User selectUserByName(String username) {
		Connection conn = null;
		try {
			conn = DBCon.getConn();
			String sql = "select * from users where username = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				User user = new User();
				user.setUsername(rs.getString(1));
				user.setPassword(rs.getString(2));
				return user;
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
