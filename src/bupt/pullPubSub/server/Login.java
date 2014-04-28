package bupt.pullPubSub.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;

import bupt.pullPubSub.database.User;
import bupt.pullPubSub.database.UserDao;

public class Login {
	private String username;
	
	public Login(DataInputStream fromClient, DataOutputStream toClient){
		
		//Create data input and output stream
		try {
			
			try{
				User user = new User();
				//Receive user name and password from client
				user.setUsername(fromClient.readUTF());
				user.setPassword(fromClient.readUTF());
				
				//search the user's name and password whether in the database
				User userSearch = UserDao.getInstance().selectUserByName(user.getUsername());
				if(userSearch != null){
					//if exist, return "true" and user's type to client
					if(user.getPassword().equals(userSearch.getPassword())){
						toClient.writeUTF("true");
						setUsername(userSearch.getUsername());
					}
					//if does not exist, return "false" and 0 to client
					else{
						toClient.writeUTF("false");
					}
				}else
					toClient.writeUTF("false");
			}catch(InterruptedIOException ex){
				System.err.print("timeout on read");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
