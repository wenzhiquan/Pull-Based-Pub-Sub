package bupt.pullPubSub.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.List;

import bupt.pullPubSub.database.User;
import bupt.pullPubSub.database.UserDao;

public class Register {
	private User user = new User();
	private boolean isExist = false;
	private boolean result = false;
	
	public  Register(DataInputStream fromClient, DataOutputStream toClient){
		
		try {				
			try{
				//Receive user name and password from client
				user.setUsername(fromClient.readUTF());
				user.setPassword(fromClient.readUTF());
				
				//search whether the username is existed
				List<User> users = UserDao.getInstance().getAllUsers();
				
				for(User u:users){
					if(user.getUsername().equals(u.getUsername())){
						isExist = true;
						break;
					}
				}
				//if username does not exist
				if(!isExist){
					//if insert successfully
					if(UserDao.getInstance().saveUser(user)){
						toClient.writeUTF("true");
						result = true;
					}
					//if insert failed
					else{
						toClient.writeUTF("false");
						result = false;
					}
				}
				//if username has existed
				else{
					toClient.writeUTF("exist");
					result = false;
				}
			}catch(InterruptedIOException ex){
				System.err.print("timeout on read");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isExist() {
		return isExist;
	}

	public void setExist(boolean isExist) {
		this.isExist = isExist;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}
	
	
	
}
